# Configurador

O Configurator (`redweave:configurator`) é a ferramenta que abre, copia, cola e retira os Signal Nodes. Esta página descreve o item na Fase 1. A issue #17 entregou o item com outro comportamento, o de guardar a rede e ligar nós, que sai com as perguntas 126 a 168. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), o nó em [`no-de-sinal.md`](no-de-sinal.md) e o visual em [`visual.md`](../conceito/visual.md#configurator). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Com o Configurator na mão, Shift + scroll escolhe o modo: `Open` abre a Tela do Nó, `Copy` copia e cola a configuração de um nó, e `Wrench` retira o nó como item.

## Modos

O Shift + scroll com o Configurator na mão troca o modo, na ordem `Open`, `Copy` e `Wrench`. O modo fica num data component do item, e o servidor confere o pacote da troca (regras 16 e 17, perguntas 143 e 164).

| Modo | Em português | Cor | Clique num nó | Shift + clique num nó | Pergunta |
|---|---|---|---|---|---|
| `Open` | `Abrir` | Azul | Abre a Tela do Nó | — | 140 e 143 |
| `Copy` | `Copiar` | Verde | Copia a configuração dos slots | Cola a cópia | 127 e 143 |
| `Wrench` | `Chave` | Vermelho | Retira o nó como item | — | 161 |

Em qualquer modo, o Shift + clique no ar apaga a cópia (pergunta 128). O clique em qualquer outro bloco não faz nada, para um Shift + clique num bloco não apagar a cópia por engano.

A cor de cada modo aparece sempre com o nome escrito: na action bar ao trocar, no tooltip e numa faixa do visor. Os tons ficam no [`visual.md`](../conceito/visual.md#configurator) (perguntas 167 a 169).

Toda ação roda no servidor, que confere o dono (regras 16 e 17). A distância é a de interação com blocos do jogo, a mesma da Tela do Nó (pergunta 81). Num nó de uma rede `Offline`, `Open` e `Copy` não fazem nada, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` (perguntas 132 e 160). O `Wrench` funciona (pergunta 163).

### Abrir

É o único jeito de abrir a Tela do Nó: a mão vazia só mostra a dica `Use a Configurator to open this node.` (pergunta 154). Um nó `No network` abre na tela de escolha de rede, e um nó com rede abre na configuração ([`telas.md`](telas.md#tela-do-nó), pergunta 139).

### Copiar

A cópia leva cada slot em uso, com a posição, a direção, a face e o canal, e a face onde o nó de origem está grudado, no lugar da cópia anterior. As leituras não vão (pergunta 105). Copiar um nó sem rede é recusado, porque ele não tem slots.

### Colar

Colar troca todos os slots do nó de destino: os da cópia são configurados, e os outros ficam livres (pergunta 106). As leituras do nó de destino são descartadas, como em toda reconfiguração de slot.

| Regra | Como é |
|---|---|
| Face grudada | Um slot na face onde o nó de origem estava grudado vai para a face onde o nó de destino está grudado, para ler o próprio bloco em qualquer orientação (pergunta 71). As outras faces ficam como estão |
| Slots demais | Um slot cuja posição passa da quantidade de slots do nó de destino fica de fora, e a mensagem diz quantos |
| Rede | O nó continua na rede dele. Os canais são os mesmos em toda rede, porque são as 16 cores |
| Dono | Só o dono da rede do nó cola. Um nó sem rede recusa |
| Cópia | Continua no item até o Shift + clique no ar, então cola em vários nós |

### Retirar

No modo `Wrench`, o clique num nó o retira como item, que vai para o inventário, ou cai no chão se o inventário estiver cheio. É como quebrar o nó (pergunta 74): ele sai da rede, perde os slots e o nome, e o item é um Signal Node comum, que empilha. Recolocado, nasce `No network` (perguntas 139 e 161).

Só o dono usa a Chave: o dono da rede do nó e, num nó sem rede, quem o colocou, gravado no bloco (perguntas 162 e 165). A Chave vale só para o Signal Node: o Network Controller e o Network Terminal saem só quebrando (pergunta 164).

## Mensagens

Na action bar, em inglês e em português (perguntas 108 e 143).

| Ação | Resultado | Mensagem |
|---|---|---|
| Copiar | Copiou | `Copied <n> slots` |
| Copiar | Nó sem rede | `No network` |
| Colar | Colou | `Pasted <n> slots` |
| Colar | Sobraram slots | `Pasted <n> slots, <m> left out` |
| Colar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede | `No network` |
| Limpar | Apagou | `Copy cleared` |
| Retirar | Não é dono | `Only the owner of <rede> can change this node.` |
| Qualquer modo | Rede `Offline` | `<rede> is offline. Turn it on at a Network Terminal.` |

O tooltip mostra o modo, com a cor e o nome, e `Copy: <n> slots` quando há cópia.

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:configurator` |
| Nome | `Configurator`, e `Configurador` em português |
| Pilha | 1, porque cada item guarda o seu modo e a sua cópia |
| Aba do criativo | `Redweave` (pergunta 69) |
| Textura | `configurator`, da prancha [`texturas.png`](../imagens/conceito/texturas.png) (pergunta 51). A camada do visor, que mostra o modo, fica para a issue #80 (pergunta 159) |
| Data components | `redweave:node_slots`, com a cópia, e o do modo (pergunta 173). Os IDs são contrato de save e ficam fixados na #26 (regras 7 e 15) |

O item não guarda mais a rede: os componentes `redweave:network` e `redweave:network_name` saem do Configurator (pergunta 126). O `redweave:network` continua no item do Network Controller.

### Formato da cópia

O componente `redweave:node_slots` tem `attached`, a face onde o nó de origem estava grudado, e `slots`, com `index`, `direction`, `face` e `channel` de cada slot em uso. Os valores usam as mesmas chaves do save das redes, como `input`, `east` e `red` (pergunta 89).

## Pendências

| Ponto | Depende de |
|---|---|
| O ID e os valores do componente do modo | Pergunta 173 |
| Os textos das mensagens de trocar de modo e de retirar o nó, e as dicas do tooltip | Issue da implementação dos modos |

## Fora desta funcionalidade

- O visor do modo e o rótulo flutuante sobre os nós (issue #80, pergunta 159).
- A receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 09:51 | Reescreve o Configurator com os modos `Open`, `Copy` e `Wrench`, sem guardar a rede e sem ligar nós (perguntas 125 a 169). |
| Marcelo Fabiano | 2026-11-10 15:52 | Cria o documento, com as respostas das perguntas 103 a 112. |
