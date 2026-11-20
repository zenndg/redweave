package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.KeypadReport;
import io.github.zenndg.redweave.domain.module.KeypadReports;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The summary of the misses an offline owner gets once, when joining (answers 666 and 903). */
class KeypadOwnerReportsTest {
  private static final String DOOR_UUID = "1e2f3a4b-5c6d-4e7f-8a9b-0c1d2e3f4a5b";
  private static final ModuleId DOOR = new ModuleId(UUID.fromString(DOOR_UUID));
  private static final String NAME = "Front door";
  private static final int CAP = 16;

  private final Map<PlayerId, KeypadReports> kept = new HashMap<>();
  private final KeypadOwnerReports reports = new KeypadOwnerReports(new Book(kept));

  @Test
  void aMissIsKeptForTheOwnerAndDeliveredOnce() {
    reports.missed(OWNER, DOOR, NAME, STRANGER, CAP);

    assertEquals(List.of(new KeypadReport(DOOR, NAME, STRANGER, 1)), reports.take(OWNER));
    assertTrue(reports.take(OWNER).isEmpty());
  }

  @Test
  void anOwnerWithNoMissGetsNothing() {
    assertTrue(reports.take(STRANGER).isEmpty());
  }

  /** The book in memory, as the save of the server keeps it. */
  private record Book(Map<PlayerId, KeypadReports> kept) implements KeypadReportBook {
    @Override
    public KeypadReports reportsOf(PlayerId owner) {
      return kept.getOrDefault(owner, KeypadReports.NONE);
    }

    @Override
    public void keep(PlayerId owner, KeypadReports reports) {
      kept.put(owner, reports);
    }
  }
}
