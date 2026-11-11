# Roteiro de teste manual

O que os testes automáticos não cobrem e um player precisa conferir no jogo: o visual, a tela e a sensação de uso. Os passos conferem o que o código entrega hoje. As perguntas 139 a 172 mudam os passos 2 e 19, que são reescritos junto com as issues que as implementam. O roteiro roda numa instância só com o mod e, antes da `main`, também numa do Iron Meridian (regras 30 e 31 do `AGENTS.md`). Cada passo diz o que fazer e o que se espera ver.

## Como preparar

1. `make client`, num mundo novo em criativo, com os comandos liberados. Monte tudo a uns 300 blocos do spawn, porque o jogo mantém os chunks do spawn sempre carregados, e isso atrapalha o passo 20 (pergunta 102).
2. Abra a aba `Redweave` do inventário criativo. Ela tem o Network Controller, o Signal Node e o Configurator (pergunta 69). Pegue um Configurator: ele começa no modo `Open`, que abre a Tela do Nó (pergunta 188).
3. Para o passo de outro player, abra um segundo cliente com outro nome, ou peça a alguém para entrar no mundo pela LAN.

## Fase 1: o primeiro circuito

Entregue com as issues #14, #15, #18 e #19 (pergunta 48).

| # | Faça | Espere ver |
|---|---|---|
| 1 | Coloque um Network Controller | A frente voltada para você e o núcleo do topo aceso (`linked`) |
| 2 | Clique no controlador com a mão vazia | `xxxxxxxx · owner: <você>` na action bar, com os 8 primeiros caracteres do ID |
| 3 | Ponha uma alavanca em cima de uma pedra e grude um Signal Node num lado da pedra | Uma placa fina de cobre, sem cor nos cantos, porque o nó nasce sem rede |
| 4 | Clique no nó com o Configurator no modo `Open` e clique na rede da lista | A escolha de rede, fiel a `tela-do-no-escolha-de-rede.png`, com a rede e `0 nodes`; depois do clique, a configuração, com `● Online` em verde, a rede à direita do nome do nó e o slot 1 como `Input`, e o primeiro canto colorido |
| 5 | Puxe a alavanca, com a tela aberta | O medidor sobe na hora até 15, e as barras brilham a partir de 12 |
| 6 | Grude um segundo nó numa lâmpada de redstone, abra a tela dele e escolha a rede | A rede no topo da lista, em cobre, como a última usada, e depois o slot 1 em `Input`, num canal livre |
| 7 | Troque o slot 1 para `Output` e escolha o mesmo canal do primeiro nó | O botão de cobre muda para `Output`, e a cor escolhida fica realçada |
| 8 | Puxe a alavanca algumas vezes | A lâmpada acende e apaga junto, e a lente do nó acende |
| 9 | No cubo da face, arraste o mouse e clique numa face | O bloco gira, a face sob o cursor fica em cobre, e o nome da face muda |
| 10 | Clique em `Free slot` e depois em `Clear slot` duas vezes | O slot vira uma entrada no próximo canal livre; o primeiro clique em `Clear slot` pede confirmação, e o segundo libera |
| 11 | Abra a tela com o segundo player | Tudo visível e ao vivo, mas os controles apagados e o cadeado com `Only the owner of ...` no rodapé |
| 12 | Com o segundo player, tente quebrar o controlador e um nó no survival | Nenhum dos dois quebra, e nada racha |
| 13 | Quebre o controlador com o dono, no survival | O item cai, a lâmpada apaga, e o nó, aberto com o Configurator, mostra `No network` em cinza |
| 14 | Recoloque o item do controlador a mais de 128 blocos dos nós | A rede volta, e os nós longe mostram a lente apagada com o anel tracejado vermelho, além de `Out of range` na tela |
| 15 | Quebre a pedra de um nó | O nó cai como item e sai da rede |
| 16 | Troque o idioma para Português (Brasil) | Todos os textos da tela e dos itens em português, sem nada passando da borda |

## Fase 1: as redes depois de reiniciar

Entregue com a issue #16 (perguntas 88 a 97). Comece com o circuito dos passos 1 a 8 montado, com a lâmpada acesa e um canal renomeado, se a tela já permitir.

| # | Faça | Espere ver |
|---|---|---|
| 17 | Saia para o menu e abra o mesmo mundo de novo | A lâmpada continua acesa, e a Tela do Nó mostra a mesma rede, os mesmos slots e o mesmo sinal |
| 18 | Puxe a alavanca | A lâmpada apaga e acende junto, como antes de sair |
| 19 | Grude um nó novo e abra a tela dele | A rede usada antes de sair no topo da lista, em cobre, porque a última rede usada também foi salva |
| 20 | Em Opções, ponha as distâncias de renderização e de simulação em 2. Monte o nó da alavanca a uns 100 blocos do controlador e da lâmpada, que ficam juntos, ligue a alavanca, vá até a lâmpada, saia e abra o mundo | A lâmpada acende assim que o mundo abre, com a última leitura do nó descarregado, e a Tela do Nó da alavanca mostra `Unloaded` em âmbar |
| 21 | Feche o jogo, apague `saves/<mundo>/data/redweave_networks.dat` e abra o mundo | Os controladores e os nós mostram `No network`, sem o jogo travar |
| 22 | Feche o jogo, troque o conteúdo de `redweave_networks.dat` por um texto qualquer e abra o mundo | O mundo abre sem redes, `redweave_networks.unreadable.backup.dat` aparece ao lado, e o `logs/latest.log` mostra `The Redweave save was kept in ...` (pergunta 97) |
| 23 | Feche o jogo, ponha o backup do passo 22 de volta como `redweave_networks.dat` e abra o mundo | O mesmo do passo 22, sem o jogo travar |

