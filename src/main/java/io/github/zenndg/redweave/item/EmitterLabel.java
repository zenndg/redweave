package io.github.zenndg.redweave.item;

/**
 * The name of the Signal Emitter a Signal Remote is linked to, and whether its click presses it or
 * turns it on and off, for the tooltip and the action bar: the client does not know the emitter, so
 * the server writes them on the item. They never go to the saves (answer 789).
 */
public record EmitterLabel(String name, boolean presses) {}
