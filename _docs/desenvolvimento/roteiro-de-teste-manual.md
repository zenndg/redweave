# Roteiro de teste manual

O que os testes automáticos não cobrem e um player precisa conferir no jogo: o visual, a tela e a sensação de uso. Os passos conferem o que o código entrega hoje. O roteiro roda numa instância só com o mod e, antes da `main`, também numa do Iron Meridian (regras 30 e 31 do `AGENTS.md`). Cada passo diz o que fazer e o que se espera ver.

## Como preparar

1. `make client`, num mundo novo em criativo, com os comandos liberados. Monte tudo a uns 300 blocos do spawn, porque o jogo mantém os chunks do spawn sempre carregados, e isso atrapalha o passo 20 (pergunta 102).
2. Abra a aba `Redweave` do inventário criativo. Ela tem o Network Controller, o Signal Node, o Configurator e o Network Terminal (pergunta 69). Pegue um Configurator: ele começa no modo `Open`, que abre a Tela do Nó (pergunta 188).
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
| 11 | Abra a tela com o segundo player | Tudo visível e ao vivo, mas os controles apagados e o cadeado com `Only the owner of ...` à direita do rodapé |
| 12 | Com o segundo player, tente quebrar o controlador e um nó no survival | Nenhum dos dois quebra, e nada racha |
| 13 | Quebre o controlador com o dono, no survival | O item cai, a lâmpada apaga, e o nó, aberto com o Configurator, mostra `No network` em cinza |
| 14 | Recoloque o item do controlador a mais de 128 blocos dos nós | A rede volta, e os nós longe mostram a lente apagada com o anel tracejado vermelho, além de `Out of range` na tela |
| 15 | Quebre a pedra de um nó | O nó cai como item e sai da rede |
| 16 | Troque o idioma para Português (Brasil) | Todos os textos da tela e dos itens em português, sem nada passando da borda |

## Fase 1: as redes depois de reiniciar

Entregue com a issue #16 (perguntas 88 a 97). Comece com o circuito dos passos 1 a 8 montado, com a lâmpada acesa e um canal renomeado.

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
| 25 | Agachado, gire a roda do mouse para baixo duas vezes e depois para cima uma vez | `Mode: Copy` em verde, `Mode: Toggle` em âmbar e de novo `Mode: Copy` na action bar; o slot da hotbar não muda |
| 26 | Clique no nó da lâmpada com a mão vazia | A tela não abre, e a action bar mostra `Use a Configurator to open this node.` |
| 27 | No modo `Copy`, clique no nó da lâmpada | `Copied 1 slots`, e o tooltip mostra `Copy: 1 slots` |
| 28 | Grude um nó num lado de outra lâmpada e dê Shift + clique nele | `Pasted 1 slots`, e a lâmpada nova acende e apaga junto com a alavanca |
| 29 | Shift + clique no ar | `Copy cleared`, e o tooltip perde a linha da cópia |
| 30 | No modo `Wrench`, clique no nó da lâmpada nova | `Node picked up`, o nó some e vai para o inventário, e a lâmpada apaga |
| 31 | Com o segundo player, no modo `Wrench`, clique num nó do dono; no modo `Copy`, copie um nó e cole noutro do dono | `Only the owner of ... can change this node.`, sem mudar nada |
| 32 | Clique no controlador com o Configurator | Nada acontece |
| 33 | Troque o idioma para Português (Brasil) | `Configurador`, `Modo: Abrir`, `Modo: Copiar`, `Modo: Alternar`, `Modo: Chave` e todas as mensagens em português |

## Fase 1: a escolha de rede e o nome do nó

Entregue com a issue #87 (perguntas 139 a 208). Comece com o circuito dos passos 1 a 8 montado.

