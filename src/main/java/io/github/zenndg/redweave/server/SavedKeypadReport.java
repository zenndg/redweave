package io.github.zenndg.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.redweave.domain.module.KeypadReport;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;

/**
 * One line of the summary an offline owner gets when joining, in the saves: the owner, the keypad,
 * its name, the last player who missed and the total of misses (answers 903 and 911). The keys are
 * fixed in issue #26.
 */
record SavedKeypadReport(PlayerId owner, KeypadReport report) {
  private static final String OWNER = "owner";
  private static final String KEYPAD = "keypad";
  private static final String NAME = "name";
  private static final String LAST_PLAYER = "last_player";
  private static final String MISSES = "misses";

  private static final Codec<ModuleId> MODULE_ID =
      UUIDUtil.CODEC.xmap(ModuleId::new, ModuleId::value);

  private static final MapCodec<KeypadReport> REPORT =
      RecordCodecBuilder.mapCodec(instance ->
          instance
              .group(
                  MODULE_ID.fieldOf(KEYPAD).forGetter(KeypadReport::keypad),
                  Codec.STRING.fieldOf(NAME).forGetter(KeypadReport::name),
                  SaveCodecs.PLAYER_ID.fieldOf(LAST_PLAYER).forGetter(KeypadReport::lastPlayer),
                  ExtraCodecs.POSITIVE_INT.fieldOf(MISSES).forGetter(KeypadReport::misses))
              .apply(instance, KeypadReport::new));

  static final Codec<SavedKeypadReport> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.PLAYER_ID.fieldOf(OWNER).forGetter(SavedKeypadReport::owner),
                  REPORT.forGetter(SavedKeypadReport::report))
              .apply(instance, SavedKeypadReport::new));
}
