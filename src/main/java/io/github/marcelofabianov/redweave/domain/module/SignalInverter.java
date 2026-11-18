package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.circuit.Not;
import io.github.marcelofabianov.redweave.domain.signal.DelayLine;
import io.github.marcelofabianov.redweave.domain.signal.DelayLineLimits;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The rule of the Signal Inverter, one tick at a time: the NOT of the input of 2 ticks before, as a
 * torch (answers 296, 310 and 444), and then the delay its owner chose (answers 830 to 833). An
 * input on a channel is already one tick old, so it waits one more (446). The first tick, after
 * placing or loading with nothing waiting, reads the input at once (442 and 450). More changes than
 * the limit within the window, counted as they enter the delay line, burn it out: off for a while,
 * with the line emptied, and then it reads the input again (445 and 835).
 */
public final class SignalInverter {
  /** The delay of an input on the edge of the plate, the one of a torch (answer 444). */
  public static final int EDGE_DELAY = 2;

  /** The delay of an input on a channel, which is already one tick old (answer 446). */
  public static final int CHANNEL_DELAY = 1;

  private static final String INVALID_DELAY = "A delay of 1 or 2 ticks, got %d";
  private static final String INVALID_BURNOUT = "The ticks of burnout cannot be %d";

  /** The inputs waiting for the torch, oldest first; empty until the input is read. */
  private final List<Signal> waiting;

  private final Signal output;
  private final InverterSettings settings;
  private final DelayLine line;
  private final int burnoutTicks;
  private final long clock;

  /** The ticks when a change entered the line lately, for the burnout; never saved (450). */
  private final List<Long> changes;

  private SignalInverter(Torch torch, InverterSettings settings, DelayLine line, int burnoutTicks) {
    if (burnoutTicks < 0) {
      throw new IllegalArgumentException(String.format(INVALID_BURNOUT, burnoutTicks));
    }
    this.waiting = List.copyOf(torch.waiting());
    this.output = torch.output();
    this.settings = Objects.requireNonNull(settings);
    this.line = Objects.requireNonNull(line);
    this.burnoutTicks = burnoutTicks;
    this.clock = torch.clock();
    this.changes = List.copyOf(torch.changes());
  }

  /** A new inverter: its first tick reads the input and emits at once (answer 442). */
  public static SignalInverter placed() {
    return restore(0, InverterSettings.DEFAULT, DelayLine.EMPTY);
  }

  /**
   * An inverter from the saves, with what is left of its burnout, its settings and its delay line,
   * whose output until the first change comes out is off before one that turns on and its strength
   * before one that turns off (answers 450, 836 and 854).
   */
  public static SignalInverter restore(
      int burnoutTicks, InverterSettings settings, DelayLine line) {
    Torch fresh = new Torch(List.of(), line.outputBefore(settings.strength()), 0, List.of());
    return new SignalInverter(fresh, settings, line, burnoutTicks);
  }

  /** One tick, with the input and the delay of the torch: 2 on the edge, 1 on a channel. */
  public SignalInverter tick(
      Signal input, int delay, InverterLimits limits, DelayLineLimits lineLimits) {
    Objects.requireNonNull(input);
    if (delay != EDGE_DELAY && delay != CHANNEL_DELAY) {
      throw new IllegalArgumentException(String.format(INVALID_DELAY, delay));
    }
    long now = clock + 1;
    if (burnoutTicks > 0) {
      return burnt(burnoutTicks - 1, now);
    }
    boolean fresh = waiting.isEmpty();
    List<Signal> next = filled(input, delay);
    Signal torchOutput = Not.output(input);
    if (waiting.size() == delay) {
      next = new ArrayList<>(waiting.subList(1, waiting.size()));
      next.add(input);
      torchOutput = Not.output(waiting.get(0));
    }
    Signal value = torchOutput.isOn() ? settings.strength() : Signal.OFF;
    if (fresh && line.isEmpty()) {
      return changeTo(new Torch(next, value, now, List.of()), line, false, limits);
    }
    DelayLine.Passage passage = line.pass(value, settings.delay(), output, lineLimits);
    Torch moved = new Torch(next, passage.output(), now, List.of());
    return changeTo(moved, passage.line(), passage.entered() && !fresh, limits);
  }

  /** The inverter after a tick, burnt out when the changes went past the limit (answer 445). */
  private SignalInverter changeTo(
      Torch torch, DelayLine next, boolean changed, InverterLimits limits) {
    long now = torch.clock();
    List<Long> recent = new ArrayList<>();
    changes.stream().filter(tick -> tick > now - limits.burnoutWindowTicks()).forEach(recent::add);
    if (changed) {
      recent.add(now);
    }
    if (recent.size() > limits.burnoutToggles()) {
      return burnt(limits.burnoutTicks(), now);
    }
    Torch kept = new Torch(torch.waiting(), torch.output(), now, recent);
    return new SignalInverter(kept, settings, next, 0);
  }

  /** Off, with nothing waiting, for the ticks of the burnout (answers 445 and 835). */
  private SignalInverter burnt(int ticks, long now) {
    return new SignalInverter(
        new Torch(List.of(), Signal.OFF, now, List.of()), settings, DelayLine.EMPTY, ticks);
  }

  /** The same inverter with a new strength, which the output and the line take at once (843). */
  public SignalInverter withStrength(int next) {
    Signal strength = Emitter.stepped(next);
    InverterSettings changed = new InverterSettings(strength, settings.delay());
    Torch now = new Torch(waiting, output.isOn() ? strength : Signal.OFF, clock, changes);
    return new SignalInverter(now, changed, line.withStrength(strength), burnoutTicks);
  }

  /** The same inverter with a new delay; what waits keeps its tick (answer 839). */
  public SignalInverter withDelay(OutputDelay next) {
    InverterSettings changed = new InverterSettings(settings.strength(), next);
    return new SignalInverter(torch(), changed, line, burnoutTicks);
  }

  private Torch torch() {
    return new Torch(waiting, output, clock, changes);
  }

  private static List<Signal> filled(Signal input, int delay) {
    List<Signal> filled = new ArrayList<>();
    for (int tick = 0; tick < delay; tick++) {
      filled.add(input);
    }
    return filled;
  }

  /** The chosen strength while on, nothing while off (answer 834). */
  public Signal output() {
    return output;
  }

  public InverterState state() {
    if (burnoutTicks > 0) {
      return InverterState.BURNT_OUT;
    }
    return output.isOn() ? InverterState.ON : InverterState.OFF;
  }

  public InverterSettings settings() {
    return settings;
  }

  /** The changes still waiting for the delay, saved with the inverter (answer 836). */
  public DelayLine line() {
    return line;
  }

  /** What is left of the burnout, saved with the inverter (answer 450). */
  public int burnoutTicks() {
    return burnoutTicks;
  }

  /** The torch of the inverter: the inputs it waits for, its output, its clock and changes. */
  private record Torch(List<Signal> waiting, Signal output, long clock, List<Long> changes) {}
}
