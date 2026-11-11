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
| 2 | Clique no controlador com a mão vazia | A Tela do Controlador abre, com o nome `xxxxxxxx`, os 8 primeiros caracteres do ID, `Online · 0 nodes` e `No node in this network yet.` |
| 3 | Ponha uma alavanca em cima de uma pedra e grude um Signal Node num lado da pedra | Uma placa fina de cobre, sem cor nos cantos, porque o nó nasce sem rede |
| 4 | Clique no nó com o Configurator no modo `Open` e clique na rede da lista | A escolha de rede, fiel a `tela-do-no-escolha-de-rede.png`, com a rede e `0 nodes`; depois do clique, a configuração, com `● Online` em verde, a rede à direita do nome do nó, a fileira dos 16 canais toda inativa e os cantos apagados. Ative o canal `01` em `Status`: ele vira `Enabled`, `Input`, e o primeiro canto fica branco |
| 5 | Puxe a alavanca, com a tela aberta | O medidor sobe na hora até 15, e as barras brilham a partir de 12 |
| 6 | Grude um segundo nó numa lâmpada de redstone, abra a tela dele e escolha a rede | A rede no topo da lista, em cobre, como a última usada, e depois os 16 canais inativos |
| 7 | Abra o canal `01`, troque para `Output` com ele `Disabled` e depois ative-o | O botão de cobre muda para `Output`; ativado, a aba `01` ganha a barra verde, e a direção e o cubo ficam travados |
| 8 | Puxe a alavanca algumas vezes | A lâmpada acende e apaga junto, e a lente do nó acende |
| 9 | No cubo da face, arraste o mouse e clique numa face | O bloco gira, a face sob o cursor fica em cobre, e o nome da face muda |
| 10 | Clique em `Status` para desativar o canal `01` e depois para ativá-lo | `Disabled` apaga a lâmpada e a barra verde; `Enabled` volta como estava, em `Output` |
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

## Fase 1: os estados da rede e o limite de redes

Entregue com a issue #88 (perguntas 132, 135, 145, 146, 160, 163, 177, 178 e 209 a 211). Até a #89, só o save desliga uma rede: com o jogo fechado, ponha `offline: 1b` na rede, dentro de `networks`, em `saves/<mundo>/data/redweave_networks.dat`, com um editor de NBT.