| # | Faça | Espere ver |
|---|---|---|
| 34 | Num mundo sem nenhum Network Controller, grude um nó e abra a tela dele | A lista vazia, com o ícone do controlador e `No network online. Place a Network Controller to create one.` |
| 35 | Com o segundo player, grude um nó; abra a tela dele com o dono | A tela só para leitura, com `No network`, sem lista |
| 36 | Coloque mais dois controladores e renomeie um deles. Grude um nó e abra a tela | A última usada no topo, em cobre, e as outras em ordem alfabética, cada uma com a quantidade de nós |
| 37 | Na configuração, clique no nome do nó, digite `Lever`, aperte `E` e depois Enter | O campo aceita o `E` sem fechar a tela, e o nome vira `Lever` |
| 38 | Clique no nome de novo, apague tudo e aperte Enter; depois clique, digite algo e aperte Esc | O nome volta aos 8 caracteres do ID; com Esc, nada muda e a tela continua aberta |
| 39 | Clique no nome da rede, à direita do nome do nó, e depois em `Back` | A lista com `Back` no rodapé; `Back` volta à configuração na mesma rede |
| 40 | Clique no nome da rede e escolha outra rede | O nó vai para a outra rede, com os 16 canais inativos, e mantém o nome `Lever` |
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
| 52 | Clique no controlador com a mão vazia | A tela fiel a `tela-do-controlador.png`: o quadrado da cor, o nome, `Rename`, `Online · 2 nodes`, as abas `Nodes`, `Signals`, `Access` e `Log`, e o rodapé em duas linhas |
| 52a | Feche a tela e clique no controlador com o Configurator nos modos `Open`, `Copy`, `Toggle` e `Wrench` | Só o `Open` abre a tela; os outros não fazem nada |
| 53 | Na aba `Nodes`, puxe a alavanca | Os dois nós na ordem em que entraram, com o nome, o tipo, o sinal de 0 a 15 ao vivo e o estado `Online` |
| 54 | Clique numa linha, depois em outra | A primeira abre a posição do bloco e os slots, como `01 White · Input`, com o triângulo em cobre e a barra de cobre; abrir a segunda fecha a primeira |
| 55 | Clique uma vez no nome de um nó; depois clique duas vezes nele, digite `Lever` e aperte Enter; repita, apague o texto e aperte Enter | Um clique só abre ou fecha os slots; o duplo clique abre o campo e deixa a linha como estava; o nome muda na lista e na Tela do Nó, e vazio volta aos 8 primeiros caracteres do ID |
| 56 | Clique em `Rename`, digite `Factory` e aperte Enter; depois repita e aperte Esc | O nome da rede muda no cabeçalho e na escolha de rede; o Esc não muda nada |
| 57 | Clique no quadrado da cor e escolha o verde-limão | A paleta abre sob o quadrado, com a cor atual em cobre; o clique troca a cor e fecha a paleta |
| 58 | Na aba `Signals`, clique duas vezes no nome do canal da alavanca, digite `Main door` e aperte Enter | O canal mostra o número e `Main door`, em claro, com `1 in · 1 out` e o valor ao vivo; os canais sem uso ficam apagados |
| 59 | Abra o nó da alavanca com o Configurator no modo `Open` | O slot e o nome do canal escolhido mostram `Main door` com o número do canal |
| 60 | Na aba `Access` | O dono num campo e o cadeado com `Only the owner can change this network.` |
| 61 | Com a distância de renderização em 2 chunks, ligue à rede um nó a uns 100 blocos do controlador, com uma entrada recebendo sinal, volte ao controlador e abra a tela | O nó distante com `Unloaded` em âmbar na coluna de estado, com o último sinal, e o rodapé com `1 unloaded` em âmbar e `0 out of range` em vermelho |
| 62 | Abra a tela com o segundo player | Tudo visível e ao vivo, sem a posição na linha aberta, sem `Rename`, sem paleta, sem renomear com duplo clique, e o rodapé com o cadeado |
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
| 69 | No canal `15`, olhe o painel do canal | Sem campo de nome: o título mostra a cor e o nome do canal da rede, e logo abaixo vem o `Status` |
| 70 | Desative o canal `15`, troque a direção e a face, e ative de novo | Com `Enabled`, a direção e o cubo não mudam; desativado, mudam; ativo de novo, vale a configuração nova |
| 71 | Abra a Tela do Controlador e abra a linha do nó | Só os canais ativos, como `15 Red · Input`, sem nome do slot; um nó sem canal ativo mostra `No active channel` |
| 72a | Coloque um nó novo, sem escolher rede, e cole nele a cópia | O nó entra na rede da cópia com os mesmos canais, sem passar pela escolha de rede; colado por outro player, a action bar recusa |
| 72b | Com o Configurator no modo `Toggle`, em âmbar, dê um clique e depois um Shift + clique num nó com canais configurados | `Enabled <n> channels` ativa todos; `Disabled <n> channels` desativa todos, sem abrir a tela; com outro player, a action bar recusa |
| 72 | Copie o nó com o Configurator e cole noutro | `Copied <n> slots` e `Pasted <n> slots`, contando todos os canais configurados, ativos ou não; o outro nó fica com os mesmos canais ativos e direções |
| 73 | Abra o nó com o segundo player | Tudo visível, sem lápis, com a pílula `Status` e os botões apagados |
| 74 | Troque o idioma para Português (Brasil) | `Ativo`, `Inativo` e `3 de 16 canais ativos`, sem nada passando da borda |

