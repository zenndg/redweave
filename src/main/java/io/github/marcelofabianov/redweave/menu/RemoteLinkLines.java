package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.application.NetworkView;
import io.github.marcelofabianov.redweave.application.NodeRow;
import io.github.marcelofabianov.redweave.application.SlotRow;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NodeKind;
import java.util.List;
import net.minecraft.network.chat.Component;

/**
 * The lines of each step of the link, on the server: the networks online, the nodes with an enabled
 * input and the input slots of a node (answers 282 to 286 and 359).
 */
final class RemoteLinkLines {
  private static final String NO_NAME = "";

  private RemoteLinkLines() {}

  static RemoteLinkLine ofNetwork(NetworkChoice choice) {
    return new RemoteLinkLine(
        Component.literal(choice.name()),
        choice.color().ordinal(),
        NetworkTexts.components(choice.components()),
        RemoteLinkLine.NONE);
  }

  /** Only the nodes with an enabled input take a tablet (answer 282). */
  static List<NodeRow> inputNodes(NetworkView view) {
    return view.nodes().stream()
        .filter(row -> row.kind() == NodeKind.INPUT || row.kind() == NodeKind.BOTH)
        .toList();
  }

  static RemoteLinkLine ofNode(NodeRow row) {
    return new RemoteLinkLine(
        Component.literal(row.name()),
        RemoteLinkLine.NONE,
        Component.translatable(
            RemoteLinkTexts.NODE_DETAIL,
            Component.translatable(ControllerKinds.keyOf(row.kind())),
            row.signal().strength()),
        row.state().ordinal());
  }

  static List<SlotRow> inputSlots(NodeRow row) {
    return row.slots().stream().filter(slot -> slot.direction() == Direction.INPUT).toList();
  }

  static RemoteLinkLine ofSlot(SlotRow slot, NetworkView view) {
    String name = view.channels().get(slot.channel().ordinal()).name().orElse(NO_NAME);
    return new RemoteLinkLine(
        ChannelNames.of(slot.channel(), name),
        slot.channel().ordinal(),
        Component.empty(),
        RemoteLinkLine.NONE);
  }
}
