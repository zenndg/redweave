package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ModuleSighting;
import java.util.ArrayList;
import java.util.List;

/**
 * What the loaded Signal Modules write into channels this tick. Each module adds its own while its
 * block entity ticks, and the end of the tick hands them all to the networks at once, so a module
 * in an unloaded chunk is simply not there (answers 386 and 387).
 */
public final class ModuleSightings {
  private final List<ModuleSighting> sightings = new ArrayList<>();

  public void add(ModuleSighting sighting) {
    sightings.add(sighting);
  }

  /** The sightings of this tick, which start empty again for the next one. */
  public List<ModuleSighting> drain() {
    List<ModuleSighting> drained = List.copyOf(sightings);
    sightings.clear();
    return drained;
  }
}
