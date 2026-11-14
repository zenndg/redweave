package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.module.ModuleLimits;

/** The limits of every Signal Module, read from the server config every time (answer 391). */
public final class NeoForgeModuleLimits implements ModuleLimits {
  @Override
  public int moduleNameLength() {
    return RedweaveConfig.MODULE_NAME_LENGTH.get();
  }
}
