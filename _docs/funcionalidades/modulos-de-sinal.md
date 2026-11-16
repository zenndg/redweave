# Módulos de Sinal

Os Signal Modules (`Módulos de Sinal` em português) são a família de placas que guarda toda a lógica de sinal do Redweave (perguntas 297, 300 e 308). Esta página descreve a base comum, que todo módulo herda, entregue na #103 com o primeiro módulo, o [Signal Timer](signal-timer.md). Cada módulo tem a sua página, com o que só ele faz, e aponta para esta: o [Signal Timer](signal-timer.md), o [Signal Inverter](signal-inverter.md), o [Weather Detector](weather-detector.md), o [Signal Randomizer](signal-randomizer.md), e o [Signal Lever](alavanca.md), o [Signal Button](botao.md) e o [Signal Pressure Plate](placa-de-pressao.md), módulos de fonte (pergunta 549), o [Signal Item Sensor](sensor-de-itens.md), que lê um inventário (pergunta 689), o [Signal Lock](fechadura.md), que só abre com a força exata da chave (pergunta 633), e o [Signal Lamp](lampada.md), uma barra de LED na cor do canal (pergunta 650). A rede está em [`rede-e-canais.md`](rede-e-canais.md), as telas em [`telas.md`](telas.md), e os termos são os do [`glossario.md`](../glossario.md).

O Signal Lever, o Signal Button e o Signal Pressure Plate, os módulos de onde o sinal nasce por um gatilho, formam a família dos [Emissores de Sinal](emissores.md), com um contrato comum de força, luz e saída e a base `EmitterPart` no código (perguntas 627 a 630).

## Em uma frase

Uma placa grudada em qualquer face de um bloco, com um dono, que lê e escreve sinal pelas bordas ou por canais de uma rede, e em que cada módulo só implementa a sua regra (perguntas 313, 314, 375 e 415).

## Base e módulo

| A base comum define | Cada módulo define |
|---|---|
| A placa, com a face, a rotação e o giro | Quais bordas são entrada e quais são saída |
| O dono, o nome e a rede | As suas conexões e o que cada uma faz |
| As conexões, cada uma `Edge` ou `Channel` | A sua regra por tick, no domínio, em `domain/module/` |
| A parte do agregado por tick, a contagem e o log | O que o `Toggle` faz |
| O Configurator, a quebra e o rótulo | O seu NBT próprio |
| O NBT comum e as partes comuns da tela | O centro da tela e a arte |

Resposta 415.

## A placa

| Ponto | Como é | Pergunta |
|---|---|---|
| Forma | Uma placa de 10 × 10 pixels e 2 de espessura, como o Signal Node, grudada em qualquer face de um bloco que não seja ar, no chão, na parede ou no teto | 313, 416 e 422 |
| Espaço | Uma parte do [multipart](multipart.md): divide o espaço de bloco com outras partes, uma por face, como um nó ou um Signal Cable em outra face. Até a #44, cada placa ocupa o seu espaço de bloco | 315, 511 e 513 |
| Rotação | Ao colocar, a frente aponta para onde o player olha, projetada na face, como o repetidor do jogo | 399 |
| Giro | O modo `Rotate` do Configurator gira a placa 90°: o clique no sentido horário e o Shift + clique no anti-horário, olhando-a de frente, com o clique do comparador e a borda da saída na action bar, como `Output: North`. Só o dono gira. Desde a #136, o Shift + clique no `Open` não gira mais | 399, 403, 432, 610, 616 e 618 |
| Bordas | As 4 bordas, frente, trás, esquerda e direita, ligadas aos vizinhos na mesma camada. Quais são entrada e quais são saída é fixo por módulo, e a textura mostra a seta | 314 e 377 |
| Bloco de apoio | Não recebe sinal do módulo. As exceções são o Signal Lever, o Signal Button e o Signal Pressure Plate, que o energizam com força, como uma alavanca grudada. Se ele some, a placa cai como item | 394, 407, 553 e 606 |

## Conexões

Cada entrada ou saída de um módulo é uma **conexão** (`Connection`), ligada a uma borda ou a um canal, nunca aos dois. "Porta" é só o `Gate` (perguntas 376 e 420).

