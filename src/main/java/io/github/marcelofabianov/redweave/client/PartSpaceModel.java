package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.PartLook;
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
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * The model of the part space: it joins the models of its parts into the mesh of the chunk, so a
 * space costs nothing per frame and is only built again when a part changes (answer 536).
 */
final class PartSpaceModel implements IDynamicBakedModel {
  private final BakedModel base;
  private final Function<ResourceLocation, BakedModel> models;
  private final Map<PartLook, List<BakedQuad>> turned = new ConcurrentHashMap<>();

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
    return quads;
  }

  private List<BakedQuad> bake(PartLook look) {
    return PartQuads.of(models.apply(look.model()), look);
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
