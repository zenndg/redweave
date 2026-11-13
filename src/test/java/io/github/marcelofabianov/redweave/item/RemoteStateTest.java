package io.github.marcelofabianov.redweave.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  private static final long NOW = 1000;
  private static final int PULSE = 20;
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
}
