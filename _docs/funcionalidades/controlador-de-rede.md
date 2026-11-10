# Controlador de rede

O Network Controller (`redweave:network_controller`) é o bloco onde a rede nasce. Esta página descreve o bloco na Fase 1: colocar, clicar, quebrar e o item que guarda a rede. O comportamento da rede está em [`rede-e-canais.md`](rede-e-canais.md), a tela em [`telas.md`](telas.md#tela-do-controlador) e o visual em [`visual.md`](../conceito/visual.md#network-controller). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Colocar um controlador cria uma rede, ou traz de volta a rede que o item guarda. Quebrá-lo deixa a rede `Offline` e devolve o item com o ID dela.

## Colocar

Só um player real coloca o controlador. Sem um player real, como pelo FakePlayer de outro mod, o bloco não é colocado (pergunta 58).

| O item guarda | O que acontece | Pergunta |
|---|---|---|
| Nada | Nasce uma rede nova, com o player como dono, o nome padrão e a primeira cor livre. Ela vira a rede atual do player | 25, 35 e 40 |
| Uma rede do player, sem controlador colocado | A rede volta `Online`, igual ao que era, e o alcance passa a ser medido da nova posição | 24 |
| Uma rede do player que já tem um controlador colocado | O bloco não é colocado, o item fica na mão, e a action bar mostra `This network already has a controller.` | 56 |
| Uma rede de outro player | O bloco não é colocado, o item fica na mão, e a action bar mostra `Only the owner can place this network.` | 55 |
| O ID de uma rede que não existe | Nasce uma rede nova, como com o item sem ID | 57 |

O bloco guarda na BlockEntity o `NetworkId` da rede e o UUID do dono, nas chaves de NBT `network` e `owner`. O dono vai também ao cliente, para a quebra não aparecer a quem não é dono. As chaves são contrato de save e ficam fixadas na issue #26 (pergunta 67).

## Clicar

Um clique com a mão vazia mostra uma mensagem na action bar, até a Tela do Controlador chegar com a issue #20 (pergunta 52).

| Quem clica | Mensagem | Efeito |
|---|---|---|
| O dono | `Current network: <nome>` | A rede vira a rede atual do player (pergunta 25) |
| Outro player | `<nome> · owner: <dono>` | Nenhum: só o dono troca a rede atual para ela |
| Qualquer um, num controlador sem rede | `No network` | Nenhum |

Um controlador fica sem rede quando a rede dele não existe mais no save, como quando ela foi descartada por estar inválida (pergunta 92), ou num mundo criado antes da persistência da issue #16 (pergunta 53).

## Quebrar

| Quem quebra | O que acontece | Pergunta |
|---|---|---|
| O dono, no survival | O bloco quebra e solta o item com o `NetworkId` num data component. A rede fica `Offline` | 24 e 30 |
| Outro player, no survival | O bloco não quebra | 30 |
| Explosão | O bloco não é destruído | 30 |
| Qualquer player, no criativo | O bloco quebra, sem soltar item, como no jogo puro. A rede fica `Offline` | 30 e 49 |
| Comando ou outro mod | A rede fica `Offline` | 49 |

Quando o controlador some sem o dono quebrá-lo, a rede fica `Offline` por uma operação do domínio sem quem pede, porque o bloco já não está no mundo. Sem o item, a rede continua salva `Offline`, como uma rede cujo item se perdeu (pergunta 49). A retirada pelo dono continua conferindo o dono.

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:network_controller`, o mesmo do bloco |
| Nome | `Network Controller`, e `Controlador de Rede` em português (pergunta 68) |
| Aba do criativo | `Redweave`, a aba do mod, com o ícone do controlador (pergunta 69) |
| Data component | `redweave:network`, com o `NetworkId` da rede, só quando o item veio de um controlador quebrado (regra 7 do `AGENTS.md` e pergunta 65). O ID do componente é contrato de save e fica fixado na issue #26 (regra 15) |

## Onde ficam as redes

No save do mundo, num arquivo único para o servidor, com as redes e a rede atual de cada player, como descrito em [`rede-e-canais.md`](rede-e-canais.md#onde-a-rede-fica-salva) (issue #16, perguntas 88 a 93). O objeto por servidor da pergunta 63 continua sendo a raiz de composição, e só as redes em memória deram lugar ao save.

## Config

As quatro chaves que o `NetworkLimits` lê entram com este bloco, no config do servidor, `redweave-server.toml`, que vale por mundo e é enviado aos clientes (pergunta 50). Os valores iniciais estão em [`limites.md`](../limites.md).

| Chave | Valor inicial | O que é |
|---|---|---|
| `network.range` | 128 | Raio do alcance, em blocos. 0 é ilimitado |
| `network.name_length` | 32 | Tamanho máximo do nome da rede |
| `channel.name_length` | 16 | Tamanho máximo do nome do canal |
| `node.slots` | 4 | Slots por nó |

As faixas são as da pergunta 66: o alcance vai de 0 a 30.000.000, os nomes de 1 a 64 caracteres e os slots de 1 a 9. Um valor fora da faixa volta ao padrão, com um aviso no log.

## Visual

As texturas são as da prancha [`texturas.png`](../imagens/conceito/texturas.png), extraídas para `assets/redweave/textures/block/` (pergunta 51). Nesta issue, o bloco usa as texturas de corpo e mostra o estado do núcleo. As camadas emissivas e os LEDs por canal chegam com a issue #24.

| Face | Textura |
|---|---|
| Topo | `network_controller_top`, sem rede; `network_controller_top_on`, com rede |
| Frente | `network_controller_front`, voltada para o player que colocou o bloco |

O blockstate tem `facing`, nas 4 direções horizontais, e `linked`, verdadeiro quando o bloco tem rede, que escolhe o topo (pergunta 64). Os dois são contrato de save e ficam fixados na issue #26.
| Laterais e fundo | `network_controller_side` e `network_controller_bottom` |

| Estado | Visual | Pergunta |
|---|---|---|
| `Online` | `top_on` com a camada emissiva `top_glow`, e um LED aceso por canal ligado (issue #24) | 51 |
| Sem nós | `top_on` sem a camada emissiva, então o núcleo fica fraco no escuro | 60 |
| Sem rede | `top`, com o núcleo apagado | 51 |

## Fora desta funcionalidade

- A Tela do Controlador (issue #20).
- A textura final, os LEDs ao vivo e a receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 13:10 | Aplica a persistência da #16. |
| Marcelo Fabiano | 2026-11-10 09:31 | Aplica as perguntas 67 a 69. |
| Marcelo Fabiano | 2026-11-10 09:20 | Aplica as perguntas 64 a 66 e tira o tooltip, que não foi decidido. |
| Marcelo Fabiano | 2026-11-10 08:55 | Cria o documento, com as respostas das perguntas 49 a 58, 51 e 60. |
