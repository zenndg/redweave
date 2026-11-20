package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

/**
 * What the Configurator keeps when it copies a Signal Module: the ID of its item, so it only pastes
 * on one of the same type, the network it was in, to know whether its channels still make sense,
 * and its settings, never a secret nor what it is doing now (answers 725, 726 and 729).
 */
public record ModuleCopy(String type, Optional<NetworkId> network, CompoundTag settings) {
  private static final String MISSING = "A module copy needs its type, network and settings";

  public ModuleCopy {
    Objects.requireNonNull(type, MISSING);
    Objects.requireNonNull(network, MISSING);
    Objects.requireNonNull(settings, MISSING);
    settings = settings.copy();
  }

  @Override
  public CompoundTag settings() {
    return settings.copy();
  }
}
