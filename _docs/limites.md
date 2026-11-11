# Limites

Todos os números do Redweave ficam aqui e no config, nunca fixos na lógica (regra 3 do `AGENTS.md`). Os valores abaixo são o ponto de partida e mudam conforme os testes de carga da Fase 1.

| Limite | Valor inicial | Chave de config | Fase |
|---|---|---|---|
| Canais por rede | 16 | fixo, ligado às cores de lã | 1 |
| Slots de canal por nó | 16, um por cor, de 0 a 16 ativos (pergunta 224) | fixo, ligado às cores de lã | 1 |
| Tamanho do nome do slot | 32 caracteres, de 1 a 64 (pergunta 227) | `slot.name_length` | 1 |
| Intervalo do sinal | 0 a 15 | fixo, contrato com a redstone do jogo | 1 |
| Avaliação da rede | 1 vez por tick, só com canal alterado | a definir | 1 |
| Meta de carga | 1.000 nós numa rede sem impacto visível no MSPT | — | 1 |
| Alcance do nó | 128 blocos a partir do controlador, na mesma dimensão; 0 é ilimitado, até 30.000.000 (perguntas 23 e 66) | `network.range` | 1 |
| Tamanho do nome da rede | 32 caracteres, de 1 a 64 | `network.name_length` | 1 |
| Tamanho do nome do canal | 16 caracteres, de 1 a 64 (perguntas 26 e 66) | `channel.name_length` | 1 |
| Tamanho do nome do nó | 16 caracteres, de 1 a 64 (pergunta 144) | `node.name_length` | 1 |
| Redes por player | 10, de 1 a 100; todas contam, inclusive as `Offline` e as `Unplaced` (pergunta 145) | `network.max_per_player` | 1 |
| Linhas do log por rede | 100, de 10 a 1000, só em memória (pergunta 147) | `log.size` | 1 |
| Network Terminals por player | 1 (pergunta 137) | fixo, regra do bloco | 1 |
| Mensagens por segundo por canal na ponte | a definir | a definir | 5 |

As chaves de config entram junto com o código que as lê e, depois de publicadas, só mudam com migração (regra 15). Ficam no config do servidor, `redweave-server.toml`, e um valor fora da faixa volta ao padrão, com um aviso no log (perguntas 50 e 66).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 16:40 | Troca `node.slots` pelos 16 slots fixos e adiciona `slot.name_length` (perguntas 224 e 227). |
| Zennd | 2026-11-11 09:51 | Adiciona o nome do nó, as redes por player, o log e os Terminais por player. |
| Marcelo Fabiano | 2026-11-10 09:20 | Preenche as chaves de config e as faixas. |
| Marcelo Fabiano | 2026-11-09 15:39 | Define o alcance do nó e os tamanhos dos nomes. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
