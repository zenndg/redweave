# Alavanca

A alavanca do Redweave, o Signal Lever (`redweave:signal_lever`, `Alavanca de Sinal` em português), é uma fonte de sinal ligada e desligada pelo player, parecida com a alavanca do jogo, mas com uma aparência 3D mais realista e um indicador de cor, uma luz vermelha desligada e verde ligada, a mesma nos três itens (pergunta 554). Tem uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #130, como o [Botão](botao.md) e a [Placa de pressão](placa-de-pressao.md). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementada na #130, com as perguntas 549 a 555, 558 e 576 a 587 respondidas (regra 9).

## Em uma frase

Um clique liga ou desliga a alavanca, que mostra o estado com uma luz vermelha ou verde e manda o sinal pela redstone ou por um canal da rede, como o player escolher na tela (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal, ligada e desligada pelo player, como a alavanca do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com um indicador de cor, uma luz vermelha desligada e verde ligada, a mesma nos três itens (pergunta 554) | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria, separada da do botão e da placa de pressão | 548 |

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Lever`, `redweave:signal_lever`, `Alavanca de Sinal` em português | 550 |
| Onde fica | Uma parte de placa do multipart, em qualquer face | 551 |
| Família | Um Signal Module de fonte, como o Signal Randomizer e o Weather Detector: a tela, o dono, a rede e a saída por borda ou canal vêm da base comum, e ele só implementa a sua regra | 549 |
| Força | Ajustável na tela, de 1 a 15, com 15 como padrão | 552 |
| Saída pela redstone | Forte no bloco de apoio e pela borda da frente, como os Signal Modules | 553 |
| Saída sem fio | Um canal da rede, como a saída dos Signal Modules (`Connection`) | 548 e 549 |
| Indicador | Uma luz só, que troca de cor: vermelha desligado e verde ligado. A alavanca, o botão e a placa de pressão usam o mesmo indicador | 554 |
| Dono, rede e Configurator | Iguais aos Signal Modules: dono, rede escolhida, `Open` abre a tela | 555 |
| `Toggle` | O `Toggle` do Configurator liga e desliga a alavanca | 555 |
| Ordem | Alavanca (#130), botão (#131) e placa de pressão (#132) | 558 |

## Uso e comportamento

| Ponto | Como é | Pergunta |
|---|---|---|
| Mão vazia | A mão vazia, ou um item que não seja o Configurator, liga e desliga a alavanca, como a do jogo. O Configurator segue a base dos Signal Modules | 576 |
| Quem usa | Só o dono, como nos Signal Modules; os outros veem `Only the player who placed this module can change it.` | 577 |
| Rede `Offline` | A alavanca liga e desliga sempre; o canal só não conta enquanto a rede está `Offline`. Pela borda, funciona normalmente | 578 |
| Save | `on`, um booleano, e `strength`, de 1 a 15, no NBT da parte. O `powered` da base continua sendo o visual da saída, derivado. Ficam fixados na #26 | 579 e 584 |
| Mensagem | Só no `Toggle` do Configurator: `<nome>: On` ou `<nome>: Off`. O clique à mão não mostra nada | 585 |
| Inclinação | O cabo inclina 22,5° para a frente ligada e para trás desligada, porque os modelos de bloco só giram em 0, ±22,5 e ±45 graus | 586 |
| Tempo | A saída muda no tick seguinte ao clique, pelo tick comum dos módulos | 587 |
| Som | O clique da alavanca do jogo (`LEVER_CLICK`), mais alto ao ligar e mais baixo ao desligar | 580 |
| Luz | Mostra o estado da alavanca, sempre: verde ligada e vermelha desligada. O que não chega ao canal aparece no rótulo e na tela | 581 |
| Centro da tela | A força de 1 a 15 num seletor `−` / `+` e o estado `On` ou `Off`, sem botão para ligar | 582 |
| Rótulo | `On` ou `Off` e o canal da saída, como os módulos | 582 |
| Shift com um item | Coloca o item, como no jogo, e a alavanca não muda | 576 |
| Saída pela borda | Forte no bloco de apoio e no bloco da frente, como uma alavanca grudada. Com a saída num canal, as bordas ficam em 0 | 553 |
| Cabo | Troca sinal com o [Signal Cable](signal-cable.md) pela borda da frente, e o braço do cabo encosta na placa, como nos outros módulos | 566 e 575 |

## Tela

A Tela do Signal Lever abre com o Configurator no `Open`, como a dos outros módulos; numa rede `Offline`, ela não abre, e só o `Toggle` funciona (perguntas 402 e 578). O desenho está em [`telas.md`](telas.md#tela-do-signal-lever).

| Parte | Como é | Pergunta |
|---|---|---|
| Esquerda | `On` grande, em verde, ou `Off` apagado, como a luz da alavanca | 582 |
| `Strength` | A força, de 1 a 15, entre `−` e `+` | 552 e 582 |
| `Output` | O seletor `Edge` ou `Channel`, com a grade das cores, como os outros módulos | 548 e 549 |
| Partes comuns | O título, o nome, a rede e o cadeado, da [tela comum](modulos-de-sinal.md#tela-comum) | 400 |

Em português, `Ligada`, `Desligada` e `Força`.

## Mensagens

Na action bar, em inglês e em português:

| Ação | Mensagem | Pergunta |
|---|---|---|
| `Toggle` | `<nome>: On` ou `<nome>: Off` (`<nome>: Ligada` ou `<nome>: Desligada`) | 585 |
| Clique de quem não é o dono | `Only the player who placed this module can change it.` | 577 |
| Clique à mão do dono | Nenhuma: o som e a luz bastam | 585 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), dentro de `data` no contêiner `redweave:part_space`:

| Chave | O que guarda | Pergunta |
|---|---|---|
| `on` | Se a alavanca está ligada, um booleano | 579 |
| `strength` | A força, de 1 a 15. Um valor fora da faixa vai para o log e volta a 15 (regra 14) | 584 |
| `output` | A conexão da saída: `edge` ou a chave da cor do canal | 549 |

Os campos comuns `front` e `powered` seguem a base (pergunta 560). As chaves ficam fixadas na #26 (regra 15).

## Desenho

| Ponto | Como é | Pergunta |
|---|---|---|
| Placa | 10 × 10 px e 2 de espessura, com a moldura de cobre e a seta da saída, que fica vermelha ligada | 551 e 554 |
| Peças | Uma base de cobre de 4 × 4 × 2, o cabo de aço com o pomo de deepslate, e a luz de 2 × 2 × 1, que brilha no escuro | 548 e 554 |
| Cabo | Inclina 22,5° para a frente ligada e para trás desligada | 586 |
| Mira | A placa e uma caixa sobre o cabo, de 4 a 12 px na largura e 13 px de altura a partir da face, para o cabo também poder ser mirado | 571 |
| Modelos | Gerados pelo datagen: desligada e ligada, com e sem o giro de um quarto de volta | 534 |

## Código

| Camada | Classes |
|---|---|
| Domínio | `domain/module/SignalLever`: o estado e a força, e a saída, que é a força ligada e 0 desligada |
| Aplicação | `application/ChangeLever`: só o dono liga, desliga e muda a força |
| Parte | `block/part/LeverPart`, `LeverSave` e `LeverShapes`, sobre o `ModulePart` |
| Tela | `menu/LeverMenu` e as classes `LeverScreen*`, e `client/screen/LeverScreen` |
| Datagen | `datagen/LeverModels`, `LeverEnglishTexts` e `LeverPortugueseTexts` |
| Testes | `SignalLeverTest`, `ChangeLeverTest`, `SignalLeverGameTests` e `LeverMenuGameTests` |

## Testes

- **JUnit:** a regra da alavanca em `domain/module/`: o estado, a força e a saída.
- **GameTests:** o clique à mão, o dono, a saída pela borda e pelo canal, a força, o save e o `Toggle`.
- **Roteiro manual:** a arte, a luz e o som, nos passos de [`roteiro-de-teste-manual.md`](../desenvolvimento/roteiro-de-teste-manual.md#fase-2-o-signal-lever) (pergunta 583).

## Arte

A arte segue as perguntas 185 e 186: o agente escreveu o prompt em `tmp/arte/signal_lever/` (pergunta 550), o desenvolvedor gerou as imagens, e o agente as conferiu. As texturas são `signal_lever_front`, `signal_lever_front_on` e `signal_lever_parts`, no bloco, e `signal_lever`, no item. A lateral da placa é a do Signal Timer. Os 30° do prompt viraram 22,5° (pergunta 586).

## Fora desta página

- O botão, em [`botao.md`](botao.md), e a placa de pressão, em [`placa-de-pressao.md`](placa-de-pressao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 13:15 | Marca a alavanca como implementada na #130, com a tela, as mensagens, o save, o desenho e o código. |
| Zennd | 2026-11-15 12:27 | Registra as decisões 584 a 587, da força no save, da mensagem, da inclinação e do tempo. |
| Zennd | 2026-11-15 11:47 | Registra as decisões 576 a 583, do uso, do save, do som, da luz, da tela e dos testes. |
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555 e 558. |
