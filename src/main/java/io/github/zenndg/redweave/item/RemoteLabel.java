package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.application.RemoteView;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * The names of the link of a Signal Remote, for its tooltip: the client does not know the network,
 * so the server writes them on the item each tick they change. They never go to the saves (366).
 */
public record RemoteLabel(
    String network, ChannelColor color, String node, ChannelColor channel, String channelName) {
  private static final String NO_NAME = "";

  public static RemoteLabel of(RemoteView view) {
    return new RemoteLabel(
        view.network(),
        view.color(),
        view.node(),
        view.channel(),
        view.channelName().orElse(NO_NAME));
  }

  public Optional<String> ownChannelName() {
    return channelName.isEmpty() ? Optional.empty() : Optional.of(channelName);
  }
}
