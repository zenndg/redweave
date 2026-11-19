package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ApplyRemotes;
import io.github.zenndg.redweave.application.LinkRemote;
import io.github.zenndg.redweave.application.ShowRemote;

/**
 * The use cases of the Signal Remote, wired to the saves of the server (signal-remote.md). Kept
 * apart from RedweaveServer, so each wiring stays small.
 */
public final class RemoteServices {
  private final ApplyRemotes applyRemotes;
  private final LinkRemote linkRemote;
  private final ShowRemote showRemote;

  RemoteServices(SavedDataNetworks networks) {
    applyRemotes = new ApplyRemotes(networks);
    linkRemote = new LinkRemote(networks);
    showRemote = new ShowRemote(networks);
  }

  public ApplyRemotes apply() {
    return applyRemotes;
  }

  public LinkRemote link() {
    return linkRemote;
  }

  public ShowRemote show() {
    return showRemote;
  }
}
