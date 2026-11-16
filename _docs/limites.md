# Limites

Todos os números do Redweave ficam aqui e no config, nunca fixos na lógica (regra 3 do `AGENTS.md`). Os valores abaixo são o ponto de partida e mudam conforme o teste de carga da `v0.1.0` (#25).

| Limite | Valor inicial | Chave de config | Fase |
|---|---|---|---|
| Canais por rede | 16 | fixo, ligado às cores de lã | 1 |
| Slots de canal por nó | 16, um por cor, de 0 a 16 ativos (pergunta 224) | fixo, ligado às cores de lã | 1 |
| Cores do Signal Cable | 16, as cores de lã (pergunta 494) | fixo, ligado às cores de lã | 2 |
| Partes por face de um espaço de bloco | 1: um cabo, um nó ou um módulo (pergunta 513) | fixo, regra do multipart | 2 |
| Signal Cables por espaço de bloco | 2 (pergunta 498) | fixo, regra do multipart | 2 |
| Alcance do sinal no Signal Cable | 15 blocos, perdendo 1 por bloco, como o fio do jogo; sem limite de trecho (perguntas 499 e 530) | fixo, contrato com a redstone do jogo | 2 |
| Busca do cálculo do Signal Cable | 30 espaços a partir do que mudou, o dobro do alcance, porque uma mudança só afeta pedaços a até 15 e as fontes deles estão a até 15 deles; grava só até 15 (pergunta 530) | fixo, derivado do alcance | 2 |
| Intervalo do sinal | 0 a 15 | fixo, contrato com a redstone do jogo | 1 |
| Avaliação da rede | 1 vez por tick, só com canal alterado | a definir | 1 |
| Meta de carga | 1.000 nós numa rede sem impacto visível no MSPT | — | 1 |
| Alcance do nó | 128 blocos a partir do controlador, na mesma dimensão; 0 é ilimitado, até 30.000.000 (perguntas 23 e 66) | `network.range` | 1 |
| Tamanho do nome da rede | 32 caracteres, de 1 a 64 | `network.name_length` | 1 |
| Tamanho do nome do canal | 16 caracteres, de 1 a 64 (perguntas 26 e 66) | `channel.name_length` | 1 |
| Tamanho do nome do nó | 16 caracteres, de 1 a 64 (pergunta 144) | `node.name_length` | 1 |
| Redes por player | 10, de 1 a 100; todas contam, inclusive as `Offline` e as `Unplaced` (pergunta 145) | `network.max_per_player` | 1 |
| Linhas do log por rede | 100, de 10 a 1000, só em memória (pergunta 147) | `log.size` | 1 |
| Raio do rótulo sobre os nós, os módulos e os cabos | 16 blocos, de 4 a 64 (perguntas 268, 417 e 525) | `configurator.label_range` | 1 |
| Pulso do Signal Remote no modo botão | 20 ticks, de 2 a 200 (perguntas 353 e 318) | `remote.pulse_ticks` | 1 |
| Intervalo mínimo do Signal Timer | 4 ticks, de 4 a 200 (pergunta 384) | `timer.min_interval_ticks` | 2 |
| Intervalo máximo do Signal Timer | 6000 ticks (5 min), de 200 a 72000 (1 h) (pergunta 384) | `timer.max_interval_ticks` | 2 |
| Tamanho do nome de um Signal Module | 16 caracteres, de 1 a 64 (pergunta 391) | `module.name_length` | 2 |
| Força do Signal Lever, do Signal Button e do Signal Pressure Plate | 1 a 15, 15 ao ser colocado (pergunta 552) | fixo, contrato com a redstone do jogo | 2 |
| Intervalo mínimo do Signal Randomizer | 4 ticks, de 4 a 200 (pergunta 477) | `randomizer.min_interval_ticks` | 2 |
| Intervalo máximo do Signal Randomizer | 6000 ticks (5 min), de 200 a 72000 (1 h) (pergunta 477) | `randomizer.max_interval_ticks` | 2 |
| Tempo ligado mínimo do Signal Button | 2 ticks, de 2 a 200, nunca abaixo de 2, um passo da tela (perguntas 592 e 596) | `button.min_interval_ticks` | 2 |
| Tempo ligado máximo do Signal Button | 6000 ticks (5 min), de 200 a 72000 (1 h); o padrão de um botão novo é 20 ticks, 1 s (perguntas 556 e 592) | `button.max_interval_ticks` | 2 |
| Conferência do Signal Pressure Plate ligado | 20 ticks (1 s), de 2 a 200: de quanto em quanto tempo uma placa ligada procura de novo o que está sobre ela; desligada, ela não procura (pergunta 607) | `pressure_plate.check_ticks` | 2 |
| Pulso mínimo do `Toggle` do Signal Lock | 2 ticks; o padrão de uma fechadura nova é 20 ticks, 1 s (pergunta 640) | `lock.min_pulse_ticks` | 2 |
| Pulso máximo do `Toggle` do Signal Lock | 200 ticks, 10 s (pergunta 640) | `lock.max_pulse_ticks` | 2 |
| Quantidade máxima do Signal Item Sensor | 3456, de 1 a 100000 (pergunta 693) | `item_sensor.max_count` | 2 |
| Leitura do Signal Item Sensor | A cada 10 ticks, de 2 a 100 (pergunta 695) | `item_sensor.check_ticks` | 2 |
| Alcance do Signal Alarm | 16 blocos, de 4 a 32 (pergunta 646) | `alarm.range` | 2 |
| Repetições máximas do Signal Alarm | 5, até 10 (pergunta 646) | `alarm.max_repeats` | 2 |
| Intervalo mínimo do som do Signal Alarm | 20 ticks, 1 s (pergunta 646) | `alarm.min_interval_ticks` | 2 |
| Intervalo máximo do som do Signal Alarm | 200 ticks, 10 s (pergunta 646) | `alarm.max_interval_ticks` | 2 |
| Duração máxima do Signal Alarm | 600 ticks, 30 s; o padrão é 5 s (pergunta 648) | `alarm.max_duration_ticks` | 2 |
| Atualização da luz do Signal Lamp | No máximo a cada 4 ticks, de 1 a 20 (pergunta 655) | `lamp.min_light_update_ticks` | 2 |
| Tempo aberto mínimo do Signal Keypad | 20 ticks, 1 s; o padrão é 3 s (pergunta 663) | `keypad.min_open_ticks` | 2 |
| Tempo aberto máximo do Signal Keypad | 600 ticks, 30 s (pergunta 663) | `keypad.max_open_ticks` | 2 |
| Janela das tentativas livres do Signal Keypad | 1200 ticks, 60 s, com 3 tentativas livres (pergunta 665) | `keypad.free_window_ticks` | 2 |
| Primeira espera do Signal Keypad | 100 ticks, 5 s, na 4ª tentativa errada, dobrando até a 10ª (pergunta 665) | `keypad.first_wait_ticks` | 2 |
| Espera máxima do Signal Keypad | 6000 ticks, 5 min; a contagem zera depois de 10 min sem tentar (pergunta 665) | `keypad.max_wait_ticks` | 2 |
| Cartões aceitos por item | 8 (pergunta 669) | `card.max_links_per_item` | 2 |
| Blocos protegidos por Signal Lock ou Signal Keypad | 16, de 1 a 64 (pergunta 673) | `lock.max_protected_blocks` | 2 |
| Raio da proteção dos blocos | 4 blocos, de 1 a 8 (pergunta 673) | `lock.protect_range` | 2 |
| Tags por Signal Gate | 4 (pergunta 683) | `gate.max_tags` | 2 |
| Raio do bloqueio de teletransporte do Signal Gate | 4 blocos, de 1 a 8 (pergunta 686) | `gate.teleport_block_range` | 2 |
| Trocas que queimam um Signal Inverter | 8, de 2 a 64 (pergunta 449) | `inverter.burnout_toggles` | 2 |
| Janela das trocas do Signal Inverter | 60 ticks, de 20 a 1200 (pergunta 449) | `inverter.burnout_window_ticks` | 2 |
| Tempo de queima do Signal Inverter | 160 ticks, de 20 a 6000 (pergunta 449) | `inverter.burnout_ticks` | 2 |
| Network Terminals por player | 1 (pergunta 137) | fixo, regra do bloco | 1 |
| Mensagens por segundo por canal na ponte | a definir | a definir | 5 |

As chaves de config entram junto com o código que as lê e, depois de publicadas, só mudam com migração (regra 15). Ficam no config do servidor, `redweave-server.toml`, e um valor fora da faixa volta ao padrão, com um aviso no log (perguntas 50 e 66).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 10:12 | Adiciona os limites do Signal Item Sensor (perguntas 693 e 695). |
| Zennd | 2026-11-16 09:34 | Adiciona os limites do Signal Lock, do Signal Alarm, do Signal Lamp, do Signal Keypad, do Signal Card, da proteção dos blocos e do Signal Gate (perguntas 640 a 686). |
| Zennd | 2026-11-15 15:08 | Leva a força ao Signal Pressure Plate e explica a conferência da placa ligada (pergunta 607). |
| Zennd | 2026-11-15 14:26 | Adiciona a conferência do Signal Pressure Plate (pergunta 607). |
| Zennd | 2026-11-15 14:04 | Leva a força ao Signal Button e completa os limites do tempo ligado (perguntas 556 e 596). |
| Zennd | 2026-11-15 13:42 | Adiciona os limites do tempo ligado do Signal Button (pergunta 592). |
| Zennd | 2026-11-15 13:19 | Adiciona a força do Signal Lever (pergunta 552). |
| Zennd | 2026-11-15 09:34 | Adiciona a busca do cálculo do Signal Cable e leva o raio do rótulo aos módulos e aos cabos (perguntas 417, 525 e 530). |
| Zennd | 2026-11-14 19:21 | Aponta o teste de carga da `v0.1.0` (#25). |
| Zennd | 2026-11-14 19:14 | Adiciona os limites do multipart e do Signal Cable (perguntas 494, 498, 499, 513 e 530). |
| Zennd | 2026-11-14 17:46 | Adiciona os intervalos do Signal Randomizer (pergunta 477). |
| Zennd | 2026-11-14 14:31 | Adiciona a queima do Signal Inverter (pergunta 449). |
| Zennd | 2026-11-14 09:46 | Adiciona os limites do Signal Timer e o nome dos módulos (perguntas 384 e 391). |
| Zennd | 2026-11-12 20:48 | Adiciona o pulso do Signal Remote (pergunta 353). |
| Zennd | 2026-11-12 11:58 | Tira o `slot.name_length` (pergunta 277). |
| Zennd | 2026-11-12 11:11 | Adiciona o raio do rótulo sobre os nós (pergunta 268). |
| Zennd | 2026-11-11 16:40 | Troca `node.slots` pelos 16 slots fixos e adiciona `slot.name_length` (perguntas 224 e 227). |
| Zennd | 2026-11-11 09:51 | Adiciona o nome do nó, as redes por player, o log e os Terminais por player. |
| Marcelo Fabiano | 2026-11-10 09:20 | Preenche as chaves de config e as faixas. |
| Marcelo Fabiano | 2026-11-09 15:39 | Define o alcance do nó e os tamanhos dos nomes. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
