package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.ApplyModules;
import io.github.marcelofabianov.redweave.application.ChangeAlarm;
import io.github.marcelofabianov.redweave.application.ChangeButton;
import io.github.marcelofabianov.redweave.application.ChangeItemSensor;
import io.github.marcelofabianov.redweave.application.ChangeLamp;
import io.github.marcelofabianov.redweave.application.ChangeLever;
import io.github.marcelofabianov.redweave.application.ChangeLock;
import io.github.marcelofabianov.redweave.application.ChangePressurePlate;
import io.github.marcelofabianov.redweave.application.ChangeRandomizer;
import io.github.marcelofabianov.redweave.application.ChangeTimer;
import io.github.marcelofabianov.redweave.application.ChangeWeatherDetector;
import io.github.marcelofabianov.redweave.application.ChooseModuleNetwork;
import io.github.marcelofabianov.redweave.application.IdGenerator;
import io.github.marcelofabianov.redweave.application.PlaceModule;
import io.github.marcelofabianov.redweave.application.ReadModuleChannel;
import io.github.marcelofabianov.redweave.application.ServerClock;
import io.github.marcelofabianov.redweave.application.ShowModule;
import io.github.marcelofabianov.redweave.application.SignalLogs;
import io.github.marcelofabianov.redweave.application.SyncModule;

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
  private final ChangeTimer changeTimer = new ChangeTimer();
  private final ChangeWeatherDetector changeDetector = new ChangeWeatherDetector();
  private final ChangeRandomizer changeRandomizer = new ChangeRandomizer();
  private final ChangeLever changeLever = new ChangeLever();
  private final ChangeButton changeButton = new ChangeButton();
  private final ChangePressurePlate changePressurePlate = new ChangePressurePlate();
  private final ChangeItemSensor changeItemSensor = new ChangeItemSensor();
  private final ChangeLock changeLock = new ChangeLock();
  private final ChangeLamp changeLamp = new ChangeLamp();
  private final ChangeAlarm changeAlarm = new ChangeAlarm();
  private final ChooseModuleNetwork chooseNetwork;
  private final SyncModule syncModule;

  ModuleServices(IdGenerator ids, SavedDataNetworks networks, SignalLogs logs, ServerClock clock) {
    placeModule = new PlaceModule(ids);
    applyModules = new ApplyModules(networks, logs, clock);
    showModule = new ShowModule(networks);
    readChannel = new ReadModuleChannel(networks);
    chooseNetwork = new ChooseModuleNetwork(networks);
    syncModule = new SyncModule(networks, networks);
  }

  /** The register of the modules in their networks (answers 803 and 811). */
  public SyncModule sync() {
    return syncModule;
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

  public ChangeTimer changeTimer() {
    return changeTimer;
  }

  public ChangeWeatherDetector changeDetector() {
    return changeDetector;
  }

  public ChangeRandomizer changeRandomizer() {
    return changeRandomizer;
  }

  public ChangeLever changeLever() {
    return changeLever;
  }

  public ChangeButton changeButton() {
    return changeButton;
  }

  public ChangePressurePlate changePressurePlate() {
    return changePressurePlate;
  }

  public ChangeItemSensor changeItemSensor() {
    return changeItemSensor;
  }

  public ChangeLamp changeLamp() {
    return changeLamp;
  }

  public ChangeLock changeLock() {
    return changeLock;
  }

  public ChangeAlarm changeAlarm() {
    return changeAlarm;
  }

  public ChooseModuleNetwork chooseNetwork() {
    return chooseNetwork;
  }

  public ModuleSightings sightings() {
    return sightings;
  }
}
