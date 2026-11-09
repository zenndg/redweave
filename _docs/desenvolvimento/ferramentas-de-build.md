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
| `make data` | Roda a geração de dados |
| `make check-generated` | Roda a geração de dados e reprova se `src/generated` mudar |
| `make gametest` | Roda os GameTests num servidor sem interface |
| `make stop` | Para os daemons do Gradle |

O primeiro build leva alguns minutos, porque baixa o JDK e prepara o Minecraft.

**Cobertura:** enquanto `domain/` não tiver classes, o `jacocoTestCoverageVerification` roda e passa, porque não há o que medir. Ele passa a reprovar quando o domínio chega, na issue #9.

**`make gametest`:** o jogo termina com sucesso mesmo quando o mod falha ao carregar, então `scripts/development/run-gametests.sh` só aprova se o log trouxer `All N required tests passed`. Foi conferido que o comando falha com uma GameTest reprovada e com uma classe de cliente no código comum. O log fica em `build/gametest.log`. O servidor roda em `run/gametest/`, separado do `run/` do `make client`, para um config editado à mão não mudar o resultado.

**GameTests:** ficam no pacote `gametest` e cobrem o que está fora de `domain/` (regras 28 e 29 do `AGENTS.md`). As estruturas ficam em `data/redweave/structure/`, e a `empty.nbt` tem 3×3×3 blocos de ar. Cada classe usa `@GameTestHolder(RedweaveMod.MOD_ID)` e `@PrefixGameTestTemplate(false)`, para o `template` citar a estrutura só pelo nome. A `WithoutOptionalModsGameTests` reprova se o Jade ou o The One Probe estiverem carregados, o que prova que o mod abre sem eles num servidor dedicado (regras 20 e 39 e pergunta 18).

**`make check-generated`:** a geração apaga `src/generated/resources/` antes de escrever, então um arquivo gerado editado à mão e commitado volta ao original e aparece como diferença. Por enquanto, a geração não produz nada, porque o conteúdo chega com as próximas issues.

**Traduções:** os arquivos `en_us.json` e `pt_br.json` são gerados pelo `make data`, a partir de `datagen/EnglishTexts.java` e `datagen/PortugueseTexts.java`, e ficam em `src/generated/resources/assets/redweave/lang/`. O inglês é a base (regra 1 do `AGENTS.md`). Para adicionar um texto, crie a chave nas duas classes, rode `make data` e commite os dois JSONs. O `LanguageFilesTest` lê os arquivos gerados e reprova se o português não tiver as mesmas chaves do inglês, ou se a descrição na lista de mods divergir da do `neoforge.mods.toml`. Como ele lê o que foi gerado, rode `make data` antes de `make test` depois de mudar um texto. O `make check-generated` pega quem esquecer.

**Conjunto de fontes `clientRun`:** o `make client` e o `make data` o usam, e ele herda o classpath do mod. Dependências só de cliente, como o Jade, entram nele com `clientRunRuntimeOnly`, porque o jogo procura mods no classpath da execução. `make server` e `make gametest` não o carregam, então provam que um servidor dedicado abre sem elas.

**Jar publicado:** a tarefa `jar` exclui os pacotes `gametest` e `datagen` e as estruturas de `data/redweave/structure/`, que só servem ao desenvolvimento. As execuções de desenvolvimento leem as classes direto, sem passar pelo jar.

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
| String literal | Só em constante `static final`, na mesma linha da declaração. Texto longo vira várias constantes curtas, somadas noutra constante, porque a regra confere linha por linha | Regra 22 do `AGENTS.md` |
| `TODO` | Só com issue: `TODO(#12)` | Regra 25 do `AGENTS.md` |
| Imports | Sem `*` e sem import não usado | Pilar Clareza |

## Metadados do mod

O `src/main/templates/META-INF/neoforge.mods.toml` recebe as propriedades do `gradle.properties` na tarefa `generateModMetadata`. Nunca escreva uma versão ou o ID direto nele. A licença é `LGPL-3.0-only` (pergunta 20), e o jar leva o `LICENSE` e o `COPYING`, porque a LGPL-3.0 complementa a GPL-3.0.

## Quando algo reprova

- **`make gametest`:** uma GameTest reprovou, e o nome dela e o motivo aparecem em `build/gametest.log`, antes de `required tests failed`. Se `GAME TESTS COMPLETE` não aparecer, o mod não carregou. Com `invalid dist` no log, código comum referenciou uma classe de cliente: mova a referência para `client/`.
- **`LanguageFilesTest`:**
  - `portugueseHasTheSameKeysAsEnglish`: uma chave existe só num idioma. Adicione-a na classe de textos que falta e rode `make data`.
  - `theModListDescriptionMatchesTheModsToml`: a descrição em `EnglishTexts` e a do `neoforge.mods.toml` divergem. Iguale as duas e rode `make data`.
  - `NoSuchFileException`: os arquivos ainda não foram gerados. Rode `make data`.
- **`theModOpensWithoutTheOptionalMods`:** o Jade ou o The One Probe entrou no servidor. Confira se ele continua só em `clientRunRuntimeOnly`.
- **`make check-generated`:** se o `datagen/` mudou, rode `make data` e adicione `src/generated` ao commit. Se um arquivo gerado foi editado à mão, desfaça com `git restore src/generated` e leve a mudança para o `datagen/`.
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
| Marcelo Fabiano | 2026-11-09 14:17 | Adiciona as traduções geradas e o teste de paridade. |
| Marcelo Fabiano | 2026-11-09 14:08 | Adiciona os GameTests, a geração de dados, o conjunto de fontes `clientRun` e o jar publicado. |
| Marcelo Fabiano | 2026-11-09 13:52 | Adiciona o ArchUnit e as regras de fronteira. |
| Marcelo Fabiano | 2026-11-09 13:29 | Passa os comandos para o Makefile e documenta o JUnit, o JaCoCo, o Spotless e o Checkstyle. |
| Marcelo Fabiano | 2026-11-09 11:58 | Documenta o Gradle, o ModDevGradle, o NeoForge, o Parchment e o JDK. |
