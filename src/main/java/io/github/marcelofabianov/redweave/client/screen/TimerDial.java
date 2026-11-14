package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.module.TimerState;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The clock of the Signal Timer Screen: the dial is a sprite, and the progress of the interval and
 * the arc of the pulse are painted on its track, pixel by pixel, from the top and clockwise, every
 * frame (answers 404 and 434). The middle shows the time to the next pulse, or `Paused`.
 */
final class TimerDial {
  static final int SIZE = 72;

  private static final String DIAL_PATH = "timer_screen/dial";
  private static final ResourceLocation DIAL =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, DIAL_PATH);
  private static final double CENTER = SIZE / 2.0;
  private static final int TRACK_INNER = 28;
  private static final int TRACK_OUTER = 33;
  private static final double HALF_PIXEL = 0.5;
  private static final double TURN = 2 * Math.PI;
  private static final int PULSE_MARK = 0xFF5A1E1B;
  private static final int NO_PAINT = 0;
  private static final double TICKS_PER_SECOND = 20.0;
  private static final String DECIMAL = "%.1f";
  private static final int SCALE = 2;
  private static final List<RingPixel> RING = ring();

  private TimerDial() {}

  /** A pixel of the track, with how far round the ring it is, from 0 at the top to 1. */
  private record RingPixel(int x, int y, double turn) {}

  static void draw(GuiGraphics graphics, Font font, int x, int y, TimerMenu menu) {
    graphics.blitSprite(DIAL, x, y, SIZE, SIZE);
    int interval = menu.intervalTicks();
    if (interval <= 0) {
      return;
    }
    Optional<TimerState> state = menu.state();
    double progress =
        state.equals(Optional.of(TimerState.RUNNING)) ? (double) menu.elapsedTicks() / interval : 0;
    double pulse = (double) menu.pulseTicks() / interval;
    for (RingPixel pixel : RING) {
      int color = colorOf(pixel.turn(), progress, pulse);
      if (color != NO_PAINT) {
        graphics.fill(x + pixel.x(), y + pixel.y(), x + pixel.x() + 1, y + pixel.y() + 1, color);
      }
    }
    drawMiddle(graphics, font, x, y, menu, state);
  }

  /** The pulse at the start of the cycle, lit once reached, and the copper progress after it. */
  private static int colorOf(double turn, double progress, double pulse) {
    boolean reached = turn < progress;
    if (turn < pulse) {
      return reached ? ScreenColors.REDSTONE : PULSE_MARK;
    }
    return reached ? ScreenColors.COPPER : NO_PAINT;
  }

  private static void drawMiddle(
      GuiGraphics graphics, Font font, int x, int y, TimerMenu menu, Optional<TimerState> state) {
    int middleX = x + SIZE / 2;
    int middleY = y + SIZE / 2;
    if (state.equals(Optional.of(TimerState.PAUSED))) {
      Component paused = Component.translatable(TimerScreenTexts.PAUSED);
      graphics.drawString(
          font,
          paused,
          middleX - font.width(paused) / 2,
          middleY - font.lineHeight / 2,
          ScreenColors.UNLOADED,
          false);
      return;
    }
    Component time = seconds(menu.intervalTicks() - menu.elapsedTicks());
    graphics.pose().pushPose();
    graphics.pose().translate(middleX, middleY, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(
        font, time, -font.width(time) / 2, -font.lineHeight / 2, ScreenColors.TEXT, false);
    graphics.pose().popPose();
  }

  /** Seconds with tenths, as `1.5 s`, the unit of the screen (answer 382). */
  static Component seconds(int ticks) {
    String value = String.format(Locale.ROOT, DECIMAL, ticks / TICKS_PER_SECOND);
    return Component.translatable(TimerScreenTexts.SECONDS, value);
  }

  /** The pixels of the track of the sprite, where the radius rounds to 28 up to 33 (434). */
  private static List<RingPixel> ring() {
    List<RingPixel> pixels = new ArrayList<>();
    for (int y = 0; y < SIZE; y++) {
      for (int x = 0; x < SIZE; x++) {
        double dx = x + HALF_PIXEL - CENTER;
        double dy = y + HALF_PIXEL - CENTER;
        long radius = Math.round(Math.hypot(dx, dy));
        if (radius >= TRACK_INNER && radius <= TRACK_OUTER) {
          double angle = Math.atan2(dx, -dy);
          pixels.add(new RingPixel(x, y, (angle < 0 ? angle + TURN : angle) / TURN));
        }
      }
    }
    return List.copyOf(pixels);
  }
}
