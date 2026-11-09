# Limites

Todos os números do Redweave ficam aqui e no config, nunca fixos na lógica (regra 3 do `AGENTS.md`). Os valores abaixo são o ponto de partida e mudam conforme os testes de carga da Fase 1.

| Limite | Valor inicial | Chave de config | Fase |
|---|---|---|---|
| Canais por rede | 16 | fixo, ligado às cores de lã | 1 |
| Slots de canal por nó | 4 | a definir | 1 |
| Slots de canal com upgrade | 9 | a definir | 1 |
| Intervalo do sinal | 0 a 15 | fixo, contrato com a redstone do jogo | 1 |
| Avaliação da rede | 1 vez por tick, só com canal alterado | a definir | 1 |
| Meta de carga | 1.000 nós numa rede sem impacto visível no MSPT | — | 1 |
| Alcance do nó | a definir | a definir | 1 |
| Mensagens por segundo por canal na ponte | a definir | a definir | 5 |

As chaves de config entram junto com o código que as lê e, depois de publicadas, só mudam com migração (regra 15).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
