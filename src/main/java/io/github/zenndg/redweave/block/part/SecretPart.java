package io.github.zenndg.redweave.block.part;

import java.util.List;

/**
 * A part with keys of its save that only the server keeps, as the key of the Signal Lock and the
 * code of the Signal Keypad: the block sends its parts to the clients without them (answers 637,
 * 660, 896 and 899).
 */
public interface SecretPart {
  /** The keys of its save that never reach a client. */
  List<String> secretKeys();
}
