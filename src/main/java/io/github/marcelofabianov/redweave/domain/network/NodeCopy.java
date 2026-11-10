package io.github.marcelofabianov.redweave.domain.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The slots of a node, copied by the Configurator to paste on others, and the face the node stuck
 * to. The readings stay behind, because they belong to the slot as it was (answer 105).
 */
public record NodeCopy(Face attached, List<CopiedSlot> slots) {
  private static final String MISSING = "A copy needs the face the node stuck to";
  private static final String REPEATED = "A copy has each slot once";

  public NodeCopy {
    Objects.requireNonNull(attached, MISSING);
    slots = List.copyOf(slots);
    Set<Integer> seen = new HashSet<>();
    if (!slots.stream().allMatch(slot -> seen.add(slot.index()))) {
      throw new IllegalArgumentException(REPEATED);
    }
  }

  public static NodeCopy of(Node node) {
    List<CopiedSlot> used = new ArrayList<>();
    for (int index = 0; index < node.slots().size(); index++) {
      int at = index;
      node.slots().get(index).ifPresent(slot -> used.add(new CopiedSlot(at, slot)));
    }
    return new NodeCopy(node.position().face(), used);
  }

  /**
   * Every slot of the target after the paste. A slot on the face the source stuck to goes to the
   * face the target sticks to, so it still reads its own block (answer 71), and the slots past the
   * end of the target are left out.
   */
  public Pasted onto(Node target) {
    int size = target.slots().size();
    List<Optional<ChannelSlot>> pasted =
        new ArrayList<>(Collections.nCopies(size, Optional.empty()));
    int leftOut = 0;
    for (CopiedSlot copied : slots) {
      if (copied.index() >= size) {
        leftOut++;
        continue;
      }
      pasted.set(copied.index(), Optional.of(facing(copied.slot(), target.position().face())));
    }
    return new Pasted(pasted, leftOut);
  }

  private ChannelSlot facing(ChannelSlot slot, Face targetAttached) {
    if (slot.face() != attached) {
      return slot;
    }
    return new ChannelSlot(slot.direction(), targetAttached, slot.channel());
  }
}
