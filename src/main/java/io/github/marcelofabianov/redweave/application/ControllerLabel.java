package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;

/**
 * What the floating label over a Network Controller shows with the Configurator in hand, in two
 * lines: the state of the network and how many components it has, and below its name, after a
 * square in its color (answers 773 and 804).
 */
public record ControllerLabel(
    String network, ChannelColor color, NetworkState state, int components) {}
