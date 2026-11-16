package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.ItemSensor;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Item Sensor changes its item, its count and its strength from its screen.
 * Anyone else changes nothing (answers 378, 689 and 696). The sensor lives in its part, so the
 * caller keeps what comes back.
 */
public final class ChangeItemSensor {
  public Optional<ItemSensor> item(
      PlayerId requester, SignalModule module, ItemSensor sensor, Optional<String> item) {
    return owned(requester, module).map(owner -> sensor.withItem(item));
  }

  /** A count kept from 1 to `most`, the most the config allows (answer 693). */
  public Optional<ItemSensor> count(
      PlayerId requester, SignalModule module, ItemSensor sensor, int count, int most) {
    return owned(requester, module).map(owner -> sensor.withCount(count, most));
  }

  public Optional<ItemSensor> strength(
      PlayerId requester, SignalModule module, ItemSensor sensor, int strength) {
    return owned(requester, module).map(owner -> sensor.withStrength(strength));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
