# Alavanca

A alavanca do Redweave é uma fonte de sinal ligada e desligada pelo player, parecida com a alavanca do jogo, mas com uma aparência 3D mais realista e dois indicadores de cor: vermelho quando desligada e verde quando ligada. Tem uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #130, como o [Botão](botao.md) e a [Placa de pressão](placa-de-pressao.md). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito. As perguntas 549 a 555 e 558 estão pendentes, e o código só começa com todas respondidas (regra 9 e pergunta 307).

## Em uma frase

Um clique liga ou desliga a alavanca, que mostra o estado com uma luz vermelha ou verde e manda o sinal pela redstone ou por um canal da rede, como o player escolher na tela (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal, ligada e desligada pelo player, como a alavanca do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com dois indicadores de cor: vermelho desligada e verde ligada | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria, separada da do botão e da placa de pressão | 548 |

## Pontos em aberto

| Ponto | Pergunta pendente |
|---|---|
| Família: um Signal Module de fonte, com a base comum, ou um bloco à parte | 549 |
| Nome em inglês, ID e nome em português | 550 |
| Onde fica: uma parte do multipart, em qualquer face, ou só em algumas | 551 |
| Força do sinal: 15 fixo ou ajustável | 552 |
| Como a redstone sai: no bloco de apoio, como a alavanca do jogo, ou por uma borda | 553 |
| Os dois indicadores: um aceso de cada vez, ou os dois com um apagado | 554 |
| Dono, rede, Configurator e o `Toggle` | 555 |
| A ordem entre a alavanca, o botão e a placa de pressão | 558 |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/{id}/` depois que o ID estiver decidido (pergunta 550), o desenvolvedor gera a imagem, e o agente a confere.

## Fora desta página

- O botão, em [`botao.md`](botao.md), e a placa de pressão, em [`placa-de-pressao.md`](placa-de-pressao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555 e 558. |
