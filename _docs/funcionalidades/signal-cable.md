# Signal Cable

O Signal Cable (`redweave:signal_cable`, `Cabo de Sinal` em português) é o cabo físico de redstone do Redweave, no estilo do More Red e do Project Red: grudado em qualquer face, ele faz o papel do fio de redstone do jogo, em 16 cores que isolam os cabos uns dos outros (perguntas 494, 495 e 505). É uma parte do [multipart](multipart.md) e entra na #44, junto com ele, na `v0.1.0` (perguntas 516 e 524). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #44, com a receita do cabo branco pendente na #24.

## Em uma frase

Um cabo de 4 × 3 px, em qualquer face, que se liga sozinho aos cabos da mesma cor e do mesmo dono e leva o sinal como o fio do jogo, perdendo 1 de força por bloco (perguntas 496, 497, 499, 521 e 528).

## Item

| Ponto | Como é | Pergunta |
|---|---|---|
| Item único | Um item só, `redweave:signal_cable`, que nasce branco. A cor fica no data component `redweave:cable_color`, com a chave da cor (regra 7), e o branco fica sem ele. Cabos de cores diferentes não empilham entre si | 494, 505 e 561 |
| Nome | Leva a cor, como `Red Signal Cable` e `Cabo de Sinal Vermelho`; o branco é `Signal Cable`. A aba criativa mostra as 16 cores | 564 |
| Cores | As 16 cores de lã, as mesmas dos slots do Signal Node e dos canais da rede (pergunta 224) | 494 |
| Tingir | 8 cabos de qualquer cor em volta de 1 corante, na mesa de trabalho, dão 8 cabos da cor do corante, como o vidro e a terracota tingidos. Um cabo tingido pode trocar de cor, e o corante branco o leva de volta ao branco. As 16 receitas saem do datagen, com o ID `redweave:<cor>_signal_cable` | 506, 562 e 563 |
| Receita | A do cabo branco fica para a #24, com materiais do jogo puro (regra 4), em JSON de datapack. Até lá, o cabo vem do criativo | 520 |

## Forma

| Ponto | Como é | Pergunta |
|---|---|---|
| Onde fica | Grudado em qualquer face de um bloco: chão, parede ou teto | 495 |
| Tamanho | 4 pixels de largura e 3 de altura, centrado na face. Fica um pouco mais alto que as placas do nó e dos módulos, que têm 2 px | 521 |
| Curvas | Contorna os blocos (curva externa) e passa para outra face do mesmo espaço (curva interna), com a ligação perfeita entre os pedaços, mesmo em degraus | 495 |
| No mesmo espaço | Até 2 cabos, cada um numa face diferente, de qualquer cor. Da mesma cor e em faces vizinhas, fazem a curva interna e se ligam | 498 e 504 |
| Colisão | Nenhuma | 517 |
| Mira | O contorno segue o desenho do cabo: o centro de 4 × 3 e os braços ligados | 571 |
| Desenho das curvas | Como o pó do jogo: no degrau, o cabo de baixo desenha um trecho subindo a lateral do bloco; na curva externa, cada cabo estica o braço até a quina, e os dois se encontram ali | 572 |

## Ligação

| Ponto | Como é | Pergunta |
|---|---|---|
| Automática | Todo cabo se liga ao próximo, sozinho, como o fio do jogo | 496 |
| Cor | Só com cabos da mesma cor, para cabos de cores diferentes passarem lado a lado sem um interferir no outro | 497 |
| Para que serve a cor | Só para isolar os cabos. Não liga o cabo a um canal da rede | 501 |
| Dono | Só com cabos do mesmo dono | 528 |
| Pó do jogo | Um cabo de qualquer cor se liga ao pó de redstone do jogo como componente: lê o sinal dele e o alimenta. Um pó entre cabos de cores diferentes liga os dois, a critério do player. Na passagem entre o cabo e o pó, o sinal perde 1, como de pó para pó | 507 e 565 |
| Partes do mesmo espaço | Liga-se ao nó ou ao módulo de outra face do mesmo espaço pela curva interna. O nó lê a força do cabo como se ele fosse o seu bloco de apoio, e o cabo lê o que o nó emite. O módulo só troca sinal com o cabo pela borda de trás ou da frente que apontar para a face do cabo | 514 e 566 |
| Nó no espaço vizinho | Um cabo no espaço vizinho, na mesma face do nó, liga-se a ele como no mesmo espaço: recebe o que o nó emite e o alimenta como se fosse o bloco de apoio. O mesmo vale para o pó do jogo | 574 |
| Encosto na placa vizinha | A placa do nó e a dos módulos tem 10 × 10 px. Quando há troca de sinal, o braço do cabo passa 3 px da borda do espaço e encosta nela; com o módulo, só pela borda de trás ou da frente. A mira acompanha o braço | 575 |

