package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.DetectorPart;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.PartTypes;
import io.github.marcelofabianov.redweave.block.part.RandomizerPart;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;

/**
 * The module parts of a space in a GameTest, read as the old module blocks were: the part itself in
 * place of its block entity, and its fields in place of the states of its block (answer 534).
 */
final class ModuleParts {
  /** The keys of answer 543, written here again, so a renamed key fails the test (rule 15). */
  private static final String PARTS = "parts";

  private static final String TYPE = "type";
  private static final String FACE = "face";
  private static final String DATA = "data";
  private static final String NO_MODULE = "Expected a module of the type in the space";
  private static final String MODULE_STAYED = "Expected no module in the space";
  private static final String WRONG_POWERED = "The arrow of the module is not as expected";
  private static final String WRONG_FRONT = "The front of the module is not as expected";
  private static final String WRONG_FACING = "The facing of the module is not as expected";
  private static final String WRONG_RUNNING = "The running LED of the module is not as expected";
  private static final String WRONG_BURNT = "The burnt look of the inverter is not as expected";
  private static final String WRONG_MODE = "The mode of the detector is not as expected";

  private ModuleParts() {}

  /** The module of the type in the space, if any. */
  static <T extends ModulePart> Optional<T> find(
      GameTestHelper helper, BlockPos relative, Class<T> type) {
    return PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(relative))
        .flatMap(
            space -> space.parts().stream().filter(type::isInstance).map(type::cast).findFirst());
  }

  /** The module of the type in the space, which the test placed. */
  static <T extends ModulePart> T only(GameTestHelper helper, BlockPos relative, Class<T> type) {
    return find(helper, relative, type).orElseThrow(() -> new GameTestAssertException(NO_MODULE));
  }

  /** The spot of the module of the space, in the world. */
  static PartSpot spotOf(GameTestHelper helper, BlockPos relative) {
    ModulePart module = only(helper, relative, ModulePart.class);
    return new PartSpot(helper.absolutePos(relative), module.face());
  }

  static void assertPresent(GameTestHelper helper, BlockPos relative) {
    helper.assertTrue(find(helper, relative, ModulePart.class).isPresent(), NO_MODULE);
  }

  static void assertAbsent(GameTestHelper helper, BlockPos relative) {
    helper.assertTrue(find(helper, relative, ModulePart.class).isEmpty(), MODULE_STAYED);
  }

  static void assertPowered(GameTestHelper helper, BlockPos relative, boolean powered) {
    helper.assertTrue(only(helper, relative, ModulePart.class).powered() == powered, WRONG_POWERED);
  }

  static void assertFront(GameTestHelper helper, BlockPos relative, Direction front) {
    helper.assertValueEqual(only(helper, relative, ModulePart.class).front(), front, WRONG_FRONT);
  }

  static void assertFacing(GameTestHelper helper, BlockPos relative, Direction facing) {
    helper.assertValueEqual(
        only(helper, relative, ModulePart.class).facing(), facing, WRONG_FACING);
  }

  /** The running LED of a timer, which is the `running` of its timer (answer 401). */
  static void assertTimerRunning(GameTestHelper helper, BlockPos relative, boolean running) {
    boolean now =
        only(helper, relative, TimerPart.class).timer().map(SignalTimer::isRunning).orElse(false);
    helper.assertTrue(now == running, WRONG_RUNNING);
  }

  /** The running LED of a randomizer, which is the `running` of its randomizer (answer 488). */
  static void assertRandomizerRunning(GameTestHelper helper, BlockPos relative, boolean running) {
    boolean now =
        only(helper, relative, RandomizerPart.class)
            .randomizer()
            .map(SignalRandomizer::running)
            .orElse(false);
    helper.assertTrue(now == running, WRONG_RUNNING);
  }

  /** The burnt look of an inverter, which is what is left of its burnout (answer 455). */
  static void assertBurnt(GameTestHelper helper, BlockPos relative, boolean burnt) {
    InverterState state = only(helper, relative, InverterPart.class).inverter().state();
    helper.assertTrue((state == InverterState.BURNT_OUT) == burnt, WRONG_BURNT);
  }

  /** The symbol of the top of a detector, which is its mode (answer 471). */
  static void assertMode(GameTestHelper helper, BlockPos relative, WeatherMode mode) {
    helper.assertValueEqual(
        only(helper, relative, DetectorPart.class).detector().mode(), mode, WRONG_MODE);
  }

  /**
   * Puts the module of the space back from its changed NBT, as a `/data merge` on its old block
   * did, so a test sets a state the world takes long to reach.
   */
  static void reload(GameTestHelper helper, BlockPos relative, CompoundTag tag) {
    PartSpaceBlockEntity space =
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(relative)).orElseThrow();
    ModulePart old = only(helper, relative, ModulePart.class);
    Part fresh = PartTypes.read(PartTypes.typeOf(old), old.face(), tag).orElseThrow();
    space.remove(old.face());
    space.add(fresh);
  }

  /**
   * A space with one module, loaded from its NBT and put in the world as the game loads a chunk, so
   * the module settles as it does on load (answer 425).
   */
  static <T extends ModulePart> T loadInWorld(
      GameTestHelper helper, BlockPos relative, Item item, CompoundTag data, Class<T> type) {
    helper.setBlock(relative, RedweaveBlocks.PART_SPACE.get());
    BlockPos pos = helper.absolutePos(relative);
    PartSpaceBlockEntity space =
        new PartSpaceBlockEntity(pos, helper.getLevel().getBlockState(pos));
    space.loadWithComponents(spaceTag(item, data), helper.getLevel().registryAccess());
    helper.getLevel().setBlockEntity(space);
    return only(helper, relative, type);
  }

  /** The NBT of a space with the module on the floor, as answer 543 keeps it. */
  private static CompoundTag spaceTag(Item item, CompoundTag data) {
    CompoundTag entry = new CompoundTag();
    entry.putString(TYPE, BuiltInRegistries.ITEM.getKey(item).toString());
    entry.putString(FACE, Direction.DOWN.getSerializedName());
    entry.put(DATA, data);
    ListTag parts = new ListTag();
    parts.add(entry);
    CompoundTag tag = new CompoundTag();
    tag.put(PARTS, parts);
    return tag;
  }
}
