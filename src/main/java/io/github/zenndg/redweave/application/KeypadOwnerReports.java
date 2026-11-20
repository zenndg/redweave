package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.KeypadReport;
import io.github.zenndg.redweave.domain.module.KeypadReports;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.List;

/**
 * The misses an offline owner hears about when joining: kept by owner in the save of the server,
 * and delivered once (answers 666, 903 and 911). The caller decides whether the owner is online.
 */
public final class KeypadOwnerReports {
  private final KeypadReportBook book;

  public KeypadOwnerReports(KeypadReportBook book) {
    this.book = book;
  }

  public void missed(PlayerId owner, ModuleId keypad, String name, PlayerId player, int cap) {
    book.keep(owner, book.reportsOf(owner).missed(keypad, name, player, cap));
  }

  /** The summary of the owner, which is then cleared. */
  public List<KeypadReport> take(PlayerId owner) {
    List<KeypadReport> reports = book.reportsOf(owner).reports();
    if (!reports.isEmpty()) {
      book.keep(owner, KeypadReports.NONE);
    }
    return reports;
  }
}
