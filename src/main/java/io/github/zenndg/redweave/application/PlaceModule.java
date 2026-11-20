package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;

/**
 * A player places a Signal Module. It belongs to whoever placed it, with a new ID and no network,
 * until its owner picks one on its screen (answers 31, 378 and 416).
 */
public final class PlaceModule {
  private final IdGenerator ids;

  public PlaceModule(IdGenerator ids) {
    this.ids = ids;
  }

  public SignalModule place(PlayerId owner) {
    return SignalModule.placed(new ModuleId(ids.newId()), owner);
  }
}