## Fase 1: o log da Tela do Controlador

Entregue com a issue #90 (perguntas 147, 148 e 242 a 249).

| # | Faça | Espere ver |
|---|---|---|
| 75 | Reinicie o servidor e abra a aba `Log` da Tela do Controlador | `No signal change yet.` no meio da tabela, sem linhas da reaplicação das saídas ao carregar |
| 76 | Com a tela aberta, puxe a alavanca que acende a lâmpada pela rede | Duas linhas novas no topo, sem reabrir a tela: a da lâmpada, `Output 15`, acima da da alavanca, `Input 15`, com a data e a hora do servidor em cinza e o quadrado da cor antes do canal |
| 77 | Renomeie o nó da lâmpada e puxe a alavanca de novo | As linhas novas trazem o nome novo; as antigas continuam com o nome de antes |
| 78 | Desative e ative o canal de saída da lâmpada na Tela do Nó | `Output 0` ao desativar e `Output 15` ao ativar |
| 79 | Puxe a alavanca mais vezes do que cabe na tela e role a roda do mouse | A lista rola; com mais de `log.size` linhas, as mais antigas somem |
| 80 | Troque o idioma para Português (Brasil) | A aba `Registro`, `Entrada` e `Saída` nas linhas e `Nenhuma mudança de sinal ainda.` no log vazio, sem nada passando da borda |

## Fase 1: o Network Terminal

Entregue com a issue #89 (perguntas 130 a 166 e 250 a 261). Use dois players, como nos passos anteriores.

