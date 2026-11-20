package io.github.zenndg.redweave.domain.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The Signal Cards an item accepts, in the order they were linked, up to the limit of the config;
 * the owner revokes one without the card (answers 669 and 904).
 */
public record KeypadCards(List<CardId> ids) {
  /** An item with no card linked. */
  public static final KeypadCards NONE = new KeypadCards(List.of());

  public KeypadCards {
    ids = List.copyOf(Objects.requireNonNull(ids));
  }

  public boolean accepts(CardId card) {
    return ids.contains(card);
  }

  /** The cards with this one too, kept once, or none past the limit. */
  public Optional<KeypadCards> link(CardId card, CardLimits limits) {
    if (accepts(card)) {
      return Optional.of(this);
    }
    if (ids.size() >= limits.maxLinksPerItem()) {
      return Optional.empty();
    }
    List<CardId> next = new ArrayList<>(ids);
    next.add(card);
    return Optional.of(new KeypadCards(next));
  }

  public KeypadCards revoke(CardId card) {
    List<CardId> next = new ArrayList<>(ids);
    next.remove(card);
    return new KeypadCards(next);
  }
}
