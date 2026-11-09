# Glossário

A linguagem do domínio. Os nomes são os mesmos no código, nos documentos e no jogo.

| Termo | Em inglês | O que é |
|---|---|---|
| Sinal | `Signal` | Um valor de 0 a 15, o mesmo intervalo da redstone do jogo |
| Rede | `Network` | O agregado criado no Network Controller. Tem nome, cor, dono e permissões, e reúne os nós e os canais |
| Canal | `Channel` | Uma das 16 linhas de sinal de uma rede, cada uma com uma cor de lã. Com várias entradas, vale o sinal mais forte |
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
| Signal Remote | `signal_remote` | Item de mão que age como alavanca ou botão num canal. Nome pendente (pergunta 15) |
| Ponte | `Bridge` | Na Fase 5, o nó que liga canais a um broker MQTT |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
