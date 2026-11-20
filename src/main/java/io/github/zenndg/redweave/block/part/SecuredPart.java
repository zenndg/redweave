package io.github.zenndg.redweave.block.part;

import net.minecraft.core.Direction;

/**
 * A part that guards something, as the Signal Lock: nobody breaks it by hand in survival, not even
 * its owner, who takes it out with the `Wrench` of the Configurator, and no explosion takes it nor
 * its block of support (answer 641). In creative, anyone breaks it.
 */
public interface SecuredPart {
  /** The side of its space it rests on, toward its block of support. */
  Direction face();
}
