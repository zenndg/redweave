package io.github.zenndg.redweave.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Face;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The state a Signal Remote keeps in its item: the link, the mode, the lever and the end of the
 * pulse (answers 287, 288, 356, 358 and 369).
 */
class RemoteStateTest {
  private static final UUID ID = UUID.fromString("3d4e5f6a-7b8c-4d9e-8f0a-1b2c3d4e5f6a");
  private static final UUID OTHER_ID = UUID.fromString("4e5f6a7b-8c9d-4e0f-9a1b-2c3d4e5f6a7b");
  private static final UUID NETWORK = UUID.fromString("6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b");
  private static final UUID NODE = UUID.fromString("1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e");
  private static final int SLOT = 14;
  private static final RemoteLink LINK = new RemoteLink(NETWORK, NODE, SLOT);
  private static final UUID LEVER = UUID.fromString("5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c");
  private static final BlockLocation PLACE = new BlockLocation("minecraft:overworld", 4, 64, -8);
  private static final EmitterLink EMITTER = new EmitterLink(PLACE, Face.UP, LEVER);
  private static final long NOW = 1000;
  private static final int PULSE = 20;
  private static final long NO_PULSE = 0;
  private static final long LATER = NOW + 5;
  private static final long AFTER_THE_PULSE = NOW + PULSE;

  private static RemoteState linked() {
    return RemoteState.NEW.linkedTo(LINK, ID);
  }

  @Test
  void aNewTabletHasNoLinkNoIdAndIsOff() {
    assertTrue(RemoteState.NEW.link().isEmpty());
    assertTrue(RemoteState.NEW.id().isEmpty());
    assertEquals(RemoteMode.LEVER, RemoteState.NEW.mode());
    assertFalse(RemoteState.NEW.counts(NOW));
  }

  @Test
  void theFirstLinkGivesTheIdAndALaterLinkKeepsIt() {
    RemoteState first = linked();
    RemoteState relinked = first.linkedTo(LINK, OTHER_ID);

    assertEquals(Optional.of(ID), first.id());
    assertEquals(Optional.of(ID), relinked.id());
  }

  @Test
  void aNewLinkStartsOffAndFound() {
    RemoteState relinked = linked().fired(NOW, PULSE).lost(true).linkedTo(LINK, ID);

    assertFalse(relinked.on());
    assertFalse(relinked.isLost());
  }

  @Test
  void theLeverTurnsOnAndOff() {
    RemoteState on = linked().fired(NOW, PULSE);

    assertTrue(on.counts(NOW));
    assertFalse(on.fired(NOW, PULSE).counts(NOW));
  }

  @Test
  void theButtonCountsUntilThePulseEnds() {
    RemoteState pressed = linked().withMode(RemoteMode.BUTTON).fired(NOW, PULSE);

    assertTrue(pressed.counts(LATER));
    assertFalse(pressed.counts(AFTER_THE_PULSE));
    assertFalse(pressed.on());
  }

  @Test
  void aPressDuringThePulseStartsItAgain() {
    RemoteState pressed = linked().withMode(RemoteMode.BUTTON).fired(NOW, PULSE);

    RemoteState again = pressed.fired(LATER, PULSE);

    assertTrue(again.counts(AFTER_THE_PULSE));
  }

  @Test
  void switchingTheModeLetsGoOfTheLeverAndThePulse() {
    RemoteState on = linked().fired(NOW, PULSE);

    assertFalse(on.withMode(RemoteMode.BUTTON).counts(NOW));
  }

  @Test
  void aLostLinkNeverCounts() {
    assertFalse(linked().fired(NOW, PULSE).lost(true).counts(NOW));
  }

  @Test
  void theLookFollowsTheState() {
    assertEquals(RemoteLook.UNLINKED, RemoteState.NEW.look(NOW));
    assertEquals(RemoteLook.OFF, linked().look(NOW));
    assertEquals(RemoteLook.ON, linked().fired(NOW, PULSE).look(NOW));
    assertEquals(RemoteLook.LOST, linked().lost(true).look(NOW));
  }

  /** A tablet linked to an emitter fires the emitter, never a channel (answers 778 and 791). */
  @Test
  void aLinkToAnEmitterReplacesTheNodeAndStartsOff() {
    RemoteState emitter = linked().fired(NOW, PULSE).linkedTo(EMITTER);

    assertEquals(Optional.of(EMITTER), emitter.emitter());
    assertTrue(emitter.link().isEmpty());
    assertFalse(emitter.on());
    assertEquals(Optional.of(ID), emitter.id());
  }

  @Test
  void aLinkToANodeReplacesTheEmitter() {
    RemoteState node = RemoteState.NEW.linkedTo(EMITTER).linkedTo(LINK, ID);

    assertTrue(node.emitter().isEmpty());
    assertEquals(Optional.of(LINK), node.link());
  }

  @Test
  void aTabletOfAnEmitterNeverCountsOnAChannel() {
    RemoteState emitter = RemoteState.NEW.linkedTo(EMITTER);

    assertFalse(emitter.fired(NOW, PULSE).counts(LATER));
  }

  /** The emitter may change by the hand of its owner, so the tablet always looks off (788). */
  @Test
  void aTabletOfAnEmitterLooksOffOrLost() {
    RemoteState emitter = RemoteState.NEW.linkedTo(EMITTER);

    assertEquals(RemoteLook.OFF, emitter.fired(NOW, PULSE).look(LATER));
    assertEquals(RemoteLook.LOST, emitter.lost(true).look(NOW));
  }

  /** A damaged item with both links keeps only the one of the node (rule 14). */
  @Test
  void aStateReadWithBothLinksKeepsTheNode() {
    RemoteState read =
        RemoteState.read(
            Optional.of(ID),
            Optional.of(LINK),
            Optional.of(EMITTER),
            RemoteMode.LEVER,
            false,
            NO_PULSE,
            false);

    assertEquals(Optional.of(LINK), read.link());
    assertTrue(read.emitter().isEmpty());
  }
}
