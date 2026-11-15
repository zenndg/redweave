package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import io.github.marcelofabianov.redweave.menu.PressurePlateScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Pressure Plate in Portuguese (placa-de-pressao.md and answers 550, 603,
 * 609, 611 and 621). `Strength`, `On` and `Off` are those of the Signal Lever.
 */
final class PressurePlatePortugueseTexts {
  private static final String SIGNAL_PRESSURE_PLATE = "Placa de Pressão de Sinal";
  private static final String MODE = "Modo";
  private static final String PLAYERS = "Players";
  private static final String PASSIVE = "Passivos";
  private static final String HOSTILE = "Hostis";
  private static final String ITEMS = "Itens";
  private static final String ON = "Ligada";
  private static final String OFF = "Desligada";
  private static final String SEPARATOR = " · ";
  private static final String NEW_MODE = "%s: %s";
  private static final String FLOOR_ONLY = "A Placa de Pressão de Sinal só vai no chão.";

  private PressurePlatePortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_PRESSURE_PLATE.get().getDescriptionId(), SIGNAL_PRESSURE_PLATE);
    add.accept(PressurePlateScreenTexts.MODE, MODE);
    add.accept(ModuleMessages.PRESSURE_PLATE_MODE, NEW_MODE);
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
