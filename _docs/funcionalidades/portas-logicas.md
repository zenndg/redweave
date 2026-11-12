# Portas lógicas

As portas AND, OR, XOR e NAND e a regra NOT são as regras de lógica do domínio, em Java puro (regra 10 do `AGENTS.md`). Esta página descreve só a regra, entregue na #28. Os blocos que a usam são Signal Modules: o módulo de porta, na #31 (pergunta 309), e o Signal Inverter, que usa a regra NOT, na #105 (pergunta 310). Eles completam esta página com o lado do módulo. Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Cada entrada conta como ligada acima de 0, e a saída é 15 quando a regra liga e 0 quando desliga, como a tocha de redstone do jogo (perguntas 321 e 322).

## Entrada e saída

| Ponto | Regra | Pergunta |
|---|---|---|
| Entrada | Binária: um `Signal` de 1 a 15 está ligado, e 0 está desligado. A força não muda o resultado | 321 |
| Saída | `Signal.MAX` (15) quando a regra liga, `Signal.OFF` (0) quando desliga | 322 |
| Entrada ausente | Erro de programação, que lança exceção: quem chama sempre tem os dois sinais | — |

## Tabelas-verdade

As portas têm duas entradas, A e B. "Ligada" é qualquer força de 1 a 15.

| A | B | AND | OR | XOR | NAND |
|---|---|---|---|---|---|
| 0 | 0 | 0 | 0 | 0 | 15 |
| 0 | ligada | 0 | 15 | 15 | 15 |
| ligada | 0 | 0 | 15 | 15 | 15 |
| ligada | ligada | 15 | 15 | 0 | 0 |

A regra NOT tem uma entrada só e não é uma porta (pergunta 310):

| Entrada | NOT |
|---|---|
| 0 | 15 |
| ligada | 0 |

## Ordem

A ordem das portas é `AND`, `OR`, `XOR` e `NAND`, a mesma do Shift + scroll da #32 (pergunta 326).

## No código

| Tipo | Pacote | O que faz | Pergunta |
|---|---|---|---|
| `Gate` | `domain/circuit/` | O `enum` das quatro portas, com `output(Signal a, Signal b)` | 323 e 324 |
| `Not` | `domain/circuit/` | A regra NOT, com `output(Signal)` | 323 e 324 |

A chave estável de cada porta no save, como `and`, entra na #31, com o módulo de porta, que é o primeiro a gravar o modo (pergunta 325). A base dos Signal Modules fica em `domain/module/`, criado na #103 (pergunta 323).

## Testes

Cada porta e o NOT têm a tabela-verdade escrita linha a linha, aplicada às forças 0, 1 e 15, e a saída é conferida como 0 ou 15, sem repetir a regra no teste (pergunta 327). A cobertura de `domain/circuit/` segue a regra 30.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-12 18:26 | Cria o documento, com as portas e a regra NOT da #28 (perguntas 321 a 328). |
