# Fase 1 · Sem fio

Perguntas desta fase. A mais nova fica no topo de cada tabela.

## Pendentes

| # | Pergunta | Opções | Recomendação |
|---|---|---|---|

## Respondidas

| # | Pergunta | Resposta |
|---|---|---|
| 30 | Quem pode quebrar o Network Controller de outro player? | Só o dono. Para os outros, o bloco não quebra no survival, e explosões não o destroem. No criativo, quebra normalmente |
| 29 | O nó num chunk descarregado e o nó fora do raio precisam de nomes diferentes. Como ficam? | `Unloaded` (Descarregado), em âmbar, mantém o último valor. `Out of range` (Fora de alcance), em vermelho, não conta e vale 0. Ajusta o nome usado na resposta 13, sem mudar o comportamento dela |
| 28 | Um operador do servidor pode alterar ou apagar a rede de outro player? | Não. Só o dono, sem exceção. Detalha a pergunta 22 |
| 27 | Como o Signal Remote entra na rede? | Como uma fonte de entrada sem posição no mundo, com ID próprio, que entra no sinal mais forte do canal. Volta a 0 ao sair do inventário ou ficar fora do alcance, para o canal não ficar preso ligado. Os detalhes do item ficam na issue do Remote |
| 26 | O canal tem só a cor, ou o player pode dar um nome a ele? | Cor fixa e nome opcional. O nome padrão é o da cor, como `Red`, e o dono pode renomeá-lo na aba `Signals`, com o limite de caracteres no config |
| 25 | Em qual rede e em qual canal entra um Signal Node recém-colocado? | Na rede atual do player: a última que ele criou ou abriu no controlador, guardada no servidor. Entra com 1 slot de entrada na face em que foi grudado, no primeiro canal livre. Sem rede atual, fica `No network`. O Configurador troca a rede depois |
| 24 | O que acontece com a rede quando o Network Controller é quebrado? | A rede fica salva no mundo, no servidor, e não no bloco, então funciona com o chunk do controlador descarregado. Quebrado, o item guarda o ID da rede num data component. Sem o controlador, a rede fica `Offline`: os canais vão a 0 e os nós mostram `No network`. Recolocado, ela volta igual |
| 23 | Até onde um nó pode estar do Network Controller? | Na mesma dimensão, dentro de um raio em blocos a partir do controlador, no config, com valor inicial de 128. O valor 0 significa ilimitado. Fora do raio, o nó não conta para o canal. Outra dimensão nunca alcança |
| 22 | Quem pode fazer o quê numa rede? | Só o dono altera a rede: liga nós, configura slots, renomeia a rede e os canais e liga o Signal Remote. Os demais players só veem as telas, em modo de leitura |
| 21 | Até onde vai a fronteira de `domain/` e `application/` no ArchUnit? | Estrita, como no Cubit: `domain/` só depende de si e do Java, e `application/` só de si, de `domain/` e do Java. Um caso de uso usa um adapter do mod só por uma porta. Detalha a regra 10 do `AGENTS.md` |
| 20 | Qual identificador SPDX de licença vai no `neoforge.mods.toml` e nas páginas do mod? | `LGPL-3.0-only`: só a versão 3 da LGPL. Detalha a pergunta 7 |
| 19 | Quais hooks de git entram? | Todos os do Cubit, adaptados: branch, histórico dos `.md` de `_docs/`, formatação, gitleaks, Conventional Commits e atribuição a IA no commit; `check`, GameTests e datagen no push. O gitleaks fica fixado com checksum em `.tools/` |
| 18 | O The One Probe não está no Iron Meridian. Como fica? | Continua opcional e na Fase 1. A versão é a última estável para 1.21.1, decidida quando a issue #23 começar. O mod abre sem ele, conferido pela GameTest da issue #5 |
| 17 | Quais versões de ferramentas a base do projeto fixa? | As mesmas do Cubit: Gradle 9.2.1 com checksum, ModDevGradle 2.0.148, NeoForge 21.1.265 (a do Iron Meridian), Parchment 2024.11.17, JUnit 6.1.3, ArchUnit 1.5.1, JaCoCo 0.8.16, Spotless 8.9.0 com google-java-format 2.0.0 e Checkstyle 14.4.0. Cada uma entra com a issue que a usa |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 15:39 | Responde as perguntas 22 a 30. |
| Marcelo Fabiano | 2026-11-09 13:56 | Responde a pergunta 21. |
| Marcelo Fabiano | 2026-11-09 11:50 | Responde as perguntas 17 a 20. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
