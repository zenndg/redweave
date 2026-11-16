# Placa de pressão

A placa de pressão do Redweave, o Signal Pressure Plate (`redweave:signal_pressure_plate`, `Placa de Pressão de Sinal` em português), é uma fonte de sinal que liga enquanto algo está sobre ela, parecida com a placa de pressão do jogo, na mesma ideia da [Alavanca](alavanca.md): aparência 3D mais realista, um indicador de cor, uma luz vermelha desligada e verde ligada, e uma tela parecida com a dos [Módulos de Sinal](modulos-de-sinal.md), em que o player escolhe se o sinal sai sem fio, por um canal da rede, como um [Signal Node](no-de-sinal.md), ou pela redstone, no mundo (pergunta 548). Entra depois do [Signal Cable](signal-cable.md), numa issue própria, a #132. Os termos são os do [`glossario.md`](../glossario.md).

É um dos [Emissores de Sinal](emissores.md) e segue o contrato comum da família: força, luz, saída, tempo, rede `Offline` e mensagem (perguntas 627 a 629).

**Estado:** implementado na #132, com as perguntas 549 a 555, 557, 558, 602 a 611 e 621 a 626 respondidas (regra 9). Entrou depois da #136, o modo de girar do Configurator (pergunta 612).

## Em uma frase

Enquanto algo pisa na placa, ela liga, mostra a luz verde e manda o sinal pela redstone ou por um canal da rede; sem nada sobre ela, volta a desligada, com a luz vermelha (pergunta 548).

## O que já está decidido

