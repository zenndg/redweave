package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.RemoteMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Remote in English (signal-remote.md). */
final class RemoteEnglishTexts {
  private static final String NAME = "Signal Remote";
  private static final String ON = "%s: On";
  private static final String OFF = "%s: Off";
  private static final String PULSE = "%s: Pulse";
  private static final String HELD = "%s is held on by another source.";
  private static final String LINK_LOST = "Link lost";
  private static final String OFFLINE = "%s is offline.";
  private static final String LINKED = "Linked: %s · %s · %s";
  private static final String HOW_TO_LINK = "Shift + right-click to link.";
  private static final String LINK = "Link: %s · %s · %s";
  private static final String NO_LINK = "No link";
  private static final String MODE = "Mode: %s";
  private static final String LEVER = "Lever";
  private static final String BUTTON = "Button";
  private static final String STATE_ON = "On";
  private static final String STATE_OFF = "Off";
  private static final String RULE = "The strongest signal on a channel wins.";

  private RemoteEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_REMOTE.get().getDescriptionId(), NAME);
    add.accept(RemoteMessages.ON, ON);
    add.accept(RemoteMessages.OFF, OFF);
    add.accept(RemoteMessages.PULSE, PULSE);
    add.accept(RemoteMessages.HELD, HELD);
    add.accept(RemoteMessages.LINK_LOST, LINK_LOST);
    add.accept(RemoteMessages.OFFLINE, OFFLINE);
    add.accept(RemoteMessages.LINKED, LINKED);
    add.accept(RemoteMessages.HOW_TO_LINK, HOW_TO_LINK);
    add.accept(RemoteMessages.LINK, LINK);
    add.accept(RemoteMessages.NO_LINK, NO_LINK);
    add.accept(RemoteMessages.MODE, MODE);
    add.accept(RemoteMessages.LEVER, LEVER);
    add.accept(RemoteMessages.BUTTON, BUTTON);
    add.accept(RemoteMessages.STATE_ON, STATE_ON);
    add.accept(RemoteMessages.STATE_OFF, STATE_OFF);
    add.accept(RemoteMessages.RULE, RULE);
  }
}
