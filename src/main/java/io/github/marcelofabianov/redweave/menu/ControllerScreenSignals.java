package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.NetworkView;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The live values of the Controller Screen, in the order of the rows: the signal and the state of
 * each node and then of each registered module, and the value of each channel. A module that writes
 * nothing this tick has {@link #NO_SIGNAL} (answer 814). Sent only when one of them changes (answer
 * 82).
 */
public record ControllerScreenSignals(
    int menu,
    List<Integer> nodeSignals,
    List<Integer> nodeStates,
    List<Integer> channelValues,
    int remotesOn,
    int modulesOn)
    implements CustomPacketPayload {
  private static final String ID = "controller_screen_signals";

  /** The signal of a module that writes nothing on a channel this tick, shown as `—` (814). */
  public static final int NO_SIGNAL = -1;

  public static final Type<ControllerScreenSignals> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  private static final StreamCodec<ByteBuf, List<Integer>> NUMBERS =
      ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list());

  public static final StreamCodec<RegistryFriendlyByteBuf, ControllerScreenSignals> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          ControllerScreenSignals::menu,
          NUMBERS,
          ControllerScreenSignals::nodeSignals,
          NUMBERS,
          ControllerScreenSignals::nodeStates,
          NUMBERS,
          ControllerScreenSignals::channelValues,
          ByteBufCodecs.VAR_INT,
          ControllerScreenSignals::remotesOn,
          ByteBufCodecs.VAR_INT,
          ControllerScreenSignals::modulesOn,
          ControllerScreenSignals::new);

  /** Before the first values arrive, nothing is on. */
  static final ControllerScreenSignals NONE =
      new ControllerScreenSignals(0, List.of(), List.of(), List.of(), 0, 0);

  public ControllerScreenSignals {
    nodeSignals = List.copyOf(nodeSignals);
    nodeStates = List.copyOf(nodeStates);
    channelValues = List.copyOf(channelValues);
  }

  static ControllerScreenSignals of(int menu, NetworkView view) {
    return new ControllerScreenSignals(
        menu,
        Stream.concat(
                view.nodes().stream().map(row -> row.signal().strength()),
                view.modules().stream()
                    .map(row -> row.signal().map(Signal::strength).orElse(NO_SIGNAL)))
            .toList(),
        Stream.concat(
                view.nodes().stream().map(row -> row.state().ordinal()),
                view.modules().stream().map(row -> row.state().ordinal()))
            .toList(),
        view.channels().stream().map(row -> row.value().strength()).toList(),
        view.remotesOn(),
        view.modulesOn());
  }

  ControllerScreenSignals forMenu(int other) {
    return new ControllerScreenSignals(
        other, nodeSignals, nodeStates, channelValues, remotesOn, modulesOn);
  }

  /** On the client: only the open Controller Screen of the same menu takes the values. */
  public static void receive(ControllerScreenSignals signals, Player player) {
    if (player.containerMenu instanceof ControllerMenu menu && menu.containerId == signals.menu()) {
      menu.acceptSignals(signals);
    }
  }

  @Override
  public Type<ControllerScreenSignals> type() {
    return TYPE;
  }
}