| # | Faça | Espere ver |
|---|---|---|
| 81 | Coloque um Network Terminal | O bloco fiel a `network-terminal.png`: o visor com as linhas coloridas virado para você, a grelha nas laterais e a moldura de cobre igual à do controlador |
| 82 | Tente colocar um segundo Terminal | Nada é colocado, o item fica, e a action bar mostra `You already have a Network Terminal at <x>, <y>, <z>.` com a posição do primeiro |
| 83 | Com o segundo player, clique no Terminal com a mão vazia e tente quebrá-lo | Nada abre, a action bar mostra `Only the owner can use this terminal.`, e o bloco não quebra |
| 83a | Como dono, clique no Terminal com o Configurator no modo `Open`; depois, nos modos `Copy`, `Toggle` e `Wrench` | O modo `Open` abre a tela do Terminal, como a mão vazia; os outros não fazem nada |
| 84 | Abra o Terminal com três redes, uma `Online`, uma `Offline` e uma `Unplaced` | A tela fiel a `tela-do-terminal.png`: as redes em ordem alfabética, com a cor, o nome, a pílula do estado e os nós; `3 networks` no topo |
| 85 | Selecione cada rede | Só os botões do estado: `Open`, `Turn off` e `Clear nodes`; `Turn on` e `Clear nodes`; `Clear nodes` e `Delete` |
| 86 | Dê duplo clique no nome de uma rede, digite um nome e aperte Enter | O nome muda na lista e na Tela do Controlador; Esc cancela |
| 87 | Desligue e religue uma rede | A pílula vira `Offline` e volta a `Online`; os nós e o controlador apagam e acendem |
| 88 | `Open` numa rede com o controlador longe ou em outra dimensão | A Tela do Controlador abre; afastando-se do Terminal, ela fecha; Esc volta ao mundo |
| 89 | `Clear nodes` uma vez e depois de novo | O primeiro clique troca o botão por `Click again to clear`; o segundo tira os nós, que ficam `No network` no mundo |
| 90 | `Delete` numa rede `Unplaced`, com os dois cliques | A rede some da lista, e os nós dela ficam `No network` |
| 91 | Quebre o Terminal e coloque outro em outro lugar; reinicie o servidor e tente colocar mais um | O primeiro libera o player; depois de reiniciar, o limite continua valendo |
| 92 | Troque o idioma para Português (Brasil) | `Terminal de Rede`, `Abrir`, `Desligar`, `Ligar`, `Limpar nós`, `Apagar`, `Não colocada` e `1 rede` ou `3 redes`, sem nada passando da borda |

## Fase 1: o visor e o rótulo do Configurator, a emissão do nó e o `Clear log`

Entregue com a issue #80 (perguntas 262 a 272).

| # | Faça | Espere ver |
|---|---|---|
| 93 | Ponha duas lâmpadas com 1 bloco de espaço entre elas e um nó de saída nesse espaço, grudado numa delas; ligue o canal | Só a lâmpada onde o nó está grudado acende; a outra fica apagada |
| 94 | Troque o modo do Configurator com Shift + scroll | O visor do item muda de cor: azul no `Open`, verde no `Copy`, âmbar no `Toggle` e vermelho no `Wrench`; o nome continua na action bar e no tooltip |
| 94a | No macOS, com uma roda de mouse, troque o modo do Configurator com Shift + scroll, para baixo e para cima | O modo avança para baixo e volta para cima, e o slot da hotbar não muda |
| 95 | Copie um nó com o modo `Copy` e depois apague a cópia com Shift + clique no ar | O pixel de cima da direita do visor acende com a cópia e apaga sem ela |
| 96 | Segure o Configurator na mão principal perto de alguns nós | Cada nó até 16 blocos mostra o rótulo em duas linhas: em cima, pequena, `■ <rede>` com o quadradinho na cor da rede; embaixo, um pouco maior, o nome do nó; atrás de parede, mais apagado; legível sem cobrir a vista |
| 97 | Renomeie um nó e a rede pelas telas, com o rótulo à vista | Os nomes no rótulo mudam na hora |
| 98 | Olhe um nó sem rede | Em cima, o quadradinho cinza e `No network`; embaixo, o nome do nó |
| 99 | Troque para outro item na mão | Todos os rótulos somem |
| 100 | Na aba `Log` da Tela do Controlador, como dono, clique em `Clear log` uma vez e depois de novo | O botão vira `Click again to clear`; no segundo clique, o log fica vazio, também na tela aberta do segundo player |
| 101 | Abra a aba `Log` com o segundo player | Sem o botão `Clear log` |
| 102 | Troque o idioma para Português (Brasil) | `Limpar log`, `Clique de novo para limpar` e `Sem rede` no rótulo, sem nada passando da borda |

## Fase 2: a Tela do Nó sem a lógica

Entregue com a issue #106 (perguntas 308 e 329 a 333).

