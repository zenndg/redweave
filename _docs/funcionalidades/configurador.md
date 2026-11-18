# Configurador

O Configurator (`redweave:configurator`) é a ferramenta que abre, copia, cola e retira os Signal Nodes, abre, gira, alterna, copia, cola e retira os Signal Modules (perguntas 393, 395, 399, 610 e 722 a 736), e retira os Signal Cables (pergunta 525). Esta página descreve o item na Fase 1. A issue #17 entregou o item com outro comportamento, o de guardar a rede e ligar nós, que sai com as perguntas 126 a 168. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), o nó em [`no-de-sinal.md`](no-de-sinal.md) e o visual em [`visual.md`](../conceito/visual.md#configurator). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Com o Configurator na mão, Shift + scroll escolhe um dos cinco modos: `Open` abre a Tela do Nó, `Rotate` gira os Signal Modules, `Copy` copia e cola a configuração de um nó, `Toggle` ativa e desativa os canais configurados dele, e `Wrench` retira o nó como item. Num Signal Module, `Open` abre a tela, `Rotate` gira a placa nos dois sentidos, `Copy` copia e cola a configuração num módulo do mesmo tipo, `Toggle` faz o que o módulo define, e `Wrench` o retira (perguntas 393, 395, 610, 616, 618 e 722 a 736). O Signal Lock nunca é copiado (pergunta 731). Num Signal Cable, só o `Wrench` faz algo: retira o cabo (pergunta 525).

## Modos

O Shift + scroll com o Configurator na mão troca o modo, na ordem `Open`, `Rotate`, `Copy`, `Toggle` e `Wrench` (perguntas 241 e 614). Para baixo avança e para cima volta, como a hotbar, e um Configurator novo começa no `Open` (pergunta 192). No macOS, o Shift transforma a roda vertical em horizontal; sem rolagem vertical, vale a horizontal invertida, como a hotbar do jogo faz (pergunta 351). O scroll só existe no cliente, que manda o sentido num pacote; o servidor confere que o item na mão principal é o Configurator e grava o modo num data component (regras 16 e 17, perguntas 143 e 164).

| Modo | Em português | Cor | Clique num nó | Shift + clique num nó | Pergunta |
|---|---|---|---|---|---|
| `Open` | `Abrir` | Azul | Abre a Tela do Nó e, como a mão vazia, as telas do Network Controller e do Network Terminal | — | 140, 143, 232 e 343 |
| `Rotate` | `Girar` | Roxo | Nada, com `Only modules can be turned.` | Nada, com a mesma mensagem | 613 a 617 |
| `Copy` | `Copiar` | Verde | Copia a configuração dos slots; num Signal Module, copia a configuração dele | Cola a cópia, num nó ou num módulo do mesmo tipo | 127, 143 e 722 |
| `Toggle` | `Alternar` | Âmbar | Ativa todos os canais configurados do nó | Desativa todos os canais configurados | 241 |
| `Wrench` | `Chave` | Vermelho | Retira o nó como item | — | 161 |

### Copiar e colar todos os itens

Desde a #154, o `Copy` copia e cola a configuração de todos os itens configuráveis do mod, não só do Signal Node (perguntas 722 a 736):

