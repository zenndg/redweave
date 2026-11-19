package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.module.Connection;

/**
 * What a module does with each of its connections, in turn: the {@link ChannelDrop} of a paste
 * takes them off their channels (answer 732), and the {@link ModuleChannels} of the register reads
 * them (answer 810).
 */
public interface ConnectionMapper {
  /** The connection the module keeps in its place. */
  Connection of(Connection connection);
}
