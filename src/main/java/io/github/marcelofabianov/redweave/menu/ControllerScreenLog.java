package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.SignalChange;
import io.github.marcelofabianov.redweave.domain.network.SignalLog;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Lines of the log of the Controller Screen, newest first: the whole log when the screen opens, and
 * then only the new lines, to the players with the screen open (answer 147 and rule 18). The size
 * of the log of the server goes along, so the screen keeps as many lines.
 */
public record ControllerScreenLog(int menu, int size, boolean reset, List<LogLine> lines)
    implements CustomPacketPayload {
  private static final String ID = "controller_screen_log";
  private static final String DEFAULT_NAME = "";

  public static final Type<ControllerScreenLog> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /**
   * A line: the date and time of the server as seconds of its local clock, the names of the moment
   * (an empty channel name shows the color), and the channel, direction and value as numbers.
   */
  public record LogLine(
      long time, String node, int channel, String channelName, int direction, int value) {
    static final StreamCodec<RegistryFriendlyByteBuf, LogLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            LogLine::time,
            ByteBufCodecs.STRING_UTF8,
            LogLine::node,
            ByteBufCodecs.VAR_INT,
            LogLine::channel,
            ByteBufCodecs.STRING_UTF8,
            LogLine::channelName,
            ByteBufCodecs.VAR_INT,
            LogLine::direction,
            ByteBufCodecs.VAR_INT,
            LogLine::value,
            LogLine::new);

    static LogLine of(SignalChange change) {
      return new LogLine(
          change.time().toEpochSecond(ZoneOffset.UTC),
          change.node(),
          change.channel().ordinal(),
          change.channelName().orElse(DEFAULT_NAME),
          change.direction().ordinal(),
          change.value().strength());
    }
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, ControllerScreenLog> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          ControllerScreenLog::menu,
          ByteBufCodecs.VAR_INT,
          ControllerScreenLog::size,
          ByteBufCodecs.BOOL,
          ControllerScreenLog::reset,
          LogLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
          ControllerScreenLog::lines,
          ControllerScreenLog::new);

  /** Before the log arrives, and for a network that is gone, the screen shows no line. */
  static final ControllerScreenLog NONE = new ControllerScreenLog(0, 0, false, List.of());

  public ControllerScreenLog {
    lines = List.copyOf(lines);
  }

  static ControllerScreenLog of(int menu, int size, List<SignalLog.Line> lines) {
    return new ControllerScreenLog(menu, size, false, linesOf(lines));
  }

  /** The whole log after the owner cleared it, which takes the place of what the screen had. */
  static ControllerScreenLog restart(int menu, int size, List<SignalLog.Line> lines) {
    return new ControllerScreenLog(menu, size, true, linesOf(lines));
  }

  private static List<LogLine> linesOf(List<SignalLog.Line> lines) {
    return lines.stream().map(line -> LogLine.of(line.change())).toList();
  }

  ControllerScreenLog forMenu(int other) {
    return new ControllerScreenLog(other, size, reset, lines);
  }

  /** The new lines on top of the ones the screen has, keeping only the size of the log. */
  ControllerScreenLog then(ControllerScreenLog newer) {
    if (newer.reset) {
      return new ControllerScreenLog(menu, newer.size, false, newer.lines);
    }
    List<LogLine> all =
        Stream.concat(newer.lines.stream(), lines.stream()).limit(newer.size).toList();
    return new ControllerScreenLog(menu, newer.size, false, all);
  }

  /** On the client: only the open Controller Screen of the same menu takes the lines. */
  public static void receive(ControllerScreenLog log, Player player) {
    if (player.containerMenu instanceof ControllerMenu menu && menu.containerId == log.menu()) {
      menu.acceptLog(log);
    }
  }

  @Override
  public Type<ControllerScreenLog> type() {
    return TYPE;
  }
}