| Ponto | Como é | Pergunta |
|---|---|---|
| `Edge` | A borda fixa do módulo. Uma saída alimenta o bloco à frente com força, como o repetidor, e um bloco sólido também passa o sinal. Uma entrada lê como a entrada do repetidor: fio, alavanca, bloco energizado ou outro módulo | 407 e 412 |
| `Channel` | Um dos 16 canais da rede do módulo | 375 e 397 |
| Valor | Ligado é 15 e desligado é 0. Uma entrada conta como ligada acima de 0 | 321 e 322 |
| Rede | Uma por módulo, escolhida entre as redes `Online` do dono. Todas as conexões em canal usam essa rede | 397 |

## Na rede

O módulo escreve direto num canal, sem nó (pergunta 375).

| Ponto | Como é | Pergunta |
|---|---|---|
| No domínio | Uma parte nova do agregado, como o `RemoteInputs`, recebe a cada tick o conjunto dos módulos carregados que escrevem num canal: o ID, a posição da placa, o canal e o sinal. A rede só fica pendente se um canal mudar (regra 18) | 387 |
| Sinal mais forte | O módulo entra na regra do mais forte junto com as leituras dos nós e os Signal Remotes | 375 |
| Alcance | O do controlador, medido da placa, como um nó. Fora dele, não conta, e o canal recebe 0 | 385 |
| Rede `Offline` ou `Unplaced` | Não conta, e uma entrada lida de um canal vale 0. A regra do módulo continua rodando | 385 |
| Rede apagada | A conexão fica sem canal, e a tela mostra `No network` até o dono escolher outra rede | 385 |
| Chunk descarregado | O módulo não recebe tick, não entra no conjunto e vale 0 no canal, para um módulo congelado não prender um canal ligado. Difere da entrada de um nó, que mantém a última leitura, porque o sinal do módulo é gerado por ele (perguntas 13 e 386). Como os módulos não aparecem em lista (pergunta 388), o estado `Unloaded` de um módulo não é mostrado em nenhuma tela | 386 |
| Entrada por canal | Lê o valor do canal no tick anterior | 387 |
| Save da rede | Nada: a configuração e o estado ficam no NBT do bloco | 387 |

### Na Tela do Controlador

- **Rodapé:** a contagem `● <n> modules on`, na linha 2, depois de `out of range`, com os módulos que contam agora: carregados, no alcance, numa rede `Online` e com a saída num canal, valendo 0 ou 15. A aba `Nodes` continua só com nós (perguntas 388, 413 e 424).
- **Log:** cada mudança do que um módulo escreve num canal vira uma linha como a de uma saída de nó, com a data e a hora, o nome do módulo, o canal, a direção `Output` e o valor. Uma entrada lida de um canal não gera linha (perguntas 392 e 396).

## Dono e permissões

| Ponto | Como é | Pergunta |
|---|---|---|
| Dono | Quem colocou a placa, gravado no NBT `owner` | 378 e 416 |
| Quem muda | Só o dono muda a tela, gira, alterna e retira o módulo. Os outros players veem a tela só para leitura | 378 |
| Rede | O dono só escolhe entre as redes dele, então nenhum módulo escreve na rede de outro player (regra 17) | 378 |
| No servidor | Toda ação confere o dono, a distância de interação de blocos do jogo e os limites do config (regras 16 e 17) | 378 |

## Configurator e mão vazia

| Com | Clique | Pergunta |
|---|---|---|
| Mão vazia | Não abre nada e mostra `Use a Configurator to open this module.` No Signal Lever, a mão vazia ou um item que não seja o Configurator liga e desliga a alavanca, e no Signal Button, aperta o botão, só pelo dono. No Signal Pressure Plate, a mão não faz nada, sem mensagem | 395, 576, 577, 588 e 606 |
| `Open` | Abre a tela do módulo | 393 |
| `Open`, com Shift | Nada; o `Open` só abre a tela | 618 |
| `Rotate` | Gira a placa 90° no sentido horário, só pelo dono | 403, 613 e 616 |
| `Rotate`, com Shift | Gira a placa 90° no sentido anti-horário, só pelo dono | 403 e 616 |
| `Copy` | O clique copia a configuração do módulo, e o Shift + clique a cola noutro módulo do mesmo tipo, levando a rede e os canais. Só o dono copia e cola. O Signal Lock nunca é copiado nem colado. O detalhe está em [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens). Muda as perguntas 395 e 421 | 722 a 736 |
| `Toggle` | O que o módulo define; no Signal Timer e no Signal Randomizer, alterna `Running` e `Stopped`, no Signal Inverter não faz nada, no Weather Detector troca o modo, no Signal Lever liga e desliga, no Signal Button aperta, no Signal Pressure Plate troca o modo, no Signal Item Sensor não faz nada, e no Signal Lock abre por um pulso, e no Signal Lamp liga e desliga, como um pulso no controle | 395, 443, 462, 480, 555, 603, 639, 658 e 697 |
| `Wrench` | Retira o módulo como item, só pelo dono. No Signal Lock, é o único jeito de retirar no survival | 395 e 641 |

