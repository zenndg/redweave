package io.github.marcelofabianov.redweave.registry;

import com.mojang.serialization.Codec;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
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
  private static final String NETWORK_NAME_ID = "network_name";
  private static final String NODE_SLOTS_ID = "node_slots";

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

  /** The name of the stored network, only for the tooltip of the Configurator (answer 112). */
  public static final Supplier<DataComponentType<String>> NETWORK_NAME =
      COMPONENTS.registerComponentType(
          NETWORK_NAME_ID,
          builder ->
              builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

  /** The slots copied by the Configurator, to paste on other nodes (answer 105). */
  public static final Supplier<DataComponentType<NodeCopy>> NODE_SLOTS =
      COMPONENTS.registerComponentType(
          NODE_SLOTS_ID,
          builder ->
              builder
                  .persistent(NodeSlotsCodec.CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(NodeSlotsCodec.CODEC)));

  private RedweaveDataComponents() {}

  public static void register(IEventBus modBus) {
    COMPONENTS.register(modBus);
  }
}
