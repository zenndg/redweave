package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.ModuleChannel;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reads the channels of a module for its entry in the network, leaving each connection as it is:
 * its output, when on a channel, and every other connection on a channel as an input (answer 810).
 */
final class ModuleChannels implements ConnectionMapper {
  private final List<ChannelColor> seen = new ArrayList<>();

  @Override
  public Connection of(Connection connection) {
    connection.channel().ifPresent(seen::add);
    return connection;
  }

  /** The channels of the module: the inputs first, then the output, if any is on a channel. */
  static List<ModuleChannel> of(ModulePart part) {
    ModuleChannels reader = new ModuleChannels();
    part.dropChannels(reader);
    List<ChannelColor> inputs = new ArrayList<>(reader.seen);
    Optional<ChannelColor> output = part.output().channel();
    output.ifPresent(inputs::remove);
    List<ModuleChannel> channels = new ArrayList<>();
    inputs.forEach(channel -> channels.add(new ModuleChannel(channel, Direction.INPUT)));
    output.ifPresent(channel -> channels.add(new ModuleChannel(channel, Direction.OUTPUT)));
    return channels;
  }
}
