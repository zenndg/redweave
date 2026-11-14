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
| Módulo | `SignalModule` | Um membro dos Signal Modules, como o `Signal Timer` (`signal_timer`), o `Signal Inverter` (`signal_inverter`), o `Weather Detector` (`weather_detector`) e o `Signal Randomizer` (`signal_randomizer`) (perguntas 300, 457 e 475) |
| Conexão | `Connection` | Uma entrada ou uma saída de um módulo, ligada a uma borda da placa (`Edge`) ou a um canal da rede do módulo (`Channel`), nunca aos dois. Não é uma porta (perguntas 376 e 420) |
| Signal Inverter | `signal_inverter` | `Inversor de Sinal` em português: o NOT do mod, o módulo que liga a saída B quando a entrada A não tem sinal e a desliga quando tem, com o atraso da tocha, e queima num laço (perguntas 296, 310, 444, 445 e 447) |
| Signal Randomizer | `signal_randomizer` | `Sorteador de Sinal` em português: o módulo de fonte que, ligado, sorteia a cada intervalo um valor de 0 a 15 (`Analog`) ou 0 ou 15 (`Binary`) e o mantém na saída até o próximo sorteio (perguntas 475, 476, 478 e 486) |
| Signal Timer | `signal_timer` | `Temporizador de Sinal` em português: o módulo que, a cada intervalo, liga a saída por um pulso, e para enquanto a entrada `Pause` recebe sinal (perguntas 379, 380 e 410) |
| Weather Detector | `weather_detector` | `Detector de Clima` em português: o módulo de fonte que liga a saída conforme o clima da dimensão, nos modos `Rain`, `Thunder` ou `Clear`, e fica em 0 onde não há clima (perguntas 457 a 459, 464 e 469) |
| Porta | `Gate` | AND, OR, XOR ou NAND, de duas entradas, como Signal Module ou dentro de um chip. Cada entrada conta como ligada acima de 0, e a saída é 15 ou 0. O NOT é o Signal Inverter (perguntas 309, 310, 321 e 322) |
| Regra NOT | `Not` | A regra de uma entrada que o Signal Inverter usa: 15 sem sinal e 0 com qualquer sinal, como a tocha do jogo. Não é uma porta (perguntas 310, 321 e 322) |
| Circuito | `Circuit` | Um grafo de portas, avaliado em ordem topológica |
| Chip | `Chip` | Um circuito gravado num item, com nome e ícone, que roda como uma peça só |
| Simulador | `Simulator` | Avalia cada rede uma vez por tick, só quando um canal mudou |
| Network Controller | `network_controller` | O bloco que é uma rede: colocá-lo cria a rede, e a tela dele a gerencia, com a lista de nós e o log |
| Network Terminal | `network_terminal` | O bloco que administra todas as redes do dono: renomear, desligar, religar, limpar os nós e apagar as `Unplaced`. Não cria redes (perguntas 130 e 131) |
| Configurador | `configurator` | Ferramenta com quatro modos: `Open` abre a Tela do Nó, `Copy` copia e cola a configuração de um nó, `Toggle` ativa ou desativa todos os canais configurados de um nó, e `Wrench` retira o nó como item (perguntas 143, 161 e 241) |
| Modo | — | O modo atual do Configurator, trocado com Shift + scroll e mostrado com uma cor e o nome (perguntas 143 e 167) |
| Log | — | A lista das últimas mudanças de valor das entradas e saídas de uma rede, na tela do Network Controller (pergunta 147) |
| Signal Remote | `signal_remote` | Item de mão com aparência de tablet, `Controle de Sinal` em português, vinculado a um slot de input de um nó, que age como alavanca ou botão no canal desse slot, sempre com 15 (perguntas 15, 279, 282, 288, 352 e 368). Na rede, é uma entrada sem posição, a parte `RemoteInputs` (perguntas 27 e 360) |
| Ponte | `Bridge` | Na Fase 5, o nó que liga canais a um broker MQTT |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
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
