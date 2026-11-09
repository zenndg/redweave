package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What the network last emitted and whether it waits for an evaluation (answer 38). It is not
 * saved: a loaded network starts pending, so the first tick applies every face again.
 */
final class Emissions {
  private Map<OutputFace, Signal> emitted = new LinkedHashMap<>();
  private boolean pending = true;

  boolean isPending() {
    return pending;
  }

  void markPending() {
    pending = true;
  }

  /**
   * Keeps what should be emitted now and gives back only the faces that changed. A face never
   * emitted before always changes, even to off, so the first tick after loading applies it.
   */
  List<OutputChange> settle(Map<OutputFace, Signal> wanted) {
    List<OutputChange> changes = new ArrayList<>();
    wanted.forEach((face, signal) -> {
      if (!signal.equals(emitted.get(face))) {
        changes.add(new OutputChange(face, signal));
      }
    });
    emitted.forEach((face, signal) -> {
      if (!wanted.containsKey(face) && signal.isOn()) {
        changes.add(new OutputChange(face, Signal.OFF));
      }
    });
    emitted = new LinkedHashMap<>(wanted);
    pending = false;
    return changes;
  }
}
