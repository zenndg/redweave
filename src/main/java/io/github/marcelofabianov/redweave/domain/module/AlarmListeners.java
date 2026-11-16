package io.github.marcelofabianov.redweave.domain.module;

/**
 * Who hears a Signal Alarm and sees its text, chosen on its screen (answer 645): its owner, its
 * owner and the players who use its network, or everyone within its reach. A redstone signal does
 * not carry who caused it, so there is no "who triggered it".
 */
public enum AlarmListeners {
  OWNER,
  NETWORK,
  RANGE;

  /** The next one, as a click on the screen cycles them. */
  public AlarmListeners next() {
    AlarmListeners[] all = values();
    return all[(ordinal() + 1) % all.length];
  }
}
