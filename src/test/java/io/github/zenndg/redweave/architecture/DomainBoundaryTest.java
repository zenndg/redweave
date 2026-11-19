package io.github.zenndg.redweave.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * The rules about package boundaries. The rules about the domain and the application allow empty
 * packages, so they hold from the first build and bite as soon as the first class arrives.
 */
@AnalyzeClasses(
    packages = DomainBoundaryTest.ROOT_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class DomainBoundaryTest {
  static final String ROOT_PACKAGE = "io.github.zenndg.redweave";
  private static final String DOMAIN = "..redweave.domain..";
  private static final String APPLICATION = "..redweave.application..";
  private static final String JAVA = "java..";
  private static final String MOD_CLIENT = "..redweave.client..";
  private static final String GAME_CLIENT = "net.minecraft.client..";
  private static final String GETTER = "get[A-Z].*";
  private static final String JADE_PLUGIN = "..redweave.compat.jade..";
  private static final String JADE = "snownee.jade..";
  private static final String TOP_PLUGIN = "..redweave.compat.top..";
  private static final String TOP = "mcjty.theoneprobe..";

  /**
   * AGENTS.md, rule 10, and answer 21: this keeps out the game and also the other packages of the
   * mod.
   */
  @ArchTest
  static final ArchRule theDomainOnlyDependsOnItselfAndJava =
      classes()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(DOMAIN, JAVA)
          .allowEmptyShould(true);

  /**
   * AGENTS.md, rule 10, and answer 21: the use cases reach the game and the adapters only through
   * the ports they declare.
   */
  @ArchTest
  static final ArchRule theApplicationOnlyDependsOnTheDomainAndJava =
      classes()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(APPLICATION, DOMAIN, JAVA)
          .allowEmptyShould(true);

  /**
   * AGENTS.md, rule 19. The GameTests only find a client class that the server happens to load, and
   * this finds any of them.
   */
  @ArchTest
  static final ArchRule commonCodeDoesNotUseTheClient =
      noClasses()
          .that()
          .resideOutsideOfPackage(MOD_CLIENT)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(MOD_CLIENT, GAME_CLIENT);

  /**
   * AGENTS.md, rule 20: only the plugin imports Jade, which Jade finds by itself, so the mod never
   * loads a class of Jade and opens without it.
   */
  @ArchTest
  static final ArchRule onlyThePluginUsesJade =
      noClasses()
          .that()
          .resideOutsideOfPackage(JADE_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(JADE);

  /** AGENTS.md, rule 20: the same isolation for The One Probe. */
  @ArchTest
  static final ArchRule onlyThePluginUsesTheOneProbe =
      noClasses()
          .that()
          .resideOutsideOfPackage(TOP_PLUGIN)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(TOP);

  /** AGENTS.md, rule 11: {@code strength()}, not {@code getStrength()}. */
  @ArchTest
  static final ArchRule theDomainHasNoGetters =
      noMethods()
          .that()
          .areDeclaredInClassesThat()
          .resideInAPackage(DOMAIN)
          .should()
          .haveNameMatching(GETTER)
          .allowEmptyShould(true);
}
