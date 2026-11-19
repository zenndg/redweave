package io.github.zenndg.redweave.domain.network;

import java.util.UUID;

/**
 * The default name of a node and of a network: the first 8 characters of the ID, such as {@code
 * 3f2a9c1b}, which fit every name limit of the config (answer 142).
 */
public final class DefaultNames {
  private static final int ID_CHARACTERS = 8;

  private DefaultNames() {}

  public static String of(UUID id) {
    return id.toString().substring(0, ID_CHARACTERS);
  }
}
