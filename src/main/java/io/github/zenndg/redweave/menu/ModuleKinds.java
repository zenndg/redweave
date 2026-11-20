package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * The kind of a registered Signal Module as the screens name it: the name of its item, as `Signal
 * Timer`, from the ID the network keeps (answers 809 and 810). An unknown ID shows as it is, so a
 * damaged one never breaks the screen (rule 14).
 */
public final class ModuleKinds {
  private ModuleKinds() {}

  public static Component nameOf(String kind) {
    ResourceLocation id = ResourceLocation.tryBuild(RedweaveMod.MOD_ID, kind);
    Item item = id == null ? Items.AIR : BuiltInRegistries.ITEM.get(id);
    return item == Items.AIR ? Component.literal(kind) : item.getDescription();
  }
}
