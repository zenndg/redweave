package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The shared base of every Signal Module: whoever placed it owns it, only the owner renames it,
 * chooses its network and puts a connection on a channel, which needs a network (answers 375, 378,
 * 389, 391, 397 and 427).
 */
class SignalModuleTest {
  private static final String MODULE_UUID = "5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c";
  private static final String OWNER_UUID = "0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d";
  private static final String STRANGER_UUID = "9f8e7d6c-5b4a-4c3d-9e2f-1a0b9c8d7e6f";
  private static final String NETWORK_UUID = "6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b";
  private static final ModuleId ID = new ModuleId(UUID.fromString(MODULE_UUID));
  private static final PlayerId OWNER = new PlayerId(UUID.fromString(OWNER_UUID));
  private static final PlayerId STRANGER = new PlayerId(UUID.fromString(STRANGER_UUID));
  private static final NetworkId NETWORK = new NetworkId(UUID.fromString(NETWORK_UUID));
  private static final String DEFAULT_NAME = "5f6a7b8c";
  private static final String DOOR_CLOCK = "Door clock";
  private static final String PADDED_DOOR_CLOCK = "  Door clock ";
  private static final String BLANK = "   ";
  private static final String TOO_LONG = "A name far too long for it";
  private static final int NAME_LENGTH = 16;
  private static final ModuleLimits LIMITS = () -> NAME_LENGTH;
  private static final Connection RED = Connection.on(ChannelColor.RED);

  @Test
  void aPlacedModuleBelongsToWhoeverPlacedItWithNoNameAndNoNetwork() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(ID, module.id());
    assertTrue(module.isOwner(OWNER));
    assertFalse(module.isOwner(STRANGER));
    assertEquals(Optional.empty(), module.name());
    assertEquals(Optional.empty(), module.network());
  }

  @Test
  void withNoNameItShowsTheFirstEightCharactersOfItsId() {
    assertEquals(DEFAULT_NAME, SignalModule.placed(ID, OWNER).displayName());
  }

  @Test
  void theOwnerRenamesItAndTheNameIsTrimmed() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.ACCEPTED, module.rename(OWNER, PADDED_DOOR_CLOCK, LIMITS));
    assertEquals(DOOR_CLOCK, module.displayName());
  }

  @Test
  void aBlankNameGoesBackToTheDefault() {
    SignalModule module = SignalModule.placed(ID, OWNER);
    module.rename(OWNER, DOOR_CLOCK, LIMITS);

    assertEquals(Outcome.ACCEPTED, module.rename(OWNER, BLANK, LIMITS));
    assertEquals(DEFAULT_NAME, module.displayName());
  }

  @Test
  void aNameLongerThanTheLimitIsRefused() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.INVALID_NAME, module.rename(OWNER, TOO_LONG, LIMITS));
    assertEquals(Optional.empty(), module.name());
  }

  @Test
  void anotherPlayerChangesNothing() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.NOT_OWNER, module.rename(STRANGER, DOOR_CLOCK, LIMITS));
    assertEquals(Outcome.NOT_OWNER, module.choose(STRANGER, NETWORK));
    assertEquals(Outcome.NOT_OWNER, module.connects(STRANGER, Connection.EDGE));
    assertEquals(Optional.empty(), module.name());
    assertEquals(Optional.empty(), module.network());
  }

  @Test
  void theOwnerChoosesTheNetwork() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.ACCEPTED, module.choose(OWNER, NETWORK));
    assertEquals(Optional.of(NETWORK), module.network());
  }

  @Test
  void aChannelNeedsANetworkAndAnEdgeDoesNot() {
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.ACCEPTED, module.connects(OWNER, Connection.EDGE));
    assertEquals(Outcome.UNKNOWN_NETWORK, module.connects(OWNER, RED));

    module.choose(OWNER, NETWORK);
    assertEquals(Outcome.ACCEPTED, module.connects(OWNER, RED));
  }

  @Test
  void aModuleWhoseNetworkIsGoneHasNoNetwork() {
    SignalModule module = SignalModule.placed(ID, OWNER);
    module.choose(OWNER, NETWORK);

    module.loseNetwork();

    assertEquals(Optional.empty(), module.network());
  }

  @Test
  void aSavedModuleComesBackAsItWas() {
    SignalModule restored =
        SignalModule.restore(ID, OWNER, Optional.of(DOOR_CLOCK), Optional.of(NETWORK));

    assertEquals(DOOR_CLOCK, restored.displayName());
    assertEquals(Optional.of(NETWORK), restored.network());
    assertEquals(OWNER, restored.owner());
  }

  @Test
  void restoringRejectsAMissingPart() {
    assertThrows(
        NullPointerException.class,
        () -> SignalModule.restore(null, OWNER, Optional.empty(), Optional.empty()));
  }
}
