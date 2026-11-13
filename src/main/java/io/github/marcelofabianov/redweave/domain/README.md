# Domínio

As regras do Redweave em Java puro, sem depender do jogo (regra 10 do `AGENTS.md`). O ArchUnit garante que `domain/` só depende de si e do Java (pergunta 21). Quem chama fornece os dados: o domínio não lê o config, o relógio nem o mundo.

O comportamento completo está em [`rede-e-canais.md`](../../../../../../../../_docs/funcionalidades/rede-e-canais.md). Este arquivo é o guia de quem mexe no código: os nomes, as regras e os porquês. Quem muda o modelo atualiza este arquivo na mesma tarefa (regra 13).

## Pacotes

| Pacote | O que guarda |
|---|---|
| `signal/` | O sinal e as cores |
| `network/` | O agregado `Network`, os nós, os canais e as permissões |
| `simulation/` | A avaliação por tick |
| `circuit/` | As portas e a regra NOT, da #28, e os chips, na Fase 3 |
| `module/` | A base e as regras dos Signal Modules, planejado na #103 (pergunta 323) |

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
| `Outcome` | O que uma operação fez: `ACCEPTED`, ou o motivo da recusa, como `NOT_OWNER`, `INVALID_NAME`, `ALREADY_ATTACHED`, `UNKNOWN_NODE`, `INVALID_SLOT`, `SLOT_ENABLED`, `ALREADY_PLACED`, `UNKNOWN_NETWORK`, `NETWORK_NOT_ONLINE`, `NETWORK_NOT_OFFLINE`, `NETWORK_LIMIT`, `NETWORK_NOT_UNPLACED` e `TERMINAL_LIMIT`. `UNKNOWN_NETWORK` serve aos casos de uso de `application/` (pergunta 41) | #10, #13, #14, #87, #89 e #20 |
| `Channel` | Um dos 16 canais da rede: a cor e um nome opcional. Sem nome, as telas mostram o nome da cor traduzido | #10 |
| `BlockLocation` | Um bloco numa dimensão, como o do controlador. A dimensão é o ID do jogo | #10 |
| `Face` | As 6 faces de um bloco, na ordem do jogo | #10 |
| `NodePosition` | Onde o nó está: o bloco e a face | #10 |
| `Node` | Um nó, com o nome opcional, a posição e os 16 slots, um por cor: o slot da posição `i` é sempre o canal `i` (pergunta 224). Slot vazio é um canal nunca ativado, e nome vazio é o padrão, que `displayName` calcula (pergunta 206). `enabledChannels` dá os canais ativos na ordem das cores, para os cantos da placa (pergunta 229) | #10, #87 e #20 |
| `NodeKind` | O tipo de um nó pelos slots ativos: `INPUT`, `OUTPUT`, `BOTH` ou `NONE`, que `Node.kind` calcula para a coluna `Type` das listas (perguntas 283 e 337) | #107 |
| `DefaultNames` | O nome padrão de um nó e de uma rede: os 8 primeiros caracteres do ID (pergunta 142) | #87 |
| `Nodes` | Dentro da rede: os nós, na ordem em que entraram, e as regras de cada um, como o nome inteiro e o slot dentro do nó. Saiu do `Network` como `Channels` | #87 |
| `ChannelSlot` | A configuração de um nó para um canal: direção, face, canal e status ativo ou inativo, sem nome próprio (pergunta 277). Só um slot ativo lê (`reads`) ou emite (`emits`). Não tem lógica: toda lógica de sinal fica nos Signal Modules (pergunta 308) | #10 e #20 |
| `SlotChanges` | Dentro da rede: o que ativar, desativar e preparar fazem a um slot. Na primeira ativação, o slot vira `Input` na face grudada; desativar guarda direção e face (perguntas 228 e 237) | #20 |
| `Direction` | `INPUT` ou `OUTPUT` | #10 |
| `NetworkLimits` | Os números da rede: tamanho dos nomes da rede, do canal e do nó, o raio do alcance e as redes por player. O domínio define a interface, e o config a implementa | #10 e #11 |
| `Range` | O alcance do controlador: uma esfera com o raio em blocos, na mesma dimensão. Raio 0 alcança a dimensão inteira | #11 |
| `NodeState` | O que as telas mostram de um nó: `ONLINE`, `UNLOADED`, `OUT_OF_RANGE`, `NO_NETWORK` ou `OFFLINE` | #11 e #88 |
| `NetworkState` | O estado da rede: `ONLINE`; `OFFLINE`, desligada pelo dono; ou `UNPLACED`, sem o controlador no mundo (perguntas 135 e 146) | #88 |
| `NetworkStatus` | Dentro da rede: onde está o controlador e se o dono a desligou. Saiu do `Network` como `Channels` | #88 |
| `NetworkSignals` | Dentro da rede: as consultas de sinal (`channelValue`, `output`, `slotSignal`, `emission`, `nodeSignal`, `nodeState` e `counts`), que só leem as partes da rede. Saiu do `Network` como `Readings` | #88 |
| `readInput`, `channelValue` e `output` | A regra do canal, no `Network`: guarda a última leitura de cada entrada, dá o mais forte entre as que contam e o que cada saída emite | #11 |
| `emission` | O que um nó emite no mundo: a mais forte entre as saídas dele, porque ele energiza com força só o bloco de apoio (perguntas 71 e 262) | #15 |
| `slotSignal` | O sinal de um slot, para o medidor da Tela do Nó: a última leitura numa entrada e o que a saída emite | #19 |
| `nodeSignal` | O sinal de um nó, para a lista da Tela do Controlador e, na #24, para a lente: o mais forte entre as últimas leituras e o que ele emite, então uma entrada recebendo sinal também acende. Um nó que não conta, fora do alcance ou numa rede que não está `Online`, vale 0 (perguntas 98, 177 e 179) | #20 |
| `Readings` | Dentro da rede: a última leitura de cada entrada e a soma das entradas por canal. Saiu do `Network` para o agregado manter uma responsabilidade por parte | #19 |
| `Simulator` | Serviço sem estado que avalia uma rede pendente: calcula os 16 canais numa passada e devolve só as faces cujo sinal mudou | #12 |
| `OutputFace` | Uma face onde um nó emite: o nó, o bloco e a face. Todas as saídas do nó nessa face emitem por ela | #12 |
| `OutputChange` | Uma face cujo sinal mudou, que o jogo aplica antes de avisar os vizinhos | #12 |
| `Emissions` | Dentro da rede: se ela espera uma avaliação e o que emitiu por último. Não vai para o save | #12 |
| `Channels` | Dentro da rede: os 16 canais e o nome de cada um. Saiu do `Network` como `Readings` | #16 |
| `NetworkSnapshot` | Tudo da rede que vai para o save: nome, cor, dono, controlador, os canais com nome, os nós e as leituras. `snapshot()` o tira da rede, e `Network.restore` refaz a rede a partir dele (pergunta 90) | #16 |
| `SlotReading` | A última leitura de um slot de entrada, como vai para o save | #16 |
| `NodeCopy` | Os slots já ativados de um nó, com o status (perguntas 230 e 277), copiados pelo Configurator, e a face onde ele estava grudado. `onto` diz como ficam os slots de outro nó depois de colar (pergunta 105) | #17 |
| `CopiedSlot` | Um slot da cópia: a posição no nó e a configuração | #17 |
| `Pasted` | Os 16 slots de um nó depois de colar e quantos ficaram ativos (perguntas 106 e 230) | #17 e #20 |
| `SlotEdits` | Dentro da rede: como um slot muda (configurar, ativar, desativar, preparar e liberar), depois de o `Network` conferir o dono. Saiu do `Network` como `SlotChanges` | #89 |
| `clearNodes` e `deletion` | O que o Network Terminal faz numa rede: tirar todos os nós, que ficam no mundo sem rede, e conferir se ela pode ser apagada, só `Unplaced`. A rede apagada sai do save pela aplicação (perguntas 133, 166 e 176) | #89 |
| `SignalChange` | Uma linha do log da Tela do Controlador: a data e a hora, os nomes do nó e do canal do momento, o canal, a direção e o valor (perguntas 147 e 245) | #90 |
| `SignalLog` | O log de uma rede: um buffer circular com o tamanho do config, só em memória, a linha mais nova primeiro. Cada linha tem uma sequência crescente, para a tela aberta receber só as novas (perguntas 147 e 242) | #90 |
| `Gate` | Uma porta de duas entradas: `AND`, `OR`, `XOR` e `NAND`, nessa ordem, a do Shift + scroll (pergunta 326). `output` dá o resultado de duas entradas (perguntas 321 a 324) | #28 |
| `Not` | A regra NOT, de uma entrada, que o Signal Inverter usa: `output` dá 15 sem sinal e 0 com sinal (perguntas 310 e 324) | #28 |
| `RemoteId` | O ID de um Signal Remote, ganho no primeiro vínculo. Cópias de um tablet o dividem e contam como uma fonte só (perguntas 27 e 358) | #22 |
| `RemoteInput` | Um Signal Remote que conta neste tick, no slot de input vinculado: o slot, não o canal, para o tablet acompanhar a troca de canal (perguntas 287 e 360) | #22 |
| `RemoteInputs` | Dentro da rede: os tablets que contam neste tick, entregues inteiros a cada tick e fora do save. Põem 15 no canal de cada slot de input vinculado, na regra do mais forte (perguntas 352, 356 e 360) | #22 |
| `NetworkRemotes` | O lado dos Signal Remotes da rede, por `remotes()`: `apply` recebe os tablets do tick, `accepts` diz se um slot aceita vínculo, `reaches` diz se um player naquele lugar alcança a rede (pergunta 354), `othersHold` diz se o canal do tablet fica ligado sem ele (pergunta 370), e `on` conta os que valem agora. Saiu do `Network` para o agregado ficar abaixo de 400 linhas | #22 |
| `SlotOutputs` | O que cada slot de saída de uma rede emitiu na última olhada, para o log saber qual mudou. A primeira olhada só aprende os valores, e um slot que não emite vale 0 (perguntas 243, 244 e 246) | #90 |

