package io.github.zenndg.redweave.menu;

import java.util.Optional;

/**
 * The buttons of the screen of the link as numbers for `clickMenuButton`: back, the two modes, and
 * then one number per line of the step, so the client points to a position, never to an ID.
 */
public final class RemoteLinkCodes {
  public static final int BACK = 0;
  public static final int LEVER = 1;
  public static final int BUTTON = 2;
  private static final int FIRST_LINE = 3;

  private RemoteLinkCodes() {}

  public static int line(int index) {
    return FIRST_LINE + index;
  }

  /** The line of a number, or none for back and the modes. */
  public static Optional<Integer> lineOf(int code) {
    return code >= FIRST_LINE ? Optional.of(code - FIRST_LINE) : Optional.empty();
  }
}
