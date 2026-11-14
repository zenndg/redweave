package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.DetectorScreenTexts;
import io.github.marcelofabianov.redweave.menu.InverterScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Modules in English (modulos-de-sinal.md, signal-timer.md,
 * signal-inverter.md and weather-detector.md).
 */
final class ModuleEnglishTexts {
  private static final String SIGNAL_TIMER = "Signal Timer";
  private static final String USE_TO_OPEN = "Use a Configurator to open this module.";
  private static final String PICKED_UP = "Module picked up";
  private static final String NOT_OWNER = "Only the player who placed this module can change it.";
  private static final String OUTPUT = "Output: %s";
  private static final String NOT_COPIED = "Modules cannot be copied yet.";
  private static final String TIMER_RUNNING = "%s: Running";
  private static final String TIMER_STOPPED = "%s: Stopped";
  private static final String SCREEN_INTERVAL = "Interval";
  private static final String SCREEN_PULSE = "Pulse";
  private static final String SCREEN_OUTPUT = "Output";
  private static final String SCREEN_PAUSE = "Pause";
  private static final String SCREEN_EDGE = "Edge";
  private static final String SCREEN_CHANNEL = "Channel";
  private static final String SCREEN_RUNNING = "Running";
  private static final String SCREEN_STOPPED = "Stopped";
  private static final String SCREEN_PAUSED = "Paused";
  private static final String SCREEN_SECONDS = "%s s";
  private static final String SIGNAL_INVERTER = "Signal Inverter";
  private static final String INVERTER_NOTHING = "Signal Inverter has nothing to toggle.";
  private static final String INVERTER_ON = "On";
  private static final String INVERTER_OFF = "Off";
  private static final String INVERTER_BURNT_OUT = "Burnt out";
  private static final String WEATHER_DETECTOR = "Weather Detector";
  private static final String DETECTOR_MODE_MESSAGE = "%s: %s";
  private static final String DETECTOR_MODE = "Mode";
  private static final String DETECTOR_RAIN = "Rain";
  private static final String DETECTOR_THUNDER = "Thunder";
  private static final String DETECTOR_CLEAR = "Clear";
  private static final String DETECTOR_ON = "On";
  private static final String DETECTOR_OFF = "Off";
  private static final String DETECTOR_NO_WEATHER = "No weather";
  private static final String SCREEN_TICKS = "%s ticks";

  private ModuleEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveBlocks.SIGNAL_TIMER.get().getDescriptionId(), SIGNAL_TIMER);
    add.accept(ModuleMessages.USE_TO_OPEN, USE_TO_OPEN);
    add.accept(ModuleMessages.PICKED_UP, PICKED_UP);
    add.accept(ModuleMessages.NOT_OWNER, NOT_OWNER);
    add.accept(ModuleMessages.OUTPUT, OUTPUT);
    add.accept(ModuleMessages.NOT_COPIED, NOT_COPIED);
    add.accept(ModuleMessages.TIMER_RUNNING, TIMER_RUNNING);
    add.accept(ModuleMessages.TIMER_STOPPED, TIMER_STOPPED);
    add.accept(TimerScreenTexts.INTERVAL, SCREEN_INTERVAL);
    add.accept(TimerScreenTexts.PULSE, SCREEN_PULSE);
    add.accept(TimerScreenTexts.OUTPUT, SCREEN_OUTPUT);
    add.accept(TimerScreenTexts.PAUSE, SCREEN_PAUSE);
    add.accept(TimerScreenTexts.EDGE, SCREEN_EDGE);
    add.accept(TimerScreenTexts.CHANNEL, SCREEN_CHANNEL);
    add.accept(TimerScreenTexts.RUNNING, SCREEN_RUNNING);
    add.accept(TimerScreenTexts.STOPPED, SCREEN_STOPPED);
    add.accept(TimerScreenTexts.PAUSED, SCREEN_PAUSED);
    add.accept(TimerScreenTexts.SECONDS, SCREEN_SECONDS);
    add.accept(TimerScreenTexts.TICKS, SCREEN_TICKS);
    add.accept(RedweaveBlocks.SIGNAL_INVERTER.get().getDescriptionId(), SIGNAL_INVERTER);
    add.accept(ModuleMessages.INVERTER_NOTHING_TO_TOGGLE, INVERTER_NOTHING);
    add.accept(InverterScreenTexts.ON, INVERTER_ON);
    add.accept(InverterScreenTexts.OFF, INVERTER_OFF);
    add.accept(InverterScreenTexts.BURNT_OUT, INVERTER_BURNT_OUT);
    addDetector(add);
  }

  /** The Weather Detector (weather-detector.md). */
  private static void addDetector(BiConsumer<String, String> add) {
    add.accept(RedweaveBlocks.WEATHER_DETECTOR.get().getDescriptionId(), WEATHER_DETECTOR);
    add.accept(ModuleMessages.DETECTOR_MODE, DETECTOR_MODE_MESSAGE);
    add.accept(DetectorScreenTexts.MODE, DETECTOR_MODE);
    add.accept(DetectorScreenTexts.RAIN, DETECTOR_RAIN);
    add.accept(DetectorScreenTexts.THUNDER, DETECTOR_THUNDER);
    add.accept(DetectorScreenTexts.CLEAR, DETECTOR_CLEAR);
    add.accept(DetectorScreenTexts.ON, DETECTOR_ON);
    add.accept(DetectorScreenTexts.OFF, DETECTOR_OFF);
    add.accept(DetectorScreenTexts.NO_WEATHER, DETECTOR_NO_WEATHER);
  }
}
