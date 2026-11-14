package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.IdGenerator;
import io.github.marcelofabianov.redweave.application.PlaceModule;

/**
 * The use cases of the Signal Modules, wired to the server (modulos-de-sinal.md). Kept apart from
 * RedweaveServer, so each wiring stays small.
 */
public final class ModuleServices {
  private final PlaceModule placeModule;

  ModuleServices(IdGenerator ids) {
    placeModule = new PlaceModule(ids);
  }

  public PlaceModule place() {
    return placeModule;
  }
}
