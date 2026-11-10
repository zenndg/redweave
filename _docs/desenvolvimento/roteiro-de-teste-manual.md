# Roteiro de teste manual

O que os testes automáticos não cobrem e um player precisa conferir no jogo: o visual, a tela e a sensação de uso. O roteiro roda numa instância só com o mod e, antes da `main`, também numa do Iron Meridian (regras 30 e 31 do `AGENTS.md`). Cada passo diz o que fazer e o que se espera ver.

## Como preparar

1. `make client`, num mundo novo em criativo, com os comandos liberados. Monte tudo a uns 300 blocos do spawn, porque o jogo mantém os chunks do spawn sempre carregados, e isso atrapalha o passo 20 (pergunta 102).
2. Abra a aba `Redweave` do inventário criativo. Ela tem o Network Controller e o Signal Node (pergunta 69).
3. Para o passo de outro player, abra um segundo cliente com outro nome, ou peça a alguém para entrar no mundo pela LAN.

## Fase 1: o primeiro circuito

Entregue com as issues #14, #15, #18 e #19 (pergunta 48).

| # | Faça | Espere ver |
|---|---|---|
| 1 | Coloque um Network Controller | A frente voltada para você e o núcleo do topo aceso (`linked`) |
| 2 | Clique no controlador com a mão vazia | `Current network: Network xxxx` na action bar |
| 3 | Ponha uma alavanca em cima de uma pedra e grude um Signal Node num lado da pedra | Uma placa fina de cobre, com o primeiro canto colorido na cor do canal |
| 4 | Clique no nó com a mão vazia | A Tela do Nó, fiel a `tela-do-no.png`, com `● Online · Network xxxx` em verde e o slot 1 como `Input` |
| 5 | Puxe a alavanca, com a tela aberta | O medidor sobe na hora até 15, e as barras brilham a partir de 12 |
| 6 | Grude um segundo nó numa lâmpada de redstone e abra a tela dele | O slot 1 em `Input`, num canal livre |
| 7 | Troque o slot 1 para `Output` e escolha o mesmo canal do primeiro nó | O botão de cobre muda para `Output`, e a cor escolhida fica realçada |
| 8 | Puxe a alavanca algumas vezes | A lâmpada acende e apaga junto, e a lente do nó acende |
| 9 | No cubo da face, arraste o mouse e clique numa face | O bloco gira, a face sob o cursor fica em cobre, e o nome da face muda |
| 10 | Clique em `Free slot` e depois em `Clear slot` duas vezes | O slot vira uma entrada no próximo canal livre; o primeiro clique em `Clear slot` pede confirmação, e o segundo libera |
| 11 | Abra a tela com o segundo player | Tudo visível e ao vivo, mas os controles apagados e o cadeado com `Only the owner of ...` no rodapé |
| 12 | Com o segundo player, tente quebrar o controlador e um nó no survival | Nenhum dos dois quebra, e nada racha |
| 13 | Quebre o controlador com o dono, no survival | O item cai, a lâmpada apaga, e o nó, ao ser clicado, mostra `No network` em cinza |
| 14 | Recoloque o item do controlador a mais de 128 blocos dos nós | A rede volta, e os nós longe mostram a lente apagada com o anel tracejado vermelho, além de `Out of range` na tela |
| 15 | Quebre a pedra de um nó | O nó cai como item e sai da rede |
| 16 | Troque o idioma para Português (Brasil) | Todos os textos da tela e dos itens em português, sem nada passando da borda |

## Fase 1: as redes depois de reiniciar

Entregue com a issue #16 (perguntas 88 a 97). Comece com o circuito dos passos 1 a 8 montado, com a lâmpada acesa e um canal renomeado, se a tela já permitir.

