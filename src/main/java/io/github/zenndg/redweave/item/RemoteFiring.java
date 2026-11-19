package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.application.RemoteView;
import io.github.zenndg.redweave.config.NeoForgeRemotePulse;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.RemoteId;
import io.github.zenndg.redweave.domain.network.RemoteInput;
import io.github.zenndg.redweave.menu.ChannelNames;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.function.IntSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * The click of a Signal Remote, on the server: whoever holds it fires, the lever switches and the
 * button pulses, and the action bar says what happened (answers 287, 288, 357, 359, 369 and 370). A
 * tablet linked to a Signal Emitter fires the emitter instead (answer 781).
 */
final class RemoteFiring {
  private static final String NO_NAME = "";
  private static final IntSupplier PULSE = new NeoForgeRemotePulse();

  private RemoteFiring() {}

  static void fire(ServerPlayer player, ItemStack stack) {
    RemoteState state = stateOf(stack);
    if (state.emitter().isPresent()) {
      EmitterRemotes.fire(player, stack, state.emitter().get());
      return;
    }
    Optional<RemoteInput> tablet = inputOf(state);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (tablet.isEmpty() || server.isEmpty()) {
      say(player, Component.translatable(RemoteMessages.HOW_TO_LINK));
      return;
    }
    NetworkId network = new NetworkId(state.link().orElseThrow().network());
    Optional<RemoteView> view = server.get().remotes().show().show(network, tablet.get());
    if (view.isEmpty()) {
      stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), state.lost(true));
      say(player, Component.translatable(RemoteMessages.LINK_LOST));
      return;
    }
    if (view.get().offline()) {
      say(player, Component.translatable(RemoteMessages.OFFLINE, view.get().network()));
      return;
    }
    RemoteState fired = state.fired(player.serverLevel().getGameTime(), PULSE.getAsInt());
    stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), fired);
    say(player, messageOf(fired, view.get()));
  }

  static RemoteState stateOf(ItemStack stack) {
    return stack.getOrDefault(RedweaveDataComponents.SIGNAL_REMOTE.get(), RemoteState.NEW);
  }

  /** The tablet as the network sees it, once it has an ID and a link. */
  static Optional<RemoteInput> inputOf(RemoteState state) {
    if (state.id().isEmpty() || state.link().isEmpty()) {
      return Optional.empty();
    }
    RemoteLink link = state.link().get();
    return Optional.of(
        new RemoteInput(new RemoteId(state.id().get()), new NodeId(link.node()), link.slot()));
  }

  private static Component messageOf(RemoteState fired, RemoteView view) {
    Component channel = ChannelNames.marked(view.channel(), view.channelName().orElse(NO_NAME));
    if (view.othersHold()) {
      return Component.translatable(RemoteMessages.HELD, channel);
    }
    if (fired.mode() == RemoteMode.BUTTON) {
      return Component.translatable(RemoteMessages.PULSE, channel);
    }
    return Component.translatable(
        RemoteMessages.FIRED,
        channel,
        RemoteMessages.state(fired.on(), RemoteMessages.ON, RemoteMessages.OFF));
  }

  private static void say(ServerPlayer player, Component message) {
    player.displayClientMessage(message, true);
  }
}
