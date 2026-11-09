# Domínio

As regras do Redweave em Java puro, sem depender do jogo (regra 10 do `AGENTS.md`). O ArchUnit garante que `domain/` só depende de si e do Java (pergunta 21). Quem chama fornece os dados: o domínio não lê o config, o relógio nem o mundo.

O comportamento completo está em [`rede-e-canais.md`](../../../../../../../../_docs/funcionalidades/rede-e-canais.md). Este arquivo é o guia de quem mexe no código: os nomes, as regras e os porquês. Quem muda o modelo atualiza este arquivo na mesma tarefa (regra 13).

## Pacotes

| Pacote | O que guarda |
|---|---|
| `signal/` | O sinal e as cores |
| `network/` | O agregado `Network`, os nós, os canais e as permissões |
| `simulation/` | A avaliação por tick |
| `circuit/` | Portas e chips, nas Fases 2 e 3 |

## Linguagem

Os nomes são os do [`glossario.md`](../../../../../../../../_docs/glossario.md). A coluna "Issue" diz onde cada tipo nasce.

| Termo | Significado | Issue |
|---|---|---|
| `Signal` | Um valor de 0 a 15, o mesmo intervalo da redstone do jogo. `OFF` e `MAX` são os extremos, `isOn` diz se passa de 0, `strongest` fica com o mais forte de dois, e `clamped` converte um valor lido do mundo no sinal válido mais próximo | #9 |
| `ChannelColor` | Uma das 16 cores de lã, na ordem do jogo. Dá a cor dos canais e da rede. `number` vai de 1 a 16, como nas telas, a `key` é o nome usado nos saves, e `fromKey` devolve vazio para uma chave desconhecida | #9 |
| `NetworkId` | O ID da rede, um UUID único no servidor e que nunca muda | #9 |
| `NodeId` | O ID de um nó, um UUID | #9 |
| `Network` | O agregado: nome, cor, dono, controlador, os 16 canais, os nós e as fontes sem posição | #10 |
| `Channel` | Um dos 16 canais da rede: a cor, o nome e o valor | #10 |
| `Node` | Um nó grudado numa face de um bloco, com os seus slots | #10 |
| `ChannelSlot` | A configuração de um nó para um canal: direção, face, canal e lógica | #10 |
| `Direction` | `INPUT` ou `OUTPUT` | #10 |
| `NetworkLimits` | Os números da rede, como slots por nó e tamanho dos nomes. O domínio define a interface, e o config a implementa | #10 |
| `ChannelValue` | A regra do canal: o mais forte entre as entradas que contam | #11 |
| `Simulator` | Avalia a rede uma vez por tick, só com canal alterado | #12 |

## Regras e porquês

- **Sinal de 0 a 15:** é o contrato com a redstone do jogo, então o construtor de `Signal` rejeita qualquer outro valor (regra 5). Quem lê do mundo converte antes, num valor seguro (regra 14).
- **Converter, não falhar:** o construtor de `Signal` rejeita um valor fora do intervalo, porque isso é erro de programação. Um valor que vem do mundo ou de outro mod passa por `Signal.clamped`, e quem chama registra no log (regra 14).
- **Chave da cor fixada no teste:** a `key` sai do nome da constante, então o teste fixa as 16 chaves. Renomear uma constante reprova o teste antes de quebrar um save (regra 15). Uma chave desconhecida, como de um save danificado, devolve vazio em vez de derrubar o jogo.
- **IDs vêm de fora:** `NetworkId` e `NodeId` só recebem o UUID. Quem chama gera com uma porta `IdGenerator`, para o domínio não ter aleatoriedade e os testes usarem IDs fixos (pergunta 31).
- **16 canais, sempre:** cada canal é uma cor de lã, então a rede nasce com os 16 e nunca cria nem apaga um. Assim, um slot sempre aponta para um canal que existe.
- **Vale o mais forte:** é a regra da própria redstone, em que um fio ligado a duas fontes fica com a mais forte. Sem entradas, o canal vale 0.
- **Chunk descarregado congela:** a entrada de um nó `Unloaded` mantém o último valor, como a redstone do jogo num chunk descarregado (perguntas 13 e 29). O domínio não sabe de chunks: quem chama informa o estado do nó.
- **Fora de alcance não conta:** um nó fora do raio ou em outra dimensão vale 0 (pergunta 23). O raio vem de quem chama, porque o domínio não lê o config.
- **Remote volta a 0:** uma fonte sem posição que sai do inventário ou do alcance para de contar, para o canal não ficar preso ligado (pergunta 27).
- **Rede `Offline` zera tudo:** sem o controlador colocado, todos os canais valem 0 (pergunta 24).
- **Só o dono altera:** toda operação que muda a rede recebe quem pede e confere se é o dono, sem exceção para operadores (perguntas 22 e 28).
- **Uma rede por nó:** o estado do nó na tela é um só, como `Online · <rede>`, então um nó não pertence a duas redes.
- **Primeiro canal livre:** é o primeiro, na ordem das cores, sem nenhum slot na rede (pergunta 25).
- **Avaliação por mudança:** a rede marca os canais cuja entrada mudou e só os recalcula no fim do tick, uma vez (regra 18). Só as saídas cujo valor mudou são devolvidas a quem chama, para o jogo não fazer block updates em cascata.
- **Números por interface:** limites como slots por nó e tamanho dos nomes vêm de `NetworkLimits`, nunca de constantes no domínio (regra 3).
- **Sem `get*`:** `strength()`, não `getStrength()` (regra 11), conferido pelo ArchUnit.

