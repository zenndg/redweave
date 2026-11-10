# Configurador

O Configurator (`redweave:configurator`) é a ferramenta que liga nós a uma rede e copia e cola a configuração de um nó em outros. Esta página descreve o item na Fase 1, com as ações da issue #17. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), o nó em [`no-de-sinal.md`](no-de-sinal.md) e o visual em [`visual.md`](../conceito/visual.md#configurator). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Clique no Network Controller para guardar a rede, clique num nó para ligá-lo a ela, e Shift + clique num nó para copiar a configuração e colá-la em outros nós.

## Ações

Toda ação roda no servidor, que confere o dono da rede (regras 16 e 17). A distância é a de interação com blocos do jogo, a mesma da Tela do Nó (pergunta 81).

| Ação | O que acontece | Pergunta |
|---|---|---|
| Clique no Network Controller | O dono guarda a rede no item. A rede atual do player não muda | 104 |
| Clique num nó, sem cópia | Liga o nó à rede guardada, com 1 entrada no primeiro canal livre. Quem usa precisa ser dono da rede antiga e da nova | 42 e 104 |
| Shift + clique num nó | Copia a configuração dos slots para o item, no lugar da cópia anterior | 105 |
| Clique num nó, com cópia | Cola a cópia no nó. Dá para colar em vários nós | 106 |
| Shift + clique no ar | Apaga a cópia | 106 |

O clique no nó com o Configurator não abre a Tela do Nó, que abre só com a mão vazia.

### Guardar a rede

Só o dono guarda a rede, porque só ele poderia ligar nós a ela (pergunta 42). O item recebe o `NetworkId` no componente `redweave:network`, o mesmo do item do controlador (pergunta 65), e o nome da rede no componente `redweave:network_name`, que serve só ao tooltip (pergunta 112). Depois de uma renomeação, o tooltip mostra o nome antigo até o próximo clique no controlador, e as ações usam sempre o ID.

### Ligar um nó

É o caso de uso `LinkNode`: o nó sai da rede antiga, perde os slots e as leituras, e entra na rede guardada como um nó recém-colocado, com 1 entrada na face onde está grudado, no primeiro canal livre (perguntas 25 e 42). Um nó sem rede entra em qualquer rede do próprio player.

### Copiar

A cópia leva cada slot em uso, com a posição, a direção, a face e o canal, e a face onde o nó de origem está grudado. As leituras não vão (pergunta 105). Copiar um nó sem rede é recusado, porque ele não tem slots.

### Colar

Colar troca todos os slots do nó de destino: os da cópia são configurados, e os outros ficam livres (pergunta 106). As leituras do nó de destino são descartadas, como em toda reconfiguração de slot.

| Regra | Como é |
|---|---|
| Face grudada | Um slot na face onde o nó de origem estava grudado vai para a face onde o nó de destino está grudado, para ler o próprio bloco em qualquer orientação (pergunta 71). As outras faces ficam como estão |
| Slots demais | Um slot cuja posição passa da quantidade de slots do nó de destino fica de fora, e a mensagem diz quantos |
| Rede | O nó continua na rede dele. Os canais são os mesmos em toda rede, porque são as 16 cores |
| Dono | Só o dono da rede do nó cola. Um nó sem rede recusa |
| Cópia | Continua no item até o Shift + clique no ar |

## Mensagens

Na action bar, em inglês e em português (pergunta 108).

| Ação | Resultado | Mensagem |
|---|---|---|
| Controlador | Guardou | `Configurator: <rede>` |
| Controlador | Outro player | `Only the owner can use this network.` |
| Controlador | Sem rede | `No network` |
| Ligar | Ligou | `Linked to <rede>` |
| Ligar | Sem rede guardada | `Click a Network Controller first.` |
| Ligar | Já está nela | `This node is already in <rede>.` |
| Ligar | Não é dono das duas | `You must own both networks.` |
| Ligar | A rede guardada sumiu | `The stored network no longer exists.` |
| Copiar | Copiou | `Copied <n> slots` |
| Copiar | Nó sem rede | `No network` |
| Colar | Colou | `Pasted <n> slots` |
| Colar | Sobraram slots | `Pasted <n> slots, <m> left out` |
| Colar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede | `No network` |
| Limpar | Apagou | `Copy cleared` |

O tooltip mostra `Network: <nome>` ou `No network stored`, `Copy: <n> slots` quando há cópia, e as dicas `Click a controller to store its network.` e `Shift + click a node to copy it.`

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:configurator` |
| Nome | `Configurator`, e `Configurador` em português |
| Pilha | 1, porque cada item guarda a sua rede e a sua cópia |
| Aba do criativo | `Redweave` (pergunta 69) |
| Textura | `configurator`, da prancha [`texturas.png`](../imagens/conceito/texturas.png) (pergunta 51). A camada `configurator_channel`, do visor, fica para a issue #80 |
| Data components | `redweave:network`, `redweave:network_name` e `redweave:node_slots`. Os IDs são contrato de save e ficam fixados na #26 (regras 7 e 15) |

### Formato da cópia

O componente `redweave:node_slots` tem `attached`, a face onde o nó de origem estava grudado, e `slots`, com `index`, `direction`, `face` e `channel` de cada slot em uso. Os valores usam as mesmas chaves do save das redes, como `input`, `east` e `red` (pergunta 89).

## Fora desta funcionalidade

- O visor ao vivo e o rótulo flutuante sobre os nós (issue #80, pergunta 103).
- O Label, que guarda uma configuração para reutilizar (issue #81, perguntas 110 e 111).
- A receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 15:52 | Cria o documento, com as respostas das perguntas 103 a 112. |
