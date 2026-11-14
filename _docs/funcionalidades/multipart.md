# Multipart

O multipart do Redweave deixa várias **partes** dividirem o mesmo espaço de bloco: um Signal Node na parede e um [Signal Cable](signal-cable.md) no chão, por exemplo (perguntas 502 e 511). Vale para todo o mod: o Signal Cable, o [Signal Node](no-de-sinal.md) e os [Módulos de Sinal](modulos-de-sinal.md) são partes. Esta página descreve o sistema, que entra na #44 junto com o Signal Cable, na `v0.1.0` (perguntas 516 e 524). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Um espaço de bloco vira um bloco contêiner, `redweave:part_space`, que guarda até 6 partes, uma por face, com no máximo 2 cabos, e cada parte responde por si: forma, desenho, tick, save e clique (perguntas 512, 513 e 523).

## Por que existe

O jogo guarda um bloco só por espaço. Para um cabo passar no chão de um espaço que tem um nó na parede, os dois precisam ser partes de um mesmo bloco. O Redweave usa um sistema próprio, sem a CB Multipart, para não ganhar uma dependência obrigatória (regra 39) e para o port para o 26.x depender só do mod (pergunta 512).

## Sistema

| Ponto | Como é | Pergunta |
|---|---|---|
| Implementação | Própria: um bloco contêiner cujo `BlockEntity` guarda as partes. A forma, o desenho, o tick e o save delegam a cada parte, e a regra de sinal continua no domínio | 512 |
| Código | A ocupação fica em `domain/part/`. No adaptador, as partes ficam em `block/part/`, com uma interface `Part` curta (forma, sinal, tick, save e clique) e uma classe por tipo | 537 e 538 |
| Desenho | Um modelo dinâmico junta os modelos JSON de cada parte na malha do chunk e só é refeito quando uma parte muda, sem custo por frame | 536 |
| Drop | No código: cada parte devolve o próprio item, e o `part_space` não tem loot table | 539 |
| Partes | O Signal Cable, o Signal Node e os Signal Modules (o Signal Timer, o Signal Inverter, o Weather Detector, o Signal Randomizer e os próximos) | 511 |
| Ocupação | Uma parte por face: em cada uma das 6 faces do espaço, ou um cabo, ou um nó, ou um módulo, nunca dois na mesma face. No máximo 2 cabos por espaço | 498 e 513 |
| Face ocupada | Colocar numa face ocupada, ou um terceiro cabo num espaço, não coloca a parte: o item fica na mão, e a action bar mostra `This face is taken.` ou `This space already has 2 cables.`, em inglês e em português | 533 |
| Sinal no espaço | Partes do mesmo espaço trocam sinal como vizinhas: um cabo no chão se liga ao nó ou ao módulo na parede pela curva interna. O nó continua lendo e emitindo no bloco de apoio, e o módulo nas bordas da placa | 514 |
| Mira | Só a parte sob a mira responde: quebrar tira só ela e devolve só o item dela, o Configurator abre, gira, alterna ou retira só ela, e o contorno de seleção mostra só essa parte | 515 |
| Colisão | Nenhuma: o player e as criaturas passam, e só a mira enxerga a forma de cada parte, como o pó de redstone do jogo | 517 |
| Dono | Cada parte tem o seu dono, como hoje o nó e os módulos | 378 e 526 |

## IDs e save

| Ponto | Como é | Pergunta |
|---|---|---|
| Bloco | Um bloco contêiner com ID próprio, `redweave:part_space` | 523 |
| Itens | Mantêm os IDs de hoje: `signal_node`, `signal_timer`, `signal_inverter`, `weather_detector`, `signal_randomizer` e os próximos módulos, e `signal_cable` | 523 |
| NBT | O contêiner guarda uma lista de partes, cada uma com o tipo, a face e o NBT que a parte já tem hoje (perguntas 76, 165, 411, 450, 467 e 481) | 523 |
| Migração | Nenhuma: o Signal Node e os 4 módulos existentes viram partes antes da primeira versão publicada, e nada foi publicado. Tudo fica fixado na #26 (regra 15) | 516 |

## Conversão do que já existe

Na #44, o Signal Node e os 4 Signal Modules entregues até a #109 deixam de ser blocos próprios e viram partes, sem mudar o que o player vê nem o que eles fazem. Os módulos a partir da #110 já nascem como partes (pergunta 516). As regras desses blocos, como a placa, o dono, a rede, as conexões, o Configurator e as telas, continuam as das páginas de cada um.

## Testes

- **JUnit:** as regras de ocupação, uma parte por face e até 2 cabos.
- **GameTests:** os atuais do nó e dos módulos passando como partes; duas partes no mesmo espaço; quebrar só a parte mirada; o save do contêiner.
- **Roteiro manual:** a mira, o contorno de seleção e o desempenho (pergunta 527).

## Visual das partes

Hoje o Signal Node e os módulos guardam o seu visual em estados do bloco: `facing`, `front`, `powered`, `running`, `burnt`, `mode` e `out_of_range` (perguntas 72, 411, 428, 455, 471 e 488). Um bloco contêiner tem um só estado para o espaço inteiro e não comporta as combinações de até 6 partes. Por isso, cada parte guarda o seu visual no NBT dela, sincronizado com o cliente, e o contêiner monta o desenho juntando o modelo de cada parte. Os estados de hoje viram campos da parte, com os mesmos nomes, fixados na #26 (regra 15), e o que o player vê não muda (pergunta 534).

## Fora desta página

- O Signal Cable, em [`signal-cable.md`](signal-cable.md).
- A prévia de colocação, na #33, e o Jade e o The One Probe, na #23, que passam a mostrar as partes.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 20:27 | Registra as decisões 536 a 539, do código, do desenho e do drop. |
| Zennd | 2026-11-14 19:34 | Fecha a pendência do visual das partes (pergunta 534). |
| Zennd | 2026-11-14 19:13 | Cria o documento, com as decisões 502, 511 a 517, 523, 524 e 533. |
