# Glossário

A linguagem do domínio. Os nomes são os mesmos no código, nos documentos e no jogo.

| Termo | Em inglês | O que é |
|---|---|---|
| Sinal | `Signal` | Um valor de 0 a 15, o mesmo intervalo da redstone do jogo |
| Rede | `Network` | O agregado criado ao colocar um Network Controller: cada controlador é uma rede. Tem nome, cor e dono, e reúne os nós e os canais. Só o dono a altera (perguntas 22 e 129) |
| Canal | `Channel` | Uma das 16 linhas de sinal de uma rede, cada uma com uma cor de lã e um nome, que o dono pode mudar. Com várias entradas, vale o sinal mais forte |
| Cor do canal | `ChannelColor` | Uma das 16 cores de lã. Dá a cor dos canais e da rede |
| Última rede usada | `last_used` (save) e `LastUsedNetworkFinder` | A última rede em que o player colocou um nó. Fica destacada na escolha de rede de um nó novo (pergunta 157). Substitui a rede atual, que acabou (pergunta 139) |
| Alcance | — | O raio em blocos a partir do Network Controller, na mesma dimensão (pergunta 23) |
| Online | `Online` | Rede com o controlador colocado e ligada, ou nó funcionando dentro do alcance |
| Offline | `NetworkState.OFFLINE` | Rede desligada pelo dono no Network Terminal: todos os canais valem 0, e só o Terminal age sobre ela (perguntas 132 e 135) |
| Não colocada | `NetworkState.UNPLACED` | Rede com o controlador fora do mundo, quebrado ou perdido: todos os canais valem 0 (perguntas 135 e 146) |
| Descarregado | `Unloaded` | Nó num chunk descarregado: a entrada mantém o último valor (perguntas 13 e 29) |
| Fora de alcance | `Out of range` | Nó fora do raio ou em outra dimensão: não conta e vale 0 (perguntas 23 e 29) |
| Sem rede | `No network` | Nó sem rede, ou numa rede `Unplaced`. Um nó novo nasce assim e não pode ser configurado até ter rede (pergunta 139) |
| Nó | `Node` | Peça grudada numa face de um bloco. Lê ou emite redstone ali, por slots de canal. Tem um nome, que nasce com os 8 primeiros caracteres do ID (perguntas 142 e 144); sem nome próprio, mostra esse padrão (`displayName`, pergunta 206) |
| Tipo do nó | `NodeKind` | O que um nó faz pelos slots ativos: `In`, `Out`, `In/Out` ou `—`, na coluna `Type` das listas de nós (perguntas 283, 337 e 339) |
| Escolha de rede | `NetworkChoices` | A lista das redes `Online` de um player na Tela do Nó, com a última usada no topo, para quem colocou um nó novo e para o dono que troca o nó de rede (perguntas 139, 141 e 203) |
| Slot de canal | `ChannelSlot` | A configuração de um nó para um canal: status, direção, face do bloco e canal da rede. Não tem lógica: o nó só lê e emite (pergunta 308) |
| Direção | `Direction` | Entrada, quando o nó lê o bloco, ou saída, quando o nó emite no bloco |
| Signal Modules | — | `Módulos de Sinal` em português: a família de placas grudadas na face de um bloco, como o Signal Node, com base comum, que guarda toda a lógica de sinal. Cada uma tem um dono, quem a colocou, e escreve direto num canal, sem nó. Cada módulo só implementa a sua regra (perguntas 297, 300, 308, 313, 314, 375, 378, 410 e 415) |
| Módulo | `SignalModule` | Um membro dos Signal Modules, como o `Signal Timer` (`signal_timer`), o `Signal Inverter` (`signal_inverter`), o `Weather Detector` (`weather_detector`), o `Signal Randomizer` (`signal_randomizer`), o `Signal Lever` (`signal_lever`), o `Signal Button` (`signal_button`) o `Signal Pressure Plate` (`signal_pressure_plate`), o `Signal Item Sensor` (`signal_item_sensor`), o `Signal Lock` (`signal_lock`) e o `Signal Lamp` (`signal_lamp`) (perguntas 300, 457, 475, 550, 633, 650 e 691) |
| Conexão | `Connection` | Uma entrada ou uma saída de um módulo, ligada a uma borda da placa (`Edge`) ou a um canal da rede do módulo (`Channel`), nunca aos dois. Não é uma porta (perguntas 376 e 420) |
| Signal Inverter | `signal_inverter` | `Inversor de Sinal` em português: o NOT do mod, o módulo que liga a saída B quando a entrada A não tem sinal e a desliga quando tem, com o atraso da tocha, e queima num laço (perguntas 296, 310, 444, 445 e 447) |
| Signal Randomizer | `signal_randomizer` | `Sorteador de Sinal` em português: o módulo de fonte que, ligado, sorteia a cada intervalo um valor de 0 a 15 (`Analog`) ou 0 ou 15 (`Binary`) e o mantém na saída até o próximo sorteio (perguntas 475, 476, 478 e 486) |
| Signal Timer | `signal_timer` | `Temporizador de Sinal` em português: o módulo que, a cada intervalo, liga a saída por um pulso, e para enquanto a entrada `Pause` recebe sinal (perguntas 379, 380 e 410) |
| Multipart | `part_space` (bloco) | O sistema próprio que deixa várias partes dividirem um espaço de bloco: um bloco contêiner, `redweave:part_space`, guarda até 6 partes, uma por face, com no máximo 2 cabos (perguntas 502, 511 a 513 e 523) |
| Parte | `Part` | Uma peça do multipart, numa face de um espaço de bloco: um Signal Cable, um Signal Node ou um Signal Module. Responde por si à mira, ao clique, à quebra e ao Configurator (perguntas 511 e 515) |
| Signal Emitters | — | `Emissores de Sinal` em português: a família dos Signal Modules sem entrada acionados por um gatilho, como o clique, o aperto ou algo pisando, de onde o sinal nasce, com um contrato comum de força, luz e saída. Hoje, o Signal Lever, o Signal Button e o Signal Pressure Plate (perguntas 627 a 629 e 632, [`emissores.md`](funcionalidades/emissores.md)) |
| Signal Alarm | `signal_alarm` | `Alarme de Sinal` em português: um Signal Module de saída que toca um som, mostra um texto e pulsa uma luz vermelha ao receber sinal (perguntas 643 a 649, [`alarme.md`](funcionalidades/alarme.md)) |
| Signal Card | `signal_card` | `Cartão de Sinal` em português: um cartão de acesso com ID único, vinculado pelo dono a fechaduras, teclados e portões (perguntas 668 a 671, [`cartao.md`](funcionalidades/cartao.md)) |
| Signal Gate | `signal_gate_controller` | `Portão de Sinal` em português: uma moldura com um campo que só deixa passar quem se enquadra nas regras; não teletransporta (perguntas 677 a 687, [`portao.md`](funcionalidades/portao.md)) |
| Signal Keypad | `signal_keypad` | `Teclado de Sinal` em português: um Signal Module que libera o sinal com uma senha numérica (perguntas 659 a 667, [`teclado.md`](funcionalidades/teclado.md)) |
| Signal Lamp | `signal_lamp` | `Lâmpada de Sinal` em português: uma barra de LED do multipart, na cor do seu canal de intensidade, com o nível de luz no mundo pela força recebida e um canal de controle opcional, entregue na #143 (perguntas 650 a 658 e 715 a 721, [`lampada.md`](funcionalidades/lampada.md)) |
| Signal Item Sensor | `signal_item_sensor` | `Sensor de Itens de Sinal` em português: um Signal Module em placa grudada num inventário que emite a força escolhida quando ele tem exatamente a quantidade escolhida de um item, somando todos os slots. Só lê, não guarda nem move itens, e não é um dos Signal Emitters. Entrou na #148 (perguntas 689 a 704, [`sensor-de-itens.md`](funcionalidades/sensor-de-itens.md)) |
| Signal Lock | `signal_lock` | `Fechadura de Sinal` em português: um Signal Module que só abre com a força exata da chave e só sai pelo `Wrench` do dono, entregue na #141 (perguntas 633 a 642 e 707 a 714, [`fechadura.md`](funcionalidades/fechadura.md)) |
| Proteção dos blocos | — | Os blocos que um Signal Lock ou Signal Keypad protege contra explosão, quebra por outros players e pistão (perguntas 672 a 676, [`protecao.md`](funcionalidades/protecao.md)) |
| Signal Button | `signal_button` | `Botão de Sinal` em português: um Signal Module de fonte, apertado pelo player, que volta sozinho depois do tempo escolhido na tela, com uma luz vermelha desligado e verde ligado e a saída pela redstone ou por um canal. Só o dono o aperta, pela mão ou pelo `Toggle`, e a força, de 1 a 15, e o tempo ligado ficam na tela. Entrou na #131 (perguntas 548 a 556 e 588 a 600) |
| Signal Lever | `signal_lever` | `Alavanca de Sinal` em português: um Signal Module de fonte, ligado e desligado pelo player, com uma luz vermelha desligada e verde ligada e a saída pela redstone ou por um canal. Só o dono o usa, pela mão ou pelo `Toggle`, e a força vai de 1 a 15 na tela. Entrou na #130 (perguntas 548 a 555 e 576 a 587) |
| Signal Pressure Plate | `signal_pressure_plate` | `Placa de Pressão de Sinal` em português: um Signal Module de fonte, só no chão, ligado enquanto algo do modo escolhido (`PlateMode`: `Players`, `Passive`, `Hostile` ou `Items`) está sobre ele, com uma luz vermelha desligado e verde ligado e a saída pela redstone ou por um canal. O `Toggle` troca o modo, e a força, de 1 a 15, e o modo ficam na tela. Entrou na #132 (perguntas 548 a 557, 602 a 611 e 621 a 626) |
| Signal Cable | `signal_cable` | `Cabo de Sinal` em português: o cabo físico de redstone, um item só que nasce branco e ganha uma das 16 cores com corante. Liga-se só a cabos da mesma cor e do mesmo dono, e leva o sinal como o fio do jogo (perguntas 494, 497, 499, 505 e 528) |
| Trecho | — | Os pedaços de Signal Cable ligados entre si, de uma cor e de um dono, que o servidor calcula num passo só quando algo muda (pergunta 509) |
| Espaço de partes | `PartSpace` | A ocupação de um espaço de bloco do multipart, no domínio: qual tipo de parte (`PartKind`, cabo ou placa) está em cada face, com uma parte por face e até 2 cabos. Diz se uma parte nova cabe (`Fit`) (perguntas 513 e 537) |
| Isolamento | `Insulation` | A cor e o dono de um Signal Cable. Dois cabos só se ligam com o mesmo isolamento (perguntas 497, 528 e 540) |
| Curva interna | `INNER_CURVE` | A ligação entre dois Signal Cables em faces vizinhas do mesmo espaço de bloco, sem perda de força (perguntas 495 e 541) |
| Curva externa | — | A ligação de um Signal Cable que contorna a quina de um bloco para a face lateral dele, perdendo 1, como a passagem para o próximo espaço (perguntas 495 e 572) |
| Degrau | — | A ligação de um Signal Cable que sobe ou desce um bloco, como o pó do jogo numa escada; o cabo de baixo desenha o trecho na lateral (perguntas 495 e 572) |
| Weather Detector | `weather_detector` | `Detector de Clima` em português: o módulo de fonte que liga a saída conforme o clima da dimensão, nos modos `Rain`, `Thunder` ou `Clear`, e fica em 0 onde não há clima (perguntas 457 a 459, 464 e 469) |
| Porta | `Gate` | AND, OR, XOR ou NAND, de duas entradas, como Signal Module ou dentro de um chip. Cada entrada conta como ligada acima de 0, e a saída é 15 ou 0. O NOT é o Signal Inverter (perguntas 309, 310, 321 e 322) |
| Regra NOT | `Not` | A regra de uma entrada que o Signal Inverter usa: 15 sem sinal e 0 com qualquer sinal, como a tocha do jogo. Não é uma porta (perguntas 310, 321 e 322) |
| Circuito | `Circuit` | Um grafo de portas, avaliado em ordem topológica |
| Chip | `Chip` | Um circuito gravado num item, com nome e ícone, que roda como uma peça só |
| Simulador | `Simulator` | Avalia cada rede uma vez por tick, só quando um canal mudou |
| Network Controller | `network_controller` | O bloco que é uma rede: colocá-lo cria a rede, e a tela dele a gerencia, com a lista de nós e o log |
| Network Terminal | `network_terminal` | O bloco que administra todas as redes do dono: renomear, desligar, religar, limpar os nós e apagar as `Unplaced`. Não cria redes (perguntas 130 e 131) |
| Configurador | `configurator` | Ferramenta com cinco modos: `Open` abre a Tela do Nó, `Rotate` gira os Signal Modules, `Copy` copia e cola a configuração de um nó ou de um Signal Module, menos o Signal Lock e o Signal Keypad (perguntas 722 a 736), `Toggle` ativa ou desativa todos os canais configurados de um nó, e `Wrench` retira o nó como item. Num Signal Module, `Open` abre a tela, `Rotate` gira a placa, no sentido horário ou, com Shift, no anti-horário, `Toggle` faz o que o módulo define, e `Wrench` o retira (perguntas 143, 161, 241, 393, 395, 610 e 613 a 618) |
| Modo | — | O modo atual do Configurator, trocado com Shift + scroll e mostrado com uma cor e o nome (perguntas 143 e 167) |
| Log | — | A lista das últimas mudanças de valor das entradas e saídas de uma rede, na tela do Network Controller (pergunta 147) |
| Signal Remote | `signal_remote` | Item de mão com aparência de tablet, `Controle de Sinal` em português, vinculado a um slot de input de um nó, que age como alavanca ou botão no canal desse slot, sempre com 15 (perguntas 15, 279, 282, 288, 352 e 368). Na rede, é uma entrada sem posição, a parte `RemoteInputs` (perguntas 27 e 360) |
| Ponte | `Bridge` | Na Fase 5, o nó que liga canais a um broker MQTT |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 14:00 | O `Copy` do Configurador também copia os Signal Modules (perguntas 722 a 736). |
| Zennd | 2026-11-16 13:18 | Leva o Signal Lamp à lista de módulos e diz o que entrou na #143. |
| Zennd | 2026-11-16 12:23 | Leva o Signal Lock à lista de módulos e diz o que entrou na #141. |
| Zennd | 2026-11-16 11:27 | Leva o Signal Item Sensor à lista de módulos e diz o que entrou na #148. |
| Zennd | 2026-11-16 10:12 | Adiciona o Signal Item Sensor (pergunta 691). |
| Zennd | 2026-11-16 09:34 | Adiciona o Signal Alarm, o Signal Card, o Signal Gate, o Signal Keypad, o Signal Lamp, o Signal Lock e a proteção dos blocos (perguntas 633 a 687). |
| Zennd | 2026-11-16 08:13 | Limita os Signal Emitters aos módulos acionados por um gatilho (pergunta 632). |
| Zennd | 2026-11-16 08:01 | Adiciona os Signal Emitters (pergunta 627). |
| Zennd | 2026-11-15 15:08 | Leva o Signal Pressure Plate à lista de módulos e diz o que entrou na #132, com os 4 modos (perguntas 621 a 626). |
| Zennd | 2026-11-15 14:37 | O Configurator passa a ter cinco modos, com o `Rotate` (perguntas 610 e 613 a 618). |
| Zennd | 2026-11-15 14:04 | Leva o Signal Button à lista de módulos e diz o que entrou na #131 (perguntas 550 e 588 a 600). |
| Zennd | 2026-11-15 13:19 | Leva o Signal Lever à lista de módulos e diz o que entrou na #130 (perguntas 550 e 576 a 587). |
| Zennd | 2026-11-15 09:36 | Adiciona o espaço de partes, o isolamento, a curva interna, a curva externa e o degrau (perguntas 495, 537, 540, 541 e 572). |
| Zennd | 2026-11-14 21:23 | Adiciona o Signal Lever, o Signal Button e o Signal Pressure Plate (perguntas 548 a 557). |
| Zennd | 2026-11-14 19:21 | Leva os Signal Modules ao Configurator. |
| Zennd | 2026-11-14 19:14 | Adiciona o multipart, a parte, o Signal Cable e o trecho (perguntas 494 a 533). |
| Zennd | 2026-11-14 17:46 | Adiciona o Signal Randomizer, da #109 (perguntas 475 a 493). |
| Zennd | 2026-11-14 16:40 | Adiciona o Weather Detector, da #108 (perguntas 457 a 473). |
| Zennd | 2026-11-14 14:31 | Adiciona o Signal Inverter, da #105 (perguntas 441 a 455). |
| Zennd | 2026-11-14 09:46 | Detalha os Signal Modules e adiciona a conexão e o Signal Timer, da #103 (perguntas 375 a 420). |
| Zennd | 2026-11-12 20:48 | Detalha o Signal Remote, da #22 (perguntas 352, 360 e 368). |
| Zennd | 2026-11-12 19:01 | Adiciona o tipo do nó, da #107 (perguntas 337 e 339). |
| Zennd | 2026-11-12 18:26 | Detalha a porta e adiciona a regra NOT, da #28 (perguntas 321 a 324). |
| Zennd | 2026-11-12 17:20 | Corrige o Configurator para quatro modos, com o `Toggle` (pergunta 241). |
| Zennd | 2026-11-12 16:50 | Tira a `Logic`, adiciona os Signal Modules em placa, muda a porta e o Signal Remote (perguntas 279, 282, 297 a 310, 313 e 314). |
| Zennd | 2026-11-11 14:09 | Liga os estados `Offline` e `Unplaced` ao `NetworkState`, da #88. |
| Zennd | 2026-11-11 13:46 | Liga a última rede usada, o nome do nó e a escolha de rede aos nomes do código, da #87. |
| Zennd | 2026-11-11 09:51 | Adiciona o Network Terminal, os estados `Offline` e `Unplaced`, o modo e o log; troca a rede atual pela última rede usada e tira a etiqueta (perguntas 125 a 157). |
| Marcelo Fabiano | 2026-11-10 16:29 | Adiciona a etiqueta. |
| Marcelo Fabiano | 2026-11-09 15:39 | Adiciona os termos da rede e dos canais e aplica as perguntas 22 a 29. |
| Marcelo Fabiano | 2026-11-09 10:34 | Confirma o nome Signal Remote (pergunta 15). |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
