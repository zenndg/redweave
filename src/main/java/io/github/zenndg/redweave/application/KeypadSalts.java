package io.github.zenndg.redweave.application;

/** Gives a new random salt for the code of a Signal Keypad, in hex (answer 897). */
@FunctionalInterface
public interface KeypadSalts {
  String next();
}
