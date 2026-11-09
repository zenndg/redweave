# Fase 1 · Sem fio

Perguntas desta fase. A mais nova fica no topo de cada tabela.

## Pendentes

| # | Pergunta | Opções | Recomendação |
|---|---|---|---|

## Respondidas

| # | Pergunta | Resposta |
|---|---|---|
| 39 | Um nó com dois slots de saída na mesma face, em canais diferentes: que sinal o bloco recebe? | O mais forte entre as saídas da face, como dois fios de redstone no mesmo bloco. A mudança que o simulador devolve é por face do nó |
| 38 | Onde ficam a marca de pendente de avaliação e o último valor emitido por cada saída? | Na rede, e o `Simulator` é um serviço de domínio sem estado, que recebe a rede e devolve as mudanças. O último valor emitido não vai para o save: ao carregar, a rede começa pendente, e o primeiro tick reaplica todas as saídas |
| 37 | Onde fica a última leitura de cada entrada, que mantém o canal de um nó `Unloaded`? | Salva com a rede, no mundo. Depois de reiniciar o servidor, um nó num chunk descarregado continua contribuindo com o valor que tinha, como a redstone congelada do jogo. Detalha a pergunta 13 |
| 36 | Que forma tem o alcance do Network Controller? | Esfera: a distância em linha reta até o bloco do nó, comparada pelos quadrados, sem raiz nem arredondamento. Detalha a pergunta 23 |
| 35 | Qual nome uma rede recebe ao ser criada? | `Network` seguido dos 4 primeiros caracteres do ID, como `Network 3f2a`, em inglês. Quem chama monta o nome, e o domínio só valida. O dono renomeia depois |
| 34 | O que acontece com um nome de rede ou de canal vazio, ou maior que o limite do config? | Os espaços das pontas são removidos. Vazio ou acima do limite, a operação é recusada com `INVALID_NAME`, e o nome fica como estava. No canal, um nome vazio volta ao nome da cor, porque o nome do canal é opcional. A tela limita o campo ao mesmo tamanho |
| 33 | O agregado `Network` é mutável ou imutável? | Mutável e protegido: uma entidade cujas operações mudam o estado validando as invariantes. As consultas devolvem cópias imutáveis ou value objects, e os nós e os slots são value objects trocados inteiros |
| 32 | Como o agregado `Network` recusa um pedido esperado, como o de quem não é o dono? | Com um resultado tipado: cada operação devolve um `Outcome` (`ACCEPTED`, `NOT_OWNER`, `INVALID_NAME` e outros), e quem chama decide o que mostrar. Exceção fica só para erro de programação |
| 31 | Qual é a forma de `NetworkId` e `NodeId`, e quem os gera? | UUID nos dois, como o jogo faz com entidades e players. O domínio só recebe o ID: quem chama gera com `UUID.randomUUID()` por uma porta `IdGenerator`, com uma versão fixa nos testes |
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
| Marcelo Fabiano | 2026-11-09 16:57 | Responde as perguntas 38 e 39. |
| Marcelo Fabiano | 2026-11-09 16:39 | Responde as perguntas 36 e 37. |
| Marcelo Fabiano | 2026-11-09 16:27 | Responde as perguntas 32 a 35. |
| Marcelo Fabiano | 2026-11-09 16:10 | Responde a pergunta 31. |
| Marcelo Fabiano | 2026-11-09 15:39 | Responde as perguntas 22 a 30. |
| Marcelo Fabiano | 2026-11-09 13:56 | Responde a pergunta 21. |
| Marcelo Fabiano | 2026-11-09 11:50 | Responde as perguntas 17 a 20. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
