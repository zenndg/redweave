# Lâmpada

O Signal Lamp (`redweave:signal_lamp`, `Lâmpada de Sinal` em português) é uma barra de LED do multipart que acende na cor do seu canal, com a intensidade da força recebida, e emite a força escolhida no bloco de apoio (pergunta 650). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 650 a 658 respondidas (regra 9), a implementar na #143.

## Em uma frase

A lâmpada escuta um único canal, tem sempre a cor dele, acende no nível de luz da força recebida, sem piscar, e acesa emite a força escolhida na tela (perguntas 650 e 651).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Lamp`, `redweave:signal_lamp`, `Lâmpada de Sinal` em português | 650 |
| Canal e cor | Um único canal de intensidade; a cor da lâmpada é sempre a cor desse canal | 650 |
| Luz | A força recebida é o nível de luz, de 0 a 15. A luz no mundo é branca, porque o jogo não tem luz colorida; a cor fica só na lâmpada, numa textura emissiva tingida. É estática: só muda quando o sinal muda | 650 |
| Força emitida | Acesa, emite a força escolhida na tela, de 1 a 15 | 651 |
| Forma | Uma parte do multipart, como o Signal Node: uma barra de LED de 12 × 4 px e 2 de espessura, centrada na face, no sentido de onde o player olha; o `Rotate` a gira 90° | 652 |
| Saída | Como o Signal Node: com força, só no bloco de apoio, e com o cabo e o pó da mesma face ao lado | 262, 574 e 653 |
| Rede | Fora do alcance, com a rede `Offline` ou sem rede, apaga e não emite, com a cor escura; o rótulo mostra `Offline` ou `Out of range` | 654 |
| Desempenho | O nível de luz muda no máximo a cada `lamp.min_light_update_ticks`, em [`limites.md`](../limites.md); o sinal emitido segue sem atraso | 655 |
| Controle | Um segundo canal opcional, da mesma rede e de qualquer cor, com o modo escolhido pelo dono: `Hold` (com sinal no controle, a lâmpada fica desligada) ou `Pulse` (cada pulso alterna). Padrão `Hold` | 656 |
| Desligada pelo controle | Apaga e não emite, com a cor escura e um indicador vermelho; o rótulo mostra `Off` | 657 |
| `Toggle` | Liga e desliga à mão, como um pulso no controle, com `<nome>: On` ou `<nome>: Off` | 658 |
| Dono e Configurator | Como os Signal Modules: dono, rede, `Open`, `Rotate` e `Wrench` | 658 |

Os números ficam em [`limites.md`](../limites.md).

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_lamp/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

JUnit da regra (a intensidade, o controle `Hold` e `Pulse`); GameTests no mundo (a saída no bloco de apoio, a rede `Offline`, o controle, o `Toggle`); roteiro manual da cor, da luz e do desempenho (pergunta 658).

## Fora desta página

- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 650 a 658. |
