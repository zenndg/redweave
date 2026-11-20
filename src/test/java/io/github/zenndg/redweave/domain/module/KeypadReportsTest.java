package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The summary an offline owner gets when joining: by keypad, its name, the last player who missed
 * and the total of misses, up to the cap, dropping the oldest keypad (answers 666, 903 and 911).
 */
class KeypadReportsTest {
  private static final String DOOR_UUID = "7a8b9c0d-1e2f-4a3b-8c4d-5e6f7a8b9c0d";
  private static final String VAULT_UUID = "8b9c0d1e-2f3a-4b4c-9d5e-6f7a8b9c0d1e";
  private static final String THIEF_UUID = "9c0d1e2f-3a4b-4c5d-8e6f-7a8b9c0d1e2f";
  private static final String GUEST_UUID = "0d1e2f3a-4b5c-4d6e-9f7a-8b9c0d1e2f3a";
  private static final ModuleId DOOR = new ModuleId(UUID.fromString(DOOR_UUID));
  private static final ModuleId VAULT = new ModuleId(UUID.fromString(VAULT_UUID));
  private static final PlayerId THIEF = new PlayerId(UUID.fromString(THIEF_UUID));
  private static final PlayerId GUEST = new PlayerId(UUID.fromString(GUEST_UUID));
  private static final String DOOR_NAME = "Front door";
  private static final String VAULT_NAME = "Vault";
  private static final int CAP = 16;
  private static final int TWO = 2;

  @Test
  void eachMissAddsToTheTotalOfItsKeypadWithTheLastPlayer() {
    KeypadReports reports =
        KeypadReports.NONE.missed(DOOR, DOOR_NAME, THIEF, CAP).missed(DOOR, DOOR_NAME, GUEST, CAP);

    assertEquals(List.of(new KeypadReport(DOOR, DOOR_NAME, GUEST, TWO)), reports.reports());
  }

  @Test
  void pastTheCapTheOldestKeypadLeaves() {
    KeypadReports reports =
        KeypadReports.NONE.missed(DOOR, DOOR_NAME, THIEF, 1).missed(VAULT, VAULT_NAME, THIEF, 1);

    assertEquals(List.of(new KeypadReport(VAULT, VAULT_NAME, THIEF, 1)), reports.reports());
  }

  @Test
  void aKeypadMissedAgainBecomesTheNewest() {
    KeypadReports reports =
        KeypadReports.NONE
            .missed(DOOR, DOOR_NAME, THIEF, TWO)
            .missed(VAULT, VAULT_NAME, THIEF, TWO)
            .missed(DOOR, DOOR_NAME, THIEF, TWO);

    assertEquals(VAULT, reports.reports().getFirst().keypad());
    assertEquals(DOOR, reports.reports().getLast().keypad());
  }

  @Test
  void anInvalidReportIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new KeypadReport(DOOR, DOOR_NAME, THIEF, 0));
    assertThrows(NullPointerException.class, () -> new KeypadReport(null, DOOR_NAME, THIEF, 1));
  }
}
