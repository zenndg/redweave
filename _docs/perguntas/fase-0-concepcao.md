# Fase 0: concepção

Perguntas respondidas antes do código, ao definir o que o Redweave é. A mais nova fica no topo de cada tabela.

## Pendentes

As recomendações abaixo só valem depois da resposta do desenvolvedor.

| # | Pergunta | Opções | Recomendação |
|---|---|---|---|

## Respondidas

| # | Pergunta | Resposta |
|---|---|---|
| 16 | Qual é a fonte da verdade do board: o GitHub Projects ou o board do claude.ai? | GitHub Projects, com as issues nas milestones. O board do claude.ai é aposentado |
| 15 | O controle remoto do Redweave se chama Signal Remote, para não confundir com o Cubit Remote no mesmo pack? | Sim: Signal Remote, com o ID `signal_remote` |
| 14 | Em que idioma ficam issues, PRs e commits? | Português, como no Cubit. Código, IDs e o `README.md` ficam em inglês |
| 13 | O que acontece com um canal quando o chunk de um nó de entrada descarrega? | O canal mantém o último valor lido pelo nó, como a redstone do jogo, que congela num chunk descarregado. A Tela do Controlador marca o nó como fora de alcance. O estado passou a se chamar `Unloaded` (pergunta 29) |
| 12 | Onde fica o projeto? | Num repositório privado no GitHub, `marcelofabianov/redweave`, com a gestão do board lá |
| 11 | O Redweave adota o processo do Cubit? | Sim: regras do `AGENTS.md`, pilares, perguntas numeradas, histórico nos documentos, Gitflow, gates e GameTests |
| 10 | O que o Redweave aproveita do Cubit? | Processo, documentação, gates, GameTests com teste sem Jade, teste de paridade das traduções, `domain/README.md`, value objects em `record`, números em config por seções, compat isolado e a linguagem visual das telas |
| 9 | Qual é o nome do mod? | Redweave |
| 8 | Qual protocolo usa a ponte com o mundo real, na Fase 5? | MQTT |
| 7 | Qual é a licença do código? | LGPL-3.0. Trechos reaproveitados de mods MIT mantêm o aviso de copyright original |
| 6 | Quais integrações com outros mods entram primeiro? | Jade e The One Probe. As outras ficam para a Fase 4 |
| 5 | O fio físico de redstone entra no MVP? | Não. Entra na Fase 4, junto com o multipart |
| 4 | Como tratar o multipart? | Adiar para a Fase 4. O MVP usa nós e portas de bloco inteiro |
| 3 | Qual versão do Minecraft o mod mira? | 1.21.1 NeoForge e, depois, 26.x. O core sem classes do jogo facilita o port |
| 2 | Em que linguagem o mod é escrito? | Java 21 |
| 1 | O Redweave transporta só sinal ou também itens, fluidos e energia? | Só sinal de redstone. Transporte fica com os mods de logística |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 15:39 | Aponta a pergunta 29 na resposta 13. |
| Marcelo Fabiano | 2026-11-09 10:34 | Responde as perguntas 13 a 16. |
| Marcelo Fabiano | 2026-11-09 10:05 | Registra as perguntas da concepção. |