| # | Faça | Espere ver |
|---|---|---|
| 103 | Abra um nó com rede e escolha um canal | O painel termina logo abaixo do medidor e do cubo da face, sem a linha `Logic` e sem a dica `Logic arrives in Phase 2.`; o rodapé vem logo abaixo do painel, sem espaço vazio |
| 104 | Passe o mouse no pé do painel | Nenhum tooltip |
| 105 | Coloque um nó novo com o dono de 5 redes ou mais e abra a escolha de rede | 3 redes por vez, e a roda do mouse rola a lista até a última |
| 106 | Troque o idioma para Português (Brasil) | Sem `Lógica` e sem `A lógica chega na Fase 2.`, e nada passando da borda |

## Fase 1: as colunas de tipo e estado da lista de nós

Entregue com a issue #107 (perguntas 283, 284 e 335 a 341). Use o circuito dos passos 1 a 8 e o nó distante do passo 61.

| # | Faça | Espere ver |
|---|---|---|
| 107 | Abra a aba `Nodes` da Tela do Controlador | As colunas `Node`, `Type`, `Signal` e `State`, sem a coluna `Position`; o nó da alavanca com `In`, o da lâmpada com `Out` |
| 108 | Ative no nó da alavanca um slot de saída, e desative todos os slots de outro nó | O nó da alavanca passa a `In/Out`, e o nó sem slot ativo mostra `—` |
| 109 | Olhe o nó distante do passo 61 e afaste-se até um nó passar do raio do controlador | `Unloaded` em âmbar e `Out of range` em vermelho só na coluna de estado; nome, tipo e sinal na cor normal |
| 110 | Como dono, clique numa linha; depois abra a tela com o segundo player e clique na mesma linha | Para o dono, `Position: x, y, z` em cinza acima dos canais; para o segundo player, só os canais |
| 111 | Troque o idioma para Português (Brasil) | `Tipo`, `Estado`, `Ent`, `Saí`, `Ent/Saí`, `Descarregado`, `Fora de alcance` e `Posição:`, sem nada passando da borda |

## Fase 1: a Tela do Nó em duas colunas

Entregue com a issue #122 (perguntas 346 a 348). Use o circuito dos passos 1 a 8.

| # | Faça | Espere ver |
|---|---|---|
| 112 | Abra o nó da alavanca e escolha o canal dela | O painel em duas colunas, com uma linha entre elas: à esquerda, o título, `Status`, `Direction` e `Signal` com o medidor e o número; à direita, o cubo de 40 px com `Block face` e o nome da face ao lado; nada sobrando à direita |
| 113 | Puxe a alavanca com a tela aberta | O medidor do painel sobe até 15, e as barras brilham a partir de 12 |
| 114 | Desative o canal, gire o cubo arrastando e escolha outra face | O cubo maior gira e escolhe a face como antes; o medidor mostra 0 |
| 115 | Olhe o rodapé, como dono e com o segundo player | Uma linha só: `<n> of 16 channels active` à esquerda; para o segundo player, também o cadeado à direita |
| 116 | Troque o idioma para Português (Brasil) | `Sinal`, `Face do bloco` e `<n> de 16 canais ativos`, sem nada passando da borda |

## Fase 1: o Signal Remote

Entregue com a issue #22 (perguntas 352 a 374). Use o circuito dos passos 1 a 8, com a alavanca e a lâmpada, e dois players.

