# Fechadura

O Signal Lock (`redweave:signal_lock`, `Fechadura de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) de transformação que só abre com a força exata do sinal: ele espera uma força única, a chave, e emite a força definida quando a entrada bate com ela (pergunta 633). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 633 a 642 respondidas (regra 9), a implementar na #141.

## Em uma frase

Com a força da chave na entrada, o Signal Lock abre, acende a luz verde e emite a força escolhida; com qualquer outra força, fica trancado (perguntas 633 e 634).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Lock`, `redweave:signal_lock`, `Fechadura de Sinal` em português, para não confundir com o `Latch` da #111 | 633 |
| Família | Um Signal Module de transformação, com a base comum dos módulos | 633 |
| Regra | Abre só com a força exata da chave; com outras forças, nada | 633 |
| Saída | Ligada enquanto a entrada estiver na força da chave, e desligada quando ela muda, como uma porta lógica | 634 |
| Conexões | A entrada pela borda de trás ou por um canal, e a saída pela borda da frente ou por um canal, escolhidas na tela, como no Signal Inverter | 635 |
| Padrões | A chave e a força emitida começam em 15 e se ajustam de 1 a 15 na tela. A força 0 nunca abre | 636 |
| Segredo | Só o dono vê a chave. O rótulo mostra só `Locked` ou `Unlocked`, e os outros players veem `?` na tela | 637 |
| Visual | A luz dos [Signal Emitters](emissores.md), vermelha trancado e verde aberto, e um cadeado 3D pequeno na placa, que abre quando a chave bate | 638 |
| `Toggle` | Abre por um pulso, com `<nome>: Unlocked`. O dono escolhe a duração na tela: padrão de 1 s, de 2 ticks a 10 s, com os limites em [`limites.md`](../limites.md) | 639 e 640 |
| Retirada | Só o dono, só com o `Wrench` do Configurator. Ninguém quebra à mão no survival, com `Only the owner can remove this with a Configurator.`; no criativo, qualquer um | 641 |
| Explosão | Resiste e protege o próprio bloco de apoio | 641 |
| Cartão | Abre com um [Signal Card](cartao.md) vinculado, como o pulso do `Toggle` | 669 e 670 |
| Proteção da porta | Protege os blocos escolhidos pelo dono, na [proteção dos blocos](protecao.md) | 672 a 676 |
| Ordem | O primeiro dos itens novos, antes do módulo de memória (#110) | 642 |

Os números ficam em [`limites.md`](../limites.md).

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_lock/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

JUnit da regra (a chave, a saída, a força 0); GameTests no mundo (a entrada e a saída pela borda e pelo canal, o pulso do `Toggle`, o segredo, a retirada e a explosão); roteiro manual da arte e da tela (pergunta 642).

## Fora desta página

- O cartão, em [`cartao.md`](cartao.md), e a proteção dos blocos, em [`protecao.md`](protecao.md).
- O `Threshold` (#115), que liga acima de um limiar; o Signal Lock testa igualdade.
- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 633 a 642. |
