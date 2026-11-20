package io.github.zenndg.redweave.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.ItemSensorPart;
import io.github.zenndg.redweave.block.part.Part;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the item a Signal Item Sensor expects in the window of its plate, small and flat, as an
 * item frame draws its item (answer 697). The models of the parts cannot show an item of the game,
 * so this is the only thing the part space draws each frame, and only for the sensors with an item.
 */
public final class ItemSensorRenderer implements BlockEntityRenderer<PartSpaceBlockEntity> {
  /** From the middle of the space to just over the top of the plate, 2 px from the face. */
  private static final float TO_WINDOW = 0.5F - 2.1F / 16;

  /** The item in the 4 × 4 window: a quarter of a block, where an item frame draws half. */
  private static final float SCALE = 0.25F;

  private static final float CENTER = 0.5F;
  private static final float HALF_TURN = 180;
  private static final float QUARTER_TURN = 90;

  public ItemSensorRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      PartSpaceBlockEntity space,
      float partialTick,
      PoseStack pose,
      MultiBufferSource buffers,
      int light,
      int overlay) {
    for (Part part : space.parts()) {
      if (part instanceof ItemSensorPart sensor) {
        sensor
            .expected()
            .ifPresent(
                item -> draw(space, sensor.face(), new ItemStack(item), pose, buffers, light));
      }
    }
  }

  private static void draw(
      PartSpaceBlockEntity space,
      Direction face,
      ItemStack stack,
      PoseStack pose,
      MultiBufferSource buffers,
      int light) {
    pose.pushPose();
    pose.translate(
        CENTER + face.getStepX() * TO_WINDOW,
        CENTER + face.getStepY() * TO_WINDOW,
        CENTER + face.getStepZ() * TO_WINDOW);
    turnTo(pose, face.getOpposite());
    pose.scale(SCALE, SCALE, SCALE);
    Minecraft.getInstance()
        .getItemRenderer()
        .renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            light,
            OverlayTexture.NO_OVERLAY,
            pose,
            buffers,
            space.getLevel(),
            0);
    pose.popPose();
  }

  /** Turns the item to face away from the plate, as an item frame turns to where it faces. */
  private static void turnTo(PoseStack pose, Direction facing) {
    if (facing.getAxis().isVertical()) {
      pose.mulPose(Axis.XP.rotationDegrees(-QUARTER_TURN * facing.getAxisDirection().getStep()));
      pose.mulPose(Axis.YP.rotationDegrees(HALF_TURN));
      return;
    }
    pose.mulPose(Axis.YP.rotationDegrees(HALF_TURN - facing.toYRot()));
  }
}
