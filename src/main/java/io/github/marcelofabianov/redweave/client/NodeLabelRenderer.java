package io.github.marcelofabianov.redweave.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.CableLine;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.Label;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.ModuleLine;
import io.github.marcelofabianov.redweave.network.ReceivedLabels;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * Draws the label over each node the server sent, while the player holds the Configurator in the
 * main hand, facing the camera like a name tag, and dimmer behind walls. Two lines: the network,
 * small, after a square in its color, and the node, a little bigger (answers 268 and 274 to 276).
 * The Signal Modules get theirs too (answer 417), and the Signal Cables one line (answer 525).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID, value = Dist.CLIENT)
public final class NodeLabelRenderer {
  /** The size of a name tag of the game; the lines are a part of it (answer 276). */
  private static final float NAME_TAG = 0.025F;

  private static final float NETWORK_SCALE = NAME_TAG * 0.5F;
  private static final float NODE_SCALE = NAME_TAG * 0.7F;
  private static final double CENTER = 0.5;
  private static final double ABOVE = 0.75;

  /**
   * How far a label moves from the center of a shared space toward the side of its part (answer
   * 547). It stays inside the space, and the labels of opposite sides end 0.7 apart, more than the
   * two lines of a label, about 0.36 high.
   */
  private static final double TOWARD_SIDE = 0.35;

  private static final double NODE_LINE_UP = 0.0;
  private static final double NETWORK_LINE_UP = 0.2;
  private static final int SQUARE = 7;
  private static final int SQUARE_GAP = 3;
  private static final int SQUARE_TOP = 0;
  private static final float SQUARE_DEPTH = 0.01F;
  private static final int THROUGH_WALLS = 0x40FFFFFF;
  private static final int IN_SIGHT = 0xFFFFFFFF;
  private static final int BACKGROUND = 0x40000000;
  private static final int NO_BACKGROUND = 0;
  private static final int OPAQUE = 0xFF000000;
  private static final int NO_NETWORK_SQUARE = 0xFF8A9099;
  private static final float HALF = 2F;
  private static final String SEPARATOR = " · ";

  private NodeLabelRenderer() {}

