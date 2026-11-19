package io.github.zenndg.redweave.client;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.CableLook;
import io.github.zenndg.redweave.block.part.PartLook;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * The model of the part space: it joins the models of its parts into the mesh of the chunk, so a
 * space costs nothing per frame and is only built again when a part changes (answer 536).
 */
final class PartSpaceModel implements IDynamicBakedModel {
  /** The texture of the glass of the Signal Lamp, drawn translucent (answer 825). */
  private static final String GLASS_PATH = "block/signal_lamp_glass";

  private static final ResourceLocation GLASS =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, GLASS_PATH);

  private final BakedModel base;
  private final Function<ResourceLocation, BakedModel> models;
  private final Map<PartLook, List<BakedQuad>> turned = new ConcurrentHashMap<>();
  private final Map<CableLook, List<BakedQuad>> cables = new ConcurrentHashMap<>();

  PartSpaceModel(BakedModel base, Function<ResourceLocation, BakedModel> models) {
    this.base = base;
    this.models = models;
  }

  @Override
  public List<BakedQuad> getQuads(
      BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
    List<PartLook> looks = data.get(PartSpaceBlockEntity.LOOKS);
    if (side != null || looks == null) {
      return List.of();
    }
    List<BakedQuad> quads = new ArrayList<>();
    for (PartLook look : looks) {
      quads.addAll(turned.computeIfAbsent(look, this::bake));
    }
    List<CableLook> pieces = data.get(PartSpaceBlockEntity.CABLES);
    if (pieces != null) {
      for (CableLook piece : pieces) {
        quads.addAll(cables.computeIfAbsent(piece, this::bakeCable));
      }
    }
    return layerOf(quads, renderType);
  }

  /**
   * The glass of the Signal Lamp goes to the translucent layer and the rest to the layers of the
   * space, so a frosted dome shows the core behind it (answer 825). Without a layer, all go.
   */
  private static List<BakedQuad> layerOf(List<BakedQuad> quads, RenderType renderType) {
    if (renderType == null) {
      return quads;
    }
    boolean translucent = renderType == RenderType.translucent();
    return quads.stream().filter(quad -> isGlass(quad) == translucent).toList();
  }

  private static boolean isGlass(BakedQuad quad) {
    return GLASS.equals(quad.getSprite().contents().name());
  }

  /** The layers of the space, and the translucent one for the glass (answer 825). */
  @Override
  public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
    return ChunkRenderTypeSet.union(
        base.getRenderTypes(state, rand, data), ChunkRenderTypeSet.of(RenderType.translucent()));
  }

  private List<BakedQuad> bake(PartLook look) {
    return PartQuads.of(models.apply(look.model()), look);
  }

  private List<BakedQuad> bakeCable(CableLook piece) {
    return CableQuads.of(models.apply(piece.model()), piece);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return base.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return base.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return base.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return base.getParticleIcon();
  }

  @Override
  public ItemOverrides getOverrides() {
    return ItemOverrides.EMPTY;
  }
}
