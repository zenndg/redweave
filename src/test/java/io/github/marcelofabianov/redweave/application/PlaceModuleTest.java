package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A placed module belongs to whoever placed it, with a new ID and no network (answer 416). */
class PlaceModuleTest {
  @Test
  void aPlacedModuleGetsANewIdItsOwnerAndNoNetwork() {
    SignalModule placed = new PlaceModule(new FixedIds(List.of(NODE_ID))).place(OWNER);

    assertEquals(new ModuleId(NODE_ID), placed.id());
    assertTrue(placed.isOwner(OWNER));
    assertEquals(Optional.empty(), placed.network());
    assertEquals(Optional.empty(), placed.name());
  }
}
