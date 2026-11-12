package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * The number a button of the Network Terminal sends: the row of the list and the action. The client
 * names a network by its row in the list the menu keeps, never by its ID (rule 17).
 */
public final class TerminalScreenCodes {
  private static final TerminalAction[] ACTIONS = TerminalAction.values();

  private TerminalScreenCodes() {}

  /** A click on a row of the list. */
  public record Click(int row, TerminalAction action) {}

  public static int button(Click click) {
    return click.row() * ACTIONS.length + click.action().ordinal();
  }

  /** The click of a number, or none for a negative one. */
  public static Optional<Click> clickOf(int code) {
    if (code < 0) {
      return Optional.empty();
    }
    return Optional.of(new Click(code / ACTIONS.length, ACTIONS[code % ACTIONS.length]));
  }
}