## Sinal

| Ponto | Como é | Pergunta |
|---|---|---|
| Perda | Como o fio do jogo: perde 1 de força por bloco, de 15 até 0. A curva interna, dentro do mesmo espaço, não perde nada. Um repetidor, um nó ou um módulo renova o sinal | 499 e 541 |
| O que energiza | Como o pó do jogo: energia fraca no bloco onde está grudado e no bloco para onde aponta numa ponta solta | 508 |
| O que lê | O sinal de alavancas, botões, repetidores, nós e módulos ao lado | 508 |
| Cabo solto | Sem nenhuma ligação, energiza como o pó em cruz: o bloco de apoio e os 4 vizinhos em volta, na face | 568 |
| Componentes | Vira a ponta para os mesmos componentes do pó do jogo: tudo o que é fonte de sinal ou aceita redstone | 569 |
| Save | `color` e `owner` em `data`; a força é recalculada ao carregar e só vai ao cliente | 567 |
| Nós e módulos | Integra-se como o fio do jogo, sem nada novo: o nó lê e emite no bloco, e o módulo lê e emite pelas bordas da placa (perguntas 71, 407 e 412) | 500 |
| Outro dono | Pelo sinal físico, o cabo energiza blocos e alimenta nós, módulos e pó de qualquer dono; o dono só impede a ligação de cabo com cabo | 531 |
| Energia de outro cabo | O cabo ignora a energia que outro cabo põe num bloco, de qualquer cor ou dono, como o pó do jogo | 570 |

## Cálculo no servidor

O pó do jogo atualiza bloco por bloco, em cascata, e a regra 18 proíbe isso. O Signal Cable tem o mesmo resultado do jogo por outro caminho (pergunta 509):

| Ponto | Como é | Pergunta |
|---|---|---|
| Trecho | Quando algo muda, o servidor acha o trecho ligado de uma cor, calcula a força de cada pedaço pela distância das fontes, no mesmo tick, e só avisa os blocos vizinhos que mudaram, como os mods de redstone rápida. A regra fica em `domain/cable/`: o `Insulation` decide quais cabos se ligam, e o `CableRun` calcula a força | 509, 537 e 540 |
| Tamanho | Sem limite no config: o sinal some em 15 blocos, então o cálculo só percorre até onde a força chega a partir das fontes. Uma mudança só afeta pedaços a até 15 espaços, e as fontes deles estão a até 30, então a busca para em 30 e grava só até 15 (os números ficam em [`limites.md`](../limites.md)) | 530 |
| Chunk descarregado | O cálculo nunca carrega um chunk (regra 6): o trecho para na borda, os pedaços do outro lado não são atualizados até o chunk carregar, e ao carregar o trecho é recalculado | 529 |

## Dono, quebra e Configurator

| Ponto | Como é | Pergunta |
|---|---|---|
| Dono | Como o Signal Node: quem coloca é o dono, e só ele quebra e retira o cabo no survival; no criativo, qualquer um quebra (perguntas 74 e 394) | 526 |
| Quebrar | Tira só o cabo mirado, e o item, com a cor, cai no chão | 515 e 525 |
| `Wrench` | Retira o cabo e manda o item, com a cor, para o inventário do player, ou para o chão se não houver espaço | 525 |
| `Open`, `Copy` e `Toggle` | Não fazem nada: o cabo não tem tela nem estado | 525 |
| `Rotate` | Não faz nada e mostra `Only modules can be turned.`, porque só os Signal Modules giram | 617 |
| Rótulo | Com o Configurator na mão, o rótulo sobre o cabo mostra a cor e a força, como `15 Red · 12` | 525 |

## Visual

