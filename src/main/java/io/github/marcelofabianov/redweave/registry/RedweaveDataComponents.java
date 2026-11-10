package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The data components of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveDataComponents {
  private static final String NETWORK_ID = "network";

  private static final DeferredRegister.DataComponents COMPONENTS =
      DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RedweaveMod.MOD_ID);

  /** The network an item keeps, such as a broken controller (answers 24 and 65). */
  public static final Supplier<DataComponentType<NetworkId>> NETWORK =
      COMPONENTS.registerComponentType(
          NETWORK_ID,
          builder ->
              builder
                  .persistent(UUIDUtil.CODEC.xmap(NetworkId::new, NetworkId::value))
                  .networkSynchronized(
                      UUIDUtil.STREAM_CODEC.map(NetworkId::new, NetworkId::value)));

  private RedweaveDataComponents() {}

  public static void register(IEventBus modBus) {
    COMPONENTS.register(modBus);
  }
}
