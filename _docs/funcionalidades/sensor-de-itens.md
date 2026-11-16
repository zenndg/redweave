# Sensor de Itens

O Signal Item Sensor (`redweave:signal_item_sensor`, `Sensor de Itens de Sinal` em português) é uma placa grudada num inventário, como um baú ou um barril, que emite sinal quando ele tem exatamente a quantidade escolhida de um item (perguntas 689 a 692). Ele só lê: não guarda, não filtra nem move itens, que ficam com o baú, os funis e os mods de logística (regra 2). Entrou na #148, como o primeiro dos itens novos (pergunta 698). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #148, com as perguntas 689 a 698 e 700 a 704 respondidas (regra 9).

## Em uma frase

O dono escolhe um item e uma quantidade; quando o inventário onde a placa está grudada tem exatamente essa quantidade daquele item, somando todos os slots, o sensor emite a força escolhida pela borda ou por um canal (perguntas 692 e 696).

## Origem

Nasceu da ideia do Receptor de Itens. Ficaram só as funções de sinal: o item esperado, a quantidade, emitir a força e a configuração só pelo dono. Saíram o slot próprio, a tela pública de depósito, o filtro, o depósito parcial, a retirada por quem depositou, a automação de entrada e de saída, o `Auto reject` e as permissões de retirada, porque são armazenamento e logística de itens (regra 2) e trazem riscos de posse e de duplicação (pergunta 689).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Item Sensor`, `redweave:signal_item_sensor`, `Sensor de Itens de Sinal` em português | 691 |
| Onde fica | Uma parte de placa do multipart, em qualquer face de um bloco com inventário: baú, barril e os de outros mods, pela capability de itens do NeoForge. Lê só o bloco de apoio, pelo lado em que a placa encosta, sem pôr nem tirar nada. Num bloco sem inventário, a colocação é recusada, o item fica na mão e a action bar mostra `This needs a container.` | 690 e 694 |
| Disparo | Com exatamente N itens do tipo esperado, somando todos os slots. Com mais ou com menos, a saída desliga | 692 |
| Quantidade | De 1 até `item_sensor.max_count`, no config (veja [`limites.md`](../limites.md)); não tem teto de uma pilha, porque o inventário soma vários slots | 693 |
| Comparação | Só o tipo do item: diamante é diamante, sem olhar nome, encantamento ou dano | 697 |
| Leitura | A cada `item_sensor.check_ticks`, no config, e na hora depois de uma mudança na tela. A saída é escrita a cada tick pelo tick comum dos módulos, e a rede só é avaliada quando ela muda (regra 18). Com o chunk descarregado, a contagem vale 0, e o sensor nunca carrega o bloco (regra 6) | 695 |
| Saída | Pela borda da frente, com força no bloco da frente, ou por um canal da rede, com a força de 1 a 15. O bloco de apoio, que é o inventário, não recebe sinal | 696 |
| Padrões | Sem item, com a quantidade 1, a força 15 e a saída pela borda da frente: um sensor novo nunca dispara até o dono escolher o item | 701 |
| Quem configura | Só o dono, com o Configurator; os outros players veem a tela só para leitura | 689 |
| Luz | A luz vermelha e verde dos [Emissores de Sinal](emissores.md): verde enquanto o sensor está ligado, também com a saída num canal. Ela segue o estado de saída da placa (`powered`), que vai ao cliente, e não a contagem, que só o servidor conhece | 697 |
| Item no visor | O item esperado aparece pequeno no visor da placa, desenhado pelo cliente | 697 |
| `Toggle` | Não faz nada e mostra `Signal Item Sensor has nothing to toggle.` | 697 |
| Rótulo | O nome e, embaixo, `On` ou `Off` (`Ligado` ou `Desligado`) e o canal da saída, como os módulos, sem a contagem | 703 |
| Dono, rede, `Open`, `Rotate` e `Wrench` | Como os [Signal Modules](modulos-de-sinal.md) | 689 |

## Não é um emissor

O sensor não tem entrada pela borda, mas não entra na família dos [Emissores de Sinal](emissores.md): ele não é acionado por um gatilho no mundo, e sim pela leitura do inventário, como o Weather Detector pelo clima (pergunta 632). E todo emissor energiza o bloco de apoio, que aqui é o próprio inventário, enquanto a saída do sensor é só pela borda da frente ou pelo canal (pergunta 696). Por isso a parte herda direto de `ModulePart`, como o Weather Detector.

## Tela

Abre com o Configurator no modo `Open`. O detalhe está em [`telas.md`](telas.md#tela-do-signal-item-sensor).

| Parte | Como é | Pergunta |
|---|---|---|
| Esquerda | `On` grande, em verde, ou `Off` apagado, e embaixo a contagem atual, como `7 / 10` | 696 |
| `Item` | Um slot fantasma: o dono clica com um item no cursor para escolher, e o item continua no cursor; com o cursor vazio, o clique limpa o item esperado, e o sensor desliga. A dica do slot diz isso. Sem item, mostra `None` | 696 e 702 |
| `Count` | A quantidade, entre `−` e `+`; com Shift, muda de 10 em 10 | 696 |
| `Strength` | A força, de 1 a 15, entre `−` e `+` | 696 |
| `Output` | O seletor `Edge` ou `Channel`, como os módulos | 696 |
| Inventário | O inventário do dono embaixo, para ele pegar o item com o cursor; a tela fica com cerca de 260 px de altura | 704 |

O servidor nunca confia num item nomeado pelo cliente: ao clicar no slot fantasma, ele lê o que está no cursor do próprio menu (regras 16 e 17).

## Mensagens

| Quando | Inglês | Português |
|---|---|---|
| Colocar num bloco sem inventário | `This needs a container.` | `Isto precisa de um baú ou outro inventário.` |
| `Toggle` | `Signal Item Sensor has nothing to toggle.` | `O Sensor de Itens de Sinal não tem o que alternar.` |

## Save

As chaves ficam no NBT da parte, dentro de `data`, além das chaves comuns dos módulos, e são fixadas na #26 (pergunta 700, regra 15):

| Chave | O que guarda | Valor inválido |
|---|---|---|
| `item` | O ID do item esperado, como `minecraft:diamond`; fica ausente sem item | Um item que o jogo não conhece é descartado, com aviso no log (regra 14) |
| `count` | A quantidade esperada | Abaixo de 1, volta a 1, com aviso no log |
| `strength` | A força, de 1 a 15 | Volta a 15, com aviso no log |
| `output` | A saída: a borda ou o canal | — |

A contagem lida não vai para o save: o sensor lê o inventário de novo ao carregar.

## Código

| Camada | Classe | O que faz |
|---|---|---|
| Domínio | `domain/module/ItemSensor` | A regra: ligado só com o item escolhido e a contagem exata; a saída é a força ligada e 0 desligada; a quantidade presa de 1 ao máximo do config |
| Aplicação | `application/ChangeItemSensor` | Só o dono muda o item, a quantidade e a força |
| Config | `config/NeoForgeItemSensorLimits` | `item_sensor.max_count` e `item_sensor.check_ticks` |
| Parte | `block/part/ItemSensorPart`, `ItemSensorSave` e `ItemSensorReading` | A placa, o save e a leitura do inventário pela capability de itens do NeoForge, sem mexer em nada |
| Item | `item/ItemSensorItem` | A recusa num bloco sem inventário, pelo gancho `PartItem.refusalOn` |
| Tela | `menu/ItemSensor*` e `client/screen/ItemSensorScreen` | O menu, com o slot fantasma e o inventário do dono, e a tela |
| Cliente | `client/ItemSensorRenderer` | Desenha o item esperado no visor. É o primeiro `BlockEntityRenderer` do mod, e só trabalha nos sensores com item |
| Datagen | `datagen/ItemSensorModels` e os textos | Os modelos da placa, com o anel do visor, e os textos em inglês e português |

## Arte

A arte seguiu as perguntas 185, 186 e 699: o agente escreveu o prompt em `tmp/arte/signal_item_sensor/`, o desenvolvedor gerou as imagens, e o agente as conferiu. As texturas são `signal_item_sensor_front`, `signal_item_sensor_front_on` e `signal_item_sensor_parts`, em `textures/block/`, e o ícone `signal_item_sensor`, em `textures/item/`. A luz é a do Signal Lever, e a lateral, a do Signal Timer.

## Testes

- **JUnit:** a regra, com a contagem exata, o item esperado e a força, e o caso de uso do dono.
- **GameTests:** a placa num baú e num barril, a recusa sem inventário, a contagem em vários slots, o disparo só no número exato, a cópia do cliente com a luz verde, a saída pela borda e pelo canal, o slot fantasma, o dono e o save.
- **Roteiro manual:** a tela, o slot fantasma, o item no visor, a luz e um inventário de outro mod, nos passos do [roteiro](../desenvolvimento/roteiro-de-teste-manual.md#fase-2-o-signal-item-sensor) (pergunta 697).

## Fora desta página

- O [Signal Lock](fechadura.md) e os outros itens novos, de #141 a #147.
- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 759), e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 759). |
| Zennd | 2026-11-16 11:27 | Leva o conceito ao implementado na #148: o código, a tela, as mensagens, o save, os padrões, o rótulo e a luz (perguntas 700 a 704). |
| Zennd | 2026-11-16 10:12 | Cria o conceito do Signal Item Sensor, com as decisões 689 a 698. |
