# Ferramentas de build

As versões ficam em `gradle.properties`, exceto o Gradle, fixado em `gradle/wrapper/gradle-wrapper.properties`. Ali também fica o `distributionSha256Sum` publicado em `services.gradle.org`, para o wrapper recusar uma distribuição alterada. Ao atualizar o Gradle, troque os dois juntos. As versões são as mesmas do Cubit (pergunta 17), e cada ferramenta entra com a issue que a usa. Adaptado do Cubit.

## Comandos

Por enquanto, os comandos são os do Gradle. O `Makefile` entra com os gates, na issue #3.

| Comando | O que faz |
|---|---|
| `./gradlew build` | Compila e gera o jar em `build/libs/` |
| `./gradlew runClient` | Abre o jogo com o mod |
| `./gradlew runServer` | Inicia um servidor dedicado com o mod. Na primeira vez, exige aceitar o EULA em `run/eula.txt` |

O primeiro build leva alguns minutos, porque baixa o JDK e prepara o Minecraft.

## Ferramentas

| Ferramenta | Versão | O que faz | Por que foi escolhida |
|---|---|---|---|
| Gradle (wrapper) | 9.2.1 | Executa o build, sem precisar instalar nada | Vem no MDK oficial do NeoForge. O `gradle-wrapper.jar` foi conferido contra o SHA-256 oficial |
| ModDevGradle | 2.0.148 | Integra o Gradle ao NeoForge: prepara o Minecraft, as execuções e o jar | É o plugin do MDK oficial do 1.21.1 |
| NeoForge | 21.1.265 | O loader do mod | A versão do Iron Meridian |
| Parchment | 2024.11.17 | Dá nomes legíveis aos parâmetros do código do Minecraft | Vem no MDK e deixa o código do jogo mais fácil de ler |
| JDK 21 Adoptium | 21 | Compila o mod e o Minecraft | O foojay-resolver o baixa sozinho, porque o Java do sistema pode não ter o pacote de desenvolvimento |

## Metadados do mod

O `src/main/templates/META-INF/neoforge.mods.toml` recebe as propriedades do `gradle.properties` na tarefa `generateModMetadata`. Nunca escreva uma versão ou o ID direto nele. A licença é `LGPL-3.0-only` (pergunta 20), e o jar leva o `LICENSE` e o `COPYING`, porque a LGPL-3.0 complementa a GPL-3.0.

## Quando algo reprova

- **`Verification of Gradle distribution failed`:** o arquivo baixado não bate com o `distributionSha256Sum`. Não apague o hash: confira a versão e o valor publicado, porque o download pode estar alterado.
- **`createMinecraftArtifacts`:** a preparação do Minecraft falhou. Rode com `--info` para ler o erro.
- **`release version 21 not supported`:** o Gradle usou um Java sem o pacote de desenvolvimento. Confira se `gradle/gradle-daemon-jvm.properties` existe e rode `./gradlew --stop`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 11:58 | Documenta o Gradle, o ModDevGradle, o NeoForge, o Parchment e o JDK. |
