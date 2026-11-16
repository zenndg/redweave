# Emissores de Sinal

Os Signal Emitters (`Emissores de Sinal` em português) são a família dos [Signal Modules](modulos-de-sinal.md) de onde o sinal nasce por um gatilho no mundo, como o clique, o aperto ou algo pisando: não têm entrada, só uma saída, e ligam quando o seu gatilho acontece (perguntas 549 e 627). O [Signal Randomizer](signal-randomizer.md) e o [Weather Detector](weather-detector.md) também não têm entrada, mas ficam fora da família: eles seguem uma regra própria, de tempo ou de clima, e não o contrato de força e luz (pergunta 632). Hoje são o [Signal Lever](alavanca.md), o [Signal Button](botao.md) e o [Signal Pressure Plate](placa-de-pressao.md), e outros itens vão entrar nela. Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #139, com as perguntas 627 a 631 respondidas (regra 9), sem mudança de comportamento, de save nem de IDs.

## Em uma frase

Um emissor é um Signal Module sem entrada, acionado por um gatilho, que, quando o seu gatilho acontece, liga, acende a luz verde e manda a força escolhida pela borda ou por um canal da rede (perguntas 548 e 629).

## Contrato comum

Todo emissor segue estas regras, além da base dos [Signal Modules](modulos-de-sinal.md) (pergunta 629):

| Ponto | Como é | Pergunta |
|---|---|---|
| Força | Ajustável na tela, de 1 a 15, com 15 como padrão, salva na chave `strength` | 552 e 584 |
| Luz | Uma luz só, vermelha desligado e verde ligado, que mostra sempre o estado do emissor, mesmo quando o sinal não chega ao canal | 554 e 581 |
| Saída | Uma só: pela borda, forte no bloco de apoio e no bloco da frente, ou por um canal da rede | 553 |
| Tempo | A saída muda no tick seguinte ao gatilho, pelo tick comum dos módulos | 587 |
| Rede `Offline` | O emissor funciona; o canal só não conta enquanto a rede está `Offline` | 578 |
| Mensagem | Só no `Toggle` do Configurator; o gatilho em si não mostra mensagem | 585 |
| Dono, rede e Configurator | Os da base dos Signal Modules: dono, rede escolhida, `Open` abre a tela, `Rotate` gira, `Wrench` retira | 555, 610 e 619 |

## O que cada emissor define

| Ponto | Signal Lever | Signal Button | Signal Pressure Plate |
|---|---|---|---|
| Gatilho | O clique do dono liga e desliga | O clique do dono aperta, e volta sozinho depois do tempo ligado | Algo do modo escolhido sobre a placa |
| `Toggle` | Liga e desliga | Aperta | Troca o modo |
| Som | `LEVER_CLICK` | O do botão de pedra | O da placa de pressão de pedra |
| Peça 3D | Base, cabo inclinado e pomo | Anel e botão que afunda | Sapata que afunda |
| Onde fica | Qualquer face | Qualquer face | Só no chão |
| Página | [`alavanca.md`](alavanca.md) | [`botao.md`](botao.md) | [`placa-de-pressao.md`](placa-de-pressao.md) |

## Código

O save, os IDs e o comportamento não mudaram com a base comum (pergunta 630, regra 15):

| Camada | O que fica lá |
|---|---|
| Domínio | `domain/module/Emitter`: a interface comum, com `on()`, `strength()` e a saída, que é a força ligado e 0 desligado, e o ajuste da força de 1 a 15 (`stepped`). O `SignalLever`, o `SignalButton` e o `SignalPressurePlate` a implementam, cada um com a sua validação |
| Parte | `block/part/EmitterPart`, classe abstrata filha de `ModulePart`: a saída pela borda ou por um canal e o save da chave `output`; a luz e o visual pelo estado (`LOOKS`); os ganchos fixos (alimenta o bloco de apoio e o `Toggle` funciona com a rede `Offline`); a escrita da saída a cada tick, com o gancho `afterOutput` para o que vem depois, como a contagem do botão e a nova conferência da placa; a checagem do dono no clique à mão (`byOwner`); e o som do gatilho (`click`). A força continua salva pelo `StrengthSave` |
| Tela | `menu/EmitterScreenActions`: o começo comum das ações da tela no servidor, que acha o emissor, o módulo, quem pede e o servidor, e move a saída pela borda ou por um canal |

Ficam em cada emissor, de propósito:

- **A linha da força nas telas do cliente.** As três telas têm a mesma linha `Strength`, mas nenhum GameTest confere uma tela, e uma mudança só de desenho passaria sem ser vista.
- **O clique com um item** (`takesAnyItem`), só na alavanca e no botão, porque a placa de pressão não aceita clique (pergunta 606).
- **A leitura do save e a abertura da tela,** porque cada emissor monta o seu próprio tipo e o seu próprio menu.

## Um emissor novo

Um emissor novo herda o contrato e a base. A rodada de perguntas dele só trata do que é diferente: o gatilho, o `Toggle`, o som, a peça 3D, onde ele fica e o seu save próprio (pergunta 629).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 08:10 | Marca a família como implementada na #139 e descreve o código: `Emitter`, `EmitterPart` e `EmitterScreenActions`. |
| Zennd | 2026-11-16 08:01 | Cria o conceito da família, com as decisões 627 a 631. |
