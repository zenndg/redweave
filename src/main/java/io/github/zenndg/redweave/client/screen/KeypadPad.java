package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The keypad of 3 × 4 keys of both Signal Keypad Screens: the digits, `Clear` and `Enter`, with the
 * code typed so far shown as `•` (answers 662 and 905). The digits stay on the client until
 * `Enter`, which hands the code on.
 */
final class KeypadPad {
  static final int WIDTH = 106;
  static final int HEIGHT = 96;

  private static final int KEY = 34;
  private static final int KEY_HEIGHT = 18;
  private static final int GAP = 2;
  private static final int COLUMNS = 3;
  private static final int DISPLAY_HEIGHT = 14;
  private static final int KEYS_Y = DISPLAY_HEIGHT + GAP;
  private static final int TEXT_Y = 3;
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final String DOT = "•";
  private static final String DIGITS = "123456789";
  private static final String ZERO = "0";

  private final StringBuilder typed = new StringBuilder();

  void clear() {
    typed.setLength(0);
  }

  void draw(
      GuiGraphics graphics, Font font, int x, int y, Mouse mouse, Consumer<KeypadCode> enter) {
    graphics.fill(x, y, x + WIDTH, y + DISPLAY_HEIGHT, DISPLAY_FILL);
    Component shown = Component.literal(DOT.repeat(typed.length()));
    int textX = x + (WIDTH - font.width(shown)) / 2;
    graphics.drawString(font, shown, textX, y + TEXT_Y, ScreenColors.TEXT, false);
    keys(x, y, enter).forEach(key -> key.draw(graphics, font, mouse));
  }

  /** A click on a key, which types, clears or hands the code to `enter`; false off the keys. */
  boolean click(int x, int y, double mouseX, double mouseY, Consumer<KeypadCode> enter) {
    for (TimerButton key : keys(x, y, enter)) {
      if (key.box().contains(mouseX, mouseY)) {
        key.click().run();
        return true;
      }
    }
    return false;
  }

  private List<TimerButton> keys(int x, int y, Consumer<KeypadCode> enter) {
    List<TimerButton> keys = new ArrayList<>();
    for (int index = 0; index < DIGITS.length(); index++) {
      String digit = String.valueOf(DIGITS.charAt(index));
      keys.add(key(x, y, index, Component.literal(digit), () -> type(digit)));
    }
    int last = DIGITS.length();
    keys.add(key(x, y, last, Component.translatable(KeypadScreenTexts.CLEAR), this::clear));
    keys.add(key(x, y, last + 1, Component.literal(ZERO), () -> type(ZERO)));
    keys.add(
        key(x, y, last + 2, Component.translatable(KeypadScreenTexts.ENTER), () -> send(enter)));
    return keys;
  }

  private static TimerButton key(int x, int y, int index, Component label, Runnable click) {
    int column = index % COLUMNS;
    int row = index / COLUMNS;
    Box box =
        new Box(x + column * (KEY + GAP), y + KEYS_Y + row * (KEY_HEIGHT + GAP), KEY, KEY_HEIGHT);
    return new TimerButton(box, label, TimerButton.Look.BUTTON, click);
  }

  private void type(String digit) {
    if (typed.length() < KeypadCode.MAX_DIGITS) {
      typed.append(digit);
    }
  }

  private void send(Consumer<KeypadCode> enter) {
    Optional<KeypadCode> code =
        typed.isEmpty() ? Optional.empty() : Optional.of(new KeypadCode(typed.toString()));
    clear();
    code.ifPresent(enter);
  }
}
