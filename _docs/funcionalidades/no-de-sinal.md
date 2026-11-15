# Nó de sinal

O Signal Node (`redweave:signal_node`) é a peça que gruda numa face de qualquer bloco e liga aquele lugar a um canal da rede: lê a redstone ali ou a emite. Esta página descreve o nó no mundo na Fase 1: colocar, ler, emitir, clicar e quebrar. O comportamento da rede e dos slots está em [`rede-e-canais.md`](rede-e-canais.md), a tela em [`telas.md`](telas.md#tela-do-nó) e o visual em [`visual.md`](../conceito/visual.md#signal-node). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Grudado num bloco, o nó nasce sem rede; quando quem o colocou escolhe a rede, ele entra com os 16 canais inativos, e o dono ativa os que quiser. A entrada lê a redstone que chega ao bloco, e a saída energiza só o bloco onde o nó está grudado.

## Colocar

O nó gruda em qualquer face de qualquer bloco, como um botão. O bloco onde ele está grudado é o **bloco de apoio**. Desde a #44, o nó é uma parte do [multipart](multipart.md) e divide o espaço de bloco com outras partes, uma por face, como um Signal Cable no chão do mesmo espaço (perguntas 511 e 513).

1. O nó nasce `No network`, com o nome padrão, os 8 primeiros caracteres do ID, e não pode ser configurado até ter rede (perguntas 139 e 142).
2. Quem o colocou abre o nó com o Configurator no modo `Open` e escolhe uma das redes `Online` dele (perguntas 157 e 172).
3. O nó entra com os 16 slots, um por cor, todos inativos, e o dono ativa os canais na Tela do Nó (perguntas 224 e 226). O slot não tem nome próprio: o nome do canal da rede e o do nó já o identificam (pergunta 277).

A #15 e a #18 entregaram o nó entrando sozinho na rede atual (perguntas 25 e 70); a #87 trocou isso pela escolha, na [Tela do Nó](telas.md#escolha-de-rede), e trouxe o nome do nó, que o dono troca com um clique (pergunta 204). O nó guarda o `NodeId` na BlockEntity, com a chave de NBT `node` (pergunta 76), e o UUID de quem o colocou, com a chave `owner` (pergunta 165).

## Ler

Um slot `Input` lê a redstone de um lado do bloco de apoio, conforme a face do slot (pergunta 71).

| Face do slot | O que o nó lê |
|---|---|
| A face onde o nó está grudado | A força do próprio bloco de apoio, como um repetidor lê o bloco atrás dele. O próprio nó não conta, para uma saída do nó não se ler de volta |
| Qualquer outra face | O sinal que o vizinho do bloco de apoio, daquele lado, manda para ele |

O nó lê quando é colocado, quando um vizinho muda e quando o chunk dele carrega. Uma leitura que muda o valor deixa a rede pendente (regra 18).

## Emitir

Um slot `Output` energiza com força só o bloco de apoio, qualquer que seja a face do slot, e o resto segue a redstone do jogo a partir desse bloco: uma lâmpada com um nó grudado acende, e o bloco de apoio liga o que encosta nele. Diferente de uma alavanca, o nó não manda sinal fraco para os vizinhos do espaço dele, então duas lâmpadas separadas por 1 bloco, com o nó entre elas, acendem só a do apoio (perguntas 71 e 262). A exceção é o [Signal Cable](signal-cable.md) e o pó do jogo: um cabo ou um pó no espaço vizinho, na mesma face do nó, liga-se a ele, recebe o que ele emite e o alimenta como se fosse o bloco de apoio, como um cabo no mesmo espaço (perguntas 566 e 574).

- O nó emite o mais forte entre as saídas dele (perguntas 39 e 71).
- Um nó `Out of range`, `No network` ou numa rede `Offline` ou `Unplaced` emite 0.
- O nó só avisa os vizinhos quando o valor emitido muda.

## Avaliação

No fim de cada tick, o servidor avalia só as redes pendentes, com o simulador, e aplica as saídas que mudaram aos nós em chunks carregados. Nenhum chunk é carregado à força (regra 6). Um nó cujo chunk carrega de novo pede à rede o valor atual das saídas dele.

## Clicar

A [Tela do Nó](telas.md#tela-do-nó) abre com o [Configurator](configurador.md) no modo `Open` (pergunta 140). Um clique com a mão vazia não abre nada e mostra `Use a Configurator to open this node.` na action bar (pergunta 154). A #19 entregou a abertura com a mão vazia, que a #86 trocou pela abertura com o Configurator (pergunta 188).

## Quebrar

| Quem ou o quê | O que acontece | Pergunta |
|---|---|---|
| O dono da rede, no survival | O nó quebra, sai da rede e cai como item simples | 74 |
| Outro player, no survival | O nó não quebra | 74 |
| Qualquer player, num nó sem rede | O nó quebra | 74 |
| Qualquer player, no criativo | O nó quebra e sai da rede | 74 |
| O bloco de apoio some, uma explosão ou outro mod | O nó cai e sai da rede por uma operação sem quem pede, como na pergunta 49 | 74 |
| O dono, com o Configurator no modo `Wrench` | O nó sai da rede e vai para o inventário como item simples, ou cai no chão se o inventário estiver cheio. Num nó sem rede, só quem o colocou. Funciona também numa rede `Offline` | 161 a 165 |

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:signal_node`, o ID do item; desde a #44, o nó colocado é uma parte do bloco contêiner `redweave:part_space` (pergunta 523) |
| Nome | `Signal Node`, e `Nó de Sinal` em português |
| Aba do criativo | `Redweave` (pergunta 69) |

O item não guarda configuração, nem quando sai pela Chave (pergunta 161). Copiar e colar a configuração de um nó é o [Configurator](configurador.md).

## Visual

Uma placa fina, com cerca de 2 pixels de espessura, presa à face como um botão (`visual.md`). As texturas vêm da prancha [`texturas.png`](../imagens/conceito/texturas.png).

| Parte | Textura |
|---|---|
| Frente | `signal_node_front`, com a lente apagada; `signal_node_front_on`, com a lente acesa |
| Bordas | `signal_node_edge` |

Desde a #44, o nó é uma parte do [multipart](multipart.md), e o visual fica em campos da parte, no NBT dela, dentro de `data` no contêiner `redweave:part_space`, sincronizados com o cliente, e não mais em estados do bloco (perguntas 534 e 543). O `facing`, nas 6 direções, a face do bloco de apoio onde o nó está, não vai para o save: sai da `face` da parte no contêiner, que é o oposto dele (pergunta 546). O `powered`, verdadeiro quando o nó emite mais que 0, acende a lente (pergunta 72). O `out_of_range` troca a frente por `signal_node_front_out_of_range`, com a lente apagada e o anel tracejado vermelho. Os campos de `data` são `node`, `owner`, `powered` e `out_of_range`, contrato de save fixado na issue #26 (regra 15). O item continua `redweave:signal_node` (pergunta 523). O servidor atualiza os nós carregados de uma rede quando o estado deles muda (pergunta 86). Os 4 cantos da placa mostram os 4 primeiros canais ativos, na ordem das cores, cada um na sua cor, com a textura `signal_node_slot` tingida, e ficam apagados quando há menos de 4 ativos. Os outros aparecem só nas telas (perguntas 61, 87 e 229).

**Intensidade na lente (issue #24):** a lente mostra o sinal mais forte do nó, o maior entre a última leitura das entradas e a emissão das saídas, então uma entrada recebendo sinal também acende (pergunta 98). O `powered` dá lugar a um campo `signal`, de 0 a 15, e a lente é tingida do vermelho escuro ao vivo conforme o nível, como o fio de redstone, brilhando no escuro pela camada emissiva (pergunta 99). Um nó `Out of range`, `No network` ou numa rede `Unplaced` mostra a lente apagada; numa rede `Offline`, também, com o estado `Offline` em cinza nas telas (pergunta 177). Só a lente brilha: o nó não é fonte de luz (pergunta 100). O servidor só troca o campo quando o valor muda, sem avisar os vizinhos (regra 18). O campo é contrato de save e fica fixado na #26.

## Fora desta funcionalidade

- A Tela do Nó e a configuração dos slots ([`telas.md`](telas.md#tela-do-nó)).
- As camadas emissivas e a receita (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 09:35 | Leva o visual do nó para os campos da parte no contêiner, sem estados do bloco (perguntas 534, 543 e 546). |
| Zennd | 2026-11-15 09:07 | O nó troca sinal com o Signal Cable e o pó do jogo no espaço vizinho (pergunta 574). |
| Zennd | 2026-11-14 19:34 | Leva os estados do bloco do nó para a parte (pergunta 534). |
| Zennd | 2026-11-14 19:14 | Leva o nó ao multipart da #44 (perguntas 511, 513 e 523). |
| Zennd | 2026-11-12 17:18 | Corrige o resumo com os canais inativos e aponta a Tela do Nó para `telas.md` (pergunta 226). |
| Zennd | 2026-11-12 11:58 | Tira o nome do slot (pergunta 277). |
| Zennd | 2026-11-12 11:11 | A saída energiza só o bloco de apoio, sem o sinal fraco da alavanca (pergunta 262). |
| Zennd | 2026-11-11 16:40 | Aplica os 16 slots por cor e os cantos da placa (perguntas 224, 226 e 229). |
| Zennd | 2026-11-11 13:46 | Registra a escolha de rede e o nome do nó entregues na #87. |
| Zennd | 2026-11-11 11:21 | Aplica a pergunta 188. |
| Zennd | 2026-11-11 10:24 | Aplica a pergunta 177. |
| Zennd | 2026-11-11 09:51 | Aplica o nó que nasce sem rede, o nome, quem colocou, a abertura pelo Configurator e a Chave (perguntas 139 a 172). |
| Marcelo Fabiano | 2026-11-10 15:52 | Aponta para a página do configurador. |
| Marcelo Fabiano | 2026-11-10 15:23 | Descreve a intensidade na lente com as perguntas 98 a 100. |
| Marcelo Fabiano | 2026-11-10 13:10 | Tira a #16 do que fica fora desta funcionalidade. |
| Marcelo Fabiano | 2026-11-10 11:26 | Aplica a Tela do Nó e as perguntas 86 e 87. |
| Marcelo Fabiano | 2026-11-10 09:46 | Cria o documento, com as respostas das perguntas 70 a 76. |
