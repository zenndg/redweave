package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import net.minecraft.network.chat.Component;

/** What the action bar shows about a controller, until the screen of issue #20 (answer 52). */
public final class ControllerMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".network_controller.";

  public static final String CURRENT = PREFIX + "current";
  public static final String FOREIGN = PREFIX + "foreign";
  public static final String NO_NETWORK = PREFIX + "no_network";
  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String ALREADY_PLACED = PREFIX + "already_placed";

  private ControllerMessages() {}

  /** The refusal of a placement, or none when the outcome needs no word (answers 55 and 56). */
  public static Optional<Component> refusal(Outcome outcome) {
    return switch (outcome) {
      case NOT_OWNER -> Optional.of(Component.translatable(NOT_OWNER));
      case ALREADY_PLACED -> Optional.of(Component.translatable(ALREADY_PLACED));
      default -> Optional.empty();
    };
  }
}
