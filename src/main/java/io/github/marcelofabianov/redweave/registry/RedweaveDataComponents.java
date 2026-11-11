package io.github.marcelofabianov.redweave.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.item.NodeSlotsCodec;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The data components of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveDataComponents {
  private static final String NETWORK_ID = "network";
  private static final String NODE_SLOTS_ID = "node_slots";
  private static final String CONFIGURATOR_MODE_ID = "configurator_mode";
  private static final String UNKNOWN_MODE = "Unknown Configurator mode: ";

  /** A saved mode that is not known is refused, and the item falls back to the first mode. */
  private static final Codec<ConfiguratorMode> MODE_CODEC =
      Codec.STRING.comapFlatMap(
          name ->
              ConfiguratorMode.byName(name)
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> UNKNOWN_MODE + name)),
          ConfiguratorMode::serializedName);

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

  /** The slots copied by the Configurator, to paste on other nodes (answer 105). */
  public static final Supplier<DataComponentType<NodeCopy>> NODE_SLOTS =
      COMPONENTS.registerComponentType(
          NODE_SLOTS_ID,
          builder ->
              builder
                  .persistent(NodeSlotsCodec.CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(NodeSlotsCodec.CODEC)));

  /** The mode of the Configurator, switched with Shift + scroll (answers 143 and 173). */
  public static final Supplier<DataComponentType<ConfiguratorMode>> CONFIGURATOR_MODE =
      COMPONENTS.registerComponentType(
          CONFIGURATOR_MODE_ID,
          builder ->
              builder
                  .persistent(MODE_CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(MODE_CODEC)));

  private RedweaveDataComponents() {}

  public static void register(IEventBus modBus) {
    COMPONENTS.register(modBus);
  }
}