| # | Faça | Espere ver |
|---|---|---|
| 44 | Desligue no save a rede do circuito dos passos 1 a 8 e abra o mundo | A lâmpada, a lente dos nós e o núcleo do controlador apagam |
| 45 | Clique no controlador com a mão vazia; depois, no nó, com o Configurator nos modos `Open` e `Copy` | Nada abre nem copia, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` |
| 46 | No modo `Wrench`, clique num nó da rede desligada | `Node picked up`: a Chave funciona |
| 47 | Quebre o controlador com o dono e recoloque o item | O controlador volta com o núcleo apagado, e a rede continua desligada |
| 48 | Tire o `offline` do save e abra o mundo | A rede volta a funcionar, e a lâmpada acende com a alavanca ligada |
| 49 | Coloque controladores até ter 10 redes e tente colocar mais um | O bloco não é colocado, o item fica na mão, e a action bar mostra `You have reached the limit of 10 networks. Delete an unplaced one at a Network Terminal.` |
| 50 | Quebre um dos controladores, que fica `Unplaced`, e tente colocar um controlador novo | O limite continua valendo, porque a rede `Unplaced` também conta |
| 51 | Troque o idioma para Português (Brasil) | Os dois avisos em português |

## Fase 1: a Tela do Controlador

Entregue com a issue #20 (perguntas 22, 29, 98, 144, 149, 156, 158, 179 e 212 a 222). Use o circuito dos passos 1 a 8, com a alavanca e a lâmpada.

| # | Faça | Espere ver |
|---|---|---|
| 52 | Clique no controlador com a mão vazia | A tela fiel a `tela-do-controlador.png`: o quadrado da cor, o nome, `Rename`, `Online · 2 nodes`, as abas `Nodes`, `Signals` e `Access`, sem `Log`, e o rodapé em duas linhas |
| 52a | Feche a tela e clique no controlador com o Configurator nos modos `Open`, `Copy` e `Wrench` | Só o `Open` abre a tela; os outros não fazem nada |
| 53 | Na aba `Nodes`, puxe a alavanca | Os dois nós na ordem em que entraram, com o nome, o sinal de 0 a 15 ao vivo e a posição do bloco de cada nó |
| 54 | Clique numa linha, depois em outra | A primeira abre os slots, como `Slot 1 · 01 White · Input`, com o triângulo em cobre e a barra de cobre; abrir a segunda fecha a primeira |
| 55 | Clique uma vez no nome de um nó; depois clique duas vezes nele, digite `Lever` e aperte Enter; repita, apague o texto e aperte Enter | Um clique só abre ou fecha os slots; o duplo clique abre o campo e deixa a linha como estava; o nome muda na lista e na Tela do Nó, e vazio volta aos 8 primeiros caracteres do ID |
| 56 | Clique em `Rename`, digite `Factory` e aperte Enter; depois repita e aperte Esc | O nome da rede muda no cabeçalho e na escolha de rede; o Esc não muda nada |
| 57 | Clique no quadrado da cor e escolha o verde-limão | A paleta abre sob o quadrado, com a cor atual em cobre; o clique troca a cor e fecha a paleta |
| 58 | Na aba `Signals`, clique duas vezes no nome do canal da alavanca, digite `Main door` e aperte Enter | O canal mostra o número e `Main door`, em claro, com `1 in · 1 out` e o valor ao vivo; os canais sem uso ficam apagados |
| 59 | Abra o nó da alavanca com o Configurator no modo `Open` | O slot e o nome do canal escolhido mostram `Main door` com o número do canal |
| 60 | Na aba `Access` | O dono num campo e o cadeado com `Only the owner can change this network.` |
| 61 | Com a distância de renderização em 2 chunks, ligue à rede um nó a uns 100 blocos do controlador, com uma entrada recebendo sinal, volte ao controlador e abra a tela | O nó distante em âmbar, com o último sinal, e o rodapé com `1 unloaded` em âmbar e `0 out of range` em vermelho |
| 62 | Abra a tela com o segundo player | Tudo visível e ao vivo, sem a coluna `Position`, sem `Rename`, sem paleta, sem renomear com duplo clique, e o rodapé com o cadeado |
| 63 | Afaste-se do controlador com a tela aberta | A tela fecha ao passar do alcance de interação |
| 64 | Com 20 nós ou mais, role a lista com a roda do mouse | A lista rola, e nada passa da borda |
| 65 | Troque o idioma para Português (Brasil) | Todos os textos da tela em português, sem nada passando da borda |

## Fase 1: os 16 canais do Signal Node

Entregue com a issue #20 (perguntas 224 a 235). Use um mundo novo, porque os saves anteriores aos 16 canais não são convertidos (pergunta 235).

| # | Faça | Espere ver |
|---|---|---|
| 66 | Abra um nó com rede | A tela fiel a `tela-do-no.png`: a fileira dos 16 canais, de `01` a `16`, sem a lista de slots à esquerda e sem abas laterais |
| 67 | Ative os canais `15`, `02` e `09`, nessa ordem | Cada um ganha a barra verde, o rodapé mostra `3 of 16 channels active`, e os cantos da placa mostram laranja, cinza-claro e vermelho, na ordem das cores |
| 68 | Ative mais dois canais | Os cantos continuam com os 4 primeiros ativos, na ordem das cores |
| 69 | No canal `15`, clique no nome, digite `Redstone from lever 1` e aperte Enter | O nome aparece no campo; vazio volta a `Click to name` |
| 70 | Desative o canal `15`, troque a direção e a face, e ative de novo | Com `Enabled`, a direção e o cubo não mudam; desativado, mudam; ativo de novo, vale a configuração nova, com o nome |
| 71 | Abra a Tela do Controlador e abra a linha do nó | Só os canais ativos, como `15 Red · Input · Redstone from lever 1`; um nó sem canal ativo mostra `No active channel` |
| 72a | Coloque um nó novo, sem escolher rede, e cole nele a cópia | O nó entra na rede da cópia com os mesmos canais, sem passar pela escolha de rede; colado por outro player, a action bar recusa |
| 72 | Copie o nó com o Configurator e cole noutro | `Copied 5 slots` e `Pasted 5 slots`; o outro nó fica com os mesmos canais ativos, nomes e direções |
| 73 | Abra o nó com o segundo player | Tudo visível, sem lápis, com a pílula `Status` e os botões apagados |
| 74 | Troque o idioma para Português (Brasil) | `Nome`, `Ativo`, `Inativo` e `3 de 16 canais ativos`, sem nada passando da borda |

Anote cada desvio com o número do passo e uma captura de tela. Um desvio vira issue, e o PR que fecha a fase só segue depois de o roteiro passar.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 17:20 | Adiciona o passo 72a, de colar num nó sem rede (pergunta 238). |
| Zennd | 2026-11-11 17:05 | Ajusta os passos 7 e 70: configurar só com o canal desativado (pergunta 237). |
| Zennd | 2026-11-11 16:40 | Ajusta o passo 61 às contagens curtas do rodapé (pergunta 236). |
| Zennd | 2026-11-11 16:45 | Ajusta os passos 4, 6, 7 e 10 e adiciona os passos 66 a 74, dos 16 canais do nó. |
| Zennd | 2026-11-11 16:12 | Adiciona o passo 52a, do Configurator no controlador (pergunta 232). |
| Zennd | 2026-11-11 15:47 | Ajusta os passos 55, 58 e 62 ao duplo clique da pergunta 222. |
| Zennd | 2026-11-11 15:40 | Ajusta o passo 2 e adiciona os passos 52 a 65, da #20. |
| Zennd | 2026-11-11 14:09 | Adiciona os passos 44 a 51, da #88. |
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
