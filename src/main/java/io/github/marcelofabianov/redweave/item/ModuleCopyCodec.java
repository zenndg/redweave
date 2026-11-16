package io.github.marcelofabianov.redweave.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

/**
 * The module copy as it is in the item: `type`, the ID of the item copied; `network`, absent when
 * the module had none; and `settings`, the keys of its save that are its settings (answer 729).
 */
public final class ModuleCopyCodec {
  private static final String TYPE = "type";
  private static final String NETWORK = "network";
  private static final String SETTINGS = "settings";

  public static final Codec<ModuleCopy> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.STRING.fieldOf(TYPE).forGetter(ModuleCopy::type),
                  UUIDUtil.CODEC
                      .xmap(NetworkId::new, NetworkId::value)
                      .optionalFieldOf(NETWORK)
                      .forGetter(ModuleCopy::network),
                  CompoundTag.CODEC.fieldOf(SETTINGS).forGetter(ModuleCopy::settings))
              .apply(instance, ModuleCopy::new));

  private ModuleCopyCodec() {}
}
