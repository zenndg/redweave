package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.KeypadEntry;
import io.github.zenndg.redweave.registry.RedweaveMenus;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The keypad any player opens with an empty hand: the digits stay on the client until `Enter`, and
 * the server checks the code and says only to this player what came of it (answers 662 to 665 and
 * 914, rules 16 and 17).
 */
public final class KeypadEntryMenu extends AbstractContainerMenu {
  /** What came of the last try, in sync with the client. */
  public static final int RESULT = 0;

  /** The seconds still to wait, rounded up. */
  public static final int WAIT_SECONDS = 1;

  private static final int SIZE = 2;
  private static final double INTERACTION_SLACK = 4.0;
  private static final int TICKS_PER_SECOND = 20;

  private final PartSpot spot;
  private final ContainerData data;

  public KeypadEntryMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.KEYPAD_ENTRY.get(), id);
    this.spot = spot;
    this.data = new SimpleContainerData(SIZE);
    addDataSlots(data);
  }

  public KeypadEntryMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.KEYPAD_ENTRY.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(SIZE);
    addDataSlots(data);
  }

  public static void writeOpening(RegistryFriendlyByteBuf extra, PartSpot spot) {
    PartSpot.STREAM_CODEC.encode(extra, spot);
  }

  /** `Enter` with the code typed; the server answers only through the result. */
  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<KeypadEntry> entry =
        KeypadCodes.decode(button)
            .flatMap(code ->
                ModulePart.at(player.level(), spot, KeypadPart.class)
                    .filter(part -> player instanceof ServerPlayer)
                    .map(part -> part.enter((ServerPlayer) player, code)));
    entry.ifPresent(this::show);
    return entry.isPresent();
  }

  private void show(KeypadEntry entry) {
    data.set(RESULT, entry.kind().ordinal() + 1);
    data.set(WAIT_SECONDS, (int) ((entry.waitTicks() + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND));
  }

  /** What came of the last try, or none before any. */
  public Optional<KeypadEntry.Kind> result() {
    return ModuleScreenValues.valueAt(KeypadEntry.Kind.values(), data.get(RESULT));
  }

  public int waitSeconds() {
    return data.get(WAIT_SECONDS);
  }

  @Override
  public boolean stillValid(Player player) {
    return ModulePart.at(player.level(), spot, KeypadPart.class).isPresent()
        && player.canInteractWithBlock(spot.pos(), INTERACTION_SLACK);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }
}