| # | Faça | Espere ver |
|---|---|---|
| 17 | Saia para o menu e abra o mesmo mundo de novo | A lâmpada continua acesa, e a Tela do Nó mostra a mesma rede, os mesmos slots e o mesmo sinal |
| 18 | Puxe a alavanca | A lâmpada apaga e acende junto, como antes de sair |
| 19 | Grude um nó novo sem clicar no controlador | O nó entra na mesma rede, porque a rede atual também foi salva |
| 20 | Em Opções, ponha as distâncias de renderização e de simulação em 2. Monte o nó da alavanca a uns 100 blocos do controlador e da lâmpada, que ficam juntos, ligue a alavanca, vá até a lâmpada, saia e abra o mundo | A lâmpada acende assim que o mundo abre, com a última leitura do nó descarregado, e a Tela do Nó da alavanca mostra `Unloaded` em âmbar |
| 21 | Feche o jogo, apague `saves/<mundo>/data/redweave_networks.dat` e abra o mundo | Os controladores e os nós mostram `No network`, sem o jogo travar |
| 22 | Feche o jogo, troque o conteúdo de `redweave_networks.dat` por um texto qualquer e abra o mundo | O mundo abre sem redes, `redweave_networks.unreadable.backup.dat` aparece ao lado, e o `logs/latest.log` mostra `The Redweave save was kept in ...` (pergunta 97) |
| 23 | Feche o jogo, ponha o backup do passo 22 de volta como `redweave_networks.dat` e abra o mundo | O mesmo do passo 22, sem o jogo travar |

## Fase 1: o Configurator

Entregue com a issue #17 (perguntas 103 a 112). Comece com o circuito dos passos 1 a 8 montado, e com um segundo controlador colocado, que vira a rede atual.

| # | Faça | Espere ver |
|---|---|---|
| 24 | Pegue o Configurator na aba `Redweave` e passe o mouse sobre ele | O nome `Configurator`, `No network stored` e as duas dicas em cinza |
| 25 | Clique com ele no primeiro controlador | `Configurator: Network xxxx` na action bar, e o tooltip mostra `Network: Network xxxx` |
| 26 | Grude um nó novo, que entra na rede do segundo controlador, e clique nele com o Configurator | `Linked to Network xxxx`, e a Tela do Nó, aberta com a mão vazia, mostra a primeira rede |
| 27 | Clique de novo no mesmo nó | `This node is already in Network xxxx.`, sem mudar nada |
| 28 | Shift + clique no nó da lâmpada | `Copied 1 slots`, e o tooltip mostra `Copy: 1 slots` |
| 29 | Grude um nó num lado de outra lâmpada e clique nele | `Pasted 1 slots`, e a lâmpada nova acende e apaga junto com a alavanca |
| 30 | Shift + clique no ar | `Copy cleared`, e o tooltip perde a linha da cópia |
| 31 | Com o segundo player, clique com um Configurator no controlador e cole uma cópia num nó do dono | `Only the owner can use this network.` e `Only the owner of ... can change this node.`, sem mudar nada |
| 32 | Troque o idioma para Português (Brasil) | `Configurador` e todas as mensagens em português |
| 33 | Clique num nó e no controlador segurando um graveto | Nada acontece: a Tela do Nó não abre, e a rede atual não muda (pergunta 113) |

Anote cada desvio com o número do passo e uma captura de tela. Um desvio vira issue, e o PR que fecha a fase só segue depois de o roteiro passar.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 16:08 | Adiciona o passo do clique com item, da pergunta 113. |
| Marcelo Fabiano | 2026-11-10 15:58 | Adiciona os passos do Configurator, da #17. |
| Marcelo Fabiano | 2026-11-10 15:23 | Detalha o passo 20 e adiciona os passos 22 e 23, pela pergunta 102. |
| Marcelo Fabiano | 2026-11-10 13:55 | Adiciona os passos das redes depois de reiniciar, da #16. |
| Marcelo Fabiano | 2026-11-10 11:45 | Cria o documento, com o primeiro circuito da Fase 1. |
