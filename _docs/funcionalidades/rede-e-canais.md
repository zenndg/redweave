# Rede e canais

O comportamento da rede sem fio na Fase 1: o que é uma rede, como os canais calculam o sinal, como os nós entram e saem, quem pode alterar o quê e quando o simulador avalia. É a base do domínio (issues #9 a #13) e das telas de [`telas.md`](telas.md). Os termos são os do [`glossario.md`](../glossario.md), e os números ficam em [`limites.md`](../limites.md).

## Em uma frase

Uma rede reúne nós espalhados pelo mundo em 16 canais coloridos. Cada canal vale o sinal mais forte entre as suas entradas, e cada saída repete esse valor no bloco onde está grudada.

## Rede

A rede (`Network`) é o agregado: toda mudança nos nós, nos slots e nos canais passa por ela, e ela garante as regras abaixo.

| Parte | O que é |
|---|---|
| ID | `NetworkId`, um UUID único no servidor e que nunca muda. É o que o item do controlador guarda (perguntas 24 e 31) |
| Nome | Escolhido pelo dono, com um limite de caracteres no config. Nasce com um nome padrão e pode ser renomeado |
| Cor | Uma das 16 cores de lã, a mesma paleta dos canais. Identifica a rede nas telas, no Jade e no The One Probe. Nasce com a primeira cor que nenhuma outra rede do dono usa, ou branca com as 16 em uso (pergunta 40) |
| Dono | O player que criou a rede. Na Fase 1, o dono não muda |
| Controlador | A posição e a dimensão do Network Controller, quando ele está colocado |
| Canais | Sempre 16, um por cor de lã, existindo desde a criação |
| Nós | Os Signal Nodes ligados à rede |
| Fontes sem posição | Os Signal Remotes ligados à rede (pergunta 27) |

### Onde a rede fica salva

A rede fica salva no mundo, no servidor, e não no bloco do controlador (pergunta 24). Assim, ela funciona com o chunk do controlador descarregado, sem carregar nenhum chunk à força (regra 6 do `AGENTS.md`).

### Estado da rede

| Estado | Quando | Efeito |
|---|---|---|
| `Online` | O controlador está colocado no mundo | Os canais funcionam |
| `Offline` | O controlador foi quebrado e está como item | Todos os canais valem 0, e os nós mostram `No network` |

Ao quebrar o controlador, o item guarda o `NetworkId` num data component (regra 7). Recolocado, a rede volta `Online`, igual ao que era, e o alcance passa a ser medido da nova posição. Uma rede cujo item se perdeu fica salva `Offline`. A limpeza dessas redes fica para uma fase seguinte.

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
| Signal Remote ligado | Sim | O valor que o player escolheu no item |
| Signal Remote fora do inventário ou do alcance | Não | Vale 0, para o canal não ficar preso ligado (pergunta 27) |

O mais forte vence porque é a regra da própria redstone: um fio ligado a duas fontes fica com a mais forte.

### Saídas

Um slot de saída emite o valor do canal no bloco, na face configurada. Na Fase 1, a lógica do slot é sempre `None`, então a saída repete o canal como ele está. As outras lógicas chegam na Fase 2. Um nó `Out of range` ou numa rede `Offline` emite 0.

## Nós

O nó (`Node`) é a peça grudada numa face de um bloco. Ele pertence a no máximo uma rede.

| Parte | O que é |
|---|---|
| ID | `NodeId`, um UUID (pergunta 31) |
| Posição | A dimensão, o bloco e a face onde o nó está grudado |
| Slots | Até o limite do config: 4, ou 9 com upgrade. Cada slot é um `ChannelSlot` |

### Slot de canal

| Parte | Valores |
|---|---|
| Direção | `Input`, quando o nó lê o bloco, ou `Output`, quando emite no bloco |
| Face do bloco | Uma das 6 faces do bloco onde o nó está |
| Canal | Um dos 16 canais da rede |
| Lógica | Na Fase 1, só `None` |

### Estados do nó

| Estado | Quando | Na tela | No canal |
|---|---|---|---|
| `Online` | Na rede, com o chunk carregado e dentro do raio | Verde | Conta normalmente |
| `Unloaded` | O chunk do nó descarregou | Âmbar | A entrada mantém o último valor |
| `Out of range` | Fora do raio do controlador ou em outra dimensão | Vermelho | Não conta, e a saída emite 0 |
| `No network` | Sem rede, ou com a rede `Offline` | Cinza | Não conta |

O nó `Unloaded` não aparece no mundo, porque o chunk dele não está carregado. Por isso o âmbar só aparece nas telas.

### Alcance

O alcance é uma esfera com um raio em blocos a partir do Network Controller, na mesma dimensão (perguntas 23 e 36). Conta a distância em linha reta entre o bloco do controlador e o bloco do nó. O raio fica no config, com valor inicial de 128, e 0 significa ilimitado. Outra dimensão nunca alcança, mesmo com o raio ilimitado.

### Nó recém-colocado

O player tem uma **rede atual**: a última que ele criou ou abriu no Network Controller como dono, guardada no servidor (pergunta 25). Um Signal Node colocado por ele:

1. entra na rede atual;
2. ganha 1 slot `Input` na face em que foi grudado;
3. usa o **primeiro canal livre**, ou seja, o primeiro canal, na ordem das cores, sem nenhum slot na rede.

Sem rede atual, o nó fica `No network`. Com os 16 canais em uso, o nó entra na rede sem slot, e a Tela do Nó mostra `Free slot`. O Configurador troca a rede depois.

## Signal Remote

O Signal Remote é uma fonte de entrada sem posição no mundo, com ID próprio, ligada a um canal da rede (pergunta 27). Ele entra no cálculo do sinal mais forte como qualquer entrada. Quando sai do inventário do player ou o player fica fora do alcance da rede, a contribuição dele volta a 0. O alcance do Remote é medido da posição do player. O item, os modos de alavanca e de botão e a tela ficam na issue do Remote.

## Permissões

Só o dono altera a rede, sem exceção para operadores do servidor (perguntas 22 e 28).

| Ação | Dono | Outros players |
|---|---|---|
| Ver a Tela do Controlador e a Tela do Nó | Sim | Sim, só leitura |
| Ligar e desligar nós da rede | Sim | Não |
| Trocar um nó de rede | Sim, sendo dono da rede antiga e da nova (pergunta 42) | Não |
| Configurar slots | Sim | Não |
| Renomear a rede e os canais, e trocar a cor da rede | Sim | Não |
| Ligar um Signal Remote à rede | Sim | Não |
| Quebrar o Network Controller | Sim | Não (pergunta 30) |

- **O servidor confere tudo:** todo pacote do cliente é validado no servidor, com o dono e a distância (regras 16 e 17).
- **Controlador protegido:** para quem não é o dono, o bloco não quebra no survival, e explosões não o destroem. No criativo, quebra normalmente (pergunta 30).
- **Sem moderação:** nem um operador altera a rede de outro player. Uma rede abandonada continua como está.

## Avaliação

O simulador (`Simulator`) avalia cada rede no máximo uma vez por tick, e só quando a entrada de algum canal mudou (regra 18).

1. Ao longo do tick, uma leitura que muda o valor de uma entrada deixa a rede pendente. Ligar ou desligar um nó, configurar ou liberar um slot e colocar ou retirar o controlador também deixam. Renomear e trocar a cor, não, porque não mudam sinal nenhum.
2. No fim do tick, o simulador avalia só as redes pendentes: calcula os 16 canais numa passada e depois as saídas, e a rede deixa de estar pendente (pergunta 38).
3. Só as faces cujo valor mudou atualizam o bloco. Uma saída só avisa os blocos vizinhos quando o valor emitido muda, para não haver block updates em cascata. A saída de um nó desligado, ou de um slot liberado, volta a 0.
4. Duas saídas do mesmo nó na mesma face emitem a mais forte (pergunta 39).
5. Ao carregar o mundo, a rede começa pendente, e o primeiro tick reaplica todas as saídas.

Uma saída que alimenta uma entrada da mesma rede pelo mundo leva ao menos um tick para voltar ao canal, então nunca trava o tick. A detecção de loops e oscilação nos circuitos chega nas Fases 2 e 3.

## Abas da Tela do Controlador

Completam a [Tela do Controlador](telas.md#tela-do-controlador).

| Aba | O que mostra |
|---|---|
| `Signals` | Os 16 canais, cada um com a cor, o nome, o valor atual e quantas entradas e saídas usa. O dono renomeia um canal com um clique no nome |
| `Access` | O dono da rede e o aviso `Only the owner can change this network.` |

## Config

As chaves entram com o código que as lê (issue do config) e, depois de publicadas, só mudam com migração (regra 15). Os valores iniciais estão em [`limites.md`](../limites.md).

| Número | Valor inicial |
|---|---|
| Raio do alcance | 128 blocos, 0 é ilimitado |
| Slots por nó | 4 |
| Slots por nó com upgrade | 9 |
| Tamanho do nome da rede | 32 caracteres |
| Tamanho do nome do canal | 16 caracteres |

## Regras que o domínio garante

São as invariantes que os testes do domínio cobrem (issues #9 a #13).

1. Um `Signal` está sempre entre 0 e 15.
2. Uma rede tem sempre 16 canais, um por cor, e cada cor aparece uma vez.
3. Um nó pertence a no máximo uma rede, e não passa do limite de slots.
4. Um slot sempre aponta para um canal da própria rede.
5. O valor de um canal é o mais forte entre as entradas que contam, e 0 sem entradas.
6. Uma rede `Offline` tem todos os canais em 0.
7. Toda alteração confere se quem pede é o dono.
8. O nome da rede e o do canal respeitam o limite do config e não ficam vazios.
9. Uma rede só é avaliada quando um canal mudou, uma vez por tick.

## Fora da Fase 1

- Lógicas de slot além de `None`, portas, temporizador e contador (Fase 2).
- Circuitos e chips (Fase 3).
- Transferir a rede para outro dono e limpar redes perdidas.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 17:21 | Aplica as perguntas 40 e 42. |
| Marcelo Fabiano | 2026-11-09 16:57 | Detalha a avaliação com as perguntas 38 e 39. |
| Marcelo Fabiano | 2026-11-09 16:39 | Aplica as perguntas 36 e 37. |
| Marcelo Fabiano | 2026-11-09 16:12 | Aplica a pergunta 31 nos IDs. |
| Marcelo Fabiano | 2026-11-09 15:39 | Cria o documento, com as respostas das perguntas 13 e 22 a 30. |
