package io.github.marcelofabianov.redweave.server;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;

/**
 * Codecs that read the saves part by part: an invalid part is dropped with a warning, and the rest
 * is kept (answer 92 and rule 14). Writing stays strict, because the domain only holds valid data.
 */
final class Lenient {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from the Redweave save: {}";

  private Lenient() {}

  /** A list whose invalid items are dropped one by one. */
  static <A> Codec<List<A>> list(Codec<A> item, String part) {
    Decoder<List<A>> decoder =
        Codec.PASSTHROUGH
            .listOf()
            .map(values -> values.stream().flatMap(value -> parse(item, value, part).stream()))
            .map(items -> items.toList());
    return Codec.of(item.listOf(), decoder);
  }

  /** An optional field that is dropped when invalid, instead of the part that holds it. */
  static <A> MapCodec<Optional<A>> optionalField(String name, Codec<A> codec, String part) {
    return Codec.PASSTHROUGH
        .optionalFieldOf(name)
        .xmap(
            found -> found.flatMap(value -> parse(codec, value, part)),
            value -> value.map(present -> encode(codec, present)));
  }

  /** Logs a part the domain refused after the codecs read it, as a repeated node. */
  static void dropped(String part, String reason) {
    LOGGER.warn(DROPPED, part, reason);
  }

  private static <A> Optional<A> parse(Codec<A> codec, Dynamic<?> value, String part) {
    DataResult<A> result = codec.parse(value);
    result.error().ifPresent(error -> dropped(part, error.message()));
    return result.result();
  }

  private static <A> Dynamic<?> encode(Codec<A> codec, A value) {
    return new Dynamic<>(NbtOps.INSTANCE, codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow());
  }
}
