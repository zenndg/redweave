package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.SignalChange;
import io.github.marcelofabianov.redweave.domain.network.SignalLog;
import io.github.marcelofabianov.redweave.domain.network.SlotOutputs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The log of each network and what its outputs last emitted, only in memory: everything starts
 * empty when the server starts, and nothing goes to the saves (answers 147 and 242).
 */
public final class SignalLogs {
  private final LogLimits limits;
  private final Map<NetworkId, SignalLog> logs = new HashMap<>();
  private final Map<NetworkId, SlotOutputs> outputs = new HashMap<>();

  public SignalLogs(LogLimits limits) {
    this.limits = limits;
  }

  /** The lines of a network newer than a sequence, newest first; none for an unknown network. */
  public List<SignalLog.Line> after(NetworkId network, long sequence) {
    SignalLog log = logs.get(network);
    return log == null ? List.of() : log.after(sequence);
  }

  /** How many lines a log keeps, as the config says now (answer 147). */
  public int size() {
    return limits.logSize();
  }

  void record(NetworkId network, SignalChange change) {
    logs.computeIfAbsent(network, id -> new SignalLog(limits.logSize())).record(change);
  }

  SlotOutputs outputsOf(NetworkId network) {
    return outputs.computeIfAbsent(network, id -> new SlotOutputs());
  }
}
