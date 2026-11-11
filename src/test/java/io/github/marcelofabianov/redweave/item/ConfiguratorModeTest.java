package io.github.marcelofabianov.redweave.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The modes of the Configurator and their order: configurador.md (answers 143, 173 and 192). */
class ConfiguratorModeTest {
  private static final String OPEN = "open";
  private static final String COPY = "copy";
  private static final String WRENCH = "wrench";
  private static final String UNKNOWN = "label";
  private static final int SCROLL_DOWN = -1;
  private static final int SCROLL_UP = 1;

  @Test
  void theModesGoInTheOrderOfTheShiftScroll() {
    assertEquals(ConfiguratorMode.COPY, ConfiguratorMode.OPEN.next());
    assertEquals(ConfiguratorMode.WRENCH, ConfiguratorMode.COPY.next());
    assertEquals(ConfiguratorMode.OPEN, ConfiguratorMode.WRENCH.next());
  }

  @Test
  void theModesGoBackInTheOtherDirection() {
    assertEquals(ConfiguratorMode.WRENCH, ConfiguratorMode.OPEN.previous());
    assertEquals(ConfiguratorMode.OPEN, ConfiguratorMode.COPY.previous());
    assertEquals(ConfiguratorMode.COPY, ConfiguratorMode.WRENCH.previous());
  }

  /** Like the hotbar: scrolling down goes forward, scrolling up goes back (answer 192). */
  @Test
  void theScrollDirectionChoosesTheStep() {
    assertEquals(ConfiguratorMode.COPY, ConfiguratorMode.OPEN.scrolled(SCROLL_DOWN));
    assertEquals(ConfiguratorMode.WRENCH, ConfiguratorMode.OPEN.scrolled(SCROLL_UP));
  }

  @Test
  void theSavedNamesAreTheContractOfTheComponent() {
    assertEquals(OPEN, ConfiguratorMode.OPEN.serializedName());
    assertEquals(COPY, ConfiguratorMode.COPY.serializedName());
    assertEquals(WRENCH, ConfiguratorMode.WRENCH.serializedName());
  }

  @Test
  void everyNameComesBackAsTheSameMode() {
    Arrays.stream(ConfiguratorMode.values())
        .forEach(mode ->
            assertEquals(Optional.of(mode), ConfiguratorMode.byName(mode.serializedName())));
  }

  @Test
  void anUnknownNameIsRefused() {
    assertTrue(ConfiguratorMode.byName(UNKNOWN).isEmpty());
  }

  @Test
  void aNewConfiguratorStartsOpen() {
    assertEquals(ConfiguratorMode.OPEN, ConfiguratorMode.FIRST);
  }
}
