# Cartão

O Signal Card (`redweave:signal_card`, `Cartão de Sinal` em português) é um cartão de acesso: o dono o vincula ao [Signal Lock](fechadura.md), ao [Signal Keypad](teclado.md) ou ao [Signal Gate](portao.md), e qualquer player que o tenha na mão abre os itens vinculados (pergunta 668). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 668 a 671 respondidas (regra 9), a implementar na #145.

## Em uma frase

O dono vincula o cartão a um ou mais itens pelo slot da tela, e quem tiver o cartão na mão abre esses itens, e só eles (perguntas 668 a 670).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Card`, `redweave:signal_card`, `Cartão de Sinal` em português | 668 |
| Identidade | Um ID único num data component (regra 7). Não dá para copiar, e cada cartão fabricado ganha um ID novo | 668 |
| Receita | Na #24; até lá, vem do criativo | 601 e 668 |
| Vínculo | O dono coloca o cartão num slot exclusivo da tela do item; o vínculo é feito, e o cartão volta ao inventário. Um cartão pode ser vinculado a vários itens | 669 |
| Lista | O item guarda a lista dos IDs aceitos, com o limite em [`limites.md`](../limites.md). O dono revoga na tela do item, sem precisar do cartão | 669 |
| Uso | Clicar num item vinculado com o cartão na mão abre o item pelo tempo aberto do dono. No Signal Keypad, é como a senha certa e exige sinal na entrada; no Signal Lock, é como o pulso do `Toggle` e funciona sempre | 670 |
| Cartão errado | `This card is not linked here.`, sem contar como erro de senha | 671 |

Os números ficam em [`limites.md`](../limites.md).

## Testes

JUnit do vínculo e da revogação; GameTests no mundo (o vínculo pelo slot, o uso por outro player, o cartão errado, a cópia que não abre); roteiro manual da tela.

## Fora desta página

- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 668 a 671. |
