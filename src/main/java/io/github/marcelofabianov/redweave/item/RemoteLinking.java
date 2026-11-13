package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.NodeRow;
import io.github.marcelofabianov.redweave.application.SlotRow;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.network.RemoteId;
import io.github.marcelofabianov.redweave.domain.network.RemoteInput;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

/**
 * What the screen of the link changes on a Signal Remote, and how its link reads: the mode, the
 * link checked by the server, and the text of the link for the tooltip and the screen (answers 287,
 * 291, 357, 365 and 366).
 */
public final class RemoteLinking {
  private static final String SQUARE = "■ ";

  private RemoteLinking() {}

  public static RemoteState stateOf(ItemStack stack) {
    return RemoteFiring.stateOf(stack);
  }

  /** Another mode lets go of the lever and the pulse (answer 288). */
  public static void mode(ItemStack stack, RemoteMode mode) {
    if (stack.getItem() instanceof SignalRemoteItem) {
      stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), stateOf(stack).withMode(mode));
    }
  }

  /**
   * Links the tablet to an input slot, when the owner may (answer 357): the new link replaces the
   * old one, starts off, keeps the ID of the first link and says so (answers 291 and 358).
   */
  public static boolean link(
      ServerPlayer player, ItemStack stack, NetworkId network, NodeRow node, SlotRow slot) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || !(stack.getItem() instanceof SignalRemoteItem)) {
      return false;
    }
    PlayerId owner = new PlayerId(player.getUUID());
    if (server.get().remotes().link().link(owner, network, node.id(), slot.index())
        != Outcome.ACCEPTED) {
      return false;
    }
    RemoteLink link = new RemoteLink(network.value(), node.id().value(), slot.index());
    RemoteState linked = stateOf(stack).linkedTo(link, UUID.randomUUID());
    stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), linked);
    RemoteInput tablet =
        new RemoteInput(new RemoteId(linked.id().orElseThrow()), node.id(), slot.index());
    server
        .get()
        .remotes()
        .show()
        .show(network, tablet)
        .map(RemoteLabel::of)
        .ifPresent(label -> confirm(player, stack, label));
    return true;
  }

  /** Writes the names of the new link on the item and confirms it (answer 291). */
  private static void confirm(ServerPlayer player, ItemStack stack, RemoteLabel label) {
    stack.set(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get(), label);
    player.displayClientMessage(
        Component.translatable(RemoteMessages.LINKED, partsOf(label)), true);
  }

  /** `■ <network> · <node> · <channel>`, `No link` or `Link lost` (answer 366). */
  public static Component describe(ItemStack stack) {
    RemoteState state = stateOf(stack);
    RemoteLabel label = stack.get(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get());
    if (state.link().isEmpty() || label == null) {
      return Component.translatable(RemoteMessages.NO_LINK);
    }
    if (state.isLost()) {
      return Component.translatable(RemoteMessages.LINK_LOST);
    }
    return Component.translatable(RemoteMessages.LINK, partsOf(label));
  }

  private static Object[] partsOf(RemoteLabel label) {
    return new Object[] {
      squareOf(label.color()).append(label.network()),
      label.node(),
      ChannelNames.of(label.channel(), label.channelName())
    };
  }

  /** The square in the color of the network; the colors follow the order of the wool (224). */
  private static MutableComponent squareOf(ChannelColor color) {
    int rgb = DyeColor.byId(color.ordinal()).getTextColor();
    return Component.literal(SQUARE).withStyle(style -> style.withColor(rgb));
  }
}