Numa rede `Offline`, como no nó, o `Open` e o `Toggle` não fazem nada, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.`. No Signal Lever e no Signal Button, o `Toggle` e a mão continuam funcionando, no Signal Pressure Plate, o `Toggle` continua trocando o modo, no Signal Lamp, o `Toggle` continua ligando e desligando (pergunta 716), e no Signal Lock, o `Toggle` continua abrindo pelo pulso; o canal só não conta enquanto a rede está `Offline` (perguntas 578, 588, 606 e 707). O `Wrench` funciona, e o `Rotate` também gira a placa, porque o giro não toca na rede (pergunta 619). Um módulo sem rede abre normalmente (perguntas 402 e 431).

### Mensagens

Na action bar, em inglês e em português (pergunta 421):

| Ação | Mensagem |
|---|---|
| `Wrench` | `Module picked up` |
| Qualquer ação de quem não é o dono | `Only the player who placed this module can change it.` |
| `Toggle` | O que o módulo define; no Signal Timer e no Signal Randomizer, `<nome>: Running` ou `<nome>: Stopped`; no Signal Inverter, `Signal Inverter has nothing to toggle.`; no Weather Detector, `<nome>: <modo>`, como `<nome>: Thunder`; no Signal Lever, `<nome>: On` ou `<nome>: Off`; no Signal Button, `<nome>: Pressed`, também com o botão já apertado (perguntas 593 e 600); no Signal Pressure Plate, `<nome>: <modo>`, como `<nome>: Passive` (perguntas 603 e 621); no Signal Item Sensor, `Signal Item Sensor has nothing to toggle.` (pergunta 697); e no Signal Lock, `<nome>: Unlocked`, também com um pulso em andamento (perguntas 639 e 711); no Signal Lamp, `<nome>: On` ou `<nome>: Off` (pergunta 658) |
| Giro, com o `Rotate` | `Output: <borda>`, como `Output: North` |
| Mão vazia | `Use a Configurator to open this module.`, menos no Signal Lever, no Signal Button e no Signal Pressure Plate, que não mostram nada (perguntas 585, 588 e 606) |
| Quebrar o Signal Lock à mão no survival | `Only the owner can remove this with a Configurator.` (pergunta 641) |
| `Copy` | `Copied <tipo>` ao copiar e `Pasted` ao colar; `This copy is from a <tipo>.` num módulo de outro tipo; `Channels were cleared: the network is not available.` quando a rede da cópia não está disponível; `This item cannot be copied.` no Signal Lock (perguntas 728, 731 e 736) |
| Rede `Offline` | `<rede> is offline. Turn it on at a Network Terminal.` |

### Rótulo sobre os módulos

Com o Configurator na mão, cada módulo dentro de `configurator.label_range` mostra um rótulo de duas linhas, como os nós: em cima, o nome do módulo; embaixo, o estado e o canal da saída, se ela estiver num canal. Os estados são os que o módulo define; no Signal Timer, `Running`, `Stopped` ou `Paused`, no [Signal Inverter](signal-inverter.md), `On`, `Off` ou `Burnt out`; no [Weather Detector](weather-detector.md), `On`, `Off` ou `No weather`; no [Signal Randomizer](signal-randomizer.md), `Running` ou `Stopped`; no [Signal Lever](alavanca.md), `On` ou `Off`; no [Signal Button](botao.md), `On` ou `Off`; no [Signal Pressure Plate](placa-de-pressao.md), o estado e o modo, como `On · Players`; no [Signal Item Sensor](sensor-de-itens.md), `On` ou `Off`, sem a contagem; no [Signal Lock](fechadura.md), `Locked` ou `Unlocked`, sem a chave; e no [Signal Lamp](lampada.md), `Offline` ou `Out of range` como o nó, ou `On` ou `Off`, com o canal de intensidade (perguntas 417, 448, 465, 484, 582, 595, 609, 637, 703 e 721).

## Nome

O módulo tem um nome opcional, como o nó, com o limite de `module.name_length`. Sem nome próprio, vale o padrão do nó, os 8 primeiros caracteres do ID, em qualquer idioma, porque o log, o rótulo e a action bar são montados no servidor, que não sabe o idioma do player (perguntas 142 e 427). O nome aparece na tela, no rótulo, no log e na action bar (perguntas 389, 391 e 392).

## Tela comum

Toda tela de módulo tem as mesmas partes comuns, com os componentes da Tela do Nó (perguntas 400, 404 e 414):

| Parte | Como é |
|---|---|
| Título | O nome do tipo, como `Signal Timer`, traduzido, e, abaixo, o nome do módulo, renomeável pelo dono como na Tela do Nó |
| Rede | A escolha de rede da Tela do Nó (`NetworkChoices`), com a última usada no topo |
| Conexões | Cada uma com o seletor `Edge` ou `Channel`. Com `Channel`, a grade dos 16 botões de cor, e o `Channel` fica marcado enquanto ela está aberta; o servidor só muda quando a cor é escolhida (pergunta 456). Num módulo sem rede, o `Channel` abre primeiro a escolha de rede e, depois dela, a grade |
| Rodapé | A rede e, para quem não é o dono, o cadeado |

O centro da tela é de cada módulo.

## Quebrar

Como o nó (perguntas 74, 161 e 394):

| Quem | O que acontece |
|---|---|
| O dono, no survival | A placa quebra e cai como item |
| Outro player, no survival | A placa não quebra |
| Qualquer player, no criativo | A placa quebra |
| O dono, com o `Wrench` | A placa vai para o inventário, ou cai no chão se ele estiver cheio |

O [Signal Lock](fechadura.md) é a exceção: no survival, ninguém o quebra à mão, nem o dono, e só o `Wrench` do dono o retira; uma explosão não leva o espaço dele nem o bloco de apoio (perguntas 641 e 708).

O item é simples, empilha e não guarda a configuração, o nome nem o estado. A saída vai a 0 na hora.

## Save

O NBT comum, em `snake_case` (regras 15 e 24). As chaves ficam fixadas na #26 (pergunta 411):

| Chave | O que guarda |
|---|---|
| `module_id` | O ID do módulo, um UUID |
| `owner` | O dono |
| `name` | O nome próprio, ausente sem nome |
| `network` | A rede, ausente sem rede |
| Uma chave por conexão | `edge` ou a chave da cor do canal, como `red` |

Cada módulo acrescenta as chaves dele. Desde a #44, os módulos são partes do [multipart](multipart.md): o NBT acima fica em `data`, dentro do contêiner `redweave:part_space`, e o visual fica em campos da parte, não mais em estados do bloco (perguntas 534 e 543). O `facing`, a face onde a placa está grudada, não vai para o save: sai da `face` da parte no contêiner (pergunta 559). Os campos comuns são `front`, a direção real da borda da frente no mundo, com 6 valores e sempre perpendicular a `facing` (pergunta 428), e `powered`, em todos os módulos, numa regra só (pergunta 560). Os itens mantêm os IDs (pergunta 523).

## Config

| Chave | Valor | Pergunta |
|---|---|---|
| `module.name_length` | 16 caracteres, de 1 a 64 | 391 |

Os valores ficam em [`limites.md`](../limites.md).

## Testes

- **JUnit:** a parte nova do agregado (o mais forte, o alcance, a rede `Offline` e a pendência só com mudança) e os casos de uso.
- **GameTests:** colocar e girar, a saída por borda e por canal, o dono, o `Toggle`, o `Wrench`, a quebra e o NBT depois de salvar e carregar.
- **Roteiro manual:** telas, arte, textos e chunk descarregado (pergunta 406).

## Fora desta página

- O que cada módulo faz, na página dele.
- Copiar e colar a configuração de um módulo com o `Copy`, em [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens).
- A prévia de posicionamento (#33), o Jade e o The One Probe (#23) e as receitas (#24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 13:59 | O `Copy` copia e cola a configuração dos módulos, menos o Signal Lock (perguntas 722 a 736). |
| Zennd | 2026-11-16 13:18 | Leva o Signal Lamp à base: a lista de módulos, o `Toggle`, a rede `Offline`, as mensagens e o rótulo (perguntas 650, 658, 716 e 721). |
| Zennd | 2026-11-16 12:23 | Leva o Signal Lock à base: a lista de módulos, o `Toggle` numa rede `Offline`, o `Wrench`, a quebra, as mensagens e o rótulo (perguntas 633, 639, 641, 707, 708 e 711). |
| Zennd | 2026-11-16 11:27 | Leva o Signal Item Sensor à lista de módulos, ao `Toggle`, às mensagens e ao rótulo (perguntas 689, 697 e 703). |
| Zennd | 2026-11-16 08:10 | A frase da família dos emissores nomeia os três emissores e a base `EmitterPart` (#139). |
| Zennd | 2026-11-16 08:01 | Aponta para a família dos emissores (perguntas 627 e 628). |
| Zennd | 2026-11-15 15:07 | Leva o Signal Pressure Plate à base: o bloco de apoio, a mão, o `Toggle` numa rede `Offline`, as mensagens e o rótulo (perguntas 603, 606, 609 e 621). |
| Zennd | 2026-11-15 14:37 | O giro passa ao modo `Rotate`, nos dois sentidos, e o Shift + clique no `Open` não faz nada (perguntas 610 e 613 a 620). |
| Zennd | 2026-11-15 14:26 | O giro vai para um modo próprio do Configurator na #136 (pergunta 610). |
| Zennd | 2026-11-15 14:04 | Leva o Signal Button à base: o bloco de apoio, a mão, o `Toggle` numa rede `Offline`, as mensagens e o rótulo (perguntas 553, 588, 593, 595 e 600). |
| Zennd | 2026-11-15 13:15 | Leva o Signal Lever à base: o bloco de apoio, a mão, o `Toggle` numa rede `Offline`, as mensagens e o rótulo (perguntas 553, 576 a 578, 582 e 585). |
| Zennd | 2026-11-15 09:35 | Leva o visual dos módulos para os campos da parte no contêiner, sem estados do bloco (perguntas 534, 543, 559 e 560). |
| Zennd | 2026-11-14 19:34 | Leva os estados do bloco dos módulos para a parte (pergunta 534). |
| Zennd | 2026-11-14 19:14 | Leva os módulos ao multipart da #44 (perguntas 511 e 513). |
| Zennd | 2026-11-14 17:46 | Leva o Signal Randomizer ao `Toggle`, às mensagens e ao rótulo (perguntas 480 e 484). |
| Zennd | 2026-11-14 16:40 | Leva o Weather Detector ao `Toggle`, às mensagens e ao rótulo (perguntas 462 e 465). |
| Zennd | 2026-11-14 15:41 | Marca o `Channel` com a grade aberta (pergunta 456). |
| Zennd | 2026-11-14 14:31 | Leva o Signal Inverter ao `Toggle`, às mensagens e ao rótulo (perguntas 443 e 448). |
| Zennd | 2026-11-14 12:34 | Fixa o som do giro (pergunta 432). |
| Zennd | 2026-11-14 12:31 | Deixa o giro funcionar numa rede `Offline` (pergunta 431). |
| Zennd | 2026-11-14 11:13 | Troca `rotation` por `front` (pergunta 428). |
| Zennd | 2026-11-14 10:44 | Troca o nome padrão do módulo pelo do nó (pergunta 427). |
| Zennd | 2026-11-14 10:24 | Detalha a contagem `modules on` (pergunta 424). |
| Zennd | 2026-11-14 10:01 | Fixa o tamanho da placa (pergunta 422). |
| Zennd | 2026-11-14 09:47 | Adiciona as mensagens (pergunta 421). |
| Zennd | 2026-11-14 09:46 | Cria o documento, com as decisões 375 a 420 da #103. |
