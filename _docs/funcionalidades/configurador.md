# Configurador

O Configurator (`redweave:configurator`) é a ferramenta que abre, copia, cola e retira os Signal Nodes. Esta página descreve o item na Fase 1. A issue #17 entregou o item com outro comportamento, o de guardar a rede e ligar nós, que sai com as perguntas 126 a 168. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), o nó em [`no-de-sinal.md`](no-de-sinal.md) e o visual em [`visual.md`](../conceito/visual.md#configurator). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Com o Configurator na mão, Shift + scroll escolhe o modo: `Open` abre a Tela do Nó, `Copy` copia e cola a configuração de um nó, e `Wrench` retira o nó como item.

## Modos

O Shift + scroll com o Configurator na mão troca o modo, na ordem `Open`, `Copy`, `Toggle` e `Wrench` (pergunta 241). Para baixo avança e para cima volta, como a hotbar, e um Configurator novo começa no `Open` (pergunta 192). O scroll só existe no cliente, que manda o sentido num pacote; o servidor confere que o item na mão principal é o Configurator e grava o modo num data component (regras 16 e 17, perguntas 143 e 164).

| Modo | Em português | Cor | Clique num nó | Shift + clique num nó | Pergunta |
|---|---|---|---|---|---|
| `Open` | `Abrir` | Azul | Abre a Tela do Nó | — | 140 e 143 |
| `Copy` | `Copiar` | Verde | Copia a configuração dos slots | Cola a cópia | 127 e 143 |
| `Toggle` | `Alternar` | Âmbar | Ativa todos os canais configurados do nó | Desativa todos os canais configurados | 241 |
| `Wrench` | `Chave` | Vermelho | Retira o nó como item | — | 161 |

Em qualquer modo, o Shift + clique no ar apaga a cópia (pergunta 128). O clique em qualquer outro bloco não faz nada, para um Shift + clique num bloco não apagar a cópia por engano.

