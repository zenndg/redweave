# Itens

O catálogo de todos os itens do Redweave: os entregues, os planejados e os que ficaram como talvez, com uma linha por item. Cada linha dá o nome em inglês e em português, o ID, a família, o que o item faz e o estado, e aponta para a página de conceito, onde estão as regras completas (pergunta 705). Os termos são os do [`glossario.md`](glossario.md), as páginas de conceito estão em [`funcionalidades/`](funcionalidades/README.md), e as respostas em [`perguntas/`](perguntas/README.md) prevalecem sobre este catálogo.

Todo item novo, mudado ou planejado atualiza esta página e a seção `Items` do [`README.md`](../README.md) no mesmo PR (pergunta 705). Todo item entregue fica na aba criativa do Redweave, para os testes, com ou sem receita (pergunta 601). As receitas dos itens entregues estão em [Receitas](#receitas) (#157); as dos itens planejados ainda não foram decididas.

## Rede

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Network Controller (`Controlador de Rede`) | `redweave:network_controller` | Cria uma rede ao ser colocado: cada controlador é uma rede, com nome, cor e dono, e mostra a Tela do Controlador | Entregue na #14 | [`controlador-de-rede.md`](funcionalidades/controlador-de-rede.md) |
| Network Terminal (`Terminal de Rede`) | `redweave:network_terminal` | Lista as redes do player e deixa desligar, religar, limpar e apagar cada uma | Entregue na #89 | [`terminal-de-rede.md`](funcionalidades/terminal-de-rede.md) |

## Nó e cabo

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Signal Node (`Nó de Sinal`) | `redweave:signal_node` | Gruda em qualquer face de um bloco e lê ou emite redstone ali, nos 16 slots por cor, ligado a uma rede | Entregue na #15 | [`no-de-sinal.md`](funcionalidades/no-de-sinal.md) |
| Signal Cable (`Cabo de Sinal`) | `redweave:signal_cable` | Cabo físico de redstone em qualquer face, em 16 cores que isolam um cabo do outro, como o fio do jogo. Um item só, com a cor no data component `cable_color` | Entregue na #44 | [`signal-cable.md`](funcionalidades/signal-cable.md) |

## Ferramentas

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Configurator (`Configurador`) | `redweave:configurator` | A ferramenta que abre, gira, copia, alterna e retira os nós, os módulos e os cabos, nos modos `Open`, `Rotate`, `Copy`, `Toggle` e `Wrench` | Entregue na #17, com os modos da #86 e o `Rotate` da #136 | [`configurador.md`](funcionalidades/configurador.md) |
| Signal Remote (`Controle de Sinal`) | `redweave:signal_remote` | Um tablet ligado a um slot de entrada de um nó, que liga, desliga ou pulsa o canal à distância | Entregue na #22 | [`signal-remote.md`](funcionalidades/signal-remote.md) |

## Emissores de Sinal

Os Signal Modules sem entrada acionados por um gatilho, com o contrato comum de força, luz e saída de [`emissores.md`](funcionalidades/emissores.md) (perguntas 627 a 632).

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Signal Lever (`Alavanca de Sinal`) | `redweave:signal_lever` | Alavanca 3D ligada e desligada pelo dono, com a força escolhida e a luz vermelha e verde | Entregue na #130 | [`alavanca.md`](funcionalidades/alavanca.md) |
| Signal Button (`Botão de Sinal`) | `redweave:signal_button` | Botão apertado pelo dono, que volta sozinho depois do tempo ligado | Entregue na #131 | [`botao.md`](funcionalidades/botao.md) |
| Signal Pressure Plate (`Placa de Pressão de Sinal`) | `redweave:signal_pressure_plate` | Placa no chão, ligada enquanto algo do modo escolhido está sobre ela: `Players`, `Passive`, `Hostile` ou `Items` | Entregue na #132 | [`placa-de-pressao.md`](funcionalidades/placa-de-pressao.md) |

## Signal Modules

As placas de lógica, com a base comum de [`modulos-de-sinal.md`](funcionalidades/modulos-de-sinal.md).

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Signal Timer (`Temporizador de Sinal`) | `redweave:signal_timer` | Liga e desliga a saída num intervalo escolhido | Entregue na #103 | [`signal-timer.md`](funcionalidades/signal-timer.md) |
| Signal Inverter (`Inversor de Sinal`) | `redweave:signal_inverter` | O NOT do mod: a saída é o oposto da entrada | Entregue na #105 | [`signal-inverter.md`](funcionalidades/signal-inverter.md) |
| Weather Detector (`Detector de Clima`) | `redweave:weather_detector` | Liga a saída conforme o clima escolhido: `Rain`, `Thunder` ou `Clear` | Entregue na #108 | [`weather-detector.md`](funcionalidades/weather-detector.md) |
| Signal Randomizer (`Sorteador de Sinal`) | `redweave:signal_randomizer` | Sorteia a saída a cada intervalo, de 0 a 15 ou só 0 e 15 | Entregue na #109 | [`signal-randomizer.md`](funcionalidades/signal-randomizer.md) |
| Signal Item Sensor (`Sensor de Itens de Sinal`) | `redweave:signal_item_sensor` | Placa num inventário que emite a força escolhida quando ele tem exatamente a quantidade escolhida de um item; só lê, não guarda nem move itens | Entregue na #148 | [`sensor-de-itens.md`](funcionalidades/sensor-de-itens.md) |
| Signal Lock (`Fechadura de Sinal`) | `redweave:signal_lock` | Abre só com a força exata da chave e emite a força escolhida; só o dono a retira | Entregue na #141 | [`fechadura.md`](funcionalidades/fechadura.md) |
| Signal Alarm (`Alarme de Sinal`) | `redweave:signal_alarm` | Ao receber sinal, toca um som, mostra um texto e pulsa uma luz vermelha, pelo tempo escolhido e para quem o dono escolhe | Entregue na #142 | [`alarme.md`](funcionalidades/alarme.md) |
| Signal Keypad (`Teclado de Sinal`) | `redweave:signal_keypad` | Uma senha numérica, guardada só no servidor, que libera o sinal pelo tempo aberto | Planejado na #144 | [`teclado.md`](funcionalidades/teclado.md) |
| Módulo de memória (T flip-flop) | A definir | Cada pulso na entrada alterna a saída | Planejado na #110 | — |
| Módulo de trava (SR latch) | A definir | Uma entrada liga e a outra desliga a saída, que fica guardada | Planejado na #111 | — |
| Módulo de porta (AND, OR, XOR e NAND) | A definir | A placa das portas lógicas, com a regra já no domínio | Planejado na #31 | [`portas-logicas.md`](funcionalidades/portas-logicas.md) (a regra) |
| Módulo de horário | A definir | Liga a saída por horário do dia no jogo | Planejado na #112 | — |
| Módulo extensor de pulso | A definir | Estica um pulso curto | Planejado na #113 | — |
| Módulo detector de borda | A definir | Emite um pulso quando a entrada muda | Planejado na #114 | — |
| Módulo de limiar | A definir | Liga acima de um limiar, com histerese | Planejado na #115 | — |
| Módulo de atraso longo | A definir | Atrasa o sinal por muito tempo | Planejado na #116 | — |
| Módulo detector de players | A definir | Liga com players por perto | Planejado na #117 | — |
| Módulo contador | A definir | Conta pulsos | Planejado na #30 | — |

## Saídas

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Signal Lamp (`Lâmpada de Sinal`) | `redweave:signal_lamp` | Barra de LED do multipart na cor do seu canal, com o nível de luz pela força recebida, e um canal de controle opcional | Entregue na #143 | [`lampada.md`](funcionalidades/lampada.md) |

## Acesso

| Item | ID | O que faz | Estado | Conceito |
|---|---|---|---|---|
| Signal Card (`Cartão de Sinal`) | `redweave:signal_card` | Cartão de acesso que o dono vincula a fechaduras, teclados e portões, e que qualquer player com ele usa | Planejado na #145 | [`cartao.md`](funcionalidades/cartao.md) |
| Signal Gate (`Portão de Sinal`) | `redweave:signal_gate_frame` (moldura) e `redweave:signal_gate_controller` (controlador) | Uma moldura com um campo de onda vermelha que só deixa passar o que as regras permitem; para os outros, é sólido. Não teletransporta | Planejado na #147 | [`portao.md`](funcionalidades/portao.md) |

A proteção dos blocos da porta (#146, [`protecao.md`](funcionalidades/protecao.md)) não é um item: é uma função do Signal Lock e do Signal Keypad, escolhida com o modo `Copy` do Configurator.

## Talvez

| Item | O que faria | Estado |
|---|---|---|
| Esteiras | Transportariam itens ou entidades, o que esbarra na regra 2 (Só sinal) | Talvez, sem planejamento (pergunta 688) |

## Blocos internos

Alguns blocos do mod não são itens e não aparecem na aba criativa:

| Bloco | ID | O que é |
|---|---|---|
| Espaço de partes | `redweave:part_space` | O bloco contêiner do multipart, que guarda as partes de um espaço de bloco: nós, módulos e cabos (pergunta 523) |
| Campo do portão | `redweave:signal_gate_field` | O campo do Signal Gate, que nasce com a moldura completa (planejado na #147) |

## Receitas

As receitas dos itens entregues, na mesa de trabalho, com materiais só do jogo puro e pelo menos uma redstone em cada receita de fabricar (perguntas 745, 746 e 764). Cada uma tem o ID do item, como `redweave:signal_node`, e fica em JSON de datapack gerado pelo datagen, que o pack pode sobrescrever com KubeJS (regra 4). Todas se desbloqueiam ao pegar uma redstone. Um GameTest (`ItemRecipeGameTests`) confere que todo item registrado tem receita, com redstone, e cada grade abaixo.

Na grade, cada linha da mesa vai separada por `/`, e `·` é um slot vazio.

| Item | Grade | Dá | Pergunta |
|---|---|---|---|
| Network Controller | Deepslate polido, redstone, deepslate polido / barra de cobre, olho do ender, barra de cobre / deepslate polido, bloco de redstone, deepslate polido | 1 | 747 |
| Network Terminal | Deepslate polido, painel de vidro, deepslate polido / barra de cobre, pérola do ender, barra de cobre / deepslate polido, redstone, deepslate polido | 1 | 748 |
| Signal Node | Barra de cobre, redstone, barra de cobre / deepslate polido, pérola do ender, deepslate polido | 2 | 749 |
| Signal Cable (branco) | Lã branca × 3 / barra de cobre, redstone, barra de cobre / lã branca × 3 | 8 | 750 |
| Configurator | ·, ·, redstone / ·, barra de cobre, · / barra de cobre, ·, · | 1 | 751 |
| Signal Remote | Barra de cobre, painel de vidro, barra de cobre / barra de cobre, pérola do ender, barra de cobre / barra de cobre, redstone, barra de cobre | 1 | 752 |

### Signal Modules

Os 11 módulos entregues têm a mesma forma, para o player aprender uma vez: o item do jogo de cada módulo no topo, `·, X, · / barra de cobre, redstone, barra de cobre / deepslate polido × 3`, que dá 1 módulo (pergunta 753).

| Módulo | X | Pergunta |
|---|---|---|
| Signal Timer | Relógio | 754 |
| Signal Inverter | Tocha de redstone | 755 |
| Weather Detector | Para-raios | 756 |
| Signal Randomizer | Liberador | 757 |
| Signal Lever | Alavanca | 758 |
| Signal Button | Botão de pedra | 758 |
| Signal Pressure Plate | Placa de pressão de pedra | 758 |
| Signal Lamp | Lâmpada de redstone | 758 |
| Signal Item Sensor | Comparador | 759 |
| Signal Lock | Gancho de armadilha | 760 |
| Signal Alarm | Bloco musical | 761 |

### Tingir o Signal Cable

8 cabos brancos em volta de 1 corante dão 8 cabos da cor do corante. Só o cabo branco, sem o componente `cable_color`, entra na receita, e um cabo tingido não troca mais de cor. São 15 receitas, uma por cor menos o branco, com o ID `redweave:<cor>_signal_cable`, como `redweave:red_signal_cable` (perguntas 562, 563, 762 e 763).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | Adiciona as receitas dos itens entregues e o tingir só do cabo branco (perguntas 745 a 764, #157). |
| Zennd | 2026-11-16 15:20 | O Signal Alarm passa a entregue na #142. |
| Zennd | 2026-11-16 13:18 | Marca o Signal Lamp como entregue na #143. |
| Zennd | 2026-11-16 12:23 | Marca o Signal Lock como entregue na #141. |
| Zennd | 2026-11-16 11:34 | Cria o catálogo de itens, com os entregues, os planejados e os talvez (pergunta 705, #150). |
