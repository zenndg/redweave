package io.github.zenndg.redweave.domain.module;

/**
 * What a Signal Timer is doing: stopped by its owner, running its cycle, or paused while its
 * `Pause` input is on (answer 380).
 */
public enum TimerState {
  STOPPED,
  RUNNING,
  PAUSED
}
