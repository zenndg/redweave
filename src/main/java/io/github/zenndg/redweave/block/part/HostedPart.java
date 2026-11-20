package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;

/**
 * A part that changes from outside its space, as a Signal Module from its screen, and so keeps its
 * space to save the change and send it to the clients.
 */
public interface HostedPart {
  void attach(PartSpaceBlockEntity space);
}