| Ponto | Como é | Pergunta |
|---|---|---|
| Função | Fonte de sinal ligada enquanto algo está sobre ela, como a placa de pressão do jogo | 548 |
| Aparência | 3D, mais realista que a do jogo, com um indicador de cor, uma luz vermelha desligada e verde ligada, a mesma nos três itens (pergunta 554) | 548 |
| Tela | Parecida com a dos Signal Modules | 548 |
| Saída | Sem fio, por um canal da rede, como um Signal Node, ou pela redstone, no mundo | 548 |
| Quando entra | Depois do Signal Cable completo, numa issue própria | 548 |

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Pressure Plate`, `redweave:signal_pressure_plate`, `Placa de Pressão de Sinal` em português | 550 |
| Onde fica | Uma parte de placa do multipart, só no chão, como a do jogo | 551 |
| Família | Um Signal Module de fonte, como o Signal Randomizer e o Weather Detector: a tela, o dono, a rede e a saída por borda ou canal vêm da base comum, e ele só implementa a sua regra | 549 |
| Força | Ajustável na tela, de 1 a 15, com 15 como padrão | 552 |
| Saída pela redstone | Forte no bloco de apoio e pela borda da frente, como os Signal Modules | 553 |
| Saída sem fio | Um canal da rede, como a saída dos Signal Modules (`Connection`) | 548 e 549 |
| Indicador | Uma luz só, que troca de cor: vermelha desligado e verde ligado. A alavanca, o botão e a placa de pressão usam o mesmo indicador | 554 |
| Dono, rede e Configurator | Iguais aos Signal Modules: dono, rede escolhida, `Open` abre a tela | 555 |
| `Toggle` | Troca o modo, na ordem `Players`, `Passive`, `Hostile` e `Items`, com `<nome>: <modo>`, como no Weather Detector | 555, 603 e 621 |
| O que detecta | Um modo por vez, num seletor da tela: `Players`, `Passive`, `Hostile` ou `Items`, com a saída na força escolhida, num item só, como o modo do Weather Detector (pergunta 459). `Players` é o padrão | 557, 602, 604 e 621 |
| Ordem | Alavanca (#130), botão (#131), o modo de girar do Configurator (#136) e placa de pressão (#132) | 558 e 612 |

## Uso e comportamento

As decisões do [Signal Lever](alavanca.md) que valem para a placa: a força na chave `strength`, a luz pelo estado, a saída no tick seguinte, a saída funcionando com a rede `Offline`, a mensagem só no `Toggle` e os testes nas três fronteiras (perguntas 578, 581, 583 a 585, 587 e 606).

| Ponto | Como é | Pergunta |
|---|---|---|
| Mão | O clique à mão não faz nada, sem mensagem, como a placa do jogo. Quem pisa pode ser qualquer player ou mob do modo, não só o dono | 606 |
| O que conta | `Players` (`Players`): qualquer player, menos o espectador. `Passive` (`Passivos`): toda criatura viva que não é player nem tem a marca `Enemy` do jogo, como animais, aldeões, golems e peixes. `Hostile` (`Hostis`): toda criatura com a marca `Enemy`, como zumbi, creeper e slime, incluindo o enderman e o piglin zumbificado. `Items` (`Itens`): itens soltos no chão. Flechas, outras entidades e o suporte de armadura não contam | 605, 621 e 624 |
| Detecção | Liga no contato, na área da placa (10 × 10 px) até 4 px acima. Enquanto ligada, confere de novo a cada `pressure_plate.check_ticks` (20 ticks, 1 s) e solta quando não há mais nada do modo sobre ela. Entidades mortas e as que o jogo diz que ignoram gatilhos de bloco não contam | 607 |
| Save | `on`, `strength` e `mode` (`players`, `passive`, `hostile` ou `items`), fixados na #26 | 608 e 621 |
| Som | O da placa de pressão de pedra do jogo (`STONE_PRESSURE_PLATE_CLICK_ON` e `_OFF`) | 609 |
| Visual | A sapata afunda quando ligada e fica 0,25 px acima da placa, o que parece rente sem piscar, e a luz fica verde | 609 e 625 |
| Rótulo | O nome e, embaixo, `On` ou `Off`, o modo e o canal | 609 |
| Fora do chão | Recusada: o item fica na mão, e a action bar mostra `The Signal Pressure Plate only goes on the floor.` (`A Placa de Pressão de Sinal só vai no chão.`) | 611 e 626 |
| Saída | Pela borda: forte no bloco de apoio e no bloco da frente. Por um canal: a borda fica em 0. A saída muda no tick seguinte | 553, 587 e 606 |
| `Rotate` | O modo `Rotate` do Configurator gira a placa nos dois sentidos, e a sapata e a luz acompanham a frente | 616 |
| Cabo | Um Signal Cable na borda da frente troca sinal com a placa, e o braço dele encosta nela | 566 e 575 |

## Custo por tick

A regra 18 pede que nada rode à toa a cada tick (pergunta 607):

| Estado | O que a placa faz |
|---|---|
| Desligada | Não procura nada. O jogo avisa o espaço a cada tick em que uma entidade está dentro dele (`entityInside`), e a placa só olha essa entidade: se ela encosta na área e é do modo, a placa liga na hora |
| Ligada | Conta `pressure_plate.check_ticks` e, ao chegar a zero, faz uma busca só na área pequena da placa. Se ainda há algo do modo, recomeça a conta; se não, desliga |
| Ao carregar | A conta não vai para o save, então uma placa salva ligada confere no primeiro tick e desliga se não há nada sobre ela |

## Tela

A Tela do Signal Pressure Plate abre com o Configurator no `Open`, como a dos outros módulos; numa rede `Offline`, ela não abre, e só o `Toggle` funciona (perguntas 402 e 578). O desenho está em [`telas.md`](telas.md#tela-do-signal-pressure-plate).

| Parte | Como é | Pergunta |
|---|---|---|
| Esquerda | `On` grande, em verde, ou `Off` apagado, como a luz da placa | 609 |
| `Mode` | Os 4 modos em botões, em duas linhas de dois: `Players` e `Passive` em cima, `Hostile` e `Items` embaixo, com o escolhido aceso | 602 e 621 |
| `Strength` | A força, de 1 a 15, entre `−` e `+`, com o texto da alavanca | 552 |
| `Output` | O seletor `Edge` ou `Channel`, com a grade das cores, como os outros módulos | 548 e 549 |
| Partes comuns | O título, o nome, a rede e o cadeado, da [tela comum](modulos-de-sinal.md#tela-comum) | 400 |

Em português, `Placa de Pressão de Sinal`, `Ligada`, `Desligada`, `Modo`, `Players`, `Passivos`, `Hostis`, `Itens`, `Força` e `Saída` (pergunta 622). `On`, `Off` e `Strength` usam as chaves da alavanca, no feminino, como "placa".

## Mensagens

Na action bar, em inglês e em português:

| Ação | Mensagem | Pergunta |
|---|---|---|
| `Toggle` | `<nome>: <modo>`, como `<nome>: Passive` (`<nome>: Passivos`), também com a rede `Offline` | 603 e 621 |
| Colocar fora do chão | `The Signal Pressure Plate only goes on the floor.` (`A Placa de Pressão de Sinal só vai no chão.`) | 611 e 626 |
| Clique à mão | Nenhuma | 606 |

O rótulo do Configurator mostra o nome e, embaixo, o estado e o modo, como `On · Players` (`Ligada · Players`), e o canal da saída, se ela estiver num canal (pergunta 609).

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), dentro de `data` no contêiner `redweave:part_space`:

| Chave | O que guarda | Pergunta |
|---|---|---|
| `on` | Se a placa está ligada, um booleano | 608 |
| `strength` | A força, de 1 a 15. Um valor fora da faixa vai para o log e volta a 15 (regra 14) | 584 e 608 |
| `mode` | O modo: `players`, `passive`, `hostile` ou `items`. Um valor inválido vai para o log e volta a `players` (regra 14) | 608 e 623 |
| `output` | A conexão da saída: `edge` ou a chave da cor do canal | 549 |

Os campos comuns `front` e `powered` seguem a base (pergunta 560). As chaves ficam fixadas na #26 (regra 15).

## Desenho

| Ponto | Como é | Pergunta |
|---|---|---|
| Placa | 10 × 10 px e 2 de espessura, com a moldura de cobre, a seta da saída, que fica vermelha com a placa ligada, e um rebaixo escuro sob a sapata | 551 e 554 |
| Sapata | De deepslate, 6 × 6, com borda de cobre e grade antiderrapante, com 1 px de altura solta e 0,25 px ligada | 609 e 625 |
| Luz | A do Signal Lever, de 2 × 2 × 1, vermelha desligada e verde ligada, que brilha no escuro | 554 e 581 |
| Mira | A placa e uma caixa sobre a sapata, de 6 a 12 px na largura e até 3 px de altura a partir da face | 571 |
| Modelos | Gerados pelo datagen: desligada e ligada, com e sem o giro de um quarto de volta | 534 |

## Código

| Camada | Classes |
|---|---|
| Domínio | `domain/module/PlateMode`: os 4 modos, na ordem do `Toggle`, com a chave do save. `SignalPressurePlate`: o estado, a força e o modo, ligado quando algo do modo está sobre ela, e a saída, que é a força ligada e 0 desligada |
| Aplicação | `application/ChangePressurePlate`: só o dono muda a força e o modo pela tela |
| Config | `config/NeoForgePressurePlateCheck`, com a seção `pressure_plate` do `RedweaveConfig` |
| Parte | `block/part/PressurePlatePart`, `PressurePlateSave` e `PressurePlateShapes`, sobre o `ModulePart`, com `StrengthSave` e `RaisedShapes` divididos com a alavanca e o botão. `PlateSensing` classifica cada entidade num modo. `SteppedPart` é o gancho que o `PartSpaceBlock.entityInside` chama |
| Item | `item/PressurePlateItem`, que recusa as faces fora do chão pelo gancho `refusalOn` do `PartItem` |
| Tela | `menu/PressurePlateMenu` e as classes `PressurePlateScreen*` e `PressurePlateAction`, e `client/screen/PressurePlateScreen` |
| Datagen | `datagen/PressurePlateModels`, com `PlatePieces`, `PressurePlateEnglishTexts` e `PressurePlatePortugueseTexts` |
| Testes | `SignalPressurePlateTest`, `ChangePressurePlateTest`, `SignalPressurePlateGameTests`, `PressurePlatePartGameTests` e `PressurePlateMenuGameTests` |

## Testes

- **JUnit:** a regra da placa em `domain/module/`: os modos, ligar e desligar pelo que está sobre ela, o `Toggle`, a força e o dono no caso de uso.
- **GameTests:** o player, os mobs passivos e hostis (vaca, aldeão e zumbi), o item, a flecha, o modo errado, a soltura depois da conferência, a recusa fora do chão, o `Toggle` com a rede `Offline`, a força, a saída pela borda e pelo canal, o save, o cabo e a aba criativa.
- **Roteiro manual:** a arte, a luz e o som, nos passos de [`roteiro-de-teste-manual.md`](../desenvolvimento/roteiro-de-teste-manual.md#fase-2-o-signal-pressure-plate) (perguntas 583 e 606).

## Arte

A arte segue as perguntas 185 e 186: o agente escreveu o prompt em `tmp/arte/signal_pressure_plate/` (pergunta 550), o desenvolvedor gerou as imagens, e o agente as conferiu. As texturas são `signal_pressure_plate_front`, `signal_pressure_plate_front_on` e `signal_pressure_plate_parts`, no bloco, e `signal_pressure_plate`, no item. A lateral da placa é a do Signal Timer, e a luz, a do Signal Lever.

## Fora desta página

- A alavanca, em [`alavanca.md`](alavanca.md), e o botão, em [`botao.md`](botao.md).
- A receita, na #24, com as dos outros itens da `v0.1.0`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 08:01 | Aponta para a família dos emissores (pergunta 627). |
| Zennd | 2026-11-15 15:07 | Marca a placa como implementada na #132, com o custo por tick, a tela, as mensagens, o save, o desenho, o código e os testes. |
| Zennd | 2026-11-15 15:05 | Registra as decisões 624 a 626, do suporte de armadura, da sapata e da mensagem. |
| Zennd | 2026-11-15 14:49 | Troca os modos por `Players`, `Passive`, `Hostile` e `Items` (pergunta 621). |
| Zennd | 2026-11-15 14:26 | Fecha o conceito com as respostas 602 a 612: os modos, o Toggle, a detecção, o save, o visual e a ordem. |
| Zennd | 2026-11-14 21:22 | Fecha o conceito com as respostas 549 a 558. |
| Zennd | 2026-11-14 21:14 | Cria o conceito, com a decisão 548 e as perguntas pendentes 549 a 555, 557 e 558. |
