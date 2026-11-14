package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.ApplyModules;
import io.github.marcelofabianov.redweave.application.IdGenerator;
import io.github.marcelofabianov.redweave.application.PlaceModule;
import io.github.marcelofabianov.redweave.application.ReadModuleChannel;
import io.github.marcelofabianov.redweave.application.ServerClock;
import io.github.marcelofabianov.redweave.application.ShowModule;
import io.github.marcelofabianov.redweave.application.SignalLogs;

/**
 * The use cases of the Signal Modules, wired to the saves of the server (modulos-de-sinal.md). Kept
 * apart from RedweaveServer, so each wiring stays small.
 */
public final class ModuleServices {
  private final PlaceModule placeModule;
  private final ApplyModules applyModules;
  private final ShowModule showModule;
  private final ReadModuleChannel readChannel;
  private final ModuleSightings sightings = new ModuleSightings();

  ModuleServices(IdGenerator ids, SavedDataNetworks networks, SignalLogs logs, ServerClock clock) {
    placeModule = new PlaceModule(ids);
    applyModules = new ApplyModules(networks, logs, clock);
    showModule = new ShowModule(networks);
    readChannel = new ReadModuleChannel(networks);
  }

  public PlaceModule place() {
    return placeModule;
  }

  public ApplyModules apply() {
    return applyModules;
  }

  public ShowModule show() {
    return showModule;
  }

  public ReadModuleChannel read() {
    return readChannel;
  }

  public ModuleSightings sightings() {
    return sightings;
  }
}
