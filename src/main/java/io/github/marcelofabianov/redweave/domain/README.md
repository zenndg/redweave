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
| `Outcome` | O que uma operação fez: `ACCEPTED`, ou o motivo da recusa, como `NOT_OWNER`, `INVALID_NAME`, `ALREADY_ATTACHED`, `UNKNOWN_NODE`, `INVALID_SLOT` e `ALREADY_PLACED`. Os casos de uso de `application/` usam o mesmo vocabulário, com `UNKNOWN_NETWORK` e `NO_CURRENT_NETWORK` (pergunta 41) | #10, #13 e #14 |
| `Channel` | Um dos 16 canais da rede: a cor e um nome opcional. Sem nome, as telas mostram o nome da cor traduzido | #10 |
| `BlockLocation` | Um bloco numa dimensão, como o do controlador. A dimensão é o ID do jogo | #10 |
| `Face` | As 6 faces de um bloco, na ordem do jogo | #10 |
| `NodePosition` | Onde o nó está: o bloco e a face | #10 |
| `Node` | Um nó, com a posição e os slots. Slot vazio é slot livre | #10 |
| `ChannelSlot` | A configuração de um nó para um canal: direção, face e canal. A lógica entra na Fase 2 | #10 |
| `Direction` | `INPUT` ou `OUTPUT` | #10 |
| `NetworkLimits` | Os números da rede: slots por nó, tamanho dos nomes e o raio do alcance. O domínio define a interface, e o config a implementa | #10 e #11 |
| `Range` | O alcance do controlador: uma esfera com o raio em blocos, na mesma dimensão. Raio 0 alcança a dimensão inteira | #11 |
| `NodeState` | O que as telas mostram de um nó: `ONLINE`, `UNLOADED`, `OUT_OF_RANGE` ou `NO_NETWORK` | #11 |
| `readInput`, `channelValue` e `output` | A regra do canal, no `Network`: guarda a última leitura de cada entrada, dá o mais forte entre as que contam e o que cada saída emite | #11 |
| `emission` | O que um nó emite no mundo: a mais forte entre as saídas dele, porque ele alimenta o bloco de apoio como uma alavanca (pergunta 71) | #15 |
| `slotSignal` | O sinal de um slot, para o medidor da Tela do Nó: a última leitura numa entrada e o que a saída emite | #19 |
| `Readings` | Dentro da rede: a última leitura de cada entrada e a soma das entradas por canal. Saiu do `Network` para o agregado manter uma responsabilidade por parte | #19 |
| `Simulator` | Serviço sem estado que avalia uma rede pendente: calcula os 16 canais numa passada e devolve só as faces cujo sinal mudou | #12 |
| `OutputFace` | Uma face onde um nó emite: o nó, o bloco e a face. Todas as saídas do nó nessa face emitem por ela | #12 |
| `OutputChange` | Uma face cujo sinal mudou, que o jogo aplica antes de avisar os vizinhos | #12 |
| `Emissions` | Dentro da rede: se ela espera uma avaliação e o que emitiu por último. Não vai para o save | #12 |
| `Channels` | Dentro da rede: os 16 canais, o nome de cada um e o primeiro canal livre. Saiu do `Network` como `Readings` | #16 |
| `NetworkSnapshot` | Tudo da rede que vai para o save: nome, cor, dono, controlador, os canais com nome, os nós e as leituras. `snapshot()` o tira da rede, e `Network.restore` refaz a rede a partir dele (pergunta 90) | #16 |
| `SlotReading` | A última leitura de um slot de entrada, como vai para o save | #16 |
| `NodeCopy` | Os slots em uso de um nó, copiados pelo Configurator, e a face onde ele estava grudado. `onto` diz como ficam os slots de outro nó depois de colar (pergunta 105) | #17 |
| `CopiedSlot` | Um slot da cópia: a posição no nó e a configuração | #17 |
| `Pasted` | Os slots de um nó depois de colar e quantos slots da cópia ficaram de fora (pergunta 106) | #17 |

## Regras e porquês

