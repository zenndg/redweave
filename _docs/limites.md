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
| Força do Signal Lever | 1 a 15, 15 ao ser colocado (pergunta 552) | fixo, contrato com a redstone do jogo | 2 |
| Intervalo mínimo do Signal Randomizer | 4 ticks, de 4 a 200 (pergunta 477) | `randomizer.min_interval_ticks` | 2 |
| Intervalo máximo do Signal Randomizer | 6000 ticks (5 min), de 200 a 72000 (1 h) (pergunta 477) | `randomizer.max_interval_ticks` | 2 |
| Trocas que queimam um Signal Inverter | 8, de 2 a 64 (pergunta 449) | `inverter.burnout_toggles` | 2 |
| Janela das trocas do Signal Inverter | 60 ticks, de 20 a 1200 (pergunta 449) | `inverter.burnout_window_ticks` | 2 |
| Tempo de queima do Signal Inverter | 160 ticks, de 20 a 6000 (pergunta 449) | `inverter.burnout_ticks` | 2 |
| Network Terminals por player | 1 (pergunta 137) | fixo, regra do bloco | 1 |
| Mensagens por segundo por canal na ponte | a definir | a definir | 5 |

As chaves de config entram junto com o código que as lê e, depois de publicadas, só mudam com migração (regra 15). Ficam no config do servidor, `redweave-server.toml`, e um valor fora da faixa volta ao padrão, com um aviso no log (perguntas 50 e 66).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
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
