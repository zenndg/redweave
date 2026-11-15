# Alavanca

A alavanca do Redweave, o Signal Lever (`redweave:signal_lever`, `Alavanca de Sinal` em português), é uma fonte de sinal ligada e desligada pelo player, parecida com a alavanca do jogo, mas com uma aparência 3D mais realista e um indicador de cor, uma luz vermelha desligada e verde ligada, a mesma nos três itens (pergunta 554). Tem uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #130, como o [Botão](botao.md) e a [Placa de pressão](placa-de-pressao.md). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 549 a 555 e 558 respondidas (regra 9).

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

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_lever/` (pergunta 550), o desenvolvedor gera a imagem, e o agente a confere.

## Fora desta página

- O botão, em [`botao.md`](botao.md), e a placa de pressão, em [`placa-de-pressao.md`](placa-de-pressao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555 e 558. |
