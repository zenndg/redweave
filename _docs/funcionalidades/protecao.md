# Proteção dos blocos

A proteção dos blocos impede que outros players explodam ou quebrem a porta para passar por um [Signal Lock](fechadura.md) ou um [Signal Keypad](teclado.md): cada um deles protege os blocos escolhidos pelo dono (pergunta 672). Não é proteção de terreno, que fica com os mods próprios (regra 2). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 672 a 676 respondidas (regra 9), a implementar na #146.

## Em uma frase

O dono escolhe, com o modo `Copy` do Configurator, até 16 blocos perto de um Signal Lock ou Signal Keypad, e eles não quebram por explosão, por outros players nem por pistão, enquanto o item estiver lá (perguntas 673 a 676).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Escopo | Só os blocos ligados a um Signal Lock ou Signal Keypad; blocos inquebráveis em qualquer lugar do mundo ficaram de fora (regra 2) | 672 |
| Limites | Até 16 blocos por item, dentro de 4 blocos dele, no config, em [`limites.md`](../limites.md) | 673 |
| Escolha | Um modo novo do Configurator, `Protect`: o clique no item o seleciona, e cada clique num bloco liga ou desliga a proteção, com `Protected (3/16)`. Com o Configurator na mão, os blocos protegidos têm um contorno. O `Copy` ficou para a cópia de todos os itens (#154). Os detalhes do modo `Protect` fecham no refinamento da #146 | 674 e 723 |
| Cobertura | Não quebram por explosão, não são quebrados por outros players no survival (só o dono; no criativo, qualquer um) e não são empurrados por pistões. A porta continua abrindo pelo sinal | 675 |
| Fim | Retirado o item pelo `Wrench`, os blocos perdem a proteção; um bloco quebrado pelo dono sai da lista. A lista fica no NBT do item | 676 |

Os números ficam em [`limites.md`](../limites.md).

## Testes

GameTests no mundo: a escolha pelo `Copy`, a explosão, outro player, o pistão, o limite e o raio, e o fim da proteção; roteiro manual do contorno.

## Fora desta página

- O Signal Lock, em [`fechadura.md`](fechadura.md), e o Signal Keypad, em [`teclado.md`](teclado.md).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 13:30 | A seleção dos blocos vai para o modo `Protect` (pergunta 723). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 672 a 676. |
