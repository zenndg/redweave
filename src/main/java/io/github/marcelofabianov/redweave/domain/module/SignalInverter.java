package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.circuit.Not;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The rule of the Signal Inverter, one tick at a time: the output is the NOT of the input of 2
 * ticks before, as a torch (answers 296, 310 and 444). An input on a channel is already one tick
 * old, so it waits one more (446). The first tick, after placing or loading, reads the input at
 * once (442 and 450). More changes of the output than the limit within the window burn it out: off
 * for a while, and then it reads the input again (445).
 */
public final class SignalInverter {
  /** The delay of an input on the edge of the plate, the one of a torch (answer 444). */
  public static final int EDGE_DELAY = 2;

  /** The delay of an input on a channel, which is already one tick old (answer 446). */
  public static final int CHANNEL_DELAY = 1;

  private static final String INVALID_DELAY = "A delay of 1 or 2 ticks, got %d";
  private static final String INVALID_BURNOUT = "The ticks of burnout cannot be %d";

  /** The inputs waiting to reach the output, oldest first; empty until the input is read. */
  private final List<Signal> waiting;

  private final Signal output;
  private final int burnoutTicks;
  private final long clock;

  /** The ticks when the output changed lately, for the burnout; never saved (answer 450). */
  private final List<Long> changes;

  private SignalInverter(
      List<Signal> waiting, Signal output, int burnoutTicks, long clock, List<Long> changes) {
    if (burnoutTicks < 0) {
      throw new IllegalArgumentException(String.format(INVALID_BURNOUT, burnoutTicks));
    }
    this.waiting = List.copyOf(waiting);
    this.output = Objects.requireNonNull(output);
    this.burnoutTicks = burnoutTicks;
    this.clock = clock;
    this.changes = List.copyOf(changes);
  }

  /** A new inverter: its first tick reads the input and emits at once (answer 442). */
  public static SignalInverter placed() {
    return restore(0);
  }

  /** An inverter from the saves, which keep only what is left of its burnout (answer 450). */
  public static SignalInverter restore(int burnoutTicks) {
    return new SignalInverter(List.of(), Signal.OFF, burnoutTicks, 0, List.of());
  }

  /** One tick, with the input and its delay: 2 on the edge, 1 on a channel. */
  public SignalInverter tick(Signal input, int delay, InverterLimits limits) {
    Objects.requireNonNull(input);
    if (delay != EDGE_DELAY && delay != CHANNEL_DELAY) {
      throw new IllegalArgumentException(String.format(INVALID_DELAY, delay));
    }
    long now = clock + 1;
    if (burnoutTicks > 0) {
      return new SignalInverter(List.of(), Signal.OFF, burnoutTicks - 1, now, List.of());
    }
    if (waiting.size() != delay) {
      return changeTo(Not.output(input), filled(input, delay), now, limits);
    }
    List<Signal> next = new ArrayList<>(waiting.subList(1, waiting.size()));
    next.add(input);
    return changeTo(Not.output(waiting.get(0)), next, now, limits);
  }

  /** The new output, which counts as a change for the burnout when it differs (answer 445). */
  private SignalInverter changeTo(Signal next, List<Signal> line, long now, InverterLimits limits) {
    List<Long> recent = new ArrayList<>();
    changes.stream().filter(tick -> tick > now - limits.burnoutWindowTicks()).forEach(recent::add);
    if (!next.equals(output) && !waiting.isEmpty()) {
      recent.add(now);
    }
    if (recent.size() > limits.burnoutToggles()) {
      return new SignalInverter(List.of(), Signal.OFF, limits.burnoutTicks(), now, List.of());
    }
    return new SignalInverter(line, next, 0, now, recent);
  }

  private static List<Signal> filled(Signal input, int delay) {
    List<Signal> line = new ArrayList<>();
    for (int tick = 0; tick < delay; tick++) {
      line.add(input);
    }
    return line;
  }

  public Signal output() {
    return output;
  }

  public InverterState state() {
    if (burnoutTicks > 0) {
      return InverterState.BURNT_OUT;
    }
    return output.isOn() ? InverterState.ON : InverterState.OFF;
  }

  /** What is left of the burnout, the only part that is saved (answer 450). */
  public int burnoutTicks() {
    return burnoutTicks;
  }
}
