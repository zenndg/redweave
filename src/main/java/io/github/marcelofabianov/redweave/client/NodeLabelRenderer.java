package io.github.marcelofabianov.redweave.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.client.screen.NodeLabelLines;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate.Label;
import io.github.marcelofabianov.redweave.network.ReceivedLabels;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
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
 * main hand, facing the camera like a name tag, and dimmer behind walls (answers 268 and 270).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID, value = Dist.CLIENT)
public final class NodeLabelRenderer {
  private static final float SCALE = 0.025F;
  private static final double CENTER = 0.5;
  private static final double ABOVE = 0.9;
  private static final int LINE_HEIGHT = 10;
  private static final int THROUGH_WALLS = 0x40FFFFFF;
  private static final int IN_SIGHT = 0xFFFFFFFF;
  private static final int BACKGROUND = 0x40000000;
  private static final int NO_BACKGROUND = 0;

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
    buffers.endBatch();
  }

  @SubscribeEvent
  public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event) {
    ReceivedLabels.clear();
  }

  private static void draw(
      PoseStack pose, Camera camera, Font font, MultiBufferSource buffers, Label label) {
    Vec3 eye = camera.getPosition();
    pose.pushPose();
    pose.translate(
        label.pos().getX() + CENTER - eye.x,
        label.pos().getY() + ABOVE - eye.y,
        label.pos().getZ() + CENTER - eye.z);
    pose.mulPose(camera.rotation());
    pose.scale(SCALE, -SCALE, SCALE);
    drawLines(pose.last().pose(), font, buffers, NodeLabelLines.of(label));
    pose.popPose();
  }

  /** Each line twice, as a name tag: dim through walls and full where it can be seen. */
  private static void drawLines(
      Matrix4f matrix, Font font, MultiBufferSource buffers, List<Component> lines) {
    int top = -lines.size() * LINE_HEIGHT;
    for (int line = 0; line < lines.size(); line++) {
      Component text = lines.get(line);
      float x = -font.width(text) / 2F;
      int y = top + line * LINE_HEIGHT;
      font.drawInBatch(
          text,
          x,
          y,
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
          y,
          IN_SIGHT,
          false,
          matrix,
          buffers,
          Font.DisplayMode.NORMAL,
          NO_BACKGROUND,
          LightTexture.FULL_BRIGHT);
    }
  }
}
