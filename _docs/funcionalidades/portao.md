# Portão

O Signal Gate (`Portão de Sinal` em português) é uma barreira seletiva: uma moldura de blocos do mod com um campo no meio, que só deixa passar quem se enquadra nas regras escolhidas pelo dono; para os outros, o campo é sólido (perguntas 677 e 678). Nasceu da ideia de "portal" do desenvolvedor, sem teletransporte, que a regra 2 descarta. Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 677 a 687 respondidas (regra 9), a implementar na #147.

## Em uma frase

Com sinal no canal do controlador, o campo de onda vermelha deixa passar só quem se enquadra nas regras, e cada passagem autorizada emite um pulso (perguntas 680 a 684).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e IDs | `Signal Gate`, `Portão de Sinal` em português, com `redweave:signal_gate_frame` (moldura), `redweave:signal_gate_controller` (controlador) e o campo `redweave:signal_gate_field` | 678 |
| O que é | Uma barreira seletiva, não um teletransporte | 677 |
| Forma | Uma moldura quadrada ou retangular, com o vão de 2 × 2, 2 × 3, 3 × 2 ou 3 × 3, na vertical, nos dois eixos, ou na horizontal, como um alçapão | 679 |
| Formação | Com a moldura completa e um controlador nela, o campo nasce; quebrar um bloco da moldura desfaz o campo. Um controlador por portão | 679 |
| Campo | Como o do portal do Nether, com uma onda vermelha animada; atravessável para quem se enquadra nas regras e sólido para os outros | 680 |
| Entrada | Um canal no controlador: com sinal, o campo filtra; sem sinal, o campo some e o vão fica livre | 681 |
| Regras | Caixas de marcar: `Owner`, `Card`, `Players`, `Passive`, `Hostile` (as da pergunta 621) e `Tags`. Passa quem se enquadrar em qualquer uma. Itens e projéteis nunca passam | 682 |
| `Tags` | Tags de tipo de entidade, como `#cubit:robots`, com o limite em [`limites.md`](../limites.md); o servidor recusa tags desconhecidas, com `Unknown tag.`. O mod publica a tag vazia `#redweave:gate_passers`, que outros mods e datapacks preenchem, sem dependência (regra 39) | 683 |
| Saída | Um pulso a cada passagem autorizada, com a duração na tela (padrão de 1 s, de 2 ticks a 10 s) e a força de 1 a 15 (padrão 15), por um canal ou por uma borda do controlador | 684 |
| Indicadores | No controlador, a luz vermelha em estado normal e verde durante o pulso, e um ponto na cor do canal | 684 |
| Proteção | Como o [Signal Lock](fechadura.md): a moldura e o controlador só saem pelo `Wrench` do dono; resistem a explosão e a pistão, e o campo não é destruído por explosão | 685 |
| Teletransporte | Cancelado para entidades não autorizadas quando o destino está perto do campo de um portão ativo, com a distância em [`limites.md`](../limites.md); a entidade autorizada se teletransporta | 686 |
| Família | O controlador é um Signal Module de transformação | 687 |

Os números ficam em [`limites.md`](../limites.md).

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_gate/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

JUnit das regras (as caixas, as tags, a combinação); GameTests da colisão por entidade, da formação da moldura e do teletransporte; roteiro manual da arte e da tela (pergunta 687).

## Fora desta página

- O cartão, em [`cartao.md`](cartao.md).
- A ideia de colisão: o campo pode decidir a colisão pela entidade que encosta, pelo contexto de colisão do jogo, sem tick. É uma ideia de desenho, a confirmar na implementação.
- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 677 a 687. |
