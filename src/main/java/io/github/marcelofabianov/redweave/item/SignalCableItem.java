package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * The Signal Cable as an item: one item that is born white and takes one of the 16 wool colors in
 * the data component `cable_color`; a white cable has no component, so every white cable stacks
 * with the others (answers 494, 505 and 561). It places a cable part of its color (answer 495).
 */
public final class SignalCableItem extends PartItem {
  /** The suffix of the key of the name of a colored cable, such as `Red Signal Cable` (564). */
  public static final String COLORED_SUFFIX = ".colored";

  public SignalCableItem(Properties properties) {
    super(properties);
  }

  /** The cable part of the color of the stack, on the side (answers 495 and 561). */
  @Override
  protected Optional<Part> partOf(ItemStack stack, Direction face) {
    return Optional.of(CablePart.placed(face, colorOf(stack)));
  }

  /** The color of the cable in the stack; white when it has none (answer 561). */
  public static ChannelColor colorOf(ItemStack stack) {
    return stack.getOrDefault(RedweaveDataComponents.CABLE_COLOR.get(), ChannelColor.WHITE);
  }

  /** Cables of the color, with no component for white (answer 561). */
  public static ItemStack stackOf(ChannelColor color, int count) {
    ItemStack stack = new ItemStack(RedweaveItems.SIGNAL_CABLE.get(), count);
    if (color != ChannelColor.WHITE) {
      stack.set(RedweaveDataComponents.CABLE_COLOR.get(), color);
    }
    return stack;
  }

  /** One cable of each color, white first, in the order of the wool, for the creative tab. */
  public static List<ItemStack> everyColor() {
    return Arrays.stream(ChannelColor.values()).map(color -> stackOf(color, 1)).toList();
  }

  /** A white cable is named `Signal Cable`, and the others carry their color (answer 564). */
  @Override
  public Component getName(ItemStack stack) {
    ChannelColor color = colorOf(stack);
    if (color == ChannelColor.WHITE) {
      return super.getName(stack);
    }
    return Component.translatable(
        getDescriptionId() + COLORED_SUFFIX,
        Component.translatable(NodeScreenTexts.color(color.key())));
  }

  /** A white component, as from a damaged or edited stack, is dropped, so whites stack (561). */
  @Override
  public void verifyComponentsAfterLoad(ItemStack stack) {
    if (stack.get(RedweaveDataComponents.CABLE_COLOR.get()) == ChannelColor.WHITE) {
      stack.remove(RedweaveDataComponents.CABLE_COLOR.get());
    }
  }
}