  @SubscribeEvent
  public static void onRender(RenderLevelStageEvent event) {
    Minecraft minecraft = Minecraft.getInstance();
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES
        || minecraft.player == null
        || !minecraft.player.getMainHandItem().is(RedweaveItems.CONFIGURATOR.get())) {
      return;
    }
    MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
    for (Label label : ReceivedLabels.all()) {
      draw(event.getPoseStack(), event.getCamera(), minecraft.font, buffers, label);
    }
    for (ModuleLine module : ReceivedLabels.modules()) {
      drawModule(event.getPoseStack(), event.getCamera(), minecraft.font, buffers, module);
    }
    for (CableLine cable : ReceivedLabels.cables()) {
      drawCable(event.getPoseStack(), event.getCamera(), minecraft.font, buffers, cable);
    }
    buffers.endBatch();
  }

  @SubscribeEvent
  public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event) {
    ReceivedLabels.clear();
  }

  private static void draw(
      PoseStack pose, Camera camera, Font font, MultiBufferSource buffers, Label label) {
    Component network =
        label.hasNetwork()
            ? Component.literal(label.network())
            : Component.translatable(NodeScreenTexts.STATE_NO_NETWORK);
    int square = label.hasNetwork() ? colorOf(label.color()) : NO_NETWORK_SQUARE;
    Line top = new Line(network, NETWORK_SCALE, NETWORK_LINE_UP, square);
    Line bottom = new Line(Component.literal(label.nodeName()), NODE_SCALE, NODE_LINE_UP, 0);
    Vec3 anchor = anchorOf(label.place());
    drawLine(pose, camera, font, buffers, anchor, top);
    drawLine(pose, camera, font, buffers, anchor, bottom);
  }

  /**
   * Over a Signal Module: its name, and below, smaller, its state and the channel of its output,
   * after a square in the color of the channel (answer 417).
   */
  private static void drawModule(
      PoseStack pose, Camera camera, Font font, MultiBufferSource buffers, ModuleLine module) {
    Component state = Component.translatable(module.state());
    Optional<ChannelColor> channel = channelOf(module.channel());
    Component below =
        channel
            .<Component>map(color -> state.copy().append(SEPARATOR).append(ChannelNames.of(color)))
            .orElse(state);
    int square = channel.map(color -> OPAQUE | ChannelColors.argb(color)).orElse(0);
    Line top = new Line(Component.literal(module.name()), NODE_SCALE, NETWORK_LINE_UP, 0);
    Line bottom = new Line(below, NETWORK_SCALE, NODE_LINE_UP, square);
    Vec3 anchor = anchorOf(module.place());
    drawLine(pose, camera, font, buffers, anchor, top);
    drawLine(pose, camera, font, buffers, anchor, bottom);
  }

  /**
   * Over a Signal Cable, in one line: a square in its color, the color as the channels show it, and
   * its strength, as `15 Red · 12` (answer 525). A number outside the colors shows no label.
   */
  private static void drawCable(
      PoseStack pose, Camera camera, Font font, MultiBufferSource buffers, CableLine cable) {
    Optional<ChannelColor> color = channelOf(cable.color());
    if (color.isEmpty()) {
      return;
    }
    Component text =
        ChannelNames.of(color.get())
            .copy()
            .append(SEPARATOR)
            .append(String.valueOf(cable.strength()));
    int square = OPAQUE | ChannelColors.argb(color.get());
    drawLine(
        pose,
        camera,
        font,
        buffers,
        anchorOf(cable.place()),
        new Line(text, NODE_SCALE, NODE_LINE_UP, square));
  }

  /**
   * Where the lines of a label start: over the space, as always, when its part is alone, or moved
   * from the center toward the side of its part when the space has other labeled parts (547).
   */
  private static Vec3 anchorOf(NodeLabelsUpdate.Place place) {
    BlockPos pos = place.spot().pos();
    if (!place.shared()) {
      return new Vec3(pos.getX() + CENTER, pos.getY() + ABOVE, pos.getZ() + CENTER);
    }
    Vec3i side = place.spot().face().getNormal();
    return Vec3.atCenterOf(pos)
        .add(side.getX() * TOWARD_SIDE, side.getY() * TOWARD_SIDE, side.getZ() * TOWARD_SIDE);
  }

  private static Optional<ChannelColor> channelOf(int channel) {
    ChannelColor[] colors = ChannelColor.values();
    return channel >= 0 && channel < colors.length
        ? Optional.of(colors[channel])
        : Optional.empty();
  }

  /** One line of the label, with its size, its height over the node and a square or none (0). */
  private record Line(Component text, float scale, double up, int square) {}

  private static void drawLine(
      PoseStack pose, Camera camera, Font font, MultiBufferSource buffers, Vec3 anchor, Line line) {
    Vec3 eye = camera.getPosition();
    pose.pushPose();
    pose.translate(anchor.x - eye.x, anchor.y + line.up() - eye.y, anchor.z - eye.z);
    pose.mulPose(camera.rotation());
    pose.scale(line.scale(), -line.scale(), line.scale());
    Matrix4f matrix = pose.last().pose();
    int squareWidth = line.square() == 0 ? 0 : SQUARE + SQUARE_GAP;
    float left = -(font.width(line.text()) + squareWidth) / HALF;
    if (line.square() != 0) {
      square(matrix, buffers, left, line.square());
    }
    drawText(matrix, font, buffers, line.text(), left + squareWidth);
    pose.popPose();
  }

  /** The text twice, as a name tag: dim through walls and full where it can be seen. */
  private static void drawText(
      Matrix4f matrix, Font font, MultiBufferSource buffers, Component text, float x) {
    font.drawInBatch(
        text,
        x,
        0,
        THROUGH_WALLS,
        false,
        matrix,
        buffers,
        Font.DisplayMode.SEE_THROUGH,
        BACKGROUND,
        LightTexture.FULL_BRIGHT);
    font.drawInBatch(
        text,
        x,
        0,
        IN_SIGHT,
        false,
        matrix,
        buffers,
        Font.DisplayMode.NORMAL,
        NO_BACKGROUND,
        LightTexture.FULL_BRIGHT);
  }

  /** The square in the color of the network, before its name (answer 275). */
  private static void square(Matrix4f matrix, MultiBufferSource buffers, float x, int color) {
    VertexConsumer quad = buffers.getBuffer(RenderType.textBackgroundSeeThrough());
    float right = x + SQUARE;
    float bottom = SQUARE_TOP + SQUARE;
    quad.addVertex(matrix, x, SQUARE_TOP, SQUARE_DEPTH)
        .setColor(color)
        .setLight(LightTexture.FULL_BRIGHT);
    quad.addVertex(matrix, x, bottom, SQUARE_DEPTH)
        .setColor(color)
        .setLight(LightTexture.FULL_BRIGHT);
    quad.addVertex(matrix, right, bottom, SQUARE_DEPTH)
        .setColor(color)
        .setLight(LightTexture.FULL_BRIGHT);
    quad.addVertex(matrix, right, SQUARE_TOP, SQUARE_DEPTH)
        .setColor(color)
        .setLight(LightTexture.FULL_BRIGHT);
  }

  /** The numbers come from the server, so one outside the colors shows the grey of no network. */
  private static int colorOf(int color) {
    ChannelColor[] colors = ChannelColor.values();
    return color >= 0 && color < colors.length
        ? OPAQUE | ChannelColors.argb(colors[color])
        : NO_NETWORK_SQUARE;
  }
}