A cor de cada modo aparece sempre com o nome escrito: na action bar ao trocar, no tooltip e numa faixa do visor. Os tons ficam no [`visual.md`](../conceito/visual.md#configurator) (perguntas 167 a 169).

Toda ação roda no servidor, que confere o dono (regras 16 e 17). A distância é a de interação com blocos do jogo, a mesma da Tela do Nó (pergunta 81). Num nó de uma rede `Offline`, `Open` e `Copy` não fazem nada, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` (perguntas 132 e 160). O `Wrench` funciona (pergunta 163).

### Abrir

É o único jeito de abrir a Tela do Nó: a mão vazia só mostra a dica `Use a Configurator to open this node.` (perguntas 154 e 188). Os únicos blocos que aceitam a mão vazia são o Network Controller e o Network Terminal. No Network Controller, o modo `Open` faz o mesmo que a mão vazia e abre a Tela do Controlador (pergunta 232). Um nó `No network` abre na tela de escolha de rede, e um nó com rede abre na configuração ([`telas.md`](telas.md#tela-do-nó), pergunta 139).

### Copiar

A cópia leva os 16 slots, cada um com o canal, o status, a direção, a face e o nome, e a face onde o nó de origem está grudado, no lugar da cópia anterior. As leituras não vão (perguntas 105 e 230). Copiar um nó sem rede é recusado, porque ele não tem slots.

### Colar

Colar substitui os 16 slots do nó de destino pelos da cópia, com o status e o nome (perguntas 106 e 230). As leituras do nó de destino são descartadas, como em toda reconfiguração de slot.

| Regra | Como é |
|---|---|
| Face grudada | Um slot na face onde o nó de origem estava grudado vai para a face onde o nó de destino está grudado, para ler o próprio bloco em qualquer orientação (pergunta 71). As outras faces ficam como estão |
| Rede | O nó continua na rede dele. Os canais são os mesmos em toda rede, porque são as 16 cores |
| Dono | Só o dono da rede do nó cola. Num nó com rede, a rede não muda (pergunta 239) |
| Nó sem rede | Colar liga o nó à rede da cópia e aplica os slots, como escolher a rede na Tela do Nó: só quem colocou o nó, sendo dono da rede da cópia, com ela `Online`, e ela vira a última rede usada (pergunta 238) |
| Cópia | Continua no item até o Shift + clique no ar, então cola em vários nós |
| Sem cópia | O Shift + clique não faz nada nem mostra mensagem (pergunta 195) |

### Retirar

No modo `Wrench`, o clique num nó o retira como item, que vai para o inventário, ou cai no chão se o inventário estiver cheio. É como quebrar o nó (pergunta 74): ele sai da rede, perde os slots e o nome, e o item é um Signal Node comum, que empilha. Recolocado, nasce `No network` (perguntas 139 e 161).

Só o dono usa a Chave: o dono da rede do nó e, num nó sem rede, quem o colocou, gravado no bloco com a chave de NBT `owner` (perguntas 162, 165 e 189). Um nó colocado antes do `owner` não tem quem o colocou, e a Chave recusa para todos; a mão ainda o quebra (perguntas 74 e 193). A Chave vale só para o Signal Node: o Network Controller e o Network Terminal saem só quebrando (pergunta 164).

## Mensagens

Na action bar, em inglês e em português (perguntas 108 e 143).

| Ação | Resultado | Mensagem |
|---|---|---|
| Copiar | Copiou | `Copied <n> slots`, com todos os canais configurados, ativos ou não (pergunta 240) |
| Copiar | Nó sem rede | `No network` |
| Colar | Colou | `Pasted <n> slots`, com todos os canais configurados (pergunta 240) |
| Alternar | Clique | `Enabled <n> channels` |
| Alternar | Shift + clique | `Disabled <n> channels` |
| Alternar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede, colocado por outro player | `Only the player who placed this node can paste a network on it.` |
| Colar | Nó sem rede, rede da cópia de outro dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede, rede da cópia desligada | `<rede> is offline. Turn it on at a Network Terminal.` |
| Colar | Nó sem rede, rede da cópia que não existe mais | `No network` |
| Limpar | Apagou | `Copy cleared` |
| Trocar de modo | Trocou | `Mode: <modo>`, com o nome na cor do modo |
| Retirar | Retirou | `Node picked up` |
| Retirar | Não é dono da rede | `Only the owner of <rede> can change this node.` |
| Retirar | Nó sem rede, de outro player | `Only the player who placed this node can pick it up.` |
| Mão vazia no nó | — | `Use a Configurator to open this node.` |
| Qualquer modo | Rede `Offline` | `<rede> is offline. Turn it on at a Network Terminal.` |

O tooltip mostra `Mode: <modo>`, com o nome na cor do modo, `Copy: <n> slots` quando há cópia, a dica do modo (`Click a node to open it.`, `Click a node to copy it, Shift + click to paste.` ou `Click a node to pick it up.`) e `Shift + scroll to change the mode.` (pergunta 190).

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:configurator` |
| Nome | `Configurator`, e `Configurador` em português |
| Pilha | 1, porque cada item guarda o seu modo e a sua cópia |
| Aba do criativo | `Redweave` (pergunta 69) |
| Textura | `configurator`, da prancha [`texturas.png`](../imagens/conceito/texturas.png) (pergunta 51). A camada do visor, que mostra o modo, fica para a issue #80 (pergunta 159) |
| Data components | `redweave:node_slots`, com a cópia, e `redweave:configurator_mode`, com o modo: `open`, `copy` ou `wrench` (pergunta 173). Os IDs são contrato de save e ficam fixados na #26 (regras 7 e 15) |

O item não guarda mais a rede atual: os componentes `redweave:network` e `redweave:network_name` saem do Configurator (pergunta 126). A cópia guarda a rede do nó copiado, para colar num nó sem rede (pergunta 238). O `redweave:network` continua no item do Network Controller.

### Formato da cópia

O componente `redweave:node_slots` tem `network`, o ID da rede do nó copiado (pergunta 238), `attached`, a face onde o nó de origem estava grudado, e `slots`, com `channel`, `active`, `direction`, `face` e `name` (opcional) de cada slot já ativado alguma vez, como no save. Os valores usam as mesmas chaves do save das redes, como `input`, `east` e `red` (pergunta 89).

## Fora desta funcionalidade

- O visor do modo e o rótulo flutuante sobre os nós (issue #80, pergunta 159).
- A receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 17:45 | Adiciona o modo `Toggle` e conta todos os canais configurados na cópia (perguntas 240 e 241). |
| Zennd | 2026-11-11 17:20 | Colar num nó sem rede o liga à rede da cópia (perguntas 238 e 239). |
| Zennd | 2026-11-11 16:40 | A cópia leva os 16 slots, com o status e o nome (perguntas 224 e 230). |
| Zennd | 2026-11-11 16:12 | O modo `Open` abre a Tela do Controlador (pergunta 232). |
| Zennd | 2026-11-11 11:21 | Aplica as perguntas 188 a 195, da #86. |
| Zennd | 2026-11-11 10:24 | Aplica a pergunta 173. |
| Zennd | 2026-11-11 09:51 | Reescreve o Configurator com os modos `Open`, `Copy` e `Wrench`, sem guardar a rede e sem ligar nós (perguntas 125 a 169). |
| Marcelo Fabiano | 2026-11-10 15:52 | Cria o documento, com as respostas das perguntas 103 a 112. |
