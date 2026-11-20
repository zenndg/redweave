package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NetworkState;
import io.github.zenndg.redweave.domain.signal.ChannelColor;

/**
 * A network in the list of the Network Terminal: name, color, state and components (answers 131 and
 * 804).
 */
public record TerminalRow(
    NetworkId id, String name, ChannelColor color, NetworkState state, int components) {}
