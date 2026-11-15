# Placa de pressão

A placa de pressão do Redweave é uma fonte de sinal que liga enquanto algo está sobre ela, parecida com a placa de pressão do jogo, na mesma ideia da [Alavanca](alavanca.md): aparência 3D mais realista, dois indicadores de cor, vermelho desligada e verde ligada, e uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #132. Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito. As perguntas 549 a 555, 557 e 558 estão pendentes, e o código só começa com todas respondidas (regra 9 e pergunta 307).

## Em uma frase

Enquanto algo pisa na placa, ela liga, mostra a luz verde e manda o sinal pela redstone ou por um canal da rede; sem nada sobre ela, volta a desligada, com a luz vermelha (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal ligada enquanto algo está sobre ela, como a placa de pressão do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com dois indicadores de cor: vermelho desligada e verde ligada | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria | 548 |

## Pontos em aberto

| Ponto | Pergunta pendente |
|---|---|
| Família: um Signal Module de fonte, com a base comum, ou um bloco à parte | 549 |
| Nome em inglês, ID e nome em português | 550 |
| Onde fica: uma parte do multipart, só no chão, como a do jogo, ou em qualquer face | 551 |
| Força do sinal: 15 fixo ou ajustável | 552 |
| Como a redstone sai: no bloco de apoio, como a placa do jogo, ou por uma borda | 553 |
| Os dois indicadores: um aceso de cada vez, ou os dois com um apagado | 554 |
| Dono, rede, Configurator e o `Toggle` | 555 |
| O que a placa detecta: players, criaturas, itens, e se o sinal muda com a quantidade | 557 |
| A ordem entre a alavanca, o botão e a placa de pressão | 558 |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/{id}/` depois que o ID estiver decidido (pergunta 550), o desenvolvedor gera a imagem, e o agente a confere.

## Fora desta página

- A alavanca, em [`alavanca.md`](alavanca.md), e o botão, em [`botao.md`](botao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555, 557 e 558. |
