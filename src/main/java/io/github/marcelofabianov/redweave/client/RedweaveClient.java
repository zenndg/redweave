package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.ButtonPart;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.DetectorPart;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.LockPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.block.part.RandomizerPart;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.client.screen.ButtonScreen;
import io.github.marcelofabianov.redweave.client.screen.ControllerScreen;
import io.github.marcelofabianov.redweave.client.screen.DetectorScreen;
import io.github.marcelofabianov.redweave.client.screen.InverterScreen;
import io.github.marcelofabianov.redweave.client.screen.ItemSensorScreen;
import io.github.marcelofabianov.redweave.client.screen.LampScreen;
import io.github.marcelofabianov.redweave.client.screen.LeverScreen;
import io.github.marcelofabianov.redweave.client.screen.LockScreen;
import io.github.marcelofabianov.redweave.client.screen.NodeScreen;
import io.github.marcelofabianov.redweave.client.screen.PressurePlateScreen;
import io.github.marcelofabianov.redweave.client.screen.RandomizerScreen;
import io.github.marcelofabianov.redweave.client.screen.RemoteLinkScreen;
import io.github.marcelofabianov.redweave.client.screen.TerminalScreen;
import io.github.marcelofabianov.redweave.client.screen.TimerScreen;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** What only the client registers: the screens, the colors and the models (rule 19). */
@EventBusSubscriber(
    modid = RedweaveMod.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class RedweaveClient {
  /** The first layer of the cable item, the insulation; the second, the tips, is not tinted. */
  private static final int INSULATION_LAYER = 0;

  /** White with full opacity: the layer keeps the colors of its texture. */
  private static final int UNTINTED = 0xFFFFFFFF;

  private static final int OPAQUE = 0xFF000000;

  private RedweaveClient() {}

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(RedweaveMenus.NODE.get(), NodeScreen::new);
    event.register(RedweaveMenus.CONTROLLER.get(), ControllerScreen::new);
    event.register(RedweaveMenus.TERMINAL.get(), TerminalScreen::new);
    event.register(RedweaveMenus.REMOTE_LINK.get(), RemoteLinkScreen::new);
    event.register(RedweaveMenus.TIMER.get(), TimerScreen::new);
    event.register(RedweaveMenus.INVERTER.get(), InverterScreen::new);
    event.register(RedweaveMenus.DETECTOR.get(), DetectorScreen::new);
    event.register(RedweaveMenus.RANDOMIZER.get(), RandomizerScreen::new);
    event.register(RedweaveMenus.LEVER.get(), LeverScreen::new);
    event.register(RedweaveMenus.BUTTON.get(), ButtonScreen::new);
    event.register(RedweaveMenus.PRESSURE_PLATE.get(), PressurePlateScreen::new);
    event.register(RedweaveMenus.ITEM_SENSOR.get(), ItemSensorScreen::new);
    event.register(RedweaveMenus.LOCK.get(), LockScreen::new);
    event.register(RedweaveMenus.LAMP.get(), LampScreen::new);
  }

  /** The item a Signal Item Sensor expects, drawn in its window (answer 697). */
  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(
        RedweaveBlockEntities.PART_SPACE.get(), ItemSensorRenderer::new);
  }

  @SubscribeEvent
  public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
    event.register(RedweaveClient::partColor, RedweaveBlocks.PART_SPACE.get());
  }

  /** The insulation of a Signal Cable item takes its color; its copper tips keep theirs (518). */
  @SubscribeEvent
  public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
    event.register(
        (stack, layer) ->
            layer == INSULATION_LAYER
                ? ChannelColors.argb(SignalCableItem.colorOf(stack))
                : UNTINTED,
        RedweaveItems.SIGNAL_CABLE.get());
  }

  /** The models of the parts, which no blockstate names, so the part space can join them (536). */
  @SubscribeEvent
  public static void registerPartModels(ModelEvent.RegisterAdditional event) {
    Stream.of(
            NodePart.MODELS,
            CablePart.MODELS,
            ModulePart.models(TimerPart.ID, TimerPart.LOOKS),
            ModulePart.models(InverterPart.ID, InverterPart.LOOKS),
            ModulePart.models(DetectorPart.ID, DetectorPart.LOOKS),
            ModulePart.models(RandomizerPart.ID, RandomizerPart.LOOKS),
            ModulePart.models(LeverPart.ID, LeverPart.LOOKS),
            ModulePart.models(ButtonPart.ID, ButtonPart.LOOKS),
            ModulePart.models(PressurePlatePart.ID, PressurePlatePart.LOOKS),
            ModulePart.models(ItemSensorPart.ID, ItemSensorPart.LOOKS),
            ModulePart.models(LockPart.ID, LockPart.LOOKS),
            ModulePart.models(LampPart.ID, LampPart.LOOKS))
        .flatMap(List::stream)
        .forEach(model -> event.register(ModelResourceLocation.standalone(model)));
  }

  /** The part space draws with the models of its parts (answer 536). */
  @SubscribeEvent
  public static void wrapPartSpace(ModelEvent.ModifyBakingResult event) {
    Map<ModelResourceLocation, BakedModel> models = event.getModels();
    for (BlockState state :
        RedweaveBlocks.PART_SPACE.get().getStateDefinition().getPossibleStates()) {
      ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
      models.computeIfPresent(
          location,
          (key, base) ->
              new PartSpaceModel(
                  base, model -> models.get(ModelResourceLocation.standalone(model))));
    }
  }

  /**
   * The tint of a part: its side and its slot, from 0 to 3, which the model of the space packs in
   * the tint index (answers 87 and 536). A node tints its corners, and a cable its insulation and
   * its stripe (answers 518 and 522).
   */
  private static int partColor(
      BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
    if (level == null
        || pos == null
        || !(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return ChannelColors.FREE;
    }
    Optional<Part> part = space.at(PartQuads.faceOf(tintIndex));
    if (part.isPresent() && part.get() instanceof CablePart cable) {
      return cableColor(cable, PartQuads.slotOf(tintIndex));
    }
    if (part.isPresent() && part.get() instanceof LampPart lamp) {
      return LampColors.of(lamp);
    }
    int channel =
        part.filter(NodePart.class::isInstance)
            .map(found -> ((NodePart) found).corner(PartQuads.slotOf(tintIndex)))
            .orElse(NodeLook.FREE);
    return channel == NodeLook.FREE
        ? ChannelColors.FREE
        : ChannelColors.argb(ChannelColor.values()[channel]);
  }

  /** The insulation in the color of the cable, and the stripe in the red of the game wire (522). */
  private static int cableColor(CablePart cable, int slot) {
    return slot == CableQuads.STRIPE
        ? OPAQUE | RedStoneWireBlock.getColorForPower(cable.strength())
        : ChannelColors.argb(cable.color());
  }
}
