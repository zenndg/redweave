package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** One Network Terminal per player, kept in the saves (answers 137, 175 and 253). */
class TerminalRegistryTest {
  private static final BlockLocation FIRST = new BlockLocation(OVERWORLD, 10, 64, 10);
  private static final BlockLocation SECOND = new BlockLocation(OVERWORLD, 20, 64, 20);
  private static final TerminalLookup STANDING = location -> TerminalCheck.STANDING;
  private static final TerminalLookup GONE = location -> TerminalCheck.GONE;
  private static final TerminalLookup UNLOADED = location -> TerminalCheck.UNLOADED;

  private final InMemoryTerminals terminals = new InMemoryTerminals();
  private final PlaceTerminal place = new PlaceTerminal(terminals, terminals);
  private final BreakTerminal breakTerminal = new BreakTerminal(terminals, terminals);

  @Test
  void theFirstTerminalOfAPlayerIsKept() {
    TerminalPlacement placed = place.place(OWNER, FIRST, STANDING);

    assertEquals(Outcome.ACCEPTED, placed.outcome());
    assertEquals(Optional.of(FIRST), terminals.terminalOf(OWNER));
  }

  /** A second one is refused with where the first is, so the player finds it (answer 254). */
  @Test
  void aSecondTerminalIsRefusedWithWhereTheFirstIs() {
    place.place(OWNER, FIRST, STANDING);

    TerminalPlacement placed = place.place(OWNER, SECOND, STANDING);

    assertEquals(new TerminalPlacement(Outcome.TERMINAL_LIMIT, Optional.of(FIRST)), placed);
    assertEquals(Optional.of(FIRST), terminals.terminalOf(OWNER));
  }

  @Test
  void eachPlayerHasATerminal() {
    place.place(OWNER, FIRST, STANDING);

    assertEquals(Outcome.ACCEPTED, place.place(STRANGER, SECOND, STANDING).outcome());
  }

  /** A terminal gone without breaking, in a loaded chunk, frees the player (answer 253). */
  @Test
  void aTerminalGoneFromALoadedChunkIsReplaced() {
    place.place(OWNER, FIRST, STANDING);

    assertEquals(Outcome.ACCEPTED, place.place(OWNER, SECOND, GONE).outcome());
    assertEquals(Optional.of(SECOND), terminals.terminalOf(OWNER));
  }

  /** In an unloaded chunk the block is never read, so the first one holds (rule 6 and 253). */
  @Test
  void aTerminalInAnUnloadedChunkHolds() {
    place.place(OWNER, FIRST, STANDING);

    assertEquals(Outcome.TERMINAL_LIMIT, place.place(OWNER, SECOND, UNLOADED).outcome());
  }

  @Test
  void breakingTheTerminalFreesThePlayer() {
    place.place(OWNER, FIRST, STANDING);

    breakTerminal.broke(OWNER, FIRST);

    assertEquals(Optional.empty(), terminals.terminalOf(OWNER));
    assertEquals(Outcome.ACCEPTED, place.place(OWNER, SECOND, STANDING).outcome());
  }

  /** Only the registered block frees the player, not a stale one elsewhere. */
  @Test
  void breakingAnotherBlockKeepsTheRegisteredOne() {
    place.place(OWNER, FIRST, STANDING);

    breakTerminal.broke(OWNER, SECOND);

    assertEquals(Optional.of(FIRST), terminals.terminalOf(OWNER));
  }
}
