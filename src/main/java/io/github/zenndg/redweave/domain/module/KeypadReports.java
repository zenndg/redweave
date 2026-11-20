package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The summary of an offline owner, oldest keypad first, up to the cap of the config: past it, the
 * oldest keypad leaves (answers 903 and 911).
 */
public record KeypadReports(List<KeypadReport> reports) {
  /** An owner with no miss to hear about. */
  public static final KeypadReports NONE = new KeypadReports(List.of());

  public KeypadReports {
    reports = List.copyOf(Objects.requireNonNull(reports));
  }

  /** One more miss on the keypad, which becomes the newest, by the player. */
  public KeypadReports missed(ModuleId keypad, String name, PlayerId player, int cap) {
    List<KeypadReport> next = new ArrayList<>(reports);
    int before =
        next.stream()
            .filter(report -> report.keypad().equals(keypad))
            .mapToInt(KeypadReport::misses)
            .sum();
    next.removeIf(report -> report.keypad().equals(keypad));
    next.add(new KeypadReport(keypad, name, player, before + 1));
    while (next.size() > cap) {
      next.removeFirst();
    }
    return new KeypadReports(next);
  }
}
