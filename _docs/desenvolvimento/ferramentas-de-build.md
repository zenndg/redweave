# Ferramentas de build

As versões ficam em `gradle.properties`, exceto o Gradle, fixado em `gradle/wrapper/gradle-wrapper.properties`. Ali também fica o `distributionSha256Sum` publicado em `services.gradle.org`, para o wrapper recusar uma distribuição alterada. Ao atualizar o Gradle, troque os dois juntos. As versões são as mesmas do Cubit (pergunta 17), e cada ferramenta entra com a issue que a usa. Adaptado do Cubit.

## Comandos

Os comandos ficam no `Makefile`. Rode `make` para ver a lista. Receita com mais de 3 linhas vira um script `.sh` em `scripts/`, e o `Makefile` só o chama (regra 35 do `AGENTS.md`).

| Comando | O que faz |
|---|---|
| `make setup` | Instala as ferramentas de desenvolvimento e ativa os hooks ([`hooks-do-git.md`](hooks-do-git.md)) |
| `make check` | Roda todos os gates: build, testes, arquitetura, cobertura, formatação e análise estática |
| `make test` | Roda os testes de unidade e de arquitetura |
| `make coverage` | Confere a cobertura de `domain/` e mostra onde está o relatório |
| `make format` | Formata o código Java |
| `make format-check` | Confere a formatação sem alterar arquivos |
| `make build` | Roda os gates e gera o jar em `build/libs/` |
| `make clean` | Remove a saída do build |
| `make client` | Abre o jogo com o mod |
| `make server` | Inicia um servidor dedicado com o mod. Na primeira vez, exige aceitar o EULA em `run/eula.txt` |
| `make stop` | Para os daemons do Gradle |

O primeiro build leva alguns minutos, porque baixa o JDK e prepara o Minecraft.

**Cobertura:** enquanto `domain/` não tiver classes, o `jacocoTestCoverageVerification` roda e passa, porque não há o que medir. Ele passa a reprovar quando o domínio chega, na issue #9.

**Arquitetura:** as regras ficam em `src/test/java/.../architecture/DomainBoundaryTest.java`. As de `domain/` e `application/` aceitam pacote vazio, então valem desde já e passam a reprovar com a primeira classe. Uma constante `static final` de tipo primitivo ou `String` é copiada pelo compilador para quem a usa, então o ArchUnit não a vê como dependência. Isso não é uma brecha, porque nenhuma classe é carregada.

## Ferramentas

| Ferramenta | Versão | O que faz | Por que foi escolhida |
|---|---|---|---|
| Gradle (wrapper) | 9.2.1 | Executa o build, sem precisar instalar nada | Vem no MDK oficial do NeoForge. O `gradle-wrapper.jar` foi conferido contra o SHA-256 oficial |
| ModDevGradle | 2.0.148 | Integra o Gradle ao NeoForge: prepara o Minecraft, as execuções e o jar | É o plugin do MDK oficial do 1.21.1 |
| NeoForge | 21.1.265 | O loader do mod | A versão do Iron Meridian |
| Parchment | 2024.11.17 | Dá nomes legíveis aos parâmetros do código do Minecraft | Vem no MDK e deixa o código do jogo mais fácil de ler |
| JDK 21 Adoptium | 21 | Compila o mod e o Minecraft | O foojay-resolver o baixa sozinho, porque o Java do sistema pode não ter o pacote de desenvolvimento |
| JUnit | 6.1.3 | Roda os testes do domínio sem subir o jogo | É o padrão em Java e está na regra 28 do `AGENTS.md` |
| ArchUnit | 1.5.1 | Isola `domain/` e `application/`, separa o código comum do cliente, proíbe `get*` no domínio e deixa só `compat.jade` e `compat.top` importarem o Jade e o The One Probe | Garante as regras 10, 11, 19 e 20 do `AGENTS.md` e a pergunta 21 no build |
| JaCoCo | 0.8.16 | Mede a cobertura de `domain/`, com mínimo de 85% de instruções e de ramos | Garante a regra 30 do `AGENTS.md` |
| Spotless com google-java-format | 8.9.0 e 2.0.0 | Formata o código Java, com recuo de 2 espaços, e remove imports não usados | É o padrão mais comum em Java, sem opções para discutir |
| Checkstyle | 14.4.0 | Confere as regras de forma do código | Reprova no build o que antes dependia da revisão |

