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

Um clique com a mão vazia abre a [Tela do Nó](telas.md#tela-do-nó), da issue #19, que substitui a mensagem da action bar da pergunta 75.

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

O item não guarda configuração. Copiar e colar a configuração de um nó é o [Configurator](configurador.md).

## Visual

Uma placa fina, com cerca de 2 pixels de espessura, presa à face como um botão (`visual.md`). As texturas vêm da prancha [`texturas.png`](../imagens/conceito/texturas.png).

| Parte | Textura |
|---|---|
| Frente | `signal_node_front`, com a lente apagada; `signal_node_front_on`, com a lente acesa |
| Bordas | `signal_node_edge` |

O blockstate tem `facing`, nas 6 direções, a face do bloco de apoio onde o nó está, e `powered`, verdadeiro quando o nó emite mais que 0, que acende a lente (pergunta 72). Os dois são contrato de save e ficam fixados na issue #26. O blockstate tem também `out_of_range`, que troca a frente por `signal_node_front_out_of_range`, com a lente apagada e o anel tracejado vermelho. O servidor atualiza os nós carregados de uma rede quando o estado deles muda (pergunta 86). Os 4 cantos da placa mostram os 4 primeiros slots, cada um na cor do canal, com a textura `signal_node_slot` tingida, e ficam apagados com o slot livre. Os slots 5 a 9 aparecem só na tela (perguntas 61 e 87).

**Intensidade na lente (issue #24):** a lente mostra o sinal mais forte do nó, o maior entre a última leitura das entradas e a emissão das saídas, então uma entrada recebendo sinal também acende (pergunta 98). O `powered` dá lugar a uma propriedade `signal`, de 0 a 15, e a lente é tingida do vermelho escuro ao vivo conforme o nível, como o fio de redstone, brilhando no escuro pela camada emissiva (pergunta 99). Um nó `Out of range`, `No network` ou numa rede `Offline` mostra a lente apagada. Só a lente brilha: o nó não é fonte de luz (pergunta 100). O servidor só troca o estado quando o valor muda, sem avisar os vizinhos (regra 18). A propriedade é contrato de save e fica fixada na #26.

## Fora desta funcionalidade

- A Tela do Nó e a configuração dos slots (issue #19).
- As camadas emissivas e a receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 15:52 | Aponta para a página do configurador. |
| Marcelo Fabiano | 2026-11-10 15:23 | Descreve a intensidade na lente com as perguntas 98 a 100. |
| Marcelo Fabiano | 2026-11-10 13:10 | Tira a #16 do que fica fora desta funcionalidade. |
| Marcelo Fabiano | 2026-11-10 11:26 | Aplica a Tela do Nó e as perguntas 86 e 87. |
| Marcelo Fabiano | 2026-11-10 09:46 | Cria o documento, com as respostas das perguntas 70 a 76. |
