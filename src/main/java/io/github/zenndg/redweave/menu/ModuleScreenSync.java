package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.NetworkChoice;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * What every Signal Module Screen keeps in sync besides its live values: the names of the module
 * and of its network, and the network choice of the owner (answers 400, 404 and 427). The server
 * sends them again when they change; the client only shows them (rule 16).
 */
public final class ModuleScreenSync {
  private static final String NO_NAME = "";

  private ModuleScreenNames names;
  private List<NetworkChoice> choices = List.of();
  private List<NodeScreenChoices.Row> rows = List.of();

  private ModuleScreenSync(ModuleScreenNames names) {
    this.names = names;
  }

  /** On the server, with the names read from the block. */
  static ModuleScreenSync onServer(ServerPlayer player, PartSpot spot, int menu) {
    return new ModuleScreenSync(ModuleScreenNames.read(player, spot, menu));
  }

  /** On the client, with the names the server sent when opening and the menu ID of the client. */
  static ModuleScreenSync onClient(int menu, RegistryFriendlyByteBuf extra) {
    ModuleScreenNames opening = ModuleScreenNames.STREAM_CODEC.decode(extra);
    return new ModuleScreenSync(
        new ModuleScreenNames(
            menu, opening.module(), opening.network(), opening.color(), opening.labels()));
  }

  /** What the server writes when it opens the screen, after the position. */
  static void writeOpening(RegistryFriendlyByteBuf extra, ServerPlayer player, PartSpot spot) {
    ModuleScreenNames.STREAM_CODEC.encode(extra, ModuleScreenNames.read(player, spot, 0));
  }

  /** Sends the names and the network choice again when they change. */
  void broadcast(ServerPlayer player, PartSpot spot, int menu) {
    ModuleScreenNames now = ModuleScreenNames.read(player, spot, menu);
    if (!now.equals(names)) {
      names = now;
      ModuleScreenNames.send(player, now);
    }
    List<NetworkChoice> nowChoices = ModuleScreenActions.choices(player, spot);
    if (!nowChoices.equals(choices)) {
      choices = nowChoices;
      NodeScreenChoices.send(player, menu, nowChoices);
    }
  }

  /** The network of a row of the list kept here, so a client never sends a network ID. */
  Optional<NetworkId> choiceAt(int position) {
    return position < choices.size() ? Optional.of(choices.get(position).id()) : Optional.empty();
  }

  void acceptNames(ModuleScreenNames received) {
    names = received;
  }

  void acceptRows(List<NodeScreenChoices.Row> received) {
    rows = received;
  }

  public String moduleName() {
    return names.module();
  }

  public String networkName() {
    return names.network();
  }

  /** The color of the network, or none for a module with no network. */
  public Optional<ChannelColor> networkColor() {
    return ModuleScreenValues.colorAt(names.color());
  }

  /** The name the owner gave a channel, or empty for the name of its color (answer 221). */
  public String channelName(ChannelColor channel) {
    List<String> labels = names.labels().channels();
    return channel.ordinal() < labels.size() ? labels.get(channel.ordinal()) : NO_NAME;
  }

  public List<NodeScreenChoices.Row> rows() {
    return rows;
  }
}
