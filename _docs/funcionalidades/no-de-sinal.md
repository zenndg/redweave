# Nó de sinal

O Signal Node (`redweave:signal_node`) é a peça que gruda numa face de qualquer bloco e liga aquele lugar a um canal da rede: lê a redstone ali ou a emite. Esta página descreve o nó no mundo na Fase 1: colocar, ler, emitir, clicar e quebrar. O comportamento da rede e dos slots está em [`rede-e-canais.md`](rede-e-canais.md), a tela em [`telas.md`](telas.md#tela-do-nó) e o visual em [`visual.md`](../conceito/visual.md#signal-node). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Grudado num bloco, o nó entra na rede atual do player com uma entrada num canal livre. A entrada lê a redstone que chega ao bloco, e a saída alimenta o bloco como uma alavanca.

## Colocar

O nó gruda em qualquer face de qualquer bloco, como um botão. O bloco onde ele está grudado é o **bloco de apoio**.

1. O nó entra na rede atual do player que o colocou (pergunta 25).
2. Ganha 1 slot `Input` na face em que foi grudado, no primeiro canal livre.
3. Sem rede atual, ou com os 16 canais em uso, o nó fica no mundo assim mesmo: `No network`, ou na rede sem slot.

Quem liga o nó à rede é esta funcionalidade, que entrega também a issue #18 (pergunta 70). O nó guarda o `NodeId` na BlockEntity, com a chave de NBT `node` (pergunta 76).

## Ler

Um slot `Input` lê a redstone de um lado do bloco de apoio, conforme a face do slot (pergunta 71).

| Face do slot | O que o nó lê |
|---|---|
| A face onde o nó está grudado | A força do próprio bloco de apoio, como um repetidor lê o bloco atrás dele. O próprio nó não conta, para uma saída do nó não se ler de volta |
| Qualquer outra face | O sinal que o vizinho do bloco de apoio, daquele lado, manda para ele |

O nó lê quando é colocado, quando um vizinho muda e quando o chunk dele carrega. Uma leitura que muda o valor deixa a rede pendente (regra 18).

## Emitir

Um slot `Output` alimenta com força o bloco de apoio, como uma alavanca grudada nele, qualquer que seja a face do slot. Assim, uma lâmpada com um nó grudado acende, e o bloco de apoio liga os vizinhos dele (pergunta 71).

- O nó emite o mais forte entre as saídas dele (perguntas 39 e 71).
- Um nó `Out of range`, `No network` ou numa rede `Offline` emite 0.
- O nó só avisa os vizinhos quando o valor emitido muda.

## Avaliação

No fim de cada tick, o servidor avalia só as redes pendentes, com o simulador, e aplica as saídas que mudaram aos nós em chunks carregados. Nenhum chunk é carregado à força (regra 6). Um nó cujo chunk carrega de novo pede à rede o valor atual das saídas dele.

## Clicar

Um clique com a mão vazia mostra na action bar o estado e a rede, até a Tela do Nó chegar com a issue #19 (pergunta 75).

| Estado | Mensagem |
|---|---|
| `Online` | `Online · <rede>` |
| `Out of range` | `Out of range · <rede>` |
| `No network` | `No network` |

O estado `Unloaded` não aparece no clique, porque um nó num chunk descarregado não pode ser clicado.

## Quebrar

| Quem ou o quê | O que acontece | Pergunta |
|---|---|---|
| O dono da rede, no survival | O nó quebra, sai da rede e cai como item simples | 74 |
| Outro player, no survival | O nó não quebra | 74 |
| Qualquer player, num nó sem rede | O nó quebra | 74 |
| Qualquer player, no criativo | O nó quebra e sai da rede | 74 |
| O bloco de apoio some, uma explosão ou outro mod | O nó cai e sai da rede por uma operação sem quem pede, como na pergunta 49 | 74 |

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:signal_node`, o mesmo do bloco |
| Nome | `Signal Node`, e `Nó de Sinal` em português |
| Aba do criativo | `Redweave` (pergunta 69) |

O item não guarda configuração. Copiar e colar a configuração de um nó é o Configurador, na issue #17.

## Visual

Uma placa fina, com cerca de 2 pixels de espessura, presa à face como um botão (`visual.md`). As texturas vêm da prancha [`texturas.png`](../imagens/conceito/texturas.png).

| Parte | Textura |
|---|---|
| Frente | `signal_node_front`, com a lente apagada; `signal_node_front_on`, com a lente acesa |
| Bordas | `signal_node_edge` |

O blockstate tem `facing`, nas 6 direções, a face do bloco de apoio onde o nó está, e `powered`, verdadeiro quando o nó emite mais que 0, que acende a lente (pergunta 72). Os dois são contrato de save e ficam fixados na issue #26. O anel de fora de alcance, os pontos dos slots tingidos (pergunta 61) e a lente mais forte com o sinal chegam com a Tela do Nó (#19) e as camadas emissivas (#24).

## Fora desta funcionalidade

- A Tela do Nó e a configuração dos slots (issue #19).
- O Configurador (issue #17).
- Salvar as redes no mundo (issue #16).
- As camadas emissivas e a receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 09:46 | Cria o documento, com as respostas das perguntas 70 a 76. |
