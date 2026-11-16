# Controlador de rede

O Network Controller (`redweave:network_controller`) é o bloco onde a rede nasce. Esta página descreve o bloco na Fase 1: colocar, clicar, quebrar e o item que guarda a rede. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), a tela em [`telas.md`](telas.md#tela-do-controlador) e o visual em [`visual.md`](../conceito/visual.md#network-controller). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Cada controlador é uma rede (pergunta 129). Colocar um controlador cria uma rede, ou traz de volta a rede que o item guarda. Quebrá-lo deixa a rede `Unplaced` e devolve o item com o ID dela. As redes do player são administradas juntas no [Network Terminal](terminal-de-rede.md).

## Colocar

Só um player real coloca o controlador. Sem um player real, como pelo FakePlayer de outro mod, o bloco não é colocado (pergunta 58).

| O item guarda | O que acontece | Pergunta |
|---|---|---|
| Nada | Nasce uma rede nova, com o player como dono, o nome padrão, os 8 primeiros caracteres do ID, e a primeira cor livre | 40, 129 e 142 |
| Nada, com o player no limite de redes | O bloco não é colocado, o item fica na mão, e a action bar mostra `You have reached the limit of <n> networks. Delete an unplaced one at a Network Terminal.` | 145 e 211 |
| Uma rede do player, `Unplaced` | A rede volta igual ao que era, e o alcance passa a ser medido da nova posição. Volta no estado que tinha: `Online`, ou `Offline` se o dono a tinha desligado, com o núcleo apagado | 24, 135, 177 e 178 |
| Uma rede do player que já tem um controlador colocado | O bloco não é colocado, o item fica na mão, e a action bar mostra `This network already has a controller.` | 56 |
| Uma rede de outro player | O bloco não é colocado, o item fica na mão, e a action bar mostra `Only the owner can place this network.` | 55 |
| O ID de uma rede que não existe | Nasce uma rede nova, como com o item sem ID, com o mesmo limite de redes | 57 e 145 |

O bloco guarda na BlockEntity o `NetworkId` da rede e o UUID do dono, nas chaves de NBT `network` e `owner`. O dono vai também ao cliente, para a quebra não aparecer a quem não é dono. As chaves são contrato de save e ficam fixadas na issue #26 (pergunta 67).

## Clicar

Um clique com a mão vazia, ou com o Configurator no modo `Open`, abre a [Tela do Controlador](telas.md#tela-do-controlador) para qualquer player, só para leitura para quem não é o dono (perguntas 22 e 113). O controlador continua abrindo com a mão vazia, ao contrário do Signal Node (pergunta 154). A tela substituiu a mensagem `<nome> · owner: <dono>` da action bar (perguntas 52 e 201). Os modos `Copy`, `Toggle` e `Wrench` não agem no controlador (perguntas 164, 232 e 241).

| Situação | O que acontece | Pergunta |
|---|---|---|
| Rede `Online` | Abre a Tela do Controlador | 113 |
| Rede `Offline` | Nada abre, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` | 158 e 160 |
| Controlador sem rede | A action bar mostra `No network` | 53 e 92 |

Um controlador fica sem rede quando a rede dele não existe mais no save, como quando ela foi descartada por estar inválida (pergunta 92), ou num mundo criado antes da persistência da issue #16 (pergunta 53).

## Quebrar

| Quem quebra | O que acontece | Pergunta |
|---|---|---|
| O dono, no survival | O bloco quebra e solta o item com o `NetworkId` num data component. A rede fica `Unplaced`. Vale também com a rede `Offline` | 24, 30, 136 e 146 |
| Outro player, no survival | O bloco não quebra | 30 |
| Explosão | O bloco não é destruído | 30 |
| Qualquer player, no criativo | O bloco quebra, sem soltar item, como no jogo puro. A rede fica `Unplaced` | 30, 49 e 146 |
| Comando ou outro mod | A rede fica `Unplaced` | 49 e 146 |

Quando o controlador some sem o dono quebrá-lo, a rede fica `Unplaced` por uma operação do domínio sem quem pede, porque o bloco já não está no mundo. Sem o item, a rede continua salva `Unplaced`, como uma rede cujo item se perdeu, e o dono a apaga pelo Network Terminal (perguntas 49 e 166). A retirada pelo dono continua conferindo o dono.

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:network_controller`, o mesmo do bloco |
| Nome | `Network Controller`, e `Controlador de Rede` em português (pergunta 68) |
| Aba do criativo | `Redweave`, a aba do mod, com o ícone do controlador (pergunta 69) |
| Data component | `redweave:network`, com o `NetworkId` da rede, só quando o item veio de um controlador quebrado (regra 7 do `AGENTS.md` e pergunta 65). O ID do componente é contrato de save e fica fixado na issue #26 (regra 15) |

## Onde ficam as redes

No save do mundo, num arquivo único para o servidor, com as redes e a última rede usada por cada player (pergunta 157), como descrito em [`rede-e-canais.md`](rede-e-canais.md#onde-a-rede-fica-salva) (issue #16, perguntas 88 a 93). O objeto por servidor da pergunta 63 continua sendo a raiz de composição, e só as redes em memória deram lugar ao save.

## Config

As chaves que a rede e a Tela do Controlador leem ficam no config do servidor, `redweave-server.toml`, que vale por mundo e é enviado aos clientes (pergunta 50). Os valores iniciais estão em [`limites.md`](../limites.md).

| Chave | Valor inicial | O que é |
|---|---|---|
| `network.range` | 128 | Raio do alcance, em blocos. 0 é ilimitado |
| `network.name_length` | 32 | Tamanho máximo do nome da rede |
| `network.max_per_player` | 10 | Redes por player, de qualquer estado (pergunta 145) |
| `channel.name_length` | 16 | Tamanho máximo do nome do canal |
| `node.name_length` | 16 | Tamanho máximo do nome do nó (pergunta 144) |
| `log.size` | 100 | Linhas do log de cada rede, só em memória (pergunta 147) |
| `configurator.label_range` | 16 | Raio em blocos do rótulo sobre os nós, de 4 a 64 (pergunta 268) |

As faixas são as da pergunta 66, com as que vieram depois: o alcance vai de 0 a 30.000.000, os nomes de 1 a 64 caracteres, as redes por player de 1 a 100 e o log de 10 a 1000 linhas. A chave `node.slots` saiu com os 16 slots fixos do Signal Node (pergunta 224). Um valor fora da faixa volta ao padrão, com um aviso no log.

## Visual

As texturas são as da prancha [`texturas.png`](../imagens/conceito/texturas.png), extraídas para `assets/redweave/textures/block/` (pergunta 51). Nesta issue, o bloco usa as texturas de corpo e mostra o estado do núcleo. As camadas emissivas e os LEDs por canal chegam com a issue #24.

| Face | Textura |
|---|---|
| Topo | `network_controller_top`, sem rede; `network_controller_top_on`, com a rede `Online` |
| Frente | `network_controller_front`, voltada para o player que colocou o bloco |

O blockstate tem `facing`, nas 4 direções horizontais, e `linked`, verdadeiro quando a rede do bloco está `Online`, que escolhe o topo (perguntas 64 e 177). Os dois são contrato de save e ficam fixados na issue #26.
| Laterais e fundo | `network_controller_side` e `network_controller_bottom` |

| Estado | Visual | Pergunta |
|---|---|---|
| `Online` | `top_on` com a camada emissiva `top_glow`, e um LED aceso por canal ligado (issue #24) | 51 |
| Sem nós | `top_on` sem a camada emissiva, então o núcleo fica fraco no escuro | 60 |
| Sem rede ou rede `Offline` | `top`, com o núcleo apagado | 51, 177 |

## Fora desta funcionalidade

- O Network Terminal, que administra as redes do player ([`terminal-de-rede.md`](terminal-de-rede.md)).
- A textura final e os LEDs ao vivo (issue #24).
- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 747).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 747). |
| Zennd | 2026-11-12 17:18 | Inclui o `Toggle`, o topo aceso só com a rede `Online` e as chaves `node.name_length` e `configurator.label_range` (perguntas 144, 177, 241 e 268). |
| Zennd | 2026-11-12 09:39 | Atualiza as chaves do config: sai `node.slots` e entram `network.max_per_player` e `log.size` (perguntas 145, 147 e 224). |
| Zennd | 2026-11-11 16:12 | O Configurator no modo `Open` abre a Tela do Controlador (pergunta 232). |
| Zennd | 2026-11-11 15:40 | O clique com a mão vazia abre a Tela do Controlador, da #20. |
| Zennd | 2026-11-11 14:09 | Registra o aviso do limite de redes e o núcleo apagado da rede `Offline`, da #88. |
| Zennd | 2026-11-11 13:46 | Aplica a pergunta 201, da #87: o clique mostra a mesma mensagem para todos. |
| Zennd | 2026-11-11 10:24 | Aplica a pergunta 178. |
| Zennd | 2026-11-11 09:51 | Aplica o estado `Unplaced`, o limite de redes, o nome padrão e o fim da rede atual (perguntas 129 a 160). |
| Marcelo Fabiano | 2026-11-10 13:10 | Aplica a persistência da #16. |
| Marcelo Fabiano | 2026-11-10 09:31 | Aplica as perguntas 67 a 69. |
| Marcelo Fabiano | 2026-11-10 09:20 | Aplica as perguntas 64 a 66 e tira o tooltip, que não foi decidido. |
| Marcelo Fabiano | 2026-11-10 08:55 | Cria o documento, com as respostas das perguntas 49 a 58, 51 e 60. |
