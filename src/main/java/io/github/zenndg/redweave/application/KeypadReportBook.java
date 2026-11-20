package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.KeypadReports;
import io.github.zenndg.redweave.domain.network.PlayerId;

/** Keeps, in the save of the server, the summary of each offline owner (answer 903). */
public interface KeypadReportBook {
  KeypadReports reportsOf(PlayerId owner);

  void keep(PlayerId owner, KeypadReports reports);
}