- **Sinal de 0 a 15:** é o contrato com a redstone do jogo, então o construtor de `Signal` rejeita qualquer outro valor (regra 5). Quem lê do mundo converte antes, num valor seguro (regra 14).
- **Converter, não falhar:** o construtor de `Signal` rejeita um valor fora do intervalo, porque isso é erro de programação. Um valor que vem do mundo ou de outro mod passa por `Signal.clamped`, e quem chama registra no log (regra 14).
- **Chave da cor fixada no teste:** a `key` sai do nome da constante, então o teste fixa as 16 chaves. Renomear uma constante reprova o teste antes de quebrar um save (regra 15). Uma chave desconhecida, como de um save danificado, devolve vazio em vez de derrubar o jogo.
- **IDs vêm de fora:** `NetworkId` e `NodeId` só recebem o UUID. Quem chama gera com uma porta `IdGenerator`, para o domínio não ter aleatoriedade e os testes usarem IDs fixos (pergunta 31).
- **16 canais, sempre:** cada canal é uma cor de lã, então a rede nasce com os 16 e nunca cria nem apaga um. Assim, um slot sempre aponta para um canal que existe.
- **Vale o mais forte:** é a regra da própria redstone, em que um fio ligado a duas fontes fica com a mais forte. Sem entradas, o canal vale 0.
- **Chunk descarregado congela:** a rede guarda a última leitura de cada entrada, e é ela que conta, com o chunk carregado ou não. Por isso o carregamento só muda o estado na tela, e não o valor do canal. A leitura vai para os saves, então vale também depois de reiniciar o servidor (perguntas 13, 29 e 37). O domínio não sabe de chunks: quem chama informa se o nó está carregado.
- **Leitura pertence ao slot:** reconfigurar ou liberar um slot, ou desligar o nó, descarta a leitura, para um valor antigo não aparecer num canal novo.
- **Leitura ruim é ignorada:** uma leitura para um nó que não está na rede, para um índice fora das vagas ou para um slot que não é entrada não muda nada, para um dado ruim do mundo nunca derrubar o tick (regra 14).
- **Fora de alcance não conta:** um nó fora do raio ou em outra dimensão vale 0, e as saídas dele emitem 0 (pergunta 23). O raio vem de `NetworkLimits`, porque o domínio não lê o config.
- **Esfera pelos quadrados:** o alcance compara os quadrados das distâncias, em `long`, sem raiz, sem arredondamento e sem estouro perto da borda do mundo (pergunta 36).
- **Fora de alcance vence descarregado:** na tela, um nó fora do raio aparece como `OUT_OF_RANGE` mesmo descarregado, porque é isso que o impede de contar.
- **Remote volta a 0:** uma fonte sem posição que sai do inventário ou do alcance para de contar, para o canal não ficar preso ligado (pergunta 27).
- **Rede `Offline` zera tudo:** sem o controlador colocado, todos os canais valem 0 (pergunta 24).
- **Só o dono altera:** toda operação que muda a rede recebe quem pede e confere com `isOwner`, sem exceção para operadores (perguntas 22 e 28). O controlador também: só o dono o retira e o recoloca (pergunta 30).
- **Recusa é resultado:** um pedido de outro player ou um nome inválido são casos esperados, então a operação devolve um `Outcome` e não muda nada. Exceção fica para erro de programação, como um argumento nulo ou um nome padrão inválido na criação (perguntas 32 e 35).
- **Agregado mutável, valores imutáveis:** a rede muda pelas operações, e as consultas devolvem listas imutáveis. `Node` e `ChannelSlot` são valores: mudar um slot troca o nó inteiro (pergunta 33).
- **Nome aparado e recusado:** as pontas saem, e vazio ou longo demais é recusado. No canal, vazio volta ao nome da cor (pergunta 34).
- **Controlador liga e desliga:** criar a rede é colocar o controlador, então ela nasce `Online`. Retirar o deixa `Offline`, e recolocar volta na nova posição, com tudo igual (pergunta 24). Uma rede com o controlador colocado recusa outro com `ALREADY_PLACED`, para um item copiado não dar dois controladores a ela, e mover é retirar e recolocar (pergunta 56).
- **Controlador perdido sem pedido:** quando o bloco some sem o dono quebrá-lo, como no criativo, por comando ou por outro mod, `loseController` deixa a rede `Offline` sem receber quem pede, porque não há a quem perguntar. Só `removeController`, a quebra pelo dono, confere o dono (pergunta 49).
- **Nó perdido sem pedido:** do mesmo jeito, `loseNode` tira da rede o nó cujo bloco sumiu ou que uma explosão levou, sem receber quem pede. Só `detachNode`, a retirada pelo dono, confere o dono (pergunta 74).
- **Uma rede por nó, garantida fora:** o agregado não enxerga as outras redes, então o `LinkNode`, em `application/`, tira o nó da rede antiga antes de ligá-lo na nova. Ele confere tudo antes de mudar algo, e quem pede precisa ser dono das duas redes (pergunta 42).
- **Primeiro canal livre:** é o primeiro, na ordem das cores, sem nenhum slot na rede (pergunta 25).
- **Avaliação por mudança:** uma leitura que muda o valor, ou uma mudança de estrutura aceita, deixa a rede pendente. Renomear e trocar a cor, não, porque não mudam sinal. O simulador ignora a rede que não espera e, depois de avaliar, ela para de esperar, então uma segunda chamada no mesmo tick não faz nada (regra 18).
- **Estado na rede, simulador sem estado:** a rede sabe quando um sinal dela mudou e o que emitiu por último, para o estado de uma rede ficar num lugar só (pergunta 38).
- **Só o que mudou volta:** o simulador devolve só as faces cujo sinal mudou, para o jogo avisar os vizinhos só delas. Uma face que deixou de existir, por um nó desligado ou um slot liberado, volta a `OFF`, e não volta nada se ela já estava em `OFF`.
- **Primeiro tick reaplica tudo:** uma face nunca emitida sempre muda, até para `OFF`, porque, depois de carregar o mundo, o bloco pode ter qualquer valor (pergunta 38).
- **Mesma face, o mais forte:** duas saídas do mesmo nó na mesma face emitem a mais forte, como dois fios de redstone no mesmo bloco (pergunta 39).
- **Canais numa passada:** `channelValues` percorre os nós uma vez para os 16 canais, para a meta de 1.000 nós de `limites.md` não virar uma conta por saída.
- **Números por interface:** limites como slots por nó e tamanho dos nomes vêm de `NetworkLimits`, nunca de constantes no domínio (regra 3).
- **Restaurar sem setters:** a rede salva volta por `Network.restore`, uma fábrica que recebe o `NetworkSnapshot`, e não por métodos que mudam campos, para nenhuma regra ficar de fora (pergunta 90). A fronteira descarta o que está inválido no save antes do domínio, então um snapshot que quebra as regras do `SnapshotRules`, como um nó repetido ou uma leitura fora de uma entrada, é erro de programação (pergunta 92).
- **Limites valem para as próximas mudanças:** a rede restaurada mantém os slots de cada nó e o tamanho dos nomes, mesmo que o config agora permita menos, para uma troca de config não apagar o trabalho do player (pergunta 93).
- **Restaurada, a rede espera o primeiro tick:** o snapshot não leva o que a rede emitiu, então a rede restaurada começa pendente e reaplica todas as saídas (pergunta 38).
- **Leitura 0 não vai para o save:** é o mesmo que uma entrada nunca lida, então o snapshot só leva as leituras ligadas, e a ida e volta pelo save sai igual (pergunta 89).
- **A face grudada acompanha o nó:** ao colar, um slot na face onde o nó de origem estava grudado vai para a face onde o nó de destino está grudado, para continuar lendo o próprio bloco (perguntas 71 e 105). As outras faces ficam como estão.
- **Colar troca tudo, pelas operações do agregado:** colar configura ou libera cada slot pelo `configureSlot` e pelo `clearSlot`, então o dono é conferido e as leituras caem, sem uma operação nova no `Network`. Um slot que passa do tamanho do nó de destino fica de fora (pergunta 106).
- **Sem `get*`:** `strength()`, não `getStrength()` (regra 11), conferido pelo ArchUnit.

