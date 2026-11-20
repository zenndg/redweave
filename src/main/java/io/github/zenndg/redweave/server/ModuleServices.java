package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ApplyModules;
import io.github.zenndg.redweave.application.ChangeAlarm;
import io.github.zenndg.redweave.application.ChangeButton;
import io.github.zenndg.redweave.application.ChangeFlipFlop;
import io.github.zenndg.redweave.application.ChangeInverter;
import io.github.zenndg.redweave.application.ChangeItemSensor;
import io.github.zenndg.redweave.application.ChangeKeypad;
import io.github.zenndg.redweave.application.ChangeLamp;
import io.github.zenndg.redweave.application.ChangeLever;
import io.github.zenndg.redweave.application.ChangeLock;
import io.github.zenndg.redweave.application.ChangeOutputDelay;
import io.github.zenndg.redweave.application.ChangePressurePlate;
import io.github.zenndg.redweave.application.ChangeRandomizer;
import io.github.zenndg.redweave.application.ChangeTimer;
import io.github.zenndg.redweave.application.ChangeWeatherDetector;
import io.github.zenndg.redweave.application.ChooseModuleNetwork;
import io.github.zenndg.redweave.application.IdGenerator;
import io.github.zenndg.redweave.application.KeypadOwnerReports;
import io.github.zenndg.redweave.application.PlaceModule;
import io.github.zenndg.redweave.application.ReadModuleChannel;
import io.github.zenndg.redweave.application.ServerClock;
import io.github.zenndg.redweave.application.ShowModule;
import io.github.zenndg.redweave.application.SignalLogs;
import io.github.zenndg.redweave.application.SyncModule;

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
  private final ChangeInverter changeInverter = new ChangeInverter();
  private final ChangeOutputDelay changeOutputDelay = new ChangeOutputDelay();
  private final ChangeButton changeButton = new ChangeButton();
  private final ChangePressurePlate changePressurePlate = new ChangePressurePlate();
  private final ChangeItemSensor changeItemSensor = new ChangeItemSensor();
  private final ChangeLock changeLock = new ChangeLock();
  private final ChangeLamp changeLamp = new ChangeLamp();
  private final ChangeAlarm changeAlarm = new ChangeAlarm();
  private final ChangeFlipFlop changeFlipFlop = new ChangeFlipFlop();
  private final ChangeKeypad changeKeypad = new ChangeKeypad(new SecureKeypadSalts());
  private final KeypadOwnerReports keypadReports;
  private final ChooseModuleNetwork chooseNetwork;
  private final SyncModule syncModule;

  ModuleServices(IdGenerator ids, SavedDataNetworks networks, SignalLogs logs, ServerClock clock) {
    placeModule = new PlaceModule(ids);
    applyModules = new ApplyModules(networks, logs, clock);
    showModule = new ShowModule(networks);
    readChannel = new ReadModuleChannel(networks);
    chooseNetwork = new ChooseModuleNetwork(networks);
    syncModule = new SyncModule(networks, networks);
    keypadReports = new KeypadOwnerReports(networks);
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

  public ChangeInverter changeInverter() {
    return changeInverter;
  }

  public ChangeOutputDelay changeOutputDelay() {
    return changeOutputDelay;
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

  public ChangeFlipFlop changeFlipFlop() {
    return changeFlipFlop;
  }

  public ChangeKeypad changeKeypad() {
    return changeKeypad;
  }

  /** The summaries of the misses for the offline owners of keypads (answer 903). */
  public KeypadOwnerReports keypadReports() {
    return keypadReports;
  }

  public ChooseModuleNetwork chooseNetwork() {
    return chooseNetwork;
  }

  public ModuleSightings sightings() {
    return sightings;
  }
}
