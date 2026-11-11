package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import net.minecraft.network.chat.Component;

/** What the action bar shows about a controller: refusals, no network and a network turned off. */
public final class ControllerMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".network_controller.";

  public static final String NO_NETWORK = PREFIX + "no_network";
  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String ALREADY_PLACED = PREFIX + "already_placed";
  public static final String OFFLINE = PREFIX + "offline";
  public static final String NETWORK_LIMIT = PREFIX + "network_limit";

  private ControllerMessages() {}

  /** A network turned off takes no action from the players (answers 132 and 160). */
  public static Component offline(String network) {
    return Component.translatable(OFFLINE, network);
  }

  /**
   * The refusal of a placement, or none when the outcome needs no word (answers 55, 56 and 211).
   * The limit is the number of networks a player may have.
   */
  public static Optional<Component> refusal(Outcome outcome, int limit) {
    return switch (outcome) {
      case NOT_OWNER -> Optional.of(Component.translatable(NOT_OWNER));
      case ALREADY_PLACED -> Optional.of(Component.translatable(ALREADY_PLACED));
      case NETWORK_LIMIT -> Optional.of(Component.translatable(NETWORK_LIMIT, limit));
      default -> Optional.empty();
    };
  }
}
