package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.NodeKind;

/**
 * The text of the kind of a node, as `In` and `Out`, for the lists of nodes of the Controller
 * Screen and of the link of the Signal Remote (answers 283 and 339). Common code, because the
 * server builds the lines of the link.
 */
public final class ControllerKinds {
  private ControllerKinds() {}

  public static String keyOf(NodeKind kind) {
    return switch (kind) {
      case INPUT -> ControllerScreenTexts.KIND_INPUT;
      case OUTPUT -> ControllerScreenTexts.KIND_OUTPUT;
      case BOTH -> ControllerScreenTexts.KIND_BOTH;
      case NONE -> ControllerScreenTexts.KIND_NONE;
    };
  }
}