| Ponto | Como é | Pergunta |
|---|---|---|
| Gesto | O clique copia, o Shift + clique cola, e o Shift + clique no ar apaga a cópia, como no nó | 722 |
| Itens | O Signal Node e todos os Signal Modules: o Signal Timer, o Signal Inverter, o Weather Detector, o Signal Randomizer, o Signal Lever, o Signal Button, o Signal Pressure Plate, o Signal Item Sensor, a Signal Lamp e o Signal Alarm. O Signal Gate entra ao nascer. O Signal Cable e o Network Controller ficam fora, porque não têm configuração | 727 |
| Signal Lock e Signal Keypad | Nunca são copiados nem colados, nem as configurações sem segredo: copiar neles ou colar neles mostra `This item cannot be copied.` (`Este item não pode ser copiado.`), e a cópia que estava no Configurator continua | 724 e 731 |
| Quem copia | Só o dono do módulo; os outros veem a mensagem de dono. A cópia do nó continua como antes | 735 |
| O que entra | As chaves de configuração de cada módulo, tiradas do save dele, a rede de origem e o tipo do item. Ficam fora o dono, o nome e o estado do momento (ligado, apertado, contagem, luz, pulso em andamento) | 725 e 732 |
| Onde cola | Só num módulo do mesmo tipo de que o player é dono, com o Shift + clique. O módulo entra na rede da cópia pelo mesmo caminho da escolha de rede na tela (`ChooseModuleNetwork`): a rede precisa existir, ser do player e estar `Online` (regra 17). Assim, um módulo recém-colocado, sem rede, recebe a rede e os canais. Um módulo noutra rede do player troca para a rede copiada | 726 e 732 |
| Rede indisponível | Se a rede da cópia foi apagada, é de outro player ou está `Offline`, o resto cola sem rede, e as conexões em canal viram borda ou nenhuma, com `Channels were cleared: the network is not available.` (`Os canais foram limpos: a rede não está disponível.`) no lugar de `Pasted` | 732 e 736 |
| Estado do destino | Colar troca só as chaves copiadas e lê o resultado pelas validações do save do próprio módulo (regra 14); o estado do momento do módulo de destino fica. Valores de um config mais folgado não são reajustados ao config do servidor | 725 e 736 |
| Uma cópia por vez | Copiar um módulo apaga a cópia de nó, e copiar um nó apaga a de módulo. Colar no tipo errado mostra `This copy is from a <tipo>.` (`Esta cópia é de um(a) <tipo>.`) e não muda nada | 734 |
| Save | O data component `redweave:module_copy`, ao lado do `redweave:node_slots`, com `type` (o ID do item), `network` (o UUID da rede, opcional) e `settings` (as chaves de configuração). Fica fixado na #26 (regra 15) | 729 e 733 |
| Proteção dos blocos | A seleção dos blocos protegidos da #146 sai do `Copy` e vai para um modo novo, `Protect` | 723 |
| Ordem | Passo 20 do roadmap, antes do Signal Alarm | 730 |

As chaves que cada módulo copia:

| Módulo | Chaves copiadas |
|---|---|
| Signal Lever | `strength`, `output`, `output_delay_ticks` |
| Signal Button | `strength`, `interval`, `output`, `output_delay_ticks` |
| Signal Pressure Plate | `strength`, `mode`, `output`, `output_delay_ticks` |
| Signal Item Sensor | `item`, `count`, `strength`, `output`, `output_delay_ticks` |
| Signal Lamp | `strength`, `mode`, `intensity`, `control`, `output_delay_ticks` |
| Signal Alarm | `listeners`, `repeats`, `interval`, `duration`, `input` |
| Signal Timer | `interval_ticks`, `pulse_ticks`, `output`, `pause` |
| Signal Inverter | `input`, `output`, `strength`, `output_delay_ticks`; nunca a `delay_line` (pergunta 841) |
| Weather Detector | `mode`, `output`, `output_delay_ticks` |
| Signal Randomizer | `mode`, `interval_ticks`, `output`, `output_delay_ticks` |
| Signal Lock | Nenhuma: não é copiável (pergunta 731) |

Um módulo novo declara as suas chaves copiáveis ao nascer; uma chave esquecida não é copiada.

### Modo `Rotate`

