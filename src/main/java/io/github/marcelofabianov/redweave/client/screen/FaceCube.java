package io.github.marcelofabianov.redweave.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The block face of a slot, chosen on the real block the node sticks to, in 3D. Dragging turns the
 * block; the face under the mouse and the chosen one shine copper (answer 79).
 */
final class FaceCube {
  private static final float SCALE = 19;
  private static final float DEPTH = 150;
  private static final float DEFAULT_YAW = -35;
  private static final float DEFAULT_PITCH = 25;
  private static final float TURN_SPEED = 2.5F;
  private static final float MAX_PITCH = 89;
  private static final float HALF = 0.5F;
  private static final float OUTSET = 0.502F;
  private static final double CLICK_SLACK = 2;

  private float yaw = DEFAULT_YAW;
  private float pitch = DEFAULT_PITCH;
  private double dragged;
  private boolean pressing;

  void draw(
      GuiGraphics graphics, Box box, BlockState block, Optional<Direction> chosen, NodeView view) {
    Matrix4f matrix = matrixOf(box);
    PoseStack pose = graphics.pose();
    pose.pushPose();
    pose.mulPose(matrix);
    Lighting.setupFor3DItems();
    pose.pushPose();
    pose.translate(-HALF, -HALF, -HALF);
    Minecraft.getInstance()
        .getBlockRenderer()
        .renderSingleBlock(
            block,
            pose,
            graphics.bufferSource(),
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY);
    pose.popPose();
    graphics.flush();
    chosen.ifPresent(face -> shine(graphics, pose, face, ScreenColors.SELECTED_FACE));
    hovered(box, view).ifPresent(face -> shine(graphics, pose, face, ScreenColors.HIGHLIGHT));
    graphics.flush();
    pose.popPose();
    Lighting.setupForFlatItems();
  }

  /** The face under the mouse, nearest to the player, when the slot can be changed. */
  Optional<Direction> hovered(Box box, NodeView view) {
    if (!view.hovers(box)) {
      return Optional.empty();
    }
    return faceAt(matrixOf(box), view.mouseX(), view.mouseY());
  }

  void press() {
    pressing = true;
    dragged = 0;
  }

  void drag(double dragX, double dragY) {
    if (!pressing) {
      return;
    }
    dragged += Math.abs(dragX) + Math.abs(dragY);
    yaw += (float) dragX * TURN_SPEED;
    pitch = Mth.clamp(pitch + (float) dragY * TURN_SPEED, -MAX_PITCH, MAX_PITCH);
  }

  /** A release that barely moved is a click, which chooses the face under the mouse. */
  Optional<Direction> release(Box box, NodeView view) {
    boolean click = pressing && dragged <= CLICK_SLACK;
    pressing = false;
    return click ? hovered(box, view) : Optional.empty();
  }

  private Matrix4f matrixOf(Box box) {
    return new Matrix4f()
        .translate(box.x() + box.width() / 2.0F, box.y() + box.height() / 2.0F, DEPTH)
        .scale(SCALE, -SCALE, SCALE)
        .rotateX((float) Math.toRadians(pitch))
        .rotateY((float) Math.toRadians(yaw));
  }

  private static Optional<Direction> faceAt(Matrix4f matrix, double mouseX, double mouseY) {
    Optional<Direction> nearest = Optional.empty();
    float nearestDepth = -Float.MAX_VALUE;
    for (Direction face : Direction.values()) {
      Vector3f[] corners = cornersOf(face, HALF);
      for (Vector3f corner : corners) {
        matrix.transformPosition(corner);
      }
      float depth = 0;
      for (Vector3f corner : corners) {
        depth += corner.z / corners.length;
      }
      if (Quads.contains(onScreen(corners), mouseX, mouseY) && depth > nearestDepth) {
        nearest = Optional.of(face);
        nearestDepth = depth;
      }
    }
    return nearest;
  }

  private static double[][] onScreen(Vector3f[] corners) {
    double[][] points = new double[corners.length][];
    for (int corner = 0; corner < corners.length; corner++) {
      points[corner] = new double[] {corners[corner].x, corners[corner].y};
    }
    return points;
  }

  /** A face drawn just above the block, on both sides, so the winding never hides it. */
  private static void shine(GuiGraphics graphics, PoseStack pose, Direction face, int color) {
    VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
    Matrix4f matrix = pose.last().pose();
    Vector3f[] corners = cornersOf(face, OUTSET);
    for (int corner = 0; corner < corners.length; corner++) {
      vertex(buffer, matrix, corners[corner], color);
    }
    for (int corner = corners.length - 1; corner >= 0; corner--) {
      vertex(buffer, matrix, corners[corner], color);
    }
  }

  private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vector3f at, int color) {
    buffer.addVertex(matrix, at.x, at.y, at.z).setColor(color);
  }

  /** The 4 corners of a face of a cube centered on the origin, going around it. */
  private static Vector3f[] cornersOf(Direction face, float half) {
    Vector3f normal = new Vector3f(face.getStepX(), face.getStepY(), face.getStepZ()).mul(half);
    Vector3f first = perpendicular(face).mul(half);
    Vector3f second = new Vector3f(normal).normalize().cross(first, new Vector3f());
    return new Vector3f[] {
      new Vector3f(normal).add(first).add(second),
      new Vector3f(normal).sub(first).add(second),
      new Vector3f(normal).sub(first).sub(second),
      new Vector3f(normal).add(first).sub(second)
    };
  }

  private static Vector3f perpendicular(Direction face) {
    return face.getAxis() == Direction.Axis.Y ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
  }
}
