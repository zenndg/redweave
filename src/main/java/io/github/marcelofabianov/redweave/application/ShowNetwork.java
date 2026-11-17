package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Channel;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.ModuleEntry;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Controller Screen of a network, as one viewer sees it. Anyone may look (answer 22), but the
 * position of the nodes and the modules stays on the server for anyone but the owner (answer 149).
 * The nodes come in the order they joined the network (answer 216), and the registered Signal
 * Modules after them (answer 809).
 */
public final class ShowNetwork {
  private final NetworkFinder finder;

  public ShowNetwork(NetworkFinder finder) {
    this.finder = finder;
  }

  public Optional<NetworkView> of(PlayerId viewer, NetworkId id, LoadedNodes loaded) {
    return finder.find(id).map(network -> viewOf(network, viewer, loaded));
  }

  private static NetworkView viewOf(Network network, PlayerId viewer, LoadedNodes loaded) {
    boolean owner = network.isOwner(viewer);
    List<NodeRow> nodes =
        network.nodes().stream().map(node -> rowOf(network, node, owner, loaded)).toList();
    List<ModuleRow> modules =
        network.register().entries().stream()
            .map(entry -> rowOf(network, entry, owner, loaded))
            .toList();
    List<ChannelRow> channels = channelsOf(network);
    return new NetworkView(
        network.name(),
        network.color(),
        network.owner(),
        network.state(),
        owner,
        nodes,
        modules,
        channels,
        network.remotes().on(),
        network.modules().on());
  }

  private static NodeRow rowOf(Network network, Node node, boolean owner, LoadedNodes loaded) {
    return new NodeRow(
        node.id(),
        node.displayName(),
        network.nodeSignal(node.id()),
        network.nodeState(node.id(), loaded.isLoaded(node.position())),
        node.kind(),
        owner ? Optional.of(node.position()) : Optional.empty(),
        slotsOf(node));
  }

  /**
   * A registered module, with the rules of a node for its state and place (answers 809 and 810).
   */
  private static ModuleRow rowOf(
      Network network, ModuleEntry entry, boolean owner, LoadedNodes loaded) {
    return new ModuleRow(
        entry.id(),
        entry.name().orElse(DefaultNames.of(entry.id().value())),
        entry.kind(),
        network.register().signal(entry.id()),
        network.register().state(entry.id(), loaded.isLoaded(entry.position())),
        owner ? Optional.of(entry.position()) : Optional.empty(),
        entry.channels());
  }

  private static List<SlotRow> slotsOf(Node node) {
    List<SlotRow> rows = new ArrayList<>();
    for (int index = 0; index < node.slots().size(); index++) {
      int at = index;
      node.slots()
          .get(index)
          .filter(ChannelSlot::active)
          .ifPresent(slot -> rows.add(new SlotRow(at, slot.channel(), slot.direction())));
    }
    return rows;
  }

  /** The 16 channels, counting the slots in one pass over the nodes (_docs/limites.md, load). */
  private static List<ChannelRow> channelsOf(Network network) {
    SlotCounts counts = SlotCounts.of(network.nodes());
    return network.channels().stream().map(channel -> rowOf(network, channel, counts)).toList();
  }

  private static ChannelRow rowOf(Network network, Channel channel, SlotCounts counts) {
    return new ChannelRow(
        channel.color(),
        channel.name(),
        network.channelValue(channel.color()),
        counts.of(channel.color(), Direction.INPUT),
        counts.of(channel.color(), Direction.OUTPUT));
  }
}
