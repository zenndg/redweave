# Rede e canais

O comportamento da rede sem fio na Fase 1: o que é uma rede, como os canais calculam o sinal, como os nós entram e saem, quem pode alterar o quê e quando o simulador avalia. É a base do domínio (issues #9 a #13) e das telas de [`telas.md`](telas.md). Os termos são os do [`glossario.md`](../glossario.md), e os números ficam em [`limites.md`](../limites.md).

## Em uma frase

Uma rede reúne nós espalhados pelo mundo em 16 canais coloridos. Cada canal vale o sinal mais forte entre as suas entradas, e cada saída repete esse valor no bloco onde está grudada.

## Rede

A rede (`Network`) é o agregado: toda mudança nos nós, nos slots e nos canais passa por ela, e ela garante as regras abaixo.

| Parte | O que é |
|---|---|
| ID | `NetworkId`, um UUID único no servidor e que nunca muda. É o que o item do controlador guarda (perguntas 24 e 31) |
| Nome | Escolhido pelo dono, com um limite de caracteres no config. Nasce com os 8 primeiros caracteres do ID, como `3f2a9c1b` (pergunta 142), e o dono o renomeia no Network Terminal e na tela do controlador (pergunta 156) |
| Cor | Uma das 16 cores de lã, a mesma paleta dos canais. Identifica a rede nas telas, no Jade e no The One Probe. Nasce com a primeira cor que nenhuma outra rede do dono usa, ou branca com as 16 em uso (pergunta 40) |
| Dono | O player que criou a rede. Na Fase 1, o dono não muda |
| Controlador | A posição e a dimensão do Network Controller, quando ele está colocado. Cada controlador é uma rede, que nasce quando ele é colocado (pergunta 129) |
| Canais | Sempre 16, um por cor de lã, existindo desde a criação |
| Nós | Os Signal Nodes ligados à rede |
| Fontes sem posição | Os Signal Remotes ligados à rede (pergunta 27) |

### Onde a rede fica salva

A rede fica salva no mundo, no servidor, e não no bloco do controlador (pergunta 24). Assim, ela funciona com o chunk do controlador descarregado, sem carregar nenhum chunk à força (regra 6 do `AGENTS.md`). A issue #16 entrega a gravação.

| Ponto | Como é | Pergunta |
|---|---|---|
| Arquivo | Um só para o servidor inteiro, `data/redweave_networks.dat`, no armazenamento do Overworld, onde o jogo guarda o scoreboard. A rede não pertence a uma dimensão | 88 |
| Quando grava | Toda mudança aceita marca o arquivo como alterado, e o jogo grava no salvamento automático e ao fechar o mundo | — |
| O que vai | A rede inteira, os nós, os slots, os nomes dos canais e a última leitura de cada entrada, além da última rede usada por cada player e do Network Terminal de cada player | 37, 89, 157 e 175 |
| O que não vai | O último valor emitido por cada saída. Ao carregar, a rede começa pendente, e o primeiro tick reaplica todas as saídas | 38 |
| Formato | Contrato de save (regra 15), com versão e chaves estáveis, descrito abaixo e fixado na #26 | 89 |
| Dado inválido | Descartado na menor parte possível, com aviso no log | 92 |
| Versão desconhecida | O arquivo é copiado para `redweave_networks.v<N>.backup.dat`, e o mod começa sem redes, com um erro no log | 92 |
| Arquivo corrompido | Quando nem o jogo consegue ler o arquivo, ele é copiado para `redweave_networks.unreadable.backup.dat`, e o mod começa sem redes, com um erro no log | 97 |
| Config menor | Uma rede salva com nomes mais longos do que o config atual permite volta como estava, com aviso no log. O limite novo vale para as próximas mudanças | 93 e 224 |

#### Formato do save

A raiz tem `version`, que começa em 1, `networks` e `last_used`. Os valores de enum usam chaves `snake_case`, como `input`, `north` e `red`. A tabela mostra o formato da #16 com o que a #87 trouxe, ainda na `version` 1: o `name` do nó e o `last_used`, com `player` e `network`, no lugar do `current` (perguntas 157, 174 e 206). A #88 trouxe o `offline` da rede. O Network Terminal entra numa lista `terminals`, com `player` e o bloco (pergunta 175).

| Parte | Chaves |
|---|---|
| Rede | `id`, `owner`, `name`, `color`, `controller` (opcional), `offline` (opcional), `channels` e `nodes`. O `offline: true` só entra numa rede desligada pelo dono (pergunta 210) |
| Bloco (`controller` e o do nó) | `dimension`, `x`, `y` e `z` |
| Canal | `color` e `name`. Só os canais renomeados entram |
| Nó | `id`, `name` (opcional), `block`, `face` e `slots`. O `name` só entra quando o dono renomeia o nó, como o nome do canal (pergunta 206) |
| Slot | `channel`, `active`, `direction`, `face` e `reading`. O canal é a posição do slot no nó (pergunta 224). Entra todo slot já ativado alguma vez, mesmo desativado, porque ele guarda a direção e a face (pergunta 228), e `reading` fica de fora quando é 0. O slot não tem nome (pergunta 277) |
| Última rede usada (`last_used`) | `player` e `network` |

#### Dado inválido

| O que está inválido | O que acontece |
|---|---|
| Um slot, como com uma direção, face ou canal desconhecido, ou um `channel` repetido no nó | O slot volta a nunca ativado |
| Um nó, como sem ID ou no formato antigo, com `slot_count` ou slot com `index` | O nó sai da rede; um mundo de teste anterior aos 16 slots é recriado (pergunta 235) |
| O nome de um nó, vazio, com espaço nas pontas ou de mais de 64 caracteres | Sai só o nome, e o nó volta ao nome padrão (perguntas 92 e 206) |
| Um nó que aparece em duas redes | Fica só na primeira |
| Uma rede sem ID ou dono válido, ou com um nome vazio ou de mais de 64 caracteres | A rede não é carregada |
| Um `offline` que não é verdadeiro ou falso | Sai só a chave, e a rede fica ligada (perguntas 92 e 210) |
| Uma entrada de `last_used` que aponta para uma rede que não existe | É descartada |

O teto são as faixas do config da pergunta 66: 64 caracteres.

### Estado da rede

| Estado | Quando | Efeito | Pergunta |
|---|---|---|---|
| `Online` | O controlador está colocado no mundo, e a rede está ligada | Os canais funcionam | 135 |
| `Offline` | O dono desligou a rede no [Network Terminal](terminal-de-rede.md) | Todos os canais valem 0. O controlador e os nós dela não aceitam interação do player, e o Configurator não abre, não copia, não cola e não alterna neles. O dono ainda quebra os blocos | 132, 135, 136 e 241 |
| `Unplaced` | O controlador foi quebrado ou sumiu do mundo | Todos os canais valem 0, e os nós mostram `No network`. O Terminal não a religa | 135 e 146 |

Ao quebrar o controlador, o item guarda o `NetworkId` num data component (regra 7). Recolocado, a rede volta ao estado de antes, igual ao que era, e o alcance passa a ser medido da nova posição. Uma rede cujo item se perdeu fica salva `Unplaced`, e o dono a apaga pelo Terminal, com `Delete` (pergunta 166).

### Limite de redes

Um player tem até `network.max_per_player` redes, 10 no começo (pergunta 145 e [`limites.md`](../limites.md)). O limite é conferido ao colocar um Network Controller que cria uma rede: no limite, o bloco não é colocado, e a action bar mostra `You have reached the limit of <n> networks. Delete an unplaced one at a Network Terminal.` (pergunta 211). Todas as redes do player contam, inclusive as `Offline` e as `Unplaced`.

## Canais

| Regra | Detalhe |
|---|---|
| Quantidade | 16 por rede, fixo, porque cada canal é uma cor de lã |
| Cor | Fixa, a do canal |
| Nome | O padrão é o nome da cor, como `Red`. O dono pode renomear, com o limite de caracteres do config (pergunta 26) |
| Valor | Um `Signal`, de 0 a 15 (regra 5) |

### Valor do canal

O valor do canal é o **sinal mais forte entre as entradas que contam**. Um canal sem nenhuma entrada que conte vale 0.

| Entrada | Conta? | Com qual valor |
|---|---|---|
| Slot de entrada de um nó `Online` | Sim | O sinal de redstone que o nó lê no bloco, na face configurada |
| Slot de entrada de um nó `Unloaded` | Sim | O último valor lido antes de o chunk descarregar, salvo com a rede, então vale também depois de reiniciar o servidor (perguntas 13, 29 e 37) |
| Slot de entrada de um nó `Out of range` | Não | Vale 0 (perguntas 23 e 29) |
| Signal Remote ligado | Sim | 15, nos modos alavanca e botão (perguntas 288 e 352) |
| Signal Remote fora do inventário ou do alcance | Não | Vale 0, para o canal não ficar preso ligado (pergunta 27) |

O mais forte vence porque é a regra da própria redstone: um fio ligado a duas fontes fica com a mais forte.

### Saídas

Um slot de saída emite o valor do canal no bloco onde o nó está grudado, alimentando-o com força como uma alavanca, então todas as saídas de um nó emitem a mais forte (pergunta 71). A saída repete o canal como ele está: o slot não tem lógica, que fica toda nos Signal Modules (pergunta 308). Um nó `Out of range` ou numa rede `Offline` ou `Unplaced` emite 0.

## Nós

O nó (`Node`) é a peça grudada numa face de um bloco. Ele pertence a no máximo uma rede.

| Parte | O que é |
|---|---|
| ID | `NodeId`, um UUID (pergunta 31) |
| Nome | Nasce com os 8 primeiros caracteres do ID (pergunta 142). O dono o renomeia na Tela do Nó e na lista da tela do controlador, com o limite de `node.name_length` (pergunta 144) |
| Quem colocou | O player que colocou o nó, gravado na parte, dentro do contêiner `part_space` desde a #44 (pergunta 543). Num nó sem rede, é o dono para a escolha da rede e para a Chave do Configurator (perguntas 165 e 172) |
| Posição | A dimensão, o bloco e a face onde o nó está grudado |
| Slots | 16, um por cor de canal, fixos, cada um um `ChannelSlot`. Todos nascem inativos, e o nó pode ter de 0 a 16 ativos (perguntas 224 e 226) |

### Slot de canal

| Parte | Valores |
|---|---|
| Canal | A cor do slot: o slot 15 é sempre o canal `15 Red`. Um nó não lê e emite na mesma cor ao mesmo tempo (pergunta 224) |
| Status | `Enabled` ou `Disabled`. Só um slot ativo lê ou emite. Na primeira ativação, o slot vira `Input` na face onde o nó está grudado; desativar guarda a direção e a face (pergunta 228). A direção e a face só mudam com o slot `Disabled`, e um slot `Enabled` fica travado (pergunta 237) |
| Direção | `Input`, quando o nó lê o bloco, ou `Output`, quando emite no bloco |
| Face do bloco | Uma das 6 faces do bloco onde o nó está. Na entrada, escolhe de qual vizinho o nó lê; na saída, o nó sempre alimenta o bloco onde está grudado, como uma alavanca (pergunta 71) |

### Estados do nó

| Estado | Quando | Na tela | No canal |
|---|---|---|---|
| `Online` | Na rede, com o chunk carregado e dentro do raio | Verde | Conta normalmente |
| `Unloaded` | O chunk do nó descarregou | Âmbar | A entrada mantém o último valor |
| `Out of range` | Fora do raio do controlador ou em outra dimensão | Vermelho | Não conta, e a saída emite 0 |
| `No network` | Sem rede, ou com a rede `Unplaced` | Cinza | Não conta |
| `Offline` | Numa rede desligada pelo dono | Cinza | Não conta, e a saída emite 0 |

Um nó de uma rede `Offline` mostra o estado `Offline` em cinza nas telas, no Jade e no The One Probe, não conta e emite 0 (pergunta 177).

O nó `Unloaded` não aparece no mundo, porque o chunk dele não está carregado. Por isso o âmbar só aparece nas telas.

### Alcance

O alcance é uma esfera com um raio em blocos a partir do Network Controller, na mesma dimensão (perguntas 23 e 36). Conta a distância em linha reta entre o bloco do controlador e o bloco do nó. O raio fica no config, com valor inicial de 128, e 0 significa ilimitado. Outra dimensão nunca alcança, mesmo com o raio ilimitado.

### Nó recém-colocado

Não há rede atual: o player escolhe a rede de cada nó (pergunta 139).

1. O Signal Node nasce `No network` e não pode ser configurado até ter rede.
2. Aberto pela primeira vez, com o [Configurator](configurador.md) no modo `Open`, a Tela do Nó mostra a lista das redes `Online` de quem colocou o nó, com a última usada destacada (perguntas 157 e 172). Para os outros players, a tela abre só para leitura, com `No network`.
3. Escolhida a rede, o nó entra com os 16 slots inativos, e o dono ativa os canais que quiser (pergunta 226).
4. A tela segue para a configuração, que é onde ela abre nas próximas vezes.

Para trocar de rede, o dono clica no nome da rede na Tela do Nó (pergunta 141).

## Signal Remote

O Signal Remote é uma fonte de entrada sem posição no mundo, com ID próprio: um tablet vinculado a um slot de input de um nó, que injeta sinal no canal desse slot e acompanha a troca de canal (perguntas 27, 279, 282 e 287). Sem o vínculo, mostra `Link lost` e não dispara. Ele entra no cálculo do sinal mais forte como qualquer entrada. Quando sai do inventário do player ou o player fica fora do alcance da rede, a contribuição dele volta a 0. O alcance do Remote é medido da posição do player. Na rede, ele é a parte `RemoteInputs` do agregado, entregue a cada tick com os tablets que contam, fora do save (perguntas 356 e 360). O item, o vínculo, o disparo e quando ele conta estão em [`signal-remote.md`](signal-remote.md).

## Signal Modules

Um Signal Module escreve direto num canal da rede dele, sem nó: na rede, ele é uma parte nova do agregado, entregue a cada tick com os módulos carregados que escrevem num canal, fora do save, como o `RemoteInputs`. Entra na regra do mais forte, conta no alcance do controlador medido da placa e vale 0 fora do alcance, numa rede que não está `Online` ou num chunk descarregado. A rede só fica pendente se um canal mudar (perguntas 375, 385, 386 e 387). A base está em [`modulos-de-sinal.md`](modulos-de-sinal.md).

## Permissões

Só o dono altera a rede, sem exceção para operadores do servidor (perguntas 22 e 28).

| Ação | Dono | Outros players |
|---|---|---|
| Ver a Tela do Controlador e a Tela do Nó | Sim | Sim, só leitura, sem a posição dos nós (pergunta 149) |
| Escolher a rede de um nó sem rede | Quem colocou o nó, entre as redes `Online` dele (pergunta 172) | Não |
| Trocar um nó de rede | Sim, sendo dono da rede antiga e da nova, pela Tela do Nó (perguntas 42 e 141) | Não |
| Configurar slots e renomear nós | Sim | Não |
| Copiar, colar e retirar nós com o Configurator | Sim (perguntas 106 e 162) | Não |
| Renomear a rede e os canais, e trocar a cor da rede | Sim | Não |
| Desligar, religar, limpar os nós e apagar a rede, no Network Terminal | Sim (perguntas 131 a 134 e 166) | Não |
| Ligar um Signal Remote à rede | Sim | Não |
| Disparar um Signal Remote já vinculado | Sim | Sim, quem o segura (pergunta 357) |
| Quebrar o Network Controller | Sim | Não (pergunta 30) |

Numa rede `Offline`, nem o dono usa as telas da rede ou o Configurator, menos a Chave: só o Network Terminal age sobre ela, e o dono ainda quebra os blocos (perguntas 132, 136 e 163).

- **O servidor confere tudo:** todo pacote do cliente é validado no servidor, com o dono e a distância (regras 16 e 17).
- **Controlador protegido:** para quem não é o dono, o bloco não quebra no survival, e explosões não o destroem. No criativo, quebra normalmente (pergunta 30).
- **Sem moderação:** nem um operador altera a rede de outro player. Uma rede abandonada continua como está.

## Avaliação

O simulador (`Simulator`) avalia cada rede no máximo uma vez por tick, e só quando a entrada de algum canal mudou (regra 18).

1. Ao longo do tick, uma leitura que muda o valor de uma entrada deixa a rede pendente. Ligar ou desligar um nó, configurar ou liberar um slot, colocar ou retirar o controlador e desligar ou religar a rede no Network Terminal também deixam. Renomear e trocar a cor, não, porque não mudam sinal nenhum.
2. No fim do tick, o simulador avalia só as redes pendentes: calcula os 16 canais numa passada e depois as saídas, e a rede deixa de estar pendente (pergunta 38).
3. Só as faces cujo valor mudou atualizam o bloco. Uma saída só avisa os blocos vizinhos quando o valor emitido muda, para não haver block updates em cascata. A saída de um nó desligado, ou de um slot liberado, volta a 0.
4. Duas saídas do mesmo nó na mesma face emitem a mais forte (pergunta 39). No mundo, o nó emite a mais forte entre todas as faces dele (pergunta 71).
5. Ao carregar o mundo, a rede começa pendente, e o primeiro tick reaplica todas as saídas.

Uma saída que alimenta uma entrada da mesma rede pelo mundo leva ao menos um tick para voltar ao canal, então nunca trava o tick. A detecção de loops e oscilação nos circuitos chega nas Fases 2 e 3.

## Abas da Tela do Controlador

Completam a [Tela do Controlador](telas.md#tela-do-controlador).

| Aba | O que mostra |
|---|---|
| `Signals` | Os 16 canais, cada um com a cor, o nome, o valor atual e quantas entradas e saídas usa. O dono renomeia um canal com um duplo clique no nome (pergunta 222) |
| `Access` | O dono da rede e o aviso `Only the owner can change this network.` |

## Config

As chaves entram com o código que as lê (issue do config) e, depois de publicadas, só mudam com migração (regra 15). Os valores iniciais estão em [`limites.md`](../limites.md).

| Número | Valor inicial |
|---|---|
| Raio do alcance | 128 blocos, 0 é ilimitado |
| Tamanho do nome da rede | 32 caracteres |
| Tamanho do nome do canal | 16 caracteres |
| Tamanho do nome do nó | 16 caracteres (pergunta 144) |
| Redes por player | 10 (pergunta 145) |
| Linhas do log por rede | 100 (pergunta 147) |

## Regras que o domínio garante

São as invariantes que os testes do domínio cobrem (issues #9 a #13).

1. Um `Signal` está sempre entre 0 e 15.
2. Uma rede tem sempre 16 canais, um por cor, e cada cor aparece uma vez.
3. Um nó pertence a no máximo uma rede, e tem sempre os 16 slots, um por cor (pergunta 224).
4. Um slot sempre aponta para um canal da própria rede.
5. O valor de um canal é o mais forte entre as entradas que contam, e 0 sem entradas.
6. Uma rede `Offline` ou `Unplaced` tem todos os canais em 0.
7. Toda alteração confere se quem pede é o dono.
8. O nome da rede e o do canal respeitam o limite do config ao serem dados e nunca ficam vazios. Uma rede restaurada do save mantém os nomes que tinha (pergunta 93).
9. Uma rede só é avaliada quando um canal mudou, uma vez por tick.
10. Uma rede restaurada do save volta igual ao que foi salvo, menos o último valor emitido, e começa pendente (perguntas 38 e 90).

## Fora desta página

- O que cada Signal Module faz, na página dele, a partir de [`modulos-de-sinal.md`](modulos-de-sinal.md).
- Circuitos e chips (Fase 3).
- Transferir a rede para outro dono.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 09:37 | Quem colocou o nó fica gravado na parte do multipart (pergunta 543). |
| Zennd | 2026-11-14 09:46 | Adiciona os Signal Modules como fonte do canal, da #103 (perguntas 375 a 387). |
| Zennd | 2026-11-12 20:48 | Liga o Signal Remote à página dele, com o valor 15 e o `RemoteInputs` (perguntas 352, 357 e 360). |
| Zennd | 2026-11-12 17:27 | Marca o valor do disparo do Signal Remote como decisão da #22 (pergunta 318). |
| Zennd | 2026-11-12 17:18 | Corrige o save sem `name` do slot e com o Terminal, o `Toggle` numa rede `Offline`, o Signal Remote e a seção final (perguntas 175, 224, 241, 277, 279, 287 e 303). |
| Zennd | 2026-11-12 16:50 | Tira a lógica do slot, que fica nos Signal Modules (pergunta 308). |
| Zennd | 2026-11-12 12:00 | Tira o nome do slot (pergunta 277). |
| Zennd | 2026-11-11 17:05 | A direção e a face só mudam com o slot desativado (pergunta 237). |
| Zennd | 2026-11-11 16:40 | Aplica os 16 slots por cor, o status e o nome do slot (perguntas 224 a 228 e 235). |
| Zennd | 2026-11-11 15:47 | Aplica a pergunta 222: o canal é renomeado com duplo clique. |
| Zennd | 2026-11-11 14:09 | Aplica a #88: a chave `offline`, o estado `Offline` do nó e o aviso do limite de redes (perguntas 209 a 211). |
| Zennd | 2026-11-11 13:46 | Aplica a #87: o `name` do nó e o `last_used` no save, e o nome de nó inválido (perguntas 157, 174 e 206). |
| Zennd | 2026-11-11 10:24 | Aplica as perguntas 174, 175 e 177. |
| Zennd | 2026-11-11 09:51 | Aplica o Network Terminal, os estados `Offline` e `Unplaced`, o limite de redes, o nome do nó e o nó que escolhe a rede (perguntas 129 a 172). |
| Marcelo Fabiano | 2026-11-10 15:52 | Aponta para a página do configurador. |
| Marcelo Fabiano | 2026-11-10 13:10 | Descreve a gravação da rede com as perguntas 88 a 93, 95 e 97. |
| Marcelo Fabiano | 2026-11-10 09:45 | Aplica a pergunta 71 à face do slot e às saídas. |
| Marcelo Fabiano | 2026-11-09 17:21 | Aplica as perguntas 40 e 42. |
| Marcelo Fabiano | 2026-11-09 16:57 | Detalha a avaliação com as perguntas 38 e 39. |
| Marcelo Fabiano | 2026-11-09 16:39 | Aplica as perguntas 36 e 37. |
| Marcelo Fabiano | 2026-11-09 16:12 | Aplica a pergunta 31 nos IDs. |
| Marcelo Fabiano | 2026-11-09 15:39 | Cria o documento, com as respostas das perguntas 13 e 22 a 30. |
