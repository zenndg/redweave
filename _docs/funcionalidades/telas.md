# Telas

As telas próprias do Redweave na Fase 1: a do Signal Node e a do Network Controller. Elas seguem os pilares de [`ux.md`](../conceito/ux.md) e os elementos de interface de [`visual.md`](../conceito/visual.md#elementos-de-interface). Este documento é o conceito das telas; o comportamento da rede fica na página da rede e dos canais (issue #7).

## Princípios

- **Estilo:** janelas próprias de metal escuro com chanfro, sem a textura do baú do jogo, na mesma linha das telas do Cubit.
- **Textos:** em inglês, no `en_us.json`, e em português, no `pt_br.json`, com as mesmas chaves (regra 1 do `AGENTS.md`). As imagens mostram os textos em inglês.
- **Nada passa da borda:** texto longo é cortado com reticências, e um tooltip mostra o texto inteiro.
- **Sinal ao vivo:** valores e medidores atualizam com a tela aberta, pelo servidor (regra 16).
- **Quem abre:** qualquer player. Só o dono da rede altera alguma coisa, validado no servidor (regra 17 e pergunta 22). Para os outros, a tela abre só para leitura.
- **Escala:** as imagens mostram as telas em 2×. Os tamanhos finais em pixels da interface do jogo são definidos na implementação.

## Tela do Nó

![Tela do Nó](../imagens/telas/tela-do-no.png)

**Abre** com um clique no Signal Node com a mão vazia.

| Parte | O que mostra |
|---|---|
| Título | `Signal Node` |
| Estado | No canto superior direito: `Online · <rede>`, `Unloaded`, `Out of range` ou `No network` ([estados do nó](rede-e-canais.md#estados-do-nó)) |
| Slots | À esquerda, um botão por slot de canal: a cor e o nome do canal, a direção e o sinal, como `15 Red` e `Output · signal 12`. Slot livre aparece tracejado, com `Free slot` e `Click to use` |
| Slot escolhido | À direita, a configuração do slot selecionado |
| Direção | Seletor `Input` e `Output` |
| Face do bloco | Um cubo com a face escolhida em cobre, o nome da face, como `East`, e `click a face to change` |
| Canal | As 16 cores, em duas fileiras de 8, com a escolhida destacada, e o nome do canal |
| Lógica | `None`, `Invert`, `Pulse`, `Delay` e `Threshold`. Na Fase 1, só `None` fica habilitada, com o aviso `Logic arrives in Phase 2. Until then, Output repeats the channel as it is.` |
| Rodapé | O medidor de 15 barras com o sinal do slot e quantos slots estão em uso, como `3 of 4 slots in use` |
| Abas | À direita: `Slots`, `Upgrades` e `Info` |

A quantidade de slots vem de [`limites.md`](../limites.md), nunca fixa na tela (regra 3). O conteúdo das abas `Upgrades` e `Info` é definido junto com os upgrades.

### Comportamento

O que a tela faz, decidido na issue #19.

| Ponto | Como é | Pergunta |
|---|---|---|
| Desenho | Sprites em pixel art, em 9-slice como os do jogo, das folhas [`tela-do-no-sprites-a.png`](../imagens/telas/tela-do-no-sprites-a.png) e [`tela-do-no-sprites-b.png`](../imagens/telas/tela-do-no-sprites-b.png). Os textos usam a fonte do jogo | 78 |
| Abas | Por enquanto, só `Slots`. `Upgrades` e `Info` entram quando tiverem conteúdo | 80 |
| Selecionar um slot | Um clique no slot da lista o mostra à direita. A seleção é só do cliente | — |
| Usar um slot livre | `Click to use` cria uma entrada na face onde o nó está grudado, no primeiro canal livre, e seleciona o slot | 83 |
| Direção | Um clique em `Input` ou `Output` troca a direção do slot | — |
| Face do bloco | Um cubo 3D com o próprio bloco de apoio, girável arrastando o mouse. A face sob o cursor e a escolhida ficam realçadas em cobre, e o clique escolhe a face | 79 |
| Canal | Um clique numa das 16 cores troca o canal do slot | — |
| Liberar um slot | O botão `Clear slot` pede confirmação no segundo clique, `Click again to clear` | 84 |
| Medidor | O sinal do slot escolhido: na entrada, a última leitura; na saída, o que ela emite | — |
| Ao vivo | O estado, a rede e o sinal de cada slot atualizam a cada tick, só quando mudam | 82 |
| Só leitura | Para quem não é o dono, os controles ficam apagados, e o rodapé mostra um cadeado e `Only the owner of <rede> can change this node.` | 85 |

**O servidor confere cada ação (regras 16 e 17):** quem pede é o dono da rede do nó, o player está dentro do alcance de interação de blocos do jogo (pergunta 81), o slot existe e a face e o canal são válidos. Uma ação recusada não muda nada, e a tela volta a mostrar o estado do servidor.

## Tela do Controlador

![Tela do Controlador](../imagens/telas/tela-do-controlador.png)

**Abre** com um clique no Network Controller.

| Parte | O que mostra |
|---|---|
| Cabeçalho | A cor e o nome da rede, com o botão `Rename` |
| Estado | `Online · <n> nodes` |
| Abas | `Nodes`, `Signals` e `Access` |
| Nodes | Uma linha por nó: canal, bloco onde está, direção (`In` ou `Out`), posição e sinal. Um Signal Remote aparece com a posição `held item`. Um nó `Unloaded` aparece em âmbar, e um `Out of range`, em vermelho (pergunta 29) |
| Últimos 30 segundos | À direita, um gráfico em degraus por canal em uso, na cor do canal |
| Rodapé | Canais em uso, como `3 of 16 channels in use`, o dono e quantos nós estão `Unloaded`, em âmbar, e `Out of range`, em vermelho |

O conteúdo das abas `Signals` e `Access` está em [`rede-e-canais.md`](rede-e-canais.md#abas-da-tela-do-controlador). Um nó `Unloaded` continua contando para o canal com o último valor lido, e um `Out of range` não conta (perguntas 13, 23 e 29).

## Pendências

| Ponto | Depende de |
|---|---|
| Conteúdo das abas `Upgrades` e `Info`, que por enquanto não aparecem (pergunta 80) | Issue dos upgrades |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 11:26 | Descreve o comportamento da Tela do Nó com as perguntas 78 a 85. |
| Marcelo Fabiano | 2026-11-09 15:39 | Aplica as perguntas 22 e 29 e aponta as abas `Signals` e `Access` para a página da rede. |
| Marcelo Fabiano | 2026-11-09 11:03 | Aplica as respostas das perguntas 13 e 15. |
| Marcelo Fabiano | 2026-11-09 11:20 | Descreve as telas do nó e do controlador. |
