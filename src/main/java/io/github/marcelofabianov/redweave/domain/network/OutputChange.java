package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/** A face whose signal changed. The game applies it and only then notifies the neighbors. */
public record OutputChange(OutputFace face, Signal signal) {
  private static final String MISSING = "An output change needs a face and a signal";

  public OutputChange {
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(signal, MISSING);
  }
}
