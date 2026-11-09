# Domínio

As regras do Redweave em Java puro, sem depender do jogo (regra 10 do `AGENTS.md`). O ArchUnit garante que `domain/` só depende de si e do Java (pergunta 21). Quem chama fornece os dados: o domínio não lê o config, o relógio nem o mundo.

O comportamento completo está em [`rede-e-canais.md`](../../../../../../../../_docs/funcionalidades/rede-e-canais.md). Este arquivo é o guia de quem mexe no código: os nomes, as regras e os porquês. Quem muda o modelo atualiza este arquivo na mesma tarefa (regra 13).

## Pacotes

| Pacote | O que guarda |
|---|---|
| `signal/` | O sinal e as cores |
| `network/` | O agregado `Network`, os nós, os canais e as permissões |
| `simulation/` | A avaliação por tick |
| `circuit/` | Portas e chips, nas Fases 2 e 3 |

## Linguagem

Os nomes são os do [`glossario.md`](../../../../../../../../_docs/glossario.md). A coluna "Issue" diz onde cada tipo nasce.

| Termo | Significado | Issue |
|---|---|---|
| `Signal` | Um valor de 0 a 15, o mesmo intervalo da redstone do jogo. `OFF` e `MAX` são os extremos, `isOn` diz se passa de 0, `strongest` fica com o mais forte de dois, e `clamped` converte um valor lido do mundo no sinal válido mais próximo | #9 |
| `ChannelColor` | Uma das 16 cores de lã, na ordem do jogo. Dá a cor dos canais e da rede. `number` vai de 1 a 16, como nas telas, a `key` é o nome usado nos saves, e `fromKey` devolve vazio para uma chave desconhecida | #9 |
| `NetworkId` | O ID da rede, um UUID único no servidor e que nunca muda | #9 |
| `NodeId` | O ID de um nó, um UUID | #9 |
| `PlayerId` | Um player, pelo UUID que o jogo dá a ele. Identifica o dono | #10 |
| `Network` | O agregado: nome, cor, dono, controlador, os 16 canais e os nós. Cada operação recebe quem pede e devolve um `Outcome` | #10 |
| `Outcome` | O que uma operação fez: `ACCEPTED`, ou o motivo da recusa, como `NOT_OWNER`, `INVALID_NAME`, `ALREADY_ATTACHED`, `UNKNOWN_NODE` e `INVALID_SLOT` | #10 |
| `Channel` | Um dos 16 canais da rede: a cor e um nome opcional. Sem nome, as telas mostram o nome da cor traduzido | #10 |
| `BlockLocation` | Um bloco numa dimensão, como o do controlador. A dimensão é o ID do jogo | #10 |
| `Face` | As 6 faces de um bloco, na ordem do jogo | #10 |
| `NodePosition` | Onde o nó está: o bloco e a face | #10 |
| `Node` | Um nó, com a posição e os slots. Slot vazio é slot livre | #10 |
| `ChannelSlot` | A configuração de um nó para um canal: direção, face e canal. A lógica entra na Fase 2 | #10 |
| `Direction` | `INPUT` ou `OUTPUT` | #10 |
| `NetworkLimits` | Os números da rede: slots por nó e tamanho dos nomes. O domínio define a interface, e o config a implementa | #10 |
| `ChannelValue` | A regra do canal: o mais forte entre as entradas que contam | #11 |
| `Simulator` | Avalia a rede uma vez por tick, só com canal alterado | #12 |

## Regras e porquês

- **Sinal de 0 a 15:** é o contrato com a redstone do jogo, então o construtor de `Signal` rejeita qualquer outro valor (regra 5). Quem lê do mundo converte antes, num valor seguro (regra 14).
- **Converter, não falhar:** o construtor de `Signal` rejeita um valor fora do intervalo, porque isso é erro de programação. Um valor que vem do mundo ou de outro mod passa por `Signal.clamped`, e quem chama registra no log (regra 14).
- **Chave da cor fixada no teste:** a `key` sai do nome da constante, então o teste fixa as 16 chaves. Renomear uma constante reprova o teste antes de quebrar um save (regra 15). Uma chave desconhecida, como de um save danificado, devolve vazio em vez de derrubar o jogo.
- **IDs vêm de fora:** `NetworkId` e `NodeId` só recebem o UUID. Quem chama gera com uma porta `IdGenerator`, para o domínio não ter aleatoriedade e os testes usarem IDs fixos (pergunta 31).
- **16 canais, sempre:** cada canal é uma cor de lã, então a rede nasce com os 16 e nunca cria nem apaga um. Assim, um slot sempre aponta para um canal que existe.
- **Vale o mais forte:** é a regra da própria redstone, em que um fio ligado a duas fontes fica com a mais forte. Sem entradas, o canal vale 0.
- **Chunk descarregado congela:** a entrada de um nó `Unloaded` mantém o último valor, como a redstone do jogo num chunk descarregado (perguntas 13 e 29). O domínio não sabe de chunks: quem chama informa o estado do nó.
- **Fora de alcance não conta:** um nó fora do raio ou em outra dimensão vale 0 (pergunta 23). O raio vem de quem chama, porque o domínio não lê o config.
- **Remote volta a 0:** uma fonte sem posição que sai do inventário ou do alcance para de contar, para o canal não ficar preso ligado (pergunta 27).
- **Rede `Offline` zera tudo:** sem o controlador colocado, todos os canais valem 0 (pergunta 24).
- **Só o dono altera:** toda operação que muda a rede recebe quem pede e confere com `isOwner`, sem exceção para operadores (perguntas 22 e 28). O controlador também: só o dono o retira e o recoloca (pergunta 30).
- **Recusa é resultado:** um pedido de outro player ou um nome inválido são casos esperados, então a operação devolve um `Outcome` e não muda nada. Exceção fica para erro de programação, como um argumento nulo ou um nome padrão inválido na criação (perguntas 32 e 35).
- **Agregado mutável, valores imutáveis:** a rede muda pelas operações, e as consultas devolvem listas imutáveis. `Node` e `ChannelSlot` são valores: mudar um slot troca o nó inteiro (pergunta 33).
- **Nome aparado e recusado:** as pontas saem, e vazio ou longo demais é recusado. No canal, vazio volta ao nome da cor (pergunta 34).
- **Controlador liga e desliga:** criar a rede é colocar o controlador, então ela nasce `Online`. Retirar o deixa `Offline`, e recolocar volta na nova posição, com tudo igual (pergunta 24).
- **Uma rede por nó, garantida fora:** o agregado não enxerga as outras redes, então o caso de uso que liga um nó confere isso (issue #13).
- **Primeiro canal livre:** é o primeiro, na ordem das cores, sem nenhum slot na rede (pergunta 25).
- **Avaliação por mudança:** a rede marca os canais cuja entrada mudou e só os recalcula no fim do tick, uma vez (regra 18). Só as saídas cujo valor mudou são devolvidas a quem chama, para o jogo não fazer block updates em cascata.
- **Números por interface:** limites como slots por nó e tamanho dos nomes vêm de `NetworkLimits`, nunca de constantes no domínio (regra 3).
- **Sem `get*`:** `strength()`, não `getStrength()` (regra 11), conferido pelo ArchUnit.

