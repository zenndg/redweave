# Botão

O botão do Redweave, o Signal Button (`redweave:signal_button`, `Botão de Sinal` em português), é uma fonte de sinal que o player aperta e que volta sozinha, parecida com o botão do jogo, na mesma ideia da [Alavanca](alavanca.md): aparência 3D mais realista, um indicador de cor, uma luz vermelha desligado e verde ligado, e uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #131. Os termos são os do [`glossario.md`](../glossario.md).

É um dos [Emissores de Sinal](emissores.md) e segue o contrato comum da família: força, luz, saída, tempo, rede `Offline` e mensagem (perguntas 627 a 629).

**Estado:** implementado na #131, com as perguntas 549 a 556, 558 e 588 a 600 respondidas (regra 9).

## Em uma frase

Um clique aperta o botão, que liga, mostra a luz verde e manda o sinal pela redstone ou por um canal da rede, e depois de um tempo volta sozinho a desligado, com a luz vermelha (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal apertada pelo player, que volta sozinha, como o botão do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com um indicador de cor, uma luz vermelha desligado e verde ligado, a mesma nos três itens (pergunta 554) | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria | 548 |

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Button`, `redweave:signal_button`, `Botão de Sinal` em português | 550 |
| Onde fica | Uma parte de placa do multipart, em qualquer face | 551 |
| Família | Um Signal Module de fonte, como o Signal Randomizer e o Weather Detector: a tela, o dono, a rede e a saída por borda ou canal vêm da base comum, e ele só implementa a sua regra | 549 |
| Força | Ajustável na tela, de 1 a 15, com 15 como padrão | 552 |
| Saída pela redstone | Forte no bloco de apoio e pela borda da frente, como os Signal Modules | 553 |
| Saída sem fio | Um canal da rede, como a saída dos Signal Modules (`Connection`) | 548 e 549 |
| Indicador | Uma luz só, que troca de cor: vermelha desligado e verde ligado. A alavanca, o botão e a placa de pressão usam o mesmo indicador | 554 |
| Dono, rede e Configurator | Iguais aos Signal Modules: dono, rede escolhida, `Open` abre a tela | 555 |
| `Toggle` | O `Toggle` do Configurator aperta o botão | 555 |
| Tempo ligado | Ajustável na tela, com 1 s como padrão, como o botão de pedra do jogo. Usa a regra de ajuste do `Interval` comum dos módulos (pergunta 490), num `ButtonTime` próprio (pergunta 596) | 556 e 596 |
| Ordem | Alavanca (#130), botão (#131) e placa de pressão (#132) | 558 |

## Uso e comportamento

As decisões do [Signal Lever](alavanca.md) valem para o botão: a mão vazia ou qualquer item que não seja o Configurator aperta, só o dono aperta, funciona com a rede `Offline`, a luz mostra o estado, a força fica na chave `strength`, a mensagem aparece só no `Toggle`, a saída muda no tick seguinte e os testes ficam nas três fronteiras (perguntas 576 a 578, 581, 583 a 585, 587 e 588).

| Ponto | Como é | Pergunta |
|---|---|---|
| Som | O do botão de pedra do jogo: `STONE_BUTTON_CLICK_ON` ao apertar e `STONE_BUTTON_CLICK_OFF` ao voltar | 589 |
| Apertar de novo | Com o botão ligado, nada acontece e o tempo não recomeça, como no jogo | 590 |
| Save | `on`, `strength`, `interval` (o tempo ligado, em ticks) e `remaining` (os ticks que faltam), para voltar de onde parou ao recarregar. Ficam fixados na #26 | 591 |
| Limites do tempo | `button.min_interval_ticks`, 2 ticks, e `button.max_interval_ticks`, 6000 ticks, no config, como os do Signal Timer. Padrão de 20 ticks, 1 s | 556 e 592 |
| Tempo ligado | Um `ButtonTime` próprio, com mínimo de 2 ticks, que usa a mesma regra de ajuste do `Interval` comum, que não aceita menos de 4. Na tela, a linha `On time` (`Tempo ligado`) | 596 e 597 |
| Textos | `On` e `Off` em chaves próprias, `Ligado` e `Desligado` em português; `Pressed` é `Apertado` | 598 e 599 |
| `Toggle` com o botão apertado | Mostra `<nome>: Pressed`, e o aperto não faz nada | 600 |
| Mensagem | Só no `Toggle`: `<nome>: Pressed`. O clique à mão não mostra nada | 593 |
| Entidades | Flechas e outras entidades não apertam; só o player | 594 |
| Visual | A peça do botão afunda 1 px quando apertado, e a luz fica verde | 595 |
| Rótulo | `On` ou `Off` e o canal da saída, como o Signal Lever | 595 |
| Shift com um item | Coloca o item, como no jogo, e o botão não é apertado | 576 e 588 |
| Saída pela borda | Forte no bloco de apoio e no bloco da frente, como a alavanca. Com a saída num canal, as bordas ficam em 0 | 553 |
| Volta sozinho | A cada tick, o tempo que falta diminui, e o último tick desliga o botão, com o som de soltar. A saída dura exatamente o tempo ligado | 556 e 589 |
| Cabo | Troca sinal com o [Signal Cable](signal-cable.md) pela borda da frente, e o braço do cabo encosta na placa, como nos outros módulos | 566 e 575 |

## Tela

A Tela do Signal Button abre com o Configurator no `Open`, como a dos outros módulos; numa rede `Offline`, ela não abre, e só o `Toggle` funciona (perguntas 402 e 578). O desenho está em [`telas.md`](telas.md#tela-do-signal-button).

| Parte | Como é | Pergunta |
|---|---|---|
| Esquerda | `On` grande, em verde, ou `Off` apagado, como a luz do botão | 595 |
| `Strength` | A força, de 1 a 15, entre `−` e `+`, com o texto da alavanca | 552 |
| `On time` | O tempo ligado, entre `−` e `+`, de 0,1 s em 0,1 s, e de 1 s em 1 s com Shift, dentro dos limites do config. Com o mouse em cima, uma dica mostra os ticks | 556, 592 e 597 |
| `Output` | O seletor `Edge` ou `Channel`, com a grade das cores, como os outros módulos | 548 e 549 |
| Partes comuns | O título, o nome, a rede e o cadeado, da [tela comum](modulos-de-sinal.md#tela-comum) | 400 |

Não há botão para apertar: o botão é apertado pela mão ou pelo `Toggle`. Em português, `Botão de Sinal`, `Ligado`, `Desligado`, `Força`, `Tempo ligado` e `Saída` (perguntas 597 e 598).

## Mensagens

Na action bar, em inglês e em português:

| Ação | Mensagem | Pergunta |
|---|---|---|
| `Toggle` | `<nome>: Pressed` (`<nome>: Apertado`), também com o botão já apertado, quando o aperto não faz nada | 593, 599 e 600 |
| Clique de quem não é o dono | `Only the player who placed this module can change it.` | 577 e 588 |
| Clique à mão do dono | Nenhuma: o som e a luz bastam | 585 e 588 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), dentro de `data` no contêiner `redweave:part_space`:

| Chave | O que guarda | Pergunta |
|---|---|---|
| `on` | Se o botão está apertado, um booleano | 591 |
| `strength` | A força, de 1 a 15. Um valor fora da faixa vai para o log e volta a 15 (regra 14) | 584 e 591 |
| `interval` | O tempo ligado, em ticks. Um valor inválido vai para o log e volta a 1 s; um valor fora dos limites do config de agora é ajustado a eles, com um aviso, como no Signal Randomizer | 591 e 592 |
| `remaining` | Os ticks que faltam com o botão apertado, para ele continuar de onde parou ao recarregar; 0 com ele solto. Um botão salvo apertado sem tempo carrega solto, com um aviso | 591 |
| `output` | A conexão da saída: `edge` ou a chave da cor do canal | 549 |

Os campos comuns `front` e `powered` seguem a base (pergunta 560). As chaves ficam fixadas na #26 (regra 15). Enquanto o botão está apertado, o espaço é salvo a cada tick, porque o `remaining` muda.

## Desenho

| Ponto | Como é | Pergunta |
|---|---|---|
| Placa | 10 × 10 px e 2 de espessura, com a moldura de cobre e a seta da saída, que fica vermelha com o botão apertado | 551 e 554 |
| Anel | Um anel de cobre de 6 × 6 × 1, desenhado como 4 barras, porque o modelo sólido não mostra o meio transparente | 548 |
| Botão | De deepslate, 4 × 4, com 2 px de altura solto e 1 px apertado | 595 |
| Luz | A do Signal Lever, de 2 × 2 × 1, vermelha solto e verde apertado, que brilha no escuro | 554 e 581 |
| Mira | A placa e uma caixa sobre o anel, de 6 a 12 px na largura e até 4 px de altura a partir da face, a altura do botão solto | 571 |
| Modelos | Gerados pelo datagen: solto e apertado, com e sem o giro de um quarto de volta | 534 |

## Código

| Camada | Classes |
|---|---|
| Domínio | `domain/module/SignalButton`, que implementa o `Emitter` dos [emissores](emissores.md#código): o estado, a força, o tempo ligado e os ticks que faltam, e a saída, que é a força apertado e 0 solto. `ButtonTime`: o tempo ligado, com mínimo de 2 ticks e o ajuste do `Interval`. `ButtonLimits`: os limites do config |
| Aplicação | `application/ChangeButton`: só o dono aperta e muda a força e o tempo ligado |
| Config | `config/NeoForgeButtonLimits`, com a seção `button` do `RedweaveConfig` |
| Parte | `block/part/ButtonPart`, `ButtonSave` e `ButtonShapes`, sobre o `EmitterPart` dos [emissores](emissores.md#código), com `StrengthSave` e `RaisedShapes` divididos com a alavanca |
| Tela | `menu/ButtonMenu` e as classes `ButtonScreen*` e `ButtonAction`, e `client/screen/ButtonScreen` |
| Datagen | `datagen/ButtonModels`, com `PlatePieces` dividido com a alavanca, `ButtonEnglishTexts` e `ButtonPortugueseTexts` |
| Testes | `SignalButtonTest`, `ButtonTimeTest`, `ChangeButtonTest`, `SignalButtonGameTests` e `ButtonMenuGameTests` |

## Testes

- **JUnit:** a regra do botão em `domain/module/`: o aperto, a volta sozinho, apertar de novo, a força, o tempo ligado e os limites.
- **GameTests:** o clique à mão, o dono, a volta sozinho, apertar de novo, a saída pela borda e pelo canal, a força, o save com o tempo que falta, os limites do config, o `Toggle` e o cabo.
- **Roteiro manual:** a arte, a luz e o som, nos passos de [`roteiro-de-teste-manual.md`](../desenvolvimento/roteiro-de-teste-manual.md#fase-2-o-signal-button) (perguntas 583 e 588).

## Arte

A arte segue as perguntas 185 e 186: o agente escreveu o prompt em `tmp/arte/signal_button/` (pergunta 550), o desenvolvedor gerou as imagens, e o agente as conferiu. As texturas são `signal_button_front`, `signal_button_front_on` e `signal_button_parts`, no bloco, e `signal_button`, no item. A lateral da placa é a do Signal Timer, e a luz, a do Signal Lever.

## Fora desta página

- A alavanca, em [`alavanca.md`](alavanca.md), e a placa de pressão, em [`placa-de-pressao.md`](placa-de-pressao.md).
- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 758).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 758). |
| Zennd | 2026-11-16 08:10 | A parte e o domínio do botão passam a usar a base dos emissores (#139). |
| Zennd | 2026-11-16 08:01 | Aponta para a família dos emissores (pergunta 627). |
| Zennd | 2026-11-15 14:03 | Marca o botão como implementado na #131, com a tela, as mensagens, o save, o desenho e o código. |
| Zennd | 2026-11-15 13:59 | Registra as decisões 596 a 600, do tempo ligado, dos textos e do Toggle. |
| Zennd | 2026-11-15 13:42 | Registra as decisões 588 a 595, do uso, do som, do save, do config, da mensagem e do visual. |
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 556 e 558. |
