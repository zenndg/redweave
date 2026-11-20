package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ChangeNodeDelay;
import io.github.zenndg.redweave.application.EmitNode;

/**
 * The use cases of the output of a Signal Node, wired to the saves of the server: what it should
 * emit and its delay (no-de-sinal.md, answers 859 to 862). Kept apart from RedweaveServer, so each
 * wiring stays small.
 */
public final class NodeOutputServices {
  private final ChangeNodeDelay changeDelay;
  private final EmitNode emit;

  NodeOutputServices(SavedDataNetworks networks) {
    changeDelay = new ChangeNodeDelay(networks, networks);
    emit = new EmitNode(networks);
  }

  public ChangeNodeDelay changeDelay() {
    return changeDelay;
  }

  public EmitNode emit() {
    return emit;
  }
}
