package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * What a Signal Remote shows of its link, for the tooltip and the action bar: the network, the node
 * and the channel of the moment, whether the network is offline and whether another source holds
 * the channel on (answers 289, 359, 366 and 370).
 */
public record RemoteView(
    String network,
    ChannelColor color,
    String node,
    ChannelColor channel,
    Optional<String> channelName,
    boolean offline,
    boolean othersHold) {}
