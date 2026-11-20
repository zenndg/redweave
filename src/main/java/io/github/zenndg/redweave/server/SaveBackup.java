package io.github.zenndg.redweave.server;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

/**
 * Keeps aside a save the mod cannot read, as one from a newer version, before the empty save is
 * written over it, so going back to that version finds the networks again (answer 92).
 */
final class SaveBackup {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DATA = "data";
  private static final String EXTENSION = ".dat";
  private static final String VERSION_MARK = ".v";
  private static final String BACKUP_EXTENSION = ".backup.dat";
  private static final String UNREADABLE = ".unreadable";
  private static final String KEPT = "The Redweave save was kept in {}";
  private static final String NOT_KEPT = "The Redweave save {} could not be kept aside";

  private SaveBackup() {}

  /** The file of the save, in the data of the Overworld, the root of the world (answer 88). */
  static Path fileOf(MinecraftServer server, String name) {
    return server.getWorldPath(LevelResource.ROOT).resolve(DATA).resolve(name + EXTENSION);
  }

  /** A save of an unknown version, which goes aside with the version in its name (answer 92). */
  static void copy(Path file, String name, int version) {
    keep(file, file.resolveSibling(name + VERSION_MARK + version + BACKUP_EXTENSION));
  }

  /** A file the game could not read, if it is there at all (answer 97). */
  static void copyIfUnreadable(Path file, String name) {
    if (Files.exists(file)) {
      keep(file, file.resolveSibling(name + UNREADABLE + BACKUP_EXTENSION));
    }
  }

  private static void keep(Path file, Path backup) {
    try {
      Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
      LOGGER.error(KEPT, backup);
    } catch (IOException failure) {
      LOGGER.error(NOT_KEPT, file, failure);
    }
  }
}
