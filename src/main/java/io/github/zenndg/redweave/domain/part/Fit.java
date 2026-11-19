package io.github.zenndg.redweave.domain.part;

/**
 * Whether a part fits on a face of a block space. A refusal is an expected case, a click on a taken
 * face, so it is a result and not an exception, and the action bar shows it (answer 533).
 */
public enum Fit {
  FITS,
  /** The face already has a part, of any kind (answer 513). */
  FACE_TAKEN,
  /** The space already has its 2 cables (answer 498). */
  CABLES_FULL
}
