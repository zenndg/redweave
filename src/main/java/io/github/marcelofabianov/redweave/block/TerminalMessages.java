package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import net.minecraft.network.chat.Component;

/** What the action bar says about a Network Terminal (answers 153 and 254). */
public final class TerminalMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".network_terminal.";

  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String LIMIT = PREFIX + "limit";

  private TerminalMessages() {}

  /** Another player clicked a terminal that is not theirs (answer 153). */
  public static Component notOwner() {
    return Component.translatable(NOT_OWNER);
  }

  /** The player already has a terminal, and where it is, so it can be found (answer 254). */
  public static Component limit(BlockLocation existing) {
    return Component.translatable(LIMIT, existing.x(), existing.y(), existing.z());
  }
}
