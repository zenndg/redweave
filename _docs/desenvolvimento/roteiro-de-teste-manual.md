# Roteiro de teste manual

O que os testes automáticos não cobrem e um player precisa conferir no jogo: o visual, a tela e a sensação de uso. O roteiro roda numa instância só com o mod e, antes da `main`, também numa do Iron Meridian (regras 30 e 31 do `AGENTS.md`). Cada passo diz o que fazer e o que se espera ver.

## Como preparar

1. `make client`, num mundo novo em criativo, com os comandos liberados.
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

Anote cada desvio com o número do passo e uma captura de tela. Um desvio vira issue, e o PR que fecha a fase só segue depois de o roteiro passar.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 11:45 | Cria o documento, com o primeiro circuito da Fase 1. |
