package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.item.EmitterLink;
import io.github.zenndg.redweave.item.RemoteLink;
import io.github.zenndg.redweave.item.RemoteState;
import io.github.zenndg.redweave.item.RemoteStateCodec;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Remote in the saves of the item: a tablet saved before the emitters still reads as a
 * link to a node, a link to an emitter reads back whole, and a damaged one is dropped without
 * breaking the item (answer 786, rules 14 and 15).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RemoteSaveGameTests {
  private static final String EMPTY = "empty";
  private static final String LINK = "link";
  private static final String EMITTER = "emitter";
  private static final String NETWORK = "network";
  private static final String NODE = "node";
  private static final String SLOT = "slot";
  private static final String ID = "id";
  private static final String MODE = "mode";
  private static final String LEVER = "lever";
  private static final String DIMENSION = "dimension";
  private static final String X = "x";
  private static final String Y = "y";
  private static final String Z = "z";
  private static final String FACE = "face";
  private static final String MODULE = "module";
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NO_FACE = "sideways";
  private static final int SLOT_INDEX = 14;
  private static final int PLACE_X = 4;
  private static final int PLACE_Y = 64;
  private static final int PLACE_Z = -8;
  private static final UUID TABLET = UUID.fromString("3d4e5f6a-7b8c-4d9e-8f0a-1b2c3d4e5f6a");
  private static final UUID NETWORK_ID = UUID.fromString("6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b");
  private static final UUID NODE_ID = UUID.fromString("1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e");
  private static final UUID MODULE_ID = UUID.fromString("5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c");
  private static final BlockLocation PLACE =
      new BlockLocation(OVERWORLD, PLACE_X, PLACE_Y, PLACE_Z);
  private static final String OLD_NOT_NODE = "A tablet saved before should link to its node";
  private static final String NOT_WHOLE = "A link to an emitter should read back whole";
  private static final String DAMAGED_KEPT = "A damaged link to an emitter should be dropped";

  private RemoteSaveGameTests() {}

  @GameTest(template = EMPTY)
  public static void aTabletSavedBeforeStillLinksToItsNode(GameTestHelper helper) {
    CompoundTag link = new CompoundTag();
    link.put(NETWORK, NbtUtils.createUUID(NETWORK_ID));
    link.put(NODE, NbtUtils.createUUID(NODE_ID));
    link.putInt(SLOT, SLOT_INDEX);
    CompoundTag saved = new CompoundTag();
    saved.put(ID, NbtUtils.createUUID(TABLET));
    saved.put(LINK, link);
    saved.putString(MODE, LEVER);

    RemoteState read = read(saved).orElseThrow();

    helper.assertValueEqual(
        read.link(), Optional.of(new RemoteLink(NETWORK_ID, NODE_ID, SLOT_INDEX)), OLD_NOT_NODE);
    helper.assertTrue(read.emitter().isEmpty(), OLD_NOT_NODE);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aLinkToAnEmitterReadsBackWhole(GameTestHelper helper) {
    RemoteState linked = RemoteState.NEW.linkedTo(new EmitterLink(PLACE, Face.UP, MODULE_ID));
    Tag saved = RemoteStateCodec.CODEC.encodeStart(NbtOps.INSTANCE, linked).getOrThrow();

    helper.assertValueEqual(read(saved), Optional.of(linked), NOT_WHOLE);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aDamagedLinkToAnEmitterIsDropped(GameTestHelper helper) {
    CompoundTag emitter = new CompoundTag();
    emitter.putString(DIMENSION, OVERWORLD);
    emitter.putInt(X, PLACE_X);
    emitter.putInt(Y, PLACE_Y);
    emitter.putInt(Z, PLACE_Z);
    emitter.putString(FACE, NO_FACE);
    emitter.put(MODULE, NbtUtils.createUUID(MODULE_ID));
    CompoundTag saved = new CompoundTag();
    saved.put(EMITTER, emitter);

    helper.assertTrue(
        read(saved).map(state -> state.emitter().isEmpty()).orElse(false), DAMAGED_KEPT);
    helper.succeed();
  }

  private static Optional<RemoteState> read(Tag saved) {
    return RemoteStateCodec.CODEC.parse(NbtOps.INSTANCE, saved).result();
  }
}