| Ponto | Como é | Pergunta |
|---|---|---|
| Cor | Uma textura em tons de cinza, gerada por IA, que o jogo tinge com a cor do cabo no mundo e no item, como as folhas e a grama. As bordas do topo são claras, para a cor aparecer viva de cima, ao lado do filete | 518, 535 |
| Sinal | Um filete no topo do cabo, por cima do isolamento colorido, que vai do vermelho escuro (0) ao vermelho vivo (15), como o pó do jogo, sem mudar a cor do isolamento (princípio 1 de [`ux.md`](../conceito/ux.md)). Fica no modelo do bloco: cada mudança de força redesenha a seção do chunk, como o pó do jogo | 522 e 573 |
| Arte | Em `tmp/arte/signal_cable/`: o agente escreve o `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere (perguntas 185 e 186) | 518 |

## Código

| Camada | O que fica lá |
|---|---|
| `domain/cable/` | `Insulation` (cor e dono, `joins`), `CablePiece`, `CableLink` e `Passage` (curva interna sem perda, próximo espaço com perda 1), a porta `CableGrid` e o `CableRun`, que calcula a força de cada pedaço (perguntas 537 e 540) |
| `block/part/` | `CablePart`, a parte; `CableLinks`, a geometria das ligações e dos braços; `CableInputs`, o que o cabo lê ao lado; `CableRuns`, que roda o cálculo no servidor; `WorldCableGrid`, a porta só com espaços carregados (pergunta 529); `CableReach`, `CableTurn` e `CableShapes`, o alcance dos braços, a rotação e o contorno de mira |
| `item/` | `SignalCableItem`, que coloca um cabo da cor do item, `CableColorCodec` e `CableClicks`, o Configurator no cabo |
| `client/` e `datagen/` | `CableQuads` e o `PartSpaceModel` desenham o cabo; `CableModels` gera os modelos das peças: centro, braço, braço curto, quina, degrau e placa |

## Testes

- **JUnit:** o cálculo do trecho, com a força por distância, o isolamento por cor e as fontes (`CableRunTest` e `InsulationTest`).
- **GameTests:** o cabo perdendo força, as cores isoladas, os donos, as curvas e os degraus, o pó do jogo, o nó e o módulo no mesmo espaço e no vizinho, a ponta solta, o contorno de mira, o Configurator, o rótulo e o save.
- **Roteiro manual:** a forma, a arte, a mira e o desempenho (pergunta 527), na seção do multipart e do Signal Cable do [roteiro de teste manual](../desenvolvimento/roteiro-de-teste-manual.md).

## Fora desta página

- O multipart, em [`multipart.md`](multipart.md).
- A receita, na #24, e o Jade e o The One Probe, na #23.
- O cabo agrupado de 16 canais, que saiu do roadmap, com a #46 fechada (pergunta 519).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 14:38 | O modo `Rotate` no cabo mostra `Only modules can be turned.` (pergunta 617). |
| Zennd | 2026-11-15 09:34 | Marca o cabo como implementado na #44, com o código, os testes e a busca até 30. |
| Zennd | 2026-11-15 09:08 | Registra a decisão 575, do encosto na placa vizinha. |
| Zennd | 2026-11-15 09:07 | Registra a decisão 574, do nó no espaço vizinho. |
| Zennd | 2026-11-15 08:32 | Registra as decisões 571 a 573, da mira, das curvas e do filete. |
| Zennd | 2026-11-15 08:27 | Registra a decisão 570, da energia de outro cabo. |
| Zennd | 2026-11-14 23:07 | Registra as decisões 567 a 569, do save, do cabo solto e dos componentes. |
| Zennd | 2026-11-14 23:01 | Registra as decisões 565 e 566, do pó do jogo e da curva interna com o nó e os módulos. |
| Zennd | 2026-11-14 22:27 | Registra as decisões 561 a 564, do item, das receitas de tingir e do nome. |
| Zennd | 2026-11-14 20:33 | Registra as decisões 540 e 541, do domínio do trecho e da curva interna. |
| Zennd | 2026-11-14 20:27 | Registra o pacote do trecho (pergunta 537). |
| Zennd | 2026-11-14 19:50 | Registra a decisão 535, das bordas claras do topo. |
| Zennd | 2026-11-14 19:13 | Cria o documento, com as decisões 494 a 501, 504 a 509, 514, 515, 517 a 522 e 525 a 533. |
