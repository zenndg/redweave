# Multipart

O multipart do Redweave deixa várias **partes** dividirem o mesmo espaço de bloco: um Signal Node na parede e um [Signal Cable](signal-cable.md) no chão, por exemplo (perguntas 502 e 511). Vale para todo o mod: o Signal Cable, o [Signal Node](no-de-sinal.md) e os [Módulos de Sinal](modulos-de-sinal.md) são partes. Esta página descreve o sistema, que entra na #44 junto com o Signal Cable, na `v0.1.0` (perguntas 516 e 524). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #44, com o Signal Node, os 4 Signal Modules e o Signal Cable como partes.

## Em uma frase

Um espaço de bloco vira um bloco contêiner, `redweave:part_space`, que guarda até 6 partes, uma por face, com no máximo 2 cabos, e cada parte responde por si: forma, desenho, tick, save e clique (perguntas 512, 513 e 523).

## Por que existe

O jogo guarda um bloco só por espaço. Para um cabo passar no chão de um espaço que tem um nó na parede, os dois precisam ser partes de um mesmo bloco. O Redweave usa um sistema próprio, sem a CB Multipart, para não ganhar uma dependência obrigatória (regra 39) e para o port para o 26.x depender só do mod (pergunta 512).

## Sistema

| Ponto | Como é | Pergunta |
|---|---|---|
| Implementação | Própria: um bloco contêiner cujo `BlockEntity` guarda as partes. A forma, o desenho, o tick e o save delegam a cada parte, e a regra de sinal continua no domínio | 512 |
| Código | A ocupação fica em `domain/part/` (`PartSpace`, `PartKind` e `Fit`). No adaptador, o contêiner é o `PartSpaceBlock` com o `PartSpaceBlockEntity`, em `block/`, e as partes ficam em `block/part/`, com uma interface `Part` curta (forma, sinal, desenho, save e drop), o tick e o clique em interfaces separadas (`TickingPart` e `HandPart`), e uma classe por tipo: `NodePart`, `CablePart` e as 4 partes de módulo sobre o `ModulePart`. Uma parte é endereçada pela posição e pela face (`PartSpot`) | 537, 538 e 542 |
| Desenho | Um modelo dinâmico, o `PartSpaceModel`, junta os modelos JSON de cada parte na malha do chunk e só é refeito quando uma parte muda, sem custo por frame | 536 e 573 |
| Drop | No código: cada parte devolve o próprio item, e o `part_space` não tem loot table | 539 |
| Partes | O Signal Cable, o Signal Node e os Signal Modules (o Signal Timer, o Signal Inverter, o Weather Detector, o Signal Randomizer, o Signal Lever, o Signal Button e os próximos) | 511 |
| Ocupação | Uma parte por face: em cada uma das 6 faces do espaço, ou um cabo, ou um nó, ou um módulo, nunca dois na mesma face. No máximo 2 cabos por espaço | 498 e 513 |
| Clique numa parte | Com um item de parte na mão, clicar numa parte que já existe mira o mesmo espaço, na face oposta à clicada, como se a parte fosse transparente. Um nó clicado no topo de um cabo no chão mira a face `down`, já ocupada | 545 |
| Face ocupada | Colocar numa face ocupada, ou um terceiro cabo num espaço, não coloca a parte: o item fica na mão, e a action bar mostra `This face is taken.` ou `This space already has 2 cables.`, em inglês e em português | 533 |
| Sinal no espaço | Partes do mesmo espaço trocam sinal como vizinhas: um cabo no chão se liga ao nó ou ao módulo na parede pela curva interna. O nó lê o cabo do espaço como o seu bloco de apoio, e o módulo troca sinal com ele só pela borda de trás ou da frente. Fora disso, o nó continua lendo e emitindo no bloco de apoio, e o módulo nas bordas da placa. No espaço vizinho, o nó troca sinal só com um cabo ou um pó na mesma face | 514, 566 e 574 |
| Mira | Só a parte sob a mira responde: quebrar tira só ela e devolve só o item dela, o Configurator abre, gira, alterna ou retira só ela, e o contorno de seleção mostra só essa parte | 515 |
| Colisão | Nenhuma: o player e as criaturas passam, e só a mira enxerga a forma de cada parte, como o pó de redstone do jogo | 517 |
| Dono | Cada parte tem o seu dono, como hoje o nó e os módulos | 378 e 526 |

