package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;

/** A network in the list of the Network Terminal: name, color, state and nodes (answer 131). */
public record TerminalRow(
    NetworkId id, String name, ChannelColor color, NetworkState state, int nodes) {}
