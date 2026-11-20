package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.KeypadAttempt;
import io.github.zenndg.redweave.domain.module.KeypadAttempts;
import io.github.zenndg.redweave.domain.module.KeypadCards;
import io.github.zenndg.redweave.domain.module.KeypadSecret;
import io.github.zenndg.redweave.domain.module.SignalKeypad;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Keypad besides its module, part of the save (answer 910, rule 15). Public:
 * `strength`, `open_ticks`, `remaining`, `input` and `output`. Secret, which never reach a client:
 * `code_hash` and `code_salt`, `attempts` and `cards`. Anything invalid is dropped with a warning
 * (rule 14): a damaged code leaves the keypad with none, closed to everyone (answer 660).
 */
final class KeypadSave {
  static final String INPUT = "input";
  static final String OUTPUT = "output";
  static final String CODE_HASH = "code_hash";
  static final String CODE_SALT = "code_salt";
  static final String ATTEMPTS = "attempts";
  static final String CARDS = "cards";

  /** The keys that never reach a client (answers 896 and 910). */
  static final List<String> SECRET = List.of(CODE_HASH, CODE_SALT, ATTEMPTS, CARDS);

  private static final String OPEN_TICKS = "open_ticks";
  private static final String REMAINING = "remaining";
  private static final String PLAYER = "player";
  private static final String COUNT = "count";
  private static final String LAST_TICK = "last_tick";
  private static final String MODULE = "Signal Keypad";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Keypad";

  private KeypadSave() {}

  static void write(CompoundTag tag, SignalKeypad keypad) {
    StrengthSave.write(tag, keypad.strength());
    tag.putInt(OPEN_TICKS, keypad.openTicks());
    tag.putInt(REMAINING, keypad.remaining());
    keypad
        .secret()
        .ifPresent(secret -> {
          tag.putString(CODE_HASH, secret.hash());
          tag.putString(CODE_SALT, secret.salt());
        });
    tag.put(ATTEMPTS, attempts(keypad.attempts()));
    ListTag cards = new ListTag();
    keypad.cards().ids().forEach(card -> cards.add(NbtUtils.createUUID(card.value())));
    tag.put(CARDS, cards);
  }

  static SignalKeypad read(CompoundTag tag) {
    int open = tag.getInt(OPEN_TICKS);
    int remaining = tag.getInt(REMAINING);
    if (open < 1 || remaining < 0) {
      LOGGER.warn(DROPPED, OPEN_TICKS);
      open = SignalKeypad.PLACED_OPEN_TICKS;
      remaining = 0;
    }
    return new SignalKeypad(
        StrengthSave.read(tag, MODULE), open, remaining, secret(tag), attempts(tag), cards(tag));
  }

  private static Optional<KeypadSecret> secret(CompoundTag tag) {
    if (!tag.contains(CODE_HASH) && !tag.contains(CODE_SALT)) {
      return Optional.empty();
    }
    try {
      return Optional.of(new KeypadSecret(tag.getString(CODE_HASH), tag.getString(CODE_SALT)));
    } catch (IllegalArgumentException damaged) {
      LOGGER.warn(DROPPED, CODE_HASH);
      return Optional.empty();
    }
  }

  private static ListTag attempts(KeypadAttempts attempts) {
    ListTag list = new ListTag();
    attempts
        .byPlayer()
        .forEach((player, attempt) -> {
          CompoundTag entry = new CompoundTag();
          entry.putUUID(PLAYER, player.value());
          entry.putInt(COUNT, attempt.count());
          entry.putLong(LAST_TICK, attempt.lastTick());
          list.add(entry);
        });
    return list;
  }

  private static KeypadAttempts attempts(CompoundTag tag) {
    Map<PlayerId, KeypadAttempt> byPlayer = new HashMap<>();
    for (Tag saved : tag.getList(ATTEMPTS, Tag.TAG_COMPOUND)) {
      CompoundTag entry = (CompoundTag) saved;
      if (!entry.hasUUID(PLAYER) || entry.getInt(COUNT) < 1 || entry.getLong(LAST_TICK) < 0) {
        LOGGER.warn(DROPPED, ATTEMPTS);
        continue;
      }
      byPlayer.put(
          new PlayerId(entry.getUUID(PLAYER)),
          new KeypadAttempt(entry.getInt(COUNT), entry.getLong(LAST_TICK)));
    }
    return new KeypadAttempts(byPlayer);
  }

  private static KeypadCards cards(CompoundTag tag) {
    List<CardId> ids = new ArrayList<>();
    for (Tag saved : tag.getList(CARDS, Tag.TAG_INT_ARRAY)) {
      try {
        CardId card = new CardId(NbtUtils.loadUUID(saved));
        if (!ids.contains(card)) {
          ids.add(card);
        }
      } catch (IllegalArgumentException damaged) {
        LOGGER.warn(DROPPED, CARDS);
      }
    }
    return new KeypadCards(ids);
  }
}
