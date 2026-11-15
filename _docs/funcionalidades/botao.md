# Botão

O botão do Redweave, o Signal Button (`redweave:signal_button`, `Botão de Sinal` em português), é uma fonte de sinal que o player aperta e que volta sozinha, parecida com o botão do jogo, na mesma ideia da [Alavanca](alavanca.md): aparência 3D mais realista, um indicador de cor, uma luz vermelha desligado e verde ligado, e uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #131. Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 549 a 556, 558 e 588 a 595 respondidas (regra 9), em implementação na #131.

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
| Tempo ligado | Ajustável na tela, com o `Interval` comum dos módulos (pergunta 490) e 1 s como padrão, como o botão de pedra do jogo | 556 |
| Ordem | Alavanca (#130), botão (#131) e placa de pressão (#132) | 558 |

## Uso e comportamento

As decisões do [Signal Lever](alavanca.md) valem para o botão: a mão vazia ou qualquer item que não seja o Configurator aperta, só o dono aperta, funciona com a rede `Offline`, a luz mostra o estado, a força fica na chave `strength`, a mensagem aparece só no `Toggle`, a saída muda no tick seguinte e os testes ficam nas três fronteiras (perguntas 576 a 578, 581, 583 a 585, 587 e 588).

| Ponto | Como é | Pergunta |
|---|---|---|
| Som | O do botão de pedra do jogo: `STONE_BUTTON_CLICK_ON` ao apertar e `STONE_BUTTON_CLICK_OFF` ao voltar | 589 |
| Apertar de novo | Com o botão ligado, nada acontece e o tempo não recomeça, como no jogo | 590 |
| Save | `on`, `strength`, `interval` (o tempo ligado, em ticks) e `remaining` (os ticks que faltam), para voltar de onde parou ao recarregar. Ficam fixados na #26 | 591 |
| Limites do tempo | `button.min_interval_ticks`, 2 ticks, e `button.max_interval_ticks`, 6000 ticks, no config, como os do Signal Timer. Padrão de 20 ticks, 1 s | 556 e 592 |
| Mensagem | Só no `Toggle`: `<nome>: Pressed`. O clique à mão não mostra nada | 593 |
| Entidades | Flechas e outras entidades não apertam; só o player | 594 |
| Visual | A peça do botão afunda 1 px quando apertado, e a luz fica verde | 595 |
| Rótulo | `On` ou `Off` e o canal da saída, como o Signal Lever | 595 |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_button/` (pergunta 550), o desenvolvedor gera a imagem, e o agente a confere.

## Fora desta página

- A alavanca, em [`alavanca.md`](alavanca.md), e a placa de pressão, em [`placa-de-pressao.md`](placa-de-pressao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 13:42 | Registra as decisões 588 a 595, do uso, do som, do save, do config, da mensagem e do visual. |
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 556 e 558. |