Desde a #136, o giro dos Signal Modules tem um modo próprio, e o Shift + clique no `Open` não gira mais (perguntas 610 e 613 a 620):

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e cor | `Rotate`, `Girar` em português, em roxo `#9B6BF0`, com o valor `rotate` no data component do modo | 613 e 615 |
| Ordem | `Open`, `Rotate`, `Copy`, `Toggle` e `Wrench` no Shift + scroll | 614 |
| Num Signal Module | O clique gira a placa 90° no sentido horário e o Shift + clique, no anti-horário, olhando-a de frente, com `Output: <borda>` e o clique do comparador. Só o dono gira, e o giro funciona com a rede `Offline` | 616 e 619 |
| Num nó ou num cabo | Nada, com `Only modules can be turned.` (`Só os módulos giram.`), mesmo com a rede do nó `Offline`. Num bloco qualquer, nada, como os outros modos | 617 |
| `Open` com Shift num módulo | Nada; o `Open` só abre a tela | 618 |
| Tooltip | `Click to turn a module, Shift + click to turn it back.` (`Clique para girar um módulo, Shift + clique para voltar.`) | 619 |

Num Signal Module, o `Open` só abre a tela, e o Shift + clique nele não faz nada; o `Rotate` gira a placa 90°, no sentido horário com o clique e no anti-horário com Shift (perguntas 616 e 618); o `Toggle` faz o que o módulo define, como alternar `Running` e `Stopped` no Signal Timer, ligar e desligar o [Signal Lever](alavanca.md), com `<nome>: On` ou `<nome>: Off`, apertar o [Signal Button](botao.md), com `<nome>: Pressed`, mesmo com ele já apertado, ou trocar o modo do [Signal Pressure Plate](placa-de-pressao.md), na ordem `Players`, `Passive`, `Hostile` e `Items`, com `<nome>: <modo>`, enquanto no [Signal Item Sensor](sensor-de-itens.md) ele não faz nada e mostra `Signal Item Sensor has nothing to toggle.` (pergunta 697), e no [Signal Lock](fechadura.md) ele abre a fechadura pelo pulso escolhido na tela, com `<nome>: Unlocked`, sem reiniciar um pulso em andamento (perguntas 639 e 711), e no [Signal Lamp](lampada.md) ele liga e desliga a lâmpada, como um pulso no canal de controle, com `<nome>: On` ou `<nome>: Off` (pergunta 658), e no [Signal Alarm](alarme.md) ele silencia o alarme até a entrada desligar, com `<nome>: Silenced` (pergunta 649); o `Wrench` o retira; e o `Copy` copia a configuração do módulo e a cola noutro do mesmo tipo, menos no Signal Lock, que nunca é copiado (perguntas 722 a 736). No Signal Lever, no Signal Button, no Signal Pressure Plate, no Signal Lock, no Signal Lamp e no Signal Alarm, o `Toggle` funciona mesmo com a rede `Offline` (perguntas 578, 588, 593, 600, 603, 606, 707, 716 e 744). O Signal Lock só sai pelo `Wrench` do dono: no survival, ninguém o quebra à mão, e a action bar mostra `Only the owner can remove this with a Configurator.` (pergunta 641). O `Rotate` também gira o Signal Pressure Plate, no chão. O detalhe está em [`modulos-de-sinal.md`](modulos-de-sinal.md#configurator-e-mão-vazia).

Num [Signal Cable](signal-cable.md), o `Wrench` o retira, o `Rotate` mostra `Only modules can be turned.`, e `Open`, `Copy` e `Toggle` não fazem nada, porque o cabo não tem tela nem estado (perguntas 525 e 617). Num espaço com várias partes, o clique vale só para a parte sob a mira (pergunta 515).

Em qualquer modo, o Shift + clique no ar apaga a cópia (pergunta 128). O clique em qualquer outro bloco não faz nada, para um Shift + clique num bloco não apagar a cópia por engano.

A cor de cada modo aparece com o nome escrito na action bar ao trocar e no tooltip. O visor do item mostra só a cor do modo, porque a tela dele tem 3×2 pixels: os 2×2 da esquerda na cor do modo, e o pixel de cima da direita aceso quando há cópia (perguntas 167 a 169 e 271). Os tons ficam no [`visual.md`](../conceito/visual.md#configurator).

### Rótulo sobre os nós

Com o Configurator na mão principal, em qualquer modo, cada Signal Node até `configurator.label_range` blocos do player mostra um rótulo flutuante, mais apagado atrás de parede, como a etiqueta de nome do jogo (pergunta 268). O rótulo não usa a cor do modo (pergunta 168).

| Linha | O que mostra |
|---|---|
| De cima, com metade do tamanho da etiqueta de nome do jogo | Um quadradinho na cor da rede e o nome dela, como `■ Rede Base`; sem rede, o quadradinho cinza e `No network` |
| De baixo, com 70% desse tamanho | Um quadradinho na cor do primeiro slot habilitado, de 01 a 16, mesmo com sinal 0, e o nome do nó, como `■ Lampada 2`; sem slot habilitado, só o nome (pergunta 772) |

Fora a cor do primeiro slot habilitado, os canais não aparecem no rótulo: ficam nos pontos da placa, nas telas e no Jade e no The One Probe (perguntas 274 a 276 e 772).

Cada Network Controller dentro do mesmo raio mostra um rótulo de duas linhas sobre o bloco: em cima, pequena, o estado da rede e o número de componentes, como `Online · 3 components`; embaixo, maior, um quadradinho na cor da rede e o nome dela, como `■ Rede Base` (perguntas 773 e 804).

Nos rótulos, o canal aparece só com o número, depois do quadradinho na cor dele, como `■ 15`, ou com o nome que o dono deu, como `■ 15 Porta`, sem o nome da cor (pergunta 770).

Cada Signal Module dentro do mesmo raio também mostra um rótulo de duas linhas: o nome do módulo e, embaixo, o estado e o canal da saída, se ela estiver num canal, como `On` ou `Off` no Signal Lever e no Signal Button, o estado com o modo, como `On · Players`, no Signal Pressure Plate, `On` ou `Off`, sem a contagem, no Signal Item Sensor, `Locked` ou `Unlocked`, sem a chave, no Signal Lock, no Signal Lamp, `Offline` ou `Out of range` como o nó, ou `On` ou `Off`, com o canal de intensidade, e, no Signal Alarm, `Ringing` ou `Quiet`, com o canal de entrada (perguntas 417, 582, 595, 609, 637, 703, 721 e 743).

Cada Signal Cable dentro do mesmo raio mostra um rótulo de uma linha: um quadradinho na cor do cabo, o número da cor e a força, como `■ 15 · 12` (perguntas 525 e 770). Num espaço com mais de uma parte com rótulo, cada rótulo se desloca para o lado da sua parte (pergunta 547).

O servidor manda os dados só ao player com o Configurator na mão, com os nós carregados dentro do raio e, depois, só o que mudou, no máximo uma vez por tick. Sem o Configurator, nada vai, e nem os blocos nem o save mudam (pergunta 269 e regra 18).

Toda ação roda no servidor, que confere o dono (regras 16 e 17). A distância é a de interação com blocos do jogo, a mesma da Tela do Nó (pergunta 81). Num nó de uma rede `Offline`, `Open`, `Copy` e `Toggle` não fazem nada, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` (perguntas 132, 160 e 241). O `Wrench` funciona (pergunta 163).

### Abrir

É o único jeito de abrir a Tela do Nó: a mão vazia só mostra a dica `Use a Configurator to open this node.` (perguntas 154 e 188). Os únicos blocos que aceitam a mão vazia são o Network Controller e o Network Terminal. No Network Controller e no Network Terminal, o modo `Open` faz o mesmo que a mão vazia e abre a tela do bloco (perguntas 232 e 343). Um nó `No network` abre na tela de escolha de rede, e um nó com rede abre na configuração ([`telas.md`](telas.md#tela-do-nó), pergunta 139).

### Copiar

A cópia leva os 16 slots, cada um com o canal, o status, a direção e a face, a face onde o nó de origem está grudado e, desde a #177, o delay na saída do nó, na chave opcional `output_delay_ticks`, no lugar da cópia anterior. As leituras e as mudanças pendentes do delay não vão, e o slot não tem nome; uma cópia gravada antes da #177 cola com 0 s, e um delay acima do config de hoje é limitado ao colar (perguntas 105, 230, 277 e 868). Copiar um nó sem rede é recusado, porque ele não tem slots.

### Colar

Colar substitui os 16 slots do nó de destino pelos da cópia, com o status (perguntas 106 e 230). As leituras do nó de destino são descartadas, como em toda reconfiguração de slot.

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

Só o dono usa a Chave: o dono da rede do nó e, num nó sem rede, quem o colocou, gravado no bloco com a chave de NBT `owner` (perguntas 162, 165 e 189). Um nó colocado antes do `owner` não tem quem o colocou, e a Chave recusa para todos; a mão ainda o quebra (perguntas 74 e 193). A Chave vale para o Signal Node e para os Signal Modules ([`modulos-de-sinal.md`](modulos-de-sinal.md)); o Network Controller e o Network Terminal saem só quebrando (pergunta 164).

Num Signal Cable, a Chave retira o cabo e manda o item, com a cor, para o inventário, ou para o chão se não houver espaço. Só quem colocou o cabo o retira no survival; no criativo, qualquer um (perguntas 525 e 526).

## Mensagens

Na action bar, em inglês e em português (perguntas 108 e 143).

| Ação | Resultado | Mensagem |
|---|---|---|
| Copiar | Copiou | `Copied <n> slots`, com todos os canais configurados, ativos ou não (pergunta 240) |
| Copiar | Nó sem rede | `No network` |
| Colar | Colou | `Pasted <n> slots`, com todos os canais configurados (pergunta 240) |
| Alternar | Clique | `Enabled <n> channels` |
| Alternar | Shift + clique | `Disabled <n> channels` |
| Alternar | Nó sem rede | `No network` |
| Alternar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Não é dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede, colocado por outro player | `Only the player who placed this node can paste a network on it.` |
| Colar | Nó sem rede, rede da cópia de outro dono | `Only the owner of <rede> can change this node.` |
| Colar | Nó sem rede, rede da cópia desligada | `<rede> is offline. Turn it on at a Network Terminal.` |
| Colar | Nó sem rede, rede da cópia que não existe mais | `No network` |
| Copiar | Copiou um módulo | `Copied <tipo>`, como `Copied Signal Lever` (`Copiado: Alavanca de Sinal`) (pergunta 728) |
| Copiar ou colar | Signal Lock | `This item cannot be copied.` (`Este item não pode ser copiado.`) (perguntas 731 e 736) |
| Colar | Colou num módulo | `Pasted` (`Colado`) (pergunta 728) |
| Colar | Módulo, rede da cópia indisponível | `Channels were cleared: the network is not available.` (`Os canais foram limpos: a rede não está disponível.`), no lugar de `Pasted` (perguntas 732 e 736) |
| Colar | Item de outro tipo | `This copy is from a <tipo>.` (`Esta cópia é de um(a) <tipo>.`) (perguntas 728 e 734) |
| Copiar ou colar | Módulo de outro player | `Only the player who placed this module can change it.` (pergunta 735) |
| Limpar | Apagou | `Copy cleared` |
| Trocar de modo | Trocou | `Mode: <modo>`, com o nome na cor do modo |
| Girar | Nó ou cabo | `Only modules can be turned.` (pergunta 617) |
| Retirar | Retirou | `Node picked up` |
| Retirar | Não é dono da rede | `Only the owner of <rede> can change this node.` |
| Retirar | Nó sem rede, de outro player | `Only the player who placed this node can pick it up.` |
| Retirar | Retirou um cabo | `Cable picked up` |
| Retirar | Cabo de outro player | `Only the player who placed this cable can pick it up.` |
| Mão vazia no nó | — | `Use a Configurator to open this node.` |
| Qualquer modo | Rede `Offline` | `<rede> is offline. Turn it on at a Network Terminal.` O `Rotate` num nó mostra `Only modules can be turned.` mesmo assim |

O tooltip mostra `Mode: <modo>`, com o nome na cor do modo, `Copy: <n> slots` quando há cópia de um nó, ou `Copy: <item>`, como `Copy: Signal Lever` (`Cópia: Alavanca de Sinal`), quando há cópia de um módulo, a dica do modo (`Click to open a node or module.`, `Click to turn a module, Shift + click to turn it back.`, `Click a node or module to copy it, Shift + click to paste.` (`Clique num nó ou módulo para copiá-lo, Shift + clique para colar.`), `Click a node to enable its channels, Shift + click to disable them.` com `Click a module to start or stop it.` embaixo, ou `Click a node, module or cable to pick it up.`) e `Shift + scroll to change the mode.` (perguntas 190, 241, 430, 619 e 736).

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:configurator` |
| Nome | `Configurator`, e `Configurador` em português |
| Pilha | 1, porque cada item guarda o seu modo e a sua cópia |
| Aba do criativo | `Redweave` (pergunta 69) |
| Textura | `configurator`, da prancha [`texturas.png`](../imagens/conceito/texturas.png) (pergunta 51), com as camadas do visor: `configurator_display`, os 2×2 pixels pintados na cor do modo, e `configurator_copy`, o pixel aceso quando há cópia (pergunta 271) |
| Data components | `redweave:node_slots`, com a cópia de um nó, `redweave:module_copy`, com a cópia de um módulo (pergunta 733), e `redweave:configurator_mode`, com o modo: `open`, `rotate`, `copy`, `toggle` ou `wrench` (perguntas 173, 241 e 613). Os IDs são contrato de save e ficam fixados na #26 (regras 7 e 15) |

O item não guarda mais a rede atual: os componentes `redweave:network` e `redweave:network_name` saem do Configurator (pergunta 126). A cópia guarda a rede do nó copiado, para colar num nó sem rede (pergunta 238). O `redweave:network` continua no item do Network Controller.

### Formato da cópia

O componente `redweave:node_slots` tem `network`, o ID da rede do nó copiado (pergunta 238), `attached`, a face onde o nó de origem estava grudado, e `slots`, com `channel`, `active`, `direction` e `face` de cada slot já ativado alguma vez, como no save (pergunta 277). Os valores usam as mesmas chaves do save das redes, como `input`, `east` e `red` (pergunta 89).

## Fora desta funcionalidade

- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 751).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-18 09:40 | O `Copy` do Signal Node leva o delay na saída, nunca as mudanças pendentes (pergunta 868, #177). |
| Zennd | 2026-11-17 15:15 | O `Copy` dos emissores, do Weather Detector, do Signal Randomizer, do Signal Item Sensor e da Signal Lamp leva o delay, nunca a `delay_line` (perguntas 841 e 850, #173). |
| Zennd | 2026-11-17 13:45 | O `Copy` do Signal Inverter leva a força e o delay (pergunta 841, #171). |
| Zennd | 2026-11-17 11:11 | O rótulo do Controller conta os componentes (pergunta 804). |
| Zennd | 2026-11-17 09:59 | O rótulo do nó ganha o quadrado do primeiro slot habilitado, o Network Controller ganha rótulo, e o canal aparece sem o nome da cor (perguntas 770, 772 e 773). |
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 751). |
| Zennd | 2026-11-16 15:19 | Leva o `Toggle`, a rede `Offline`, o rótulo e a cópia ao Signal Alarm (perguntas 649, 727, 743 e 744). |
| Zennd | 2026-11-16 13:59 | A cópia de todos os itens da #154 passa a implementada, com as chaves por módulo, o `redweave:module_copy`, a rede levada na cópia e a recusa do Signal Lock (perguntas 722 a 736). |
| Zennd | 2026-11-16 13:35 | O Signal Lock e o Signal Keypad nunca são copiados, e a cópia leva a rede e os canais (perguntas 731 e 732). |
| Zennd | 2026-11-16 13:30 | Descreve a cópia de todos os itens da #154 (perguntas 722 a 730). |
| Zennd | 2026-11-16 13:18 | Leva o `Toggle`, a rede `Offline` e o rótulo ao Signal Lamp (perguntas 658, 716 e 721). |
| Zennd | 2026-11-16 12:23 | Leva o `Toggle`, a rede `Offline`, o `Wrench` e o rótulo ao Signal Lock (perguntas 637, 639, 641, 707 e 711). |
| Zennd | 2026-11-16 11:27 | Leva o `Toggle` e o rótulo ao Signal Item Sensor (perguntas 697 e 703). |
| Zennd | 2026-11-15 15:08 | Leva o `Toggle`, o `Rotate` e o rótulo ao Signal Pressure Plate (perguntas 603, 606 e 609). |
| Zennd | 2026-11-15 14:37 | O modo `Rotate` entra, na #136: cinco modos, a ordem nova, as mensagens e as dicas (perguntas 610 e 613 a 620). |
| Zennd | 2026-11-15 14:30 | Descreve o modo Rotate da #136 (perguntas 613 a 620). |
| Zennd | 2026-11-15 14:26 | O giro dos módulos vai para um modo próprio na #136 (pergunta 610). |
| Zennd | 2026-11-15 14:04 | Leva o `Toggle` e o rótulo ao Signal Button, com a rede `Offline` e o botão já apertado (perguntas 588, 593, 595 e 600). |
| Zennd | 2026-11-15 13:16 | Leva o `Toggle` e o rótulo ao Signal Lever, com a rede `Offline` (perguntas 578, 582 e 585). |
| Zennd | 2026-11-15 09:34 | Leva o Signal Cable à Chave, ao rótulo e às dicas (perguntas 525, 526 e 547). |
| Zennd | 2026-11-14 19:21 | Leva os Signal Modules à Chave e aos modos do Configurator. |
| Zennd | 2026-11-14 12:31 | Leva os módulos às dicas do tooltip (pergunta 430). |
| Zennd | 2026-11-14 09:46 | Leva os modos e o rótulo aos Signal Modules, da #103 (perguntas 393, 395, 399 e 417). |
| Zennd | 2026-11-12 19:39 | O Shift + scroll funciona no macOS, com a roda horizontal (pergunta 351). |
| Zennd | 2026-11-12 19:23 | O modo `Open` abre também o Network Terminal (pergunta 343). |
| Zennd | 2026-11-12 17:18 | Inclui o `Toggle` no resumo, na rede `Offline`, nas mensagens e no tooltip, e tira o `name` do formato da cópia (perguntas 241 e 277). |
| Zennd | 2026-11-12 11:58 | Tira o nome do slot da cópia (pergunta 277). |
| Zennd | 2026-11-12 11:46 | O rótulo sobre os nós passa a ter duas linhas, a rede e o nó (perguntas 274 a 276). |
| Zennd | 2026-11-12 11:11 | Descreve o visor do modo e o rótulo sobre os nós da #80 (perguntas 267 a 271). |
| Zennd | 2026-11-11 17:45 | Adiciona o modo `Toggle` e conta todos os canais configurados na cópia (perguntas 240 e 241). |
| Zennd | 2026-11-11 17:20 | Colar num nó sem rede o liga à rede da cópia (perguntas 238 e 239). |
| Zennd | 2026-11-11 16:40 | A cópia leva os 16 slots, com o status e o nome (perguntas 224 e 230). |
| Zennd | 2026-11-11 16:12 | O modo `Open` abre a Tela do Controlador (pergunta 232). |
| Zennd | 2026-11-11 11:21 | Aplica as perguntas 188 a 195, da #86. |
| Zennd | 2026-11-11 10:24 | Aplica a pergunta 173. |
| Zennd | 2026-11-11 09:51 | Reescreve o Configurator com os modos `Open`, `Copy` e `Wrench`, sem guardar a rede e sem ligar nós (perguntas 125 a 169). |
| Marcelo Fabiano | 2026-11-10 15:52 | Cria o documento, com as respostas das perguntas 103 a 112. |
