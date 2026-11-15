package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.DetectorPart;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.RandomizerPart;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.client.screen.ControllerScreen;
import io.github.marcelofabianov.redweave.client.screen.DetectorScreen;
import io.github.marcelofabianov.redweave.client.screen.InverterScreen;
import io.github.marcelofabianov.redweave.client.screen.NodeScreen;
import io.github.marcelofabianov.redweave.client.screen.RandomizerScreen;
import io.github.marcelofabianov.redweave.client.screen.RemoteLinkScreen;
import io.github.marcelofabianov.redweave.client.screen.TerminalScreen;
import io.github.marcelofabianov.redweave.client.screen.TimerScreen;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** What only the client registers: the screens, the colors and the models (rule 19). */
@EventBusSubscriber(
    modid = RedweaveMod.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class RedweaveClient {
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
  }

  @SubscribeEvent
  public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
    event.register(RedweaveClient::cornerColor, RedweaveBlocks.PART_SPACE.get());
  }

  /** The models of the parts, which no blockstate names, so the part space can join them (536). */
  @SubscribeEvent
  public static void registerPartModels(ModelEvent.RegisterAdditional event) {
    Stream.of(
            NodePart.MODELS,
            ModulePart.models(TimerPart.ID, TimerPart.LOOKS),
            ModulePart.models(InverterPart.ID, InverterPart.LOOKS),
            ModulePart.models(DetectorPart.ID, DetectorPart.LOOKS),
            ModulePart.models(RandomizerPart.ID, RandomizerPart.LOOKS))
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
   * The tint of a corner of a node: its side and its slot, from 0 to 3, which the model of the
   * space packs in the tint index (answers 87 and 536).
   */
  private static int cornerColor(
      BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
    if (level == null
        || pos == null
        || !(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return ChannelColors.FREE;
    }
    int channel =
        space
            .at(PartQuads.faceOf(tintIndex))
            .filter(NodePart.class::isInstance)
            .map(part -> ((NodePart) part).corner(PartQuads.slotOf(tintIndex)))
            .orElse(NodeLook.FREE);
    return channel == NodeLook.FREE
        ? ChannelColors.FREE
        : ChannelColors.argb(ChannelColor.values()[channel]);
  }
}
