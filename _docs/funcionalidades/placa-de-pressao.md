# Placa de pressão

A placa de pressão do Redweave, o Signal Pressure Plate (`redweave:signal_pressure_plate`, `Placa de Pressão de Sinal` em português), é uma fonte de sinal que liga enquanto algo está sobre ela, parecida com a placa de pressão do jogo, na mesma ideia da [Alavanca](alavanca.md): aparência 3D mais realista, um indicador de cor, uma luz vermelha desligada e verde ligada, e uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #132. Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 549 a 555, 557, 558 e 602 a 611 respondidas (regra 9). Entra depois da #136, o modo de girar do Configurator (pergunta 612).

## Em uma frase

Enquanto algo pisa na placa, ela liga, mostra a luz verde e manda o sinal pela redstone ou por um canal da rede; sem nada sobre ela, volta a desligada, com a luz vermelha (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal ligada enquanto algo está sobre ela, como a placa de pressão do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com um indicador de cor, uma luz vermelha desligada e verde ligada, a mesma nos três itens (pergunta 554) | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria | 548 |

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Pressure Plate`, `redweave:signal_pressure_plate`, `Placa de Pressão de Sinal` em português | 550 |
| Onde fica | Uma parte de placa do multipart, só no chão, como a do jogo | 551 |
| Família | Um Signal Module de fonte, como o Signal Randomizer e o Weather Detector: a tela, o dono, a rede e a saída por borda ou canal vêm da base comum, e ele só implementa a sua regra | 549 |
| Força | Ajustável na tela, de 1 a 15, com 15 como padrão | 552 |
| Saída pela redstone | Forte no bloco de apoio e pela borda da frente, como os Signal Modules | 553 |
| Saída sem fio | Um canal da rede, como a saída dos Signal Modules (`Connection`) | 548 e 549 |
| Indicador | Uma luz só, que troca de cor: vermelha desligado e verde ligado. A alavanca, o botão e a placa de pressão usam o mesmo indicador | 554 |
| Dono, rede e Configurator | Iguais aos Signal Modules: dono, rede escolhida, `Open` abre a tela | 555 |
| `Toggle` | Troca o modo, na ordem `Players`, `Passive`, `Hostile` e `Items`, com `<nome>: <modo>`, como no Weather Detector | 555, 603 e 621 |
| O que detecta | Um modo por vez, num seletor da tela: `Players`, `Passive`, `Hostile` ou `Items`, com a saída na força escolhida, num item só, como o modo do Weather Detector (pergunta 459). `Players` é o padrão | 557, 602, 604 e 621 |
| Ordem | Alavanca (#130), botão (#131), o modo de girar do Configurator (#136) e placa de pressão (#132) | 558 e 612 |

## Uso e comportamento

As decisões do [Signal Lever](alavanca.md) que valem para a placa: a força na chave `strength`, a luz pelo estado, a saída no tick seguinte, a saída funcionando com a rede `Offline`, a mensagem só no `Toggle` e os testes nas três fronteiras (perguntas 578, 581, 583 a 585, 587 e 606).

| Ponto | Como é | Pergunta |
|---|---|---|
| Mão | O clique à mão não faz nada, sem mensagem, como a placa do jogo. Quem pisa pode ser qualquer player ou mob do modo, não só o dono | 606 |
| O que conta | `Players` (`Players`): qualquer player, menos o espectador. `Passive` (`Passivos`): toda criatura viva que não é player nem tem a marca `Enemy` do jogo, como animais, aldeões, golems e peixes. `Hostile` (`Hostis`): toda criatura com a marca `Enemy`, como zumbi, creeper e slime, incluindo o enderman e o piglin zumbificado. `Items` (`Itens`): itens soltos no chão. Flechas e outras entidades não contam | 605 e 621 |
| Detecção | Liga no contato, na área da placa (10 × 10 px) até 4 px acima. Enquanto ligada, confere de novo a cada `pressure_plate.check_ticks` (20 ticks, 1 s) e solta quando não há mais nada do modo sobre ela | 607 |
| Save | `on`, `strength` e `mode` (`players`, `passive`, `hostile` ou `items`), fixados na #26 | 608 e 621 |
| Som | O da placa de pressão de pedra do jogo (`STONE_PRESSURE_PLATE_CLICK_ON` e `_OFF`) | 609 |
| Visual | A peça da placa afunda 1 px quando ligada, e a luz fica verde | 609 |
| Rótulo | O nome e, embaixo, `On` ou `Off`, o modo e o canal | 609 |
| Fora do chão | Recusada: o item fica na mão, e a action bar mostra `The Signal Pressure Plate only goes on the floor.` | 611 |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_pressure_plate/` (pergunta 550), o desenvolvedor gera a imagem, e o agente a confere.

## Fora desta página

- A alavanca, em [`alavanca.md`](alavanca.md), e o botão, em [`botao.md`](botao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 14:49 | Troca os modos por `Players`, `Passive`, `Hostile` e `Items` (pergunta 621). |
| Zennd | 2026-11-15 14:26 | Fecha o conceito com as respostas 602 a 612: os modos, o Toggle, a detecção, o save, o visual e a ordem. |
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555, 557 e 558. |
