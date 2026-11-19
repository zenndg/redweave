package io.github.zenndg.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.ModuleChannel;
import io.github.zenndg.redweave.domain.network.ModuleEntry;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.NodePosition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

/**
 * A registered Signal Module as it is in the saves of its network: its ID, its kind, its own name,
 * its block and face, and the channels it reads or writes (answers 803 and 810). A blank kind or a
 * damaged channel is dropped with a warning (answer 92 and rule 14).
 */
final class SavedModule {
  private static final String ID = "id";
  private static final String KIND = "kind";
  private static final String BLOCK = "block";
  private static final String FACE = "face";
  private static final String CHANNELS = "channels";
  private static final String DIRECTION = "direction";
  private static final String NAME_PART = "module name";
  private static final String CHANNEL_PART = "module channel";
  private static final String BLANK_KIND = "Blank kind of a module";

  private static final Codec<ModuleChannel> CHANNEL =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.CHANNEL_COLOR
                      .fieldOf(SaveCodecs.COLOR)
                      .forGetter(ModuleChannel::channel),
                  SaveCodecs.DIRECTION.fieldOf(DIRECTION).forGetter(ModuleChannel::direction))
              .apply(instance, ModuleChannel::new));

  private static final Codec<String> KIND_CODEC =
      Codec.STRING.validate(
          kind -> kind.isBlank() ? DataResult.error(() -> BLANK_KIND) : DataResult.success(kind));

  static final Codec<ModuleEntry> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.fieldOf(ID).forGetter(entry -> entry.id().value()),
                  KIND_CODEC.fieldOf(KIND).forGetter(ModuleEntry::kind),
                  Lenient.optionalField(SaveCodecs.NAME, SaveCodecs.SAVED_NAME, NAME_PART)
                      .forGetter(ModuleEntry::name),
                  SaveCodecs.BLOCK.fieldOf(BLOCK).forGetter(entry -> entry.position().block()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(entry -> entry.position().face()),
                  Lenient.list(CHANNEL, CHANNEL_PART)
                      .fieldOf(CHANNELS)
                      .forGetter(ModuleEntry::channels))
              .apply(instance, SavedModule::entry));

  private SavedModule() {}

  private static ModuleEntry entry(
      UUID id,
      String kind,
      Optional<String> name,
      BlockLocation block,
      Face face,
      List<ModuleChannel> channels) {
    return new ModuleEntry(new ModuleId(id), kind, name, new NodePosition(block, face), channels);
  }
}
