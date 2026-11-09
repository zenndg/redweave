# Fase 1 · Sem fio

Perguntas desta fase. A mais nova fica no topo de cada tabela.

## Pendentes

| # | Pergunta | Opções | Recomendação |
|---|---|---|---|

## Respondidas

| # | Pergunta | Resposta |
|---|---|---|
| 21 | Até onde vai a fronteira de `domain/` e `application/` no ArchUnit? | Estrita, como no Cubit: `domain/` só depende de si e do Java, e `application/` só de si, de `domain/` e do Java. Um caso de uso usa um adapter do mod só por uma porta. Detalha a regra 10 do `AGENTS.md` |
| 20 | Qual identificador SPDX de licença vai no `neoforge.mods.toml` e nas páginas do mod? | `LGPL-3.0-only`: só a versão 3 da LGPL. Detalha a pergunta 7 |
| 19 | Quais hooks de git entram? | Todos os do Cubit, adaptados: branch, histórico dos `.md` de `_docs/`, formatação, gitleaks, Conventional Commits e atribuição a IA no commit; `check`, GameTests e datagen no push. O gitleaks fica fixado com checksum em `.tools/` |
| 18 | O The One Probe não está no Iron Meridian. Como fica? | Continua opcional e na Fase 1. A versão é a última estável para 1.21.1, decidida quando a issue #23 começar. O mod abre sem ele, conferido pela GameTest da issue #5 |
| 17 | Quais versões de ferramentas a base do projeto fixa? | As mesmas do Cubit: Gradle 9.2.1 com checksum, ModDevGradle 2.0.148, NeoForge 21.1.265 (a do Iron Meridian), Parchment 2024.11.17, JUnit 6.1.3, ArchUnit 1.5.1, JaCoCo 0.8.16, Spotless 8.9.0 com google-java-format 2.0.0 e Checkstyle 14.4.0. Cada uma entra com a issue que a usa |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 13:56 | Responde a pergunta 21. |
| Marcelo Fabiano | 2026-11-09 11:50 | Responde as perguntas 17 a 20. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