| # | Faça | Espere ver |
|---|---|---|
| 117 | Pegue um Signal Remote no criativo e passe o mouse sobre ele | O ícone sem vínculo, com o ponto cinza; o tooltip com `No link`, `Mode: Lever`, `Off` e, em cinza, `The strongest signal on a channel wins.` e `Shift + right-click to link.` |
| 118 | Dê Shift + clique direito com o tablet | A tela do vínculo, com `No link` no topo, `Lever` escolhido e `Networks` com só as suas redes `Online` |
| 119 | Escolha a rede; depois clique em `Back` | `Nodes with an input`, só com o nó da alavanca, com o tipo, o sinal e o estado; `Back` volta às redes |
| 120 | Escolha a rede e o nó da alavanca, que tem um slot de input só | A tela fecha sem pedir o canal, a action bar mostra `Linked: ■ <rede> · <nó> · <canal>`, e o ícone fica desligado |
| 121 | Desligue a alavanca física e clique com o tablet duas vezes | `<canal>: On` e a lâmpada acende, com o ícone aceso; depois `<canal>: Off` e a lâmpada apaga |
| 122 | Abra a tela, escolha `Button` e clique com o tablet; depois clique várias vezes seguidas | `<canal>: Pulse` e a lâmpada acende por 1 segundo; cliques seguidos a mantêm acesa |
| 123 | Ligue a alavanca física e clique com o tablet em `Lever` | `<canal> is held on by another source.`, e a lâmpada continua acesa |
| 124 | Ligue o tablet, guarde-o num baú e tire de novo | A lâmpada apaga com o tablet no baú e acende de novo quando ele volta ao inventário |
| 125 | Com o tablet ligado, afaste-se mais de 128 blocos do controlador | A lâmpada apaga e acende de novo quando você volta |
| 126 | Desative o slot da alavanca na Tela do Nó e clique com o tablet | O ícone com o X âmbar, `Link lost` na action bar e no tooltip, e nada acende |
| 127 | Desligue a rede no Network Terminal e clique com um tablet vinculado | `<rede> is offline.`, e nada acende |
| 128 | Entregue um tablet vinculado ao segundo player e peça que ele clique; depois que dê Shift + clique direito | O disparo funciona com ele; a tela dele não lista a sua rede |
| 129 | Abra a Tela do Controlador com um tablet ligado | À direita do rodapé, `● 0 unloaded · ● 1 remotes on` e, embaixo, `● 0 out of range` |
| 130 | Troque o idioma para Português (Brasil) e repita os passos 117, 118 e 129 | `Controle de Sinal`, `Sem vínculo`, `Redes`, `Nós com entrada`, `Canais de entrada` e `1 controles ligados`, sem nada passando da borda |

## Fase 1: o Signal Timer e os Signal Modules

Entregue com a issue #103 (perguntas 375 a 440). Use o circuito dos passos 1 a 8, com a alavanca e a lâmpada, e dois players.

