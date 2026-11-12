package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.TerminalScreenTexts;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.ChannelLine;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.Label;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * The lines of the label over a node (answer 270): the node and its network, with the state when
 * the network is not online, up to 4 channels as `15 Red · Output · 12`, and `+3 more`. The numbers
 * come from the server, so one outside the colors or states shows a safe one.
 */
public final class NodeLabelLines {
  private static final String SEPARATOR = " · ";
  private static final int NONE = 0;

  private NodeLabelLines() {}

  public static List<Component> of(Label label) {
    List<Component> lines = new ArrayList<>();
    lines.add(head(label));
    label.channels().forEach(channel -> lines.add(channelOf(channel)));
    if (label.more() > NONE) {
      lines.add(Component.translatable(NodeScreenTexts.LABEL_MORE, label.more()));
    }
    return lines;
  }

  private static Component head(Label label) {
    MutableComponent head = Component.literal(label.nodeName() + SEPARATOR);
    if (!label.hasNetwork()) {
      return head.append(Component.translatable(NodeScreenTexts.STATE_NO_NETWORK));
    }
    head.append(Component.literal(label.network()));
    NetworkState state = stateOf(label.state());
    if (state != NetworkState.ONLINE) {
      head.append(Component.literal(SEPARATOR)).append(stateText(state));
    }
    return head;
  }

  private static Component channelOf(ChannelLine channel) {
    ChannelColor[] colors = ChannelColor.values();
    ChannelColor color =
        channel.color() >= 0 && channel.color() < colors.length
            ? colors[channel.color()]
            : colors[0];
    boolean input = channel.direction() == Direction.INPUT.ordinal();
    return Component.empty()
        .append(ChannelNames.of(color, channel.name()))
        .append(Component.literal(SEPARATOR))
        .append(Component.translatable(input ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT))
        .append(Component.literal(SEPARATOR + channel.signal()));
  }

  private static NetworkState stateOf(int state) {
    NetworkState[] states = NetworkState.values();
    return state >= 0 && state < states.length ? states[state] : NetworkState.OFFLINE;
  }

  private static Component stateText(NetworkState state) {
    return Component.translatable(
        switch (state) {
          case ONLINE -> NodeScreenTexts.STATE_ONLINE;
          case OFFLINE -> NodeScreenTexts.STATE_OFFLINE;
          case UNPLACED -> TerminalScreenTexts.STATE_UNPLACED;
        });
  }
}
