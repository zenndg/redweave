package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Outcome;
import java.util.Optional;

/** Whether a Network Terminal was placed and, when refused, where the player has one (254). */
public record TerminalPlacement(Outcome outcome, Optional<BlockLocation> existing) {}
