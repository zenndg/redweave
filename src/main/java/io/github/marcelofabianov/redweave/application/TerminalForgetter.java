package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** Takes the Network Terminal of a player out of the saves, when it is broken (answer 175). */
@FunctionalInterface
public interface TerminalForgetter {
  void forget(PlayerId player);
}