## IDs e save

| Ponto | Como é | Pergunta |
|---|---|---|
| Bloco | Um bloco contêiner com ID próprio, `redweave:part_space` | 523 |
| Itens | Mantêm os IDs de hoje: `signal_node`, `signal_timer`, `signal_inverter`, `weather_detector`, `signal_randomizer` e os próximos módulos, e `signal_cable` | 523 |
| NBT | O contêiner guarda `parts`, uma lista em que cada parte tem `type`, o ID do item, `face`, o lado do espaço onde ela encosta, como `down`, e `data`, com o NBT da parte (perguntas 76, 165, 411, 450, 467 e 481). O cabo guarda em `data` só `color` e `owner` | 523, 543, 544 e 567 |
| Migração | Nenhuma: o Signal Node e os 4 módulos existentes viram partes antes da primeira versão publicada, e nada foi publicado. Tudo fica fixado na #26 (regra 15) | 516 |

## Conversão do que já existe

Na #44, o Signal Node e os 4 Signal Modules entregues até a #109 deixaram de ser blocos próprios e viraram partes, sem mudar o que o player vê nem o que eles fazem. Os módulos novos, como o Signal Lever da #130 e o Signal Button da #131, já nascem como partes (pergunta 516). As regras desses blocos, como a placa, o dono, a rede, as conexões, o Configurator e as telas, continuam as das páginas de cada um.

## Testes

- **JUnit:** as regras de ocupação, uma parte por face e até 2 cabos (`PartSpaceTest`).
- **GameTests:** os atuais do nó e dos módulos passando como partes; duas partes no mesmo espaço; quebrar só a parte mirada; o save do contêiner; os rótulos deslocados.
- **Roteiro manual:** a mira, o contorno de seleção e o desempenho (pergunta 527), na seção do multipart e do Signal Cable do [roteiro de teste manual](../desenvolvimento/roteiro-de-teste-manual.md).

## Visual das partes

Até a #44, o Signal Node e os módulos guardavam o seu visual em estados do bloco: `facing`, `front`, `powered`, `running`, `burnt`, `mode` e `out_of_range` (perguntas 72, 411, 428, 455, 471 e 488). Um bloco contêiner tem um só estado para o espaço inteiro e não comporta as combinações de até 6 partes. Por isso, cada parte guarda o seu visual no NBT dela, sincronizado com o cliente, e o contêiner monta o desenho juntando o modelo de cada parte. Os estados viraram campos da parte, com os mesmos nomes, fixados na #26 (regra 15), e o que o player vê não mudou (pergunta 534). O `facing` do Signal Node não vai para o save, porque é sempre o oposto da `face` da parte (pergunta 546), e o dos Signal Modules também não, que guardam o `front` (pergunta 559) e o `powered`, nos 4, numa regra só (pergunta 560). Com dois nós no mesmo espaço, cada rótulo se desloca para o lado da sua parte (pergunta 547).

## Fora desta página

- O Signal Cable, em [`signal-cable.md`](signal-cable.md).
- A prévia de colocação, na #33, e o Jade e o The One Probe, na #23, que passam a mostrar as partes.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 14:04 | Leva o Signal Button à lista de partes. |
| Zennd | 2026-11-15 13:19 | Leva o Signal Lever à lista de partes. |
| Zennd | 2026-11-15 09:33 | Marca o sistema como implementado na #44 e atualiza o código, o sinal no espaço e os testes (perguntas 542, 566, 567, 573 e 574). |
| Zennd | 2026-11-14 22:13 | Registra a decisão 560, do `powered` dos módulos. |
| Zennd | 2026-11-14 21:23 | Registra a decisão 559, do `facing` dos módulos. |
| Zennd | 2026-11-14 20:58 | Registra as decisões 546 e 547, do `facing` do nó e dos rótulos. |
| Zennd | 2026-11-14 20:40 | Registra as decisões 543 a 545, do NBT e do clique numa parte. |
| Zennd | 2026-11-14 20:27 | Registra as decisões 536 a 539, do código, do desenho e do drop. |
| Zennd | 2026-11-14 19:34 | Fecha a pendência do visual das partes (pergunta 534). |
| Zennd | 2026-11-14 19:13 | Cria o documento, com as decisões 502, 511 a 517, 523, 524 e 533. |