| # | Faça | Espere ver |
|---|---|---|
| 131 | Pegue um Signal Timer no criativo e passe o mouse sobre ele | O ícone da placa, com a seta centrada no mostrador e o LED verde apagado |
| 132 | Olhando para o norte, coloque o timer no chão, ao lado de uma lâmpada ao norte; depois coloque outros na parede e no teto | A placa de 10 × 10 com a seta apontando para onde você olhava, centrada no mostrador, nas 3 posições |
| 133 | Com o Configurator no `Open`, dê Shift + clique na placa 4 vezes | A seta gira 90° no sentido horário a cada clique, com o clique do comparador e `Output: <borda>` na action bar, e volta ao início |
| 134 | Clique na placa com a mão vazia; depois com o Configurator no `Copy` | `Use a Configurator to open this module.`; depois `Modules cannot be copied yet.` |
| 135 | Com o Configurator no `Toggle`, clique no timer virado para a lâmpada | `<nome>: Running`, o LED verde acende, e a lâmpada pisca a cada 1 s por 0,5 s, com só a seta vermelha no pulso |
| 136 | Ponha um bloco de redstone atrás da placa; depois tire | A lâmpada para de piscar enquanto o bloco está lá e volta a piscar logo que ele sai |
| 137 | Clique na placa com o Configurator no `Open` | A Tela do Signal Timer, como a prancha: o relógio com o anel de cobre enchendo e o arco vermelho do pulso, o tempo até o próximo pulso no centro e `● Running` embaixo |
| 138 | Na tela, clique em `+` do `Interval`; depois Shift + clique; passe o mouse sobre o valor | O intervalo sobe 0,1 s e depois 1 s; o tooltip mostra os ticks |
| 139 | Na tela, clique no lápis e renomeie para `Door clock` | O nome muda na tela e, com o Configurator na mão, no rótulo sobre a placa |
| 140 | Clique em `Channel` do `Output` num timer sem rede; escolha a rede; escolha o canal `15` | Primeiro a escolha de rede, depois a grade de 16 cores; `■ 15 Red` ao lado de `Channel`, a rede no rodapé, e a borda da frente apagada |
| 141 | Ponha um nó com o slot `15` como saída sobre uma lâmpada | A lâmpada do nó pisca com o timer |
| 142 | Abra a Tela do Controlador | Na linha 2 do rodapé, `● 0 out of range · ● 1 modules on`, e o log com as linhas do módulo |
| 143 | Segure o Configurator perto da placa | O rótulo em duas linhas: o nome e, embaixo, `Running · 15 Red`, com o quadrado vermelho |
| 144 | Peça ao segundo player para abrir a tela, girar, alternar, retirar com o `Wrench` e quebrar a placa | A tela só para leitura, com o cadeado e `Only the player who placed this module can change it.`; as outras ações mostram o mesmo aviso, e a placa não quebra no survival |
| 145 | Desligue a rede no Network Terminal e tente o `Open` e o `Toggle`; depois gire a placa | `<rede> is offline. Turn it on at a Network Terminal.` nos dois; o giro funciona |
| 146 | Afaste-se até o chunk do timer descarregar e volte | A lâmpada do nó apaga enquanto o chunk está descarregado, e o timer volta a piscar do ponto onde parou |
| 147 | Retire o timer com o `Wrench` | `Module picked up`, a placa vai para o inventário, e a lâmpada apaga |
| 148 | Clique com um Signal Remote vinculado em `Lever` e passe o mouse sobre ele | `<canal>: On` em negrito verde e `<canal>: Off` em negrito vermelho; no tooltip, `On` ou `Off` com as mesmas cores |
| 149 | Troque o idioma para Português (Brasil) e repita os passos 135, 137, 142, 143 e 148 | `Temporizador de Sinal`, `Intervalo`, `Pulso`, `Saída`, `Pausa`, `Borda`, `Canal`, `● Rodando`, `1 módulos ligados`, `<nome>: rodando` e `ligado` ou `desligado`, sem nada passando da borda |

## Fase 1: o Signal Inverter

Entregue com a issue #105 (perguntas 441 a 455). Use o circuito dos passos 1 a 8, com a alavanca e a lâmpada, e dois players.

| # | Faça | Espere ver |
|---|---|---|
| 150 | Pegue um Signal Inverter no criativo e passe o mouse sobre ele | O ícone da placa, com a seta, o entalhe de trás e o símbolo do NOT no meio |
| 151 | Olhando para o norte, coloque o inversor no chão, com uma lâmpada ao norte; depois coloque outros na parede e no teto | A placa com a seta vermelha apontando para onde você olhava, nas 3 posições, e a lâmpada da frente acesa na hora |
| 152 | Ponha uma alavanca ligada a um bloco atrás da placa; ligue e desligue a alavanca | A lâmpada apaga com a alavanca ligada e acende com ela desligada, com o atraso de uma tocha; a seta fica vermelha só com a lâmpada acesa |
| 153 | Com o Configurator no `Toggle`, clique no inversor | `Signal Inverter has nothing to toggle.` na action bar, e nada muda |
| 154 | Com o Configurator no `Open`, dê Shift + clique na placa | A seta gira 90° no sentido horário, com o clique do comparador |
| 155 | Clique na placa com o Configurator no `Open` | A Tela do Signal Inverter, como a prancha: o diagrama A → NOT → B, com B aceso em vermelho, `● On` embaixo e, à direita, `Input` e `Output` em `Edge` |
| 156 | Com a tela aberta, ligue a alavanca de trás | No diagrama, A acende em vermelho, B apaga, e o estado vira `● Off` |
| 157 | Clique em `Channel` do `Output` num inversor sem rede; escolha a rede; escolha o canal `15`; ponha um nó com o slot `15` como saída sobre uma lâmpada | Primeiro a escolha de rede, depois a grade de 16 cores; `■ 15 Red` ao lado de `Channel`, a rede no rodapé, e a lâmpada do nó segue a saída do inversor |
| 158 | Ponha também o `Input` no canal `15`, para o inversor ler a própria saída | O inversor pisca e queima: a seta fica cinza-escura, a tela mostra `● Burnt out` em âmbar, e depois de 8 segundos ele volta a piscar e queima de novo |
| 159 | Segure o Configurator perto da placa | O rótulo em duas linhas: o nome e, embaixo, o estado (`On`, `Off` ou `Burnt out`) e a saída |
| 160 | Peça ao segundo player para abrir a tela, girar e quebrar a placa | A tela só para leitura, com o cadeado e `Only the player who placed this module can change it.`; as outras ações mostram o mesmo aviso, e a placa não quebra no survival |
| 161 | Com o inversor queimado, saia do mundo e entre de novo; depois afaste-se até o chunk descarregar e volte | Ao entrar, o inversor continua queimado até o fim do tempo que faltava; com o chunk descarregado, a lâmpada do nó apaga, e o inversor volta a funcionar quando o chunk carrega |
| 162 | Troque o idioma para Português (Brasil) e repita os passos 153, 155 e 158 | `Inversor de Sinal`, `O Inversor de Sinal não tem o que alternar.`, `Entrada`, `Saída`, `Borda`, `Canal`, `● Ligado`, `● Desligado` e `● Queimado`, sem nada passando da borda |

