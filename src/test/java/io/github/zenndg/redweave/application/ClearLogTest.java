package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NOW;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.SignalChange;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only the owner clears the log of a network, for everyone (answer 264). */
class ClearLogTest {
  private static final String NODE_NAME = "Lever";
  private static final long FROM_THE_START = 0;
  private static final int ONE_LINE = 1;
  private static final long NEVER = 0;
  private static final long ONCE = 1;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ClearLog clearLog = new ClearLog(fixture.networks, fixture.logs);

  private NetworkId networkWithALine() {
    NetworkId network = fixture.ownersNetwork();
    fixture.logs.record(
        network,
        new SignalChange(
            NOW, NODE_NAME, ChannelColor.RED, Optional.empty(), Direction.INPUT, Signal.MAX));
    return network;
  }

  @Test
  void theOwnerClearsTheLog() {
    NetworkId network = networkWithALine();

    assertEquals(Outcome.ACCEPTED, clearLog.clear(OWNER, network));

    assertEquals(List.of(), fixture.logs.after(network, FROM_THE_START));
  }

  @Test
  void anotherPlayerCannotClearTheLog() {
    NetworkId network = networkWithALine();

    assertEquals(Outcome.NOT_OWNER, clearLog.clear(STRANGER, network));

    assertEquals(ONE_LINE, fixture.logs.after(network, FROM_THE_START).size());
  }

  @Test
  void aMissingNetworkIsUnknown() {
    assertEquals(
        Outcome.UNKNOWN_NETWORK, clearLog.clear(OWNER, ApplicationFixture.MISSING_NETWORK));
  }

  /** A network that never had a log was never cleared. */
  @Test
  void aNetworkWithNoLogWasNeverCleared() {
    assertEquals(NEVER, fixture.logs.clearsOf(fixture.ownersNetwork()));
  }

  @Test
  void clearingCountsOnce() {
    NetworkId network = networkWithALine();

    clearLog.clear(OWNER, network);

    assertEquals(ONCE, fixture.logs.clearsOf(network));
  }
}
