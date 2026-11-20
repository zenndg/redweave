package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.network.NodeState;
import io.github.zenndg.redweave.registry.RedweaveMenus;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The menu of the Signal Keypad Screen of the owner: telas.md, Tela do Signal Keypad. The cards go
 * only to the owner, when opening, and the code never goes to anyone (answers 660, 669 and 904).
 * The server keeps the live values in sync and decides every click; the client only shows them
 * (rules 16 and 17).
 */
public final class KeypadMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private static final StreamCodec<ByteBuf, List<CardId>> CARDS =
      UUIDUtil.STREAM_CODEC.map(CardId::new, CardId::value).apply(ByteBufCodecs.list());

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;
  private final List<CardId> cards;

  /** On the server, with the live values read from the block. */
  public KeypadMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.KEYPAD.get(), id);
    this.spot = spot;
    this.data = new KeypadScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    this.cards = new ArrayList<>();
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public KeypadMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.KEYPAD.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(KeypadScreenLayout.SIZE);
    this.viewer = Optional.empty();
    this.sync = ModuleScreenSync.onClient(id, extra);
    this.cards = new ArrayList<>(CARDS.decode(extra));
    addDataSlots(data);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, PartSpot spot) {
    PartSpot.STREAM_CODEC.encode(extra, spot);
    ModuleScreenSync.writeOpening(extra, player, spot);
    List<CardId> owned =
        ModulePart.at(player.level(), spot, KeypadPart.class)
            .filter(part -> part.isOwner(player))
            .map(part -> part.keypad().cards().ids())
            .orElse(List.of());
    CARDS.encode(extra, owned);
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<KeypadAction> action = KeypadAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, KeypadAction action) {
    if (action.kind() != KeypadAction.Kind.CHOOSE) {
      KeypadScreenActions.apply(player, spot, action);
      return;
    }
    sync.choiceAt(action.value())
        .ifPresent(network -> ModuleScreenActions.choose(player, spot, network));
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> sync.broadcast(player, spot, containerId));
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

  @Override
  public void acceptChoices(List<NodeScreenChoices.Row> received) {
    sync.acceptRows(received);
  }

  @Override
  public PartSpot spot() {
    return spot;
  }

  @Override
  public ModuleScreenSync sync() {
    return sync;
  }

  public boolean viewerIsOwner() {
    return data.get(KeypadScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** Whether it is open now (answer 908). */
  public boolean unlocked() {
    return data.get(KeypadScreenLayout.UNLOCKED) == 1;
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(KeypadScreenLayout.NETWORK_STATE));
  }

  public Connection input() {
    return ModuleScreenValues.connectionOf(data.get(KeypadScreenLayout.INPUT));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(KeypadScreenLayout.OUTPUT));
  }

  /** The strength, from 1 to 15 (answer 663). */
  public int strength() {
    return data.get(KeypadScreenLayout.STRENGTH);
  }

  /** The open time, in whole seconds (answer 663). */
  public int openSeconds() {
    return data.get(KeypadScreenLayout.OPEN_SECONDS);
  }

  /** Whether a code is set, never the code (answer 660). */
  public boolean hasCode() {
    return data.get(KeypadScreenLayout.HAS_CODE) == 1;
  }

  /** The cards it accepts, sent only to its owner when opening (answers 669 and 904). */
  public List<CardId> cards() {
    return cards;
  }

  /** Takes a revoked card out of the list the client shows. */
  public void forget(CardId card) {
    cards.remove(card);
  }
}
