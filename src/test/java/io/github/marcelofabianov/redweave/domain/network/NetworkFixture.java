package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.UUID;

/** The same network for every test: owned by OWNER, with the controller at CONTROLLER. */
final class NetworkFixture {
  private static final String NETWORK_UUID = "6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b";
  private static final String OWNER_UUID = "0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d";
  private static final String STRANGER_UUID = "9f8e7d6c-5b4a-4c3d-9e2f-1a0b9c8d7e6f";
  private static final String OTHER_NODE_UUID = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f";
  private static final String NODE_UUID = "1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e";

  static final NetworkId ID = new NetworkId(UUID.fromString(NETWORK_UUID));
  static final PlayerId OWNER = new PlayerId(UUID.fromString(OWNER_UUID));
  static final PlayerId STRANGER = new PlayerId(UUID.fromString(STRANGER_UUID));
  static final String NAME = "Network 6f1c";
  static final ChannelColor COLOR = ChannelColor.CYAN;
  static final String OVERWORLD = "minecraft:overworld";
  static final BlockLocation CONTROLLER = new BlockLocation(OVERWORLD, 0, 64, 0);

  static final NodeId NODE = new NodeId(UUID.fromString(NODE_UUID));
  static final NodePosition NODE_POSITION =
      new NodePosition(new BlockLocation(OVERWORLD, 8, 64, 0), Face.EAST);

  static final NodeId OTHER_NODE = new NodeId(UUID.fromString(OTHER_NODE_UUID));
  static final NodePosition OTHER_NODE_POSITION =
      new NodePosition(new BlockLocation(OVERWORLD, -8, 64, 0), Face.WEST);

  private NetworkFixture() {}

  static Network network() {
    return Network.create(ID, OWNER, NAME, COLOR, CONTROLLER, TestLimits.STARTING);
  }
}
