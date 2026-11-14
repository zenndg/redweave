package io.github.marcelofabianov.redweave.domain.cable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The strength of each piece of a cable run, worked out in one pass as the game wire would end up:
 * the strongest input less the distance, losing 1 between spaces and nothing on an inner curve,
 * only between cables of the same color and owner, and only as far as a strength reaches (answers
 * 497, 499, 509, 528, 530, 540 and 541).
 */
class CableRunTest {
  private static final PlayerId OWNER = new PlayerId(new UUID(0, 1));
  private static final PlayerId STRANGER = new PlayerId(new UUID(0, 2));
  private static final Insulation RED = new Insulation(ChannelColor.RED, OWNER);
  private static final Insulation BLUE = new Insulation(ChannelColor.BLUE, OWNER);
  private static final Insulation STRANGERS_RED = new Insulation(ChannelColor.RED, STRANGER);
  private static final CablePiece BARE = new CablePiece(RED, Signal.OFF);
  private static final CablePiece SOURCE = new CablePiece(RED, Signal.MAX);
  private static final int FIRST = 0;
  private static final int SECOND = 1;
  private static final int THIRD = 2;
  private static final int REACH = CableRun.REACH;
  private static final int BEYOND = REACH + 1;
  private static final int WEAK = 5;
  private static final int FAR_LINE = REACH + REACH;
  private static final int MISSING = -1;

  private static Signal of(int strength) {
    return new Signal(strength);
  }

  @Test
  void aSourceLosesOnePerSpace() {
    TestGrid grid = TestGrid.line(THIRD + 1, BARE).put(FIRST, SOURCE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(Signal.MAX, run.get(FIRST));
    assertEquals(of(REACH - SECOND), run.get(SECOND));
    assertEquals(of(REACH - THIRD), run.get(THIRD));
  }

  @Test
  void anInnerCurveLosesNothing() {
    TestGrid grid =
        new TestGrid()
            .put(FIRST, SOURCE)
            .put(SECOND, BARE)
            .put(THIRD, BARE)
            .link(FIRST, SECOND, Passage.INNER_CURVE)
            .link(SECOND, THIRD, Passage.NEXT_SPACE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(Signal.MAX, run.get(SECOND));
    assertEquals(of(REACH - SECOND), run.get(THIRD));
  }

  @Test
  void theStrongestSourceWins() {
    TestGrid grid = TestGrid.line(THIRD + 1, BARE).put(FIRST, SOURCE).put(THIRD, SOURCE);

    Map<Integer, Signal> run = CableRun.around(Set.of(SECOND), grid);

    assertEquals(of(REACH - SECOND), run.get(SECOND));
  }

  @Test
  void aPieceKeepsItsOwnInputWhenItIsStronger() {
    TestGrid grid =
        TestGrid.line(THIRD + 1, BARE)
            .put(FIRST, new CablePiece(RED, of(WEAK)))
            .put(THIRD, new CablePiece(RED, of(WEAK + THIRD)));

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(of(WEAK), run.get(FIRST));
    assertEquals(of(WEAK + SECOND), run.get(SECOND));
  }

  @Test
  void aStrongerNeighborBeatsAWeakInput() {
    TestGrid grid =
        TestGrid.line(SECOND + 1, BARE)
            .put(FIRST, SOURCE)
            .put(SECOND, new CablePiece(RED, of(WEAK)));

    Map<Integer, Signal> run = CableRun.around(Set.of(SECOND), grid);

    assertEquals(of(REACH - SECOND), run.get(SECOND));
  }

  @Test
  void theWalkStopsThirtySpacesAway() {
    TestGrid grid = TestGrid.line(FAR_LINE + THIRD + 1, BARE);

    CableRun.around(Set.of(FIRST), grid);

    assertTrue(grid.wasAsked(FAR_LINE + SECOND));
    assertFalse(grid.wasAsked(FAR_LINE + THIRD));
  }

  @Test
  void theSignalFadesOutAfterFifteenSpaces() {
    TestGrid grid = TestGrid.line(BEYOND + 1, BARE).put(FIRST, SOURCE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(Signal.OFF, run.get(REACH));
    assertFalse(run.containsKey(BEYOND));
  }

  @Test
  void anotherColorIsNotJoined() {
    TestGrid grid = TestGrid.line(SECOND + 1, BARE).put(FIRST, SOURCE);
    grid.put(SECOND, new CablePiece(BLUE, Signal.OFF));

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertFalse(run.containsKey(SECOND));
  }

  @Test
  void anotherOwnerIsNotJoined() {
    TestGrid grid = TestGrid.line(SECOND + 1, BARE).put(FIRST, SOURCE);
    grid.put(SECOND, new CablePiece(STRANGERS_RED, Signal.OFF));

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertFalse(run.containsKey(SECOND));
  }

  @Test
  void aLoopWithoutASourceStaysOff() {
    TestGrid grid = TestGrid.line(THIRD + 1, BARE).link(THIRD, FIRST, Passage.NEXT_SPACE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(THIRD + 1, run.size());
    assertTrue(run.values().stream().allMatch(signal -> signal.equals(Signal.OFF)));
  }

  @Test
  void aSourceOutOfReachOfTheChangeStillFeedsThePiecesInReach() {
    TestGrid grid = TestGrid.line(FAR_LINE, BARE).put(FIRST, SOURCE);

    Map<Integer, Signal> run = CableRun.around(Set.of(BEYOND), grid);

    assertEquals(of(REACH - SECOND), run.get(SECOND));
    assertFalse(run.containsKey(FIRST));
  }

  @Test
  void aGoneOrUnloadedPieceStopsTheRun() {
    TestGrid grid = TestGrid.line(SECOND + 1, BARE).put(FIRST, SOURCE);
    grid.link(SECOND, MISSING, Passage.NEXT_SPACE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST), grid);

    assertEquals(SECOND + 1, run.size());
  }

  @Test
  void aGoneStartGivesNothing() {
    assertTrue(CableRun.around(Set.of(MISSING), new TestGrid()).isEmpty());
  }

  @Test
  void everyStartIsWorkedOut() {
    TestGrid grid = new TestGrid().put(FIRST, SOURCE).put(SECOND, BARE);

    Map<Integer, Signal> run = CableRun.around(Set.of(FIRST, SECOND), grid);

    assertEquals(Map.of(FIRST, Signal.MAX, SECOND, Signal.OFF), run);
  }

  @Test
  void aPieceNeedsItsInsulationAndInput() {
    assertThrows(NullPointerException.class, () -> new CablePiece(null, Signal.OFF));
    assertThrows(NullPointerException.class, () -> new CablePiece(RED, null));
  }

  @Test
  void aLinkNeedsItsPieceAndPassage() {
    assertThrows(NullPointerException.class, () -> new CableLink<>(null, Passage.NEXT_SPACE));
    assertThrows(NullPointerException.class, () -> new CableLink<>(FIRST, null));
  }

  @Test
  void onlyTheNextSpaceLosesStrength() {
    assertEquals(1, Passage.NEXT_SPACE.loss());
    assertEquals(0, Passage.INNER_CURVE.loss());
  }
}