As bibliotecas de teste e o JaCoCo vêm só do Maven Central, num `exclusiveContent`, para o build não depender do Maven da NeoForge para elas.

## Regras do Checkstyle

Ficam em `config/checkstyle/checkstyle.xml`, com as mensagens em inglês, qualquer que seja o idioma da máquina. Valem para o código e para os testes.

| Regra | Limite | Origem |
|---|---|---|
| Tamanho de função | 30 linhas | Regra 23 do `AGENTS.md` |
| Complexidade ciclomática | 10 | Pilar Clareza |
| Tamanho de arquivo | 400 linhas | Pilar Clareza |
| Número mágico | Só -1, 0, 1 e 2 são livres | Regra 22 do `AGENTS.md` |
| String literal | Só em constante `static final` | Regra 22 do `AGENTS.md` |
| `TODO` | Só com issue: `TODO(#12)` | Regra 25 do `AGENTS.md` |
| Imports | Sem `*` e sem import não usado | Pilar Clareza |

## Metadados do mod

O `src/main/templates/META-INF/neoforge.mods.toml` recebe as propriedades do `gradle.properties` na tarefa `generateModMetadata`. Nunca escreva uma versão ou o ID direto nele. A licença é `LGPL-3.0-only` (pergunta 20), e o jar leva o `LICENSE` e o `COPYING`, porque a LGPL-3.0 complementa a GPL-3.0.

## Quando algo reprova

- **`DomainBoundaryTest`:** o nome da regra que reprovou diz o motivo.
  - `theDomainOnlyDependsOnItselfAndJava`: uma classe de `domain/` importou o jogo ou outro pacote do mod. Mova a regra para fora do domínio ou inverta a dependência com uma interface.
  - `theApplicationOnlyDependsOnTheDomainAndJava`: um caso de uso importou o jogo ou um adapter. Declare uma porta em `application/` e implemente-a fora dela.
  - `commonCodeDoesNotUseTheClient`: código comum citou uma classe de cliente. Mova a referência para `client/`.
  - `onlyThePluginUsesJade` ou `onlyThePluginUsesTheOneProbe`: uma classe fora de `compat.jade` ou de `compat.top` importou o mod opcional. Mova a referência para o plugin, porque sem ele essa classe quebraria o mod.
  - `theDomainHasNoGetters`: troque `getStrength()` por `strength()`.
- **`jacocoTestCoverageVerification`:** faltam testes no domínio. Abra `build/reports/jacoco/test/html/index.html` para ver o que falta.
- **`spotlessJavaCheck`:** o código não está formatado. Rode `make format`.
- **`checkstyleMain` ou `checkstyleTest`:** uma regra de forma do código foi violada. Corrija a causa, sem suprimir a regra. O relatório fica em `build/reports/checkstyle/`.
- **`Verification of Gradle distribution failed`:** o arquivo baixado não bate com o `distributionSha256Sum`. Não apague o hash: confira a versão e o valor publicado, porque o download pode estar alterado.
- **`createMinecraftArtifacts`:** a preparação do Minecraft falhou. Rode com `--info` para ler o erro.
- **`release version 21 not supported`:** o Gradle usou um Java sem o pacote de desenvolvimento. Confira se `gradle/gradle-daemon-jvm.properties` existe e rode `make stop`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 13:52 | Adiciona o ArchUnit e as regras de fronteira. |
| Marcelo Fabiano | 2026-11-09 13:29 | Passa os comandos para o Makefile e documenta o JUnit, o JaCoCo, o Spotless e o Checkstyle. |
| Marcelo Fabiano | 2026-11-09 11:58 | Documenta o Gradle, o ModDevGradle, o NeoForge, o Parchment e o JDK. |
