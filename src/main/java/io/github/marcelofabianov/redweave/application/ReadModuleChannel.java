package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * What an input of a Signal Module on a channel reads: the value of the channel from the last tick,
 * or 0 when the module has no network, is out of range or the network is not online (385, 387).
 */
public final class ReadModuleChannel {
  private final NetworkFinder finder;

  public ReadModuleChannel(NetworkFinder finder) {
    this.finder = finder;
  }

  public Signal read(SignalModule module, BlockLocation plate, ChannelColor channel) {
    return reading(module, plate, channel).orElse(Signal.OFF);
  }

  /**
   * The value of the channel, or none while it is not available to the module: no network, out of
   * range or a network not online, which the Signal Flip-Flop keeps apart from 0 (answer 889).
   */
  public Optional<Signal> reading(SignalModule module, BlockLocation plate, ChannelColor channel) {
    return module
        .network()
        .flatMap(finder::find)
        .filter(network -> network.isOnline() && network.modules().reaches(plate))
        .map(network -> network.channelValue(channel));
  }
}
