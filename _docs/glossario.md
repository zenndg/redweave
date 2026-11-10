# Glossário

A linguagem do domínio. Os nomes são os mesmos no código, nos documentos e no jogo.

| Termo | Em inglês | O que é |
|---|---|---|
| Sinal | `Signal` | Um valor de 0 a 15, o mesmo intervalo da redstone do jogo |
| Rede | `Network` | O agregado criado no Network Controller. Tem nome, cor e dono, e reúne os nós e os canais. Só o dono a altera (pergunta 22) |
| Canal | `Channel` | Uma das 16 linhas de sinal de uma rede, cada uma com uma cor de lã e um nome, que o dono pode mudar. Com várias entradas, vale o sinal mais forte |
| Cor do canal | `ChannelColor` | Uma das 16 cores de lã. Dá a cor dos canais e da rede |
| Rede atual | — | A última rede que o player criou ou abriu como dono. Um nó recém-colocado entra nela (pergunta 25) |
| Alcance | — | O raio em blocos a partir do Network Controller, na mesma dimensão (pergunta 23) |
| Online | `Online` | Rede com o controlador colocado, ou nó funcionando dentro do alcance |
| Offline | `Offline` | Rede com o controlador quebrado: todos os canais valem 0 (pergunta 24) |
| Descarregado | `Unloaded` | Nó num chunk descarregado: a entrada mantém o último valor (perguntas 13 e 29) |
| Fora de alcance | `Out of range` | Nó fora do raio ou em outra dimensão: não conta e vale 0 (perguntas 23 e 29) |
| Sem rede | `No network` | Nó sem rede, ou numa rede `Offline` |
| Nó | `Node` | Peça grudada numa face de um bloco. Lê ou emite redstone ali, por slots de canal |
| Slot de canal | `ChannelSlot` | A configuração de um nó para um canal: direção, face do bloco, canal da rede e lógica |
| Direção | `Direction` | Entrada, quando o nó lê o bloco, ou saída, quando o nó emite no bloco |
| Lógica | `Logic` | Transformação de um slot de saída: inverter, só pulso, atraso, limiar |
| Porta | `Gate` | AND, OR, XOR, NOT ou NAND, como bloco em placa ou dentro de um chip |
| Circuito | `Circuit` | Um grafo de portas, avaliado em ordem topológica |
| Chip | `Chip` | Um circuito gravado num item, com nome e ícone, que roda como uma peça só |
| Simulador | `Simulator` | Avalia cada rede uma vez por tick, só quando um canal mudou |
| Network Controller | `network_controller` | O bloco onde a rede é criada e gerenciada |
| Configurador | `configurator` | Ferramenta para parear nós e copiar e colar configurações |
| Etiqueta | `label` | Item que guarda a configuração de um nó para reutilizar |
| Signal Remote | `signal_remote` | Item de mão que age como alavanca ou botão num canal (pergunta 15). Na rede, é uma entrada sem posição (pergunta 27) |
| Ponte | `Bridge` | Na Fase 5, o nó que liga canais a um broker MQTT |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 16:29 | Adiciona a etiqueta. |
| Marcelo Fabiano | 2026-11-09 15:39 | Adiciona os termos da rede e dos canais e aplica as perguntas 22 a 29. |
| Marcelo Fabiano | 2026-11-09 10:34 | Confirma o nome Signal Remote (pergunta 15). |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