## Regras e porquês

- **Log fora do agregado:** o log não vai para o save e não decide nenhum sinal, então fica fora do `Network`, num registro por rede na aplicação, `SignalLogs`. A hora vem de uma porta, `ServerClock`, porque o domínio não lê o relógio (perguntas 148 e 242).
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
- **Remote volta a 0 (planejado na #22):** uma fonte sem posição que sai do inventário ou do alcance para de contar, para o canal não ficar preso ligado (pergunta 27).
- **Só a rede `Online` conta:** numa rede `Offline` ou `Unplaced`, todos os canais valem 0, e as saídas emitem 0 (perguntas 24 e 135). As leituras das entradas continuam guardadas, então a rede religada volta com os valores de antes.
- **Desligar é escolha do dono:** `turnOff` só vale numa rede `Online`, e `turnOn` só numa `Offline`; uma `Unplaced` espera o controlador. O `Offline` fica guardado enquanto o controlador está fora do mundo, e a rede volta desligada (perguntas 132, 135 e 178). Só o Network Terminal da #89 chama essas operações no jogo (pergunta 209).
- **Só o dono altera:** toda operação que muda a rede recebe quem pede e confere com `isOwner`, sem exceção para operadores (perguntas 22 e 28). O controlador também: só o dono o retira e o recoloca (pergunta 30).
- **Recusa é resultado:** um pedido de outro player ou um nome inválido são casos esperados, então a operação devolve um `Outcome` e não muda nada. Exceção fica para erro de programação, como um argumento nulo ou um nome padrão inválido na criação (perguntas 32 e 35).
- **Um slot por cor:** o canal é a posição do slot, então um nó nunca lê e emite na mesma cor ao mesmo tempo, e o `Node` recusa slots fora da ordem das cores (pergunta 224).
- **Agregado mutável, valores imutáveis:** a rede muda pelas operações, e as consultas devolvem listas imutáveis. `Node` e `ChannelSlot` são valores: mudar um slot troca o nó inteiro (pergunta 33).
- **Nome aparado e recusado:** as pontas saem, e vazio ou longo demais é recusado. No canal, vazio volta ao nome da cor (pergunta 34).
- **Controlador liga e desliga:** criar a rede é colocar o controlador, então ela nasce `Online`. Retirar o deixa `Unplaced`, e recolocar volta na nova posição, com tudo igual, no estado que tinha (perguntas 24, 146 e 178). Uma rede com o controlador colocado recusa outro com `ALREADY_PLACED`, para um item copiado não dar dois controladores a ela, e mover é retirar e recolocar (pergunta 56).
- **Controlador perdido sem pedido:** quando o bloco some sem o dono quebrá-lo, como no criativo, por comando ou por outro mod, `loseController` deixa a rede `Unplaced` sem receber quem pede, porque não há a quem perguntar. Só `removeController`, a quebra pelo dono, confere o dono (pergunta 49).
- **Nó perdido sem pedido:** do mesmo jeito, `loseNode` tira da rede o nó cujo bloco sumiu ou que uma explosão levou, sem receber quem pede. Só `detachNode`, a retirada pelo dono, confere o dono (pergunta 74).
- **Uma rede por nó, garantida fora:** o agregado não enxerga as outras redes, então o `LinkNode`, em `application/`, tira o nó da rede antiga antes de ligá-lo na nova. Ele confere tudo antes de mudar algo, e quem pede precisa ser dono das duas redes (pergunta 42).
- **Só rede `Online` recebe nó:** `attachNode` recusa com `NETWORK_NOT_ONLINE` uma rede `Offline` ou `Unplaced`, porque o player só escolhe entre as redes `Online` dele (perguntas 157 e 172).
- **Nome do nó opcional:** sem nome próprio, o nó mostra os 8 primeiros caracteres do ID, calculados na hora, então não há nome padrão gravado que fique velho. Renomear para vazio limpa o nome (perguntas 144 e 206). O nome é do nó, então ele o leva ao trocar de rede, mesmo que passe do limite de hoje, como na restauração (perguntas 93 e 200).
- **Primeiro canal livre:** é o primeiro, na ordem das cores, sem nenhum slot na rede (pergunta 25).
- **Avaliação por mudança:** uma leitura que muda o valor, ou uma mudança de estrutura aceita, deixa a rede pendente. Renomear a rede, o canal ou o nó e trocar a cor, não, porque não mudam sinal. O simulador ignora a rede que não espera e, depois de avaliar, ela para de esperar, então uma segunda chamada no mesmo tick não faz nada (regra 18).
- **Estado na rede, simulador sem estado:** a rede sabe quando um sinal dela mudou e o que emitiu por último, para o estado de uma rede ficar num lugar só (pergunta 38).
- **Só o que mudou volta:** o simulador devolve só as faces cujo sinal mudou, para o jogo avisar os vizinhos só delas. Uma face que deixou de existir, por um nó desligado ou um slot liberado, volta a `OFF`, e não volta nada se ela já estava em `OFF`.
- **Primeiro tick reaplica tudo:** uma face nunca emitida sempre muda, até para `OFF`, porque, depois de carregar o mundo, o bloco pode ter qualquer valor (pergunta 38).
- **Mesma face, o mais forte:** duas saídas do mesmo nó na mesma face emitem a mais forte, como dois fios de redstone no mesmo bloco (pergunta 39).
- **Canais numa passada:** `channelValues` percorre os nós uma vez para os 16 canais, para a meta de 1.000 nós de `limites.md` não virar uma conta por saída.
- **Números por interface:** limites como o tamanho dos nomes vêm de `NetworkLimits`, nunca de constantes no domínio (regra 3).
- **Restaurar sem setters:** a rede salva volta por `Network.restore`, uma fábrica que recebe o `NetworkSnapshot`, e não por métodos que mudam campos, para nenhuma regra ficar de fora (pergunta 90). A fronteira descarta o que está inválido no save antes do domínio, então um snapshot que quebra as regras do `SnapshotRules`, como um nó repetido ou uma leitura fora de uma entrada, é erro de programação (pergunta 92).
- **Limites valem para as próximas mudanças:** a rede restaurada mantém os slots de cada nó e o tamanho dos nomes, mesmo que o config agora permita menos, para uma troca de config não apagar o trabalho do player (pergunta 93).
- **Restaurada, a rede espera o primeiro tick:** o snapshot não leva o que a rede emitiu, então a rede restaurada começa pendente e reaplica todas as saídas (pergunta 38).
- **Leitura 0 não vai para o save:** é o mesmo que uma entrada nunca lida, então o snapshot só leva as leituras ligadas, e a ida e volta pelo save sai igual (pergunta 89).
- **A face grudada acompanha o nó:** ao colar, um slot na face onde o nó de origem estava grudado vai para a face onde o nó de destino está grudado, para continuar lendo o próprio bloco (perguntas 71 e 105). As outras faces ficam como estão.
- **Colar troca tudo, pelas operações do agregado:** colar configura ou libera cada slot pelo `configureSlot` e pelo `clearSlot`, então o dono é conferido e as leituras caem, sem uma operação nova no `Network`. Os 16 slots vão com o status (perguntas 106, 230 e 277).
- **Portas binárias:** uma entrada conta como ligada acima de 0, como a tocha do jogo, para todas as portas seguirem a mesma regra e o XOR e o NAND terem sentido. A saída é 15 ou 0, e a conversão fica num lugar só, `BinaryOutput` (perguntas 321 e 322).
- **NOT fora do `Gate`:** o NOT tem uma entrada só, então é a classe `Not`, e não uma constante do `Gate` com uma entrada ignorada (pergunta 324).
- **Chave da porta com o save:** o `Gate` ainda não tem `key` nem `fromKey`, que entram na #31, com o primeiro módulo que grava o modo (pergunta 325).
- **Tipo só pelos slots ativos:** um slot desativado não lê nem emite, então não conta para o `NodeKind` (perguntas 228 e 337).
- **Tablet sem posição e sem save:** o Signal Remote não é nó, então não tem posição nem alcance próprio no domínio. Quem chama já filtra os tablets pelo alcance do player e só entrega os que contam, e o estado ligado ou desligado fica no item (perguntas 27, 354 e 356).
- **Pendência só quando um canal muda:** trocar o conjunto de tablets compara os 16 canais antes e depois, para um tablet sobre um canal já ligado não acordar o simulador (pergunta 360).
- **Tablet num slot inválido é ignorado:** um slot que deixou de ser input ativo não recebe nada, e o item mostra `Link lost`, para um vínculo velho nunca prender um canal (pergunta 287).
- **Sem `get*`:** `strength()`, não `getStrength()` (regra 11), conferido pelo ArchUnit.

