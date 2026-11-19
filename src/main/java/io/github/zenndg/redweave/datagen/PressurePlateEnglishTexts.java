package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.part.PartMessages;
import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.menu.PressurePlateScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Pressure Plate in English (placa-de-pressao.md and answers 550, 603, 609,
 * 611 and 621). `Strength`, `On` and `Off` are those of the Signal Lever.
 */
final class PressurePlateEnglishTexts {
  private static final String SIGNAL_PRESSURE_PLATE = "Signal Pressure Plate";
  private static final String MODE = "Mode";
  private static final String PLAYERS = "Players";
  private static final String PASSIVE = "Passive";
  private static final String HOSTILE = "Hostile";
  private static final String ITEMS = "Items";
  private static final String ON = "On";
  private static final String OFF = "Off";
  private static final String SEPARATOR = " · ";
  private static final String NEW_MODE = "%s: %s";
  private static final String PULSED = "%s: On";
  private static final String FLOOR_ONLY = "The Signal Pressure Plate only goes on the floor.";

  private PressurePlateEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_PRESSURE_PLATE.get().getDescriptionId(), SIGNAL_PRESSURE_PLATE);
    add.accept(PressurePlateScreenTexts.MODE, MODE);
    add.accept(ModuleMessages.PRESSURE_PLATE_MODE, NEW_MODE);
    add.accept(ModuleMessages.PRESSURE_PLATE_PULSED, PULSED);
    add.accept(PartMessages.FLOOR_ONLY, FLOOR_ONLY);
    for (PlateMode mode : PlateMode.values()) {
      String name = nameOf(mode);
      add.accept(PressurePlateScreenTexts.of(mode), name);
      add.accept(PressurePlateScreenTexts.stateOf(true, mode), ON + SEPARATOR + name);
      add.accept(PressurePlateScreenTexts.stateOf(false, mode), OFF + SEPARATOR + name);
    }
  }

  private static String nameOf(PlateMode mode) {
    return switch (mode) {
      case PLAYERS -> PLAYERS;
      case PASSIVE -> PASSIVE;
      case HOSTILE -> HOSTILE;
      case ITEMS -> ITEMS;
    };
  }
}