Anote cada desvio com o número do passo e uma captura de tela. Um desvio vira issue, e o PR que fecha a fase só segue depois de o roteiro passar.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 15:32 | Adiciona os passos 150 a 162, do Signal Inverter (pergunta 453). |
| Zennd | 2026-11-14 13:57 | Adiciona os passos 131 a 149, do Signal Timer e do destaque do Signal Remote (pergunta 406). |
| Zennd | 2026-11-12 21:49 | Adiciona os passos 117 a 130, do Signal Remote (pergunta 367). |
| Zennd | 2026-11-12 19:51 | Ajusta os passos 11, 103 e 105 e adiciona os passos 112 a 116, da Tela do Nó em duas colunas (perguntas 346 a 348). |
| Zennd | 2026-11-12 19:39 | Adiciona o passo 94a, do Shift + scroll no macOS (pergunta 351). |
| Zennd | 2026-11-12 19:23 | Adiciona o passo 83a, do Terminal aberto com o Configurator no modo `Open` (pergunta 343). |
| Zennd | 2026-11-12 19:01 | Ajusta os passos 53, 54, 61 e 62 às colunas novas e adiciona os passos 107 a 111, da #107 (pergunta 341). |
| Zennd | 2026-11-12 18:45 | Adiciona os passos 103 a 106, da Tela do Nó sem a lógica (pergunta 332). |
| Zennd | 2026-11-12 17:18 | Atualiza a aba criativa, os modos com o `Toggle`, a aba `Log`, o nó que nasce inativo e as linhas de slot (perguntas 226, 241 e 277). |
| Zennd | 2026-11-12 11:58 | Tira o nome do slot dos passos 69 a 72 (pergunta 277). |
| Zennd | 2026-11-12 11:46 | Ajusta os passos 96 a 98 e 102 ao rótulo de duas linhas (perguntas 274 a 276). |
| Zennd | 2026-11-12 11:26 | Adiciona os passos 93 a 102, da #80 (perguntas 262 a 272). |
| Zennd | 2026-11-12 10:33 | Adiciona os passos 81 a 92, do Network Terminal (perguntas 250 a 261). |
| Zennd | 2026-11-12 09:32 | Adiciona os passos 75 a 80, do log da Tela do Controlador (perguntas 242 a 249). |
| Zennd | 2026-11-11 17:45 | Adiciona o passo 72b, do modo `Toggle`, e ajusta a contagem do passo 72 (perguntas 240 e 241). |
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