## Fase 1: o Configurator

Entregue com a issue #17 e refeito na #86 (perguntas 126 a 195). Comece com o circuito dos passos 1 a 8 montado.

| # | Faça | Espere ver |
|---|---|---|
| 24 | Pegue um Configurator novo e passe o mouse sobre ele | `Mode: Open` com `Open` em azul, `Click a node to open it.` e `Shift + scroll to change the mode.` em cinza |
| 25 | Agachado, gire a roda do mouse para baixo duas vezes e depois para cima uma vez | `Mode: Copy` em verde, `Mode: Wrench` em vermelho e de novo `Mode: Copy` na action bar; o slot da hotbar não muda |
| 26 | Clique no nó da lâmpada com a mão vazia | A tela não abre, e a action bar mostra `Use a Configurator to open this node.` |
| 27 | No modo `Copy`, clique no nó da lâmpada | `Copied 1 slots`, e o tooltip mostra `Copy: 1 slots` |
| 28 | Grude um nó num lado de outra lâmpada e dê Shift + clique nele | `Pasted 1 slots`, e a lâmpada nova acende e apaga junto com a alavanca |
| 29 | Shift + clique no ar | `Copy cleared`, e o tooltip perde a linha da cópia |
| 30 | No modo `Wrench`, clique no nó da lâmpada nova | `Node picked up`, o nó some e vai para o inventário, e a lâmpada apaga |
| 31 | Com o segundo player, no modo `Wrench`, clique num nó do dono; no modo `Copy`, copie um nó e cole noutro do dono | `Only the owner of ... can change this node.`, sem mudar nada |
| 32 | Clique no controlador com o Configurator | Nada acontece |
| 33 | Troque o idioma para Português (Brasil) | `Configurador`, `Modo: Abrir`, `Modo: Copiar`, `Modo: Chave` e todas as mensagens em português |

## Fase 1: a escolha de rede e o nome do nó

Entregue com a issue #87 (perguntas 139 a 208). Comece com o circuito dos passos 1 a 8 montado.

| # | Faça | Espere ver |
|---|---|---|
| 34 | Num mundo sem nenhum Network Controller, grude um nó e abra a tela dele | A lista vazia, com o ícone do controlador e `No network online. Place a Network Controller to create one.` |
| 35 | Com o segundo player, grude um nó; abra a tela dele com o dono | A tela só para leitura, com `No network`, sem lista |
| 36 | Coloque mais dois controladores e renomeie um deles, se a tela já permitir. Grude um nó e abra a tela | A última usada no topo, em cobre, e as outras em ordem alfabética, cada uma com a quantidade de nós |
| 37 | Na configuração, clique no nome do nó, digite `Lever`, aperte `E` e depois Enter | O campo aceita o `E` sem fechar a tela, e o nome vira `Lever` |
| 38 | Clique no nome de novo, apague tudo e aperte Enter; depois clique, digite algo e aperte Esc | O nome volta aos 8 caracteres do ID; com Esc, nada muda e a tela continua aberta |
| 39 | Clique no nome da rede, à direita do nome do nó, e depois em `Back` | A lista com `Back` no rodapé; `Back` volta à configuração na mesma rede |
| 40 | Clique no nome da rede e escolha outra rede | O nó vai para a outra rede, com 1 entrada no primeiro canal livre, e mantém o nome `Lever` |
| 41 | Com o segundo player, abra a tela de um nó do dono | O nome do nó e o da rede sem lápis nem realce, e nada muda ao clicar |
| 42 | Saia para o menu, abra o mundo de novo e abra a tela do nó | O nome `Lever` continua |
| 43 | Troque o idioma para Português (Brasil) | `Escolha uma rede`, `1 nó`, `12 nós`, `Voltar` e o aviso da lista vazia em português |

Anote cada desvio com o número do passo e uma captura de tela. Um desvio vira issue, e o PR que fecha a fase só segue depois de o roteiro passar.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 13:46 | Ajusta os passos 2, 3, 4, 6 e 19 e adiciona os passos 34 a 43, da #87. |
| Zennd | 2026-11-11 11:21 | Reescreve os passos do Configurator com os modos e ajusta os passos 4 e 13, da #86. |
| Zennd | 2026-11-11 10:32 | Tira os passos 34 a 42 do Label, que saiu com a #85 (pergunta 125). |
| Zennd | 2026-11-11 09:51 | Avisa quais passos mudam com as perguntas 125 a 172. |
| Marcelo Fabiano | 2026-11-10 16:38 | Adiciona os passos do Label, da #81. |
| Marcelo Fabiano | 2026-11-10 16:08 | Adiciona o passo do clique com item, da pergunta 113. |
| Marcelo Fabiano | 2026-11-10 15:58 | Adiciona os passos do Configurator, da #17. |
| Marcelo Fabiano | 2026-11-10 15:23 | Detalha o passo 20 e adiciona os passos 22 e 23, pela pergunta 102. |
| Marcelo Fabiano | 2026-11-10 13:55 | Adiciona os passos das redes depois de reiniciar, da #16. |
| Marcelo Fabiano | 2026-11-10 11:45 | Cria o documento, com o primeiro circuito da Fase 1. |
