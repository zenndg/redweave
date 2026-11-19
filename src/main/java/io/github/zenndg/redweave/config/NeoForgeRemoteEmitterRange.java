package io.github.zenndg.redweave.config;

import java.util.function.IntSupplier;

/** The `remote.emitter_range` of the server config, in blocks; 0 is unlimited (answer 784). */
public final class NeoForgeRemoteEmitterRange implements IntSupplier {
  @Override
  public int getAsInt() {
    return RedweaveConfig.EMITTER_RANGE.get();
  }
}
