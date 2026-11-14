package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * What the floating label over a Signal Module shows with the Configurator in hand, in two lines:
 * its name, and its state with the channel of its output, when the output is on a channel (answer
 * 417). The state is the key of its text, which each module defines and the client writes in the
 * language of the player.
 */
public record ModuleLabel(String name, String state, Optional<ChannelColor> output) {}
