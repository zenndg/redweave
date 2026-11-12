# Telas

As telas próprias do Redweave na Fase 1: a do Signal Node e a do Network Controller. A do Network Terminal está em [`terminal-de-rede.md`](terminal-de-rede.md). Elas seguem os pilares de [`ux.md`](../conceito/ux.md) e os elementos de interface de [`visual.md`](../conceito/visual.md#elementos-de-interface). Este documento é o conceito das telas; o comportamento da rede fica na página da rede e dos canais (issue #7).

## Princípios

- **Estilo:** janelas próprias de metal escuro com chanfro, sem a textura do baú do jogo, na mesma linha das telas do Cubit.
- **Textos:** em inglês, no `en_us.json`, e em português, no `pt_br.json`, com as mesmas chaves (regra 1 do `AGENTS.md`). As imagens mostram os textos em inglês.
- **Nada passa da borda:** texto longo é cortado com reticências, e um tooltip mostra o texto inteiro.
- **Sinal ao vivo:** valores e medidores atualizam com a tela aberta, pelo servidor (regra 16).
- **Quem abre:** qualquer player. Só o dono da rede altera alguma coisa, validado no servidor (regra 17 e pergunta 22). Para os outros, a tela abre só para leitura.
- **Rede `Offline`:** as telas da rede não abrem, nem para o dono, e a action bar mostra `<rede> is offline. Turn it on at a Network Terminal.` Só o Network Terminal age sobre ela (perguntas 132 e 160).
- **Escala:** as imagens mostram as telas em 2×. Os tamanhos finais em pixels da interface do jogo são definidos na implementação.

## Tela do Nó

![Tela do Nó](../imagens/telas/tela-do-no.png)

**Abre** com um clique no Signal Node com o [Configurator](configurador.md) no modo `Open` (pergunta 140). A mão vazia só mostra a dica `Use a Configurator to open this node.` (pergunta 154).

### Escolha de rede

Um nó `No network` abre na escolha de rede, e não na configuração (pergunta 139). Desenho: [`tela-do-no-escolha-de-rede.png`](../imagens/telas/tela-do-no-escolha-de-rede.png), com os sprites de [`tela-do-no-sprites-c.png`](../imagens/telas/tela-do-no-sprites-c.png) (pergunta 198).

![Escolha de rede](../imagens/telas/tela-do-no-escolha-de-rede.png)

| Ponto | Como é | Pergunta |
|---|---|---|
| Quem escolhe | Só quem colocou o nó. Para os outros, a tela abre só para leitura, com `No network` | 172 |
| Lista | As redes `Online` de quem colocou o nó, com a última em que ele colocou um nó destacada | 157 |
| Escolher | Um clique numa rede liga o nó a ela, com 1 slot `Input` no primeiro canal livre, e a tela segue para a configuração | 171 |
| Linha | O quadrado na cor da rede, o nome e a quantidade de nós, como `1 node` e `12 nodes`. A última usada fica no topo, em cobre, e as outras seguem em ordem alfabética, sem diferenciar maiúsculas | 202 e 203 |
| Sem redes | A lista vazia, com o ícone do controlador e `No network online. Place a Network Controller to create one.` | 199 |
| Trocar depois | Na configuração, o clique no nome da rede volta a esta lista, para trocar de rede ou voltar mantendo a atual. É preciso ser dono das duas redes | 141 |
| Voltar | Na troca, o botão `Back` no rodapé volta à configuração sem mudar nada. Não aparece na primeira escolha | 205 |
| No servidor | A tela recebe só as linhas, e o clique manda a posição na lista que o servidor guardou, nunca o ID da rede. O servidor confere de novo quem pode escolher | 172 e regra 17 |

### Configuração

| Parte | O que mostra |
|---|---|
| Título | `Signal Node` |
| Nome do nó | Abaixo do título, o nome, que nasce com os 8 primeiros caracteres do ID. Para o dono, vem com um lápis, e o clique o troca por um campo de texto: Enter salva, Esc cancela, e vazio volta ao padrão. O texto vai num pacote próprio, e o servidor confere o dono, o alcance e o limite de `node.name_length` (perguntas 142, 144 e 204) |
| Rede | Na linha do nome, à direita, o quadrado na cor da rede e o nome dela, em qualquer estado com rede. Para o dono, o clique abre a escolha de rede (perguntas 141 e 208) |
| Estado | No canto superior direito: `Online`, `Unloaded`, `Out of range`, `No network` ou `Offline`, em cinza ([estados do nó](rede-e-canais.md#estados-do-nó)) (perguntas 177 e 208) |
| Canais | Abaixo do nome, uma fileira com os 16 canais, cada um com a cor e o número, de `01` a `16`. Os ativos têm uma barra verde embaixo, e o escolhido tem o contorno de cobre (perguntas 224 e 231) |
| Canal escolhido | Abaixo da fileira, o painel do canal: o título, como `15 Red`, ou `15 Main door` com o canal renomeado na Tela do Controlador (pergunta 221) |
| Status | A pílula `Enabled`, verde, ou `Disabled`, cinza. O clique troca o status (perguntas 226 e 228) |
| Direção | Seletor `Input` e `Output`. Só muda com o canal `Disabled`; com `Enabled`, fica travado (pergunta 237) |
| Face do bloco | Um cubo com a face escolhida em cobre, o nome da face, como `East`, e `click a face to change`. Só muda com o canal `Disabled`; um canal nunca ativado parte da face grudada (pergunta 237) |
| Lógica | `None`, `Invert`, `Pulse`, `Delay` e `Threshold`. Na Fase 1, só `None` fica habilitada, com o aviso `Logic arrives in Phase 2.` |
| Rodapé | O medidor de 15 barras com o sinal do canal escolhido e quantos canais estão ativos, como `3 of 16 channels active` |

A tela não tem abas laterais. A aba `Info` volta quando tiver conteúdo (pergunta 233).

### Comportamento

O que a tela faz, decidido na issue #19.

| Ponto | Como é | Pergunta |
|---|---|---|
| Desenho | Sprites em pixel art, em 9-slice como os do jogo, das folhas [`tela-do-no-sprites-a.png`](../imagens/telas/tela-do-no-sprites-a.png), [`tela-do-no-sprites-b.png`](../imagens/telas/tela-do-no-sprites-b.png) e [`tela-do-no-sprites-d.png`](../imagens/telas/tela-do-no-sprites-d.png). Os textos usam a fonte do jogo | 78 e 231 |
| Escolher um canal | Um clique num canal da fileira o mostra no painel. A tela abre no primeiro canal ativo, ou no `01` sem nenhum ativo. A escolha é só do cliente | — |
| Ativar e desativar | O clique na pílula `Status` troca entre `Enabled` e `Disabled`. Na primeira ativação, o canal vira `Input` na face onde o nó está grudado; desativar guarda a direção e a face | 226 e 228 |
| Configurar | Direção e face mudam só com o canal `Disabled`, que não lê nem emite, para não disparar redstone sem querer. Com `Enabled`, os controles ficam travados, e o servidor recusa a mudança. Para mudar um canal ativo, o dono desativa, configura e ativa | 237 |
| Direção | Um clique em `Input` ou `Output` troca a direção do slot | — |
| Face do bloco | Um cubo 3D com o próprio bloco de apoio, girável arrastando o mouse. A face sob o cursor e a escolhida ficam realçadas em cobre, e o clique escolhe a face | 79 |
| Medidor | O sinal do canal escolhido: na entrada, a última leitura; na saída, o que ela emite; inativo, 0 | — |
| Ao vivo | O estado, a rede, os nomes, a lista de escolha e o sinal de cada slot atualizam a cada tick, só quando mudam | 82 |
| Só leitura | Para quem não é o dono, os controles ficam apagados, e o rodapé mostra um cadeado e `Only the owner of <rede> can change this node.` | 85 |

**O servidor confere cada ação (regras 16 e 17):** quem pede é o dono da rede do nó, o player está dentro do alcance de interação de blocos do jogo (pergunta 81), o canal existe e a face é válida. Uma ação recusada não muda nada, e a tela volta a mostrar o estado do servidor.

## Tela do Controlador

![Tela do Controlador](../imagens/telas/tela-do-controlador.png)

**Abre** com um clique no Network Controller com a mão vazia ou com o Configurator no modo `Open` (pergunta 232), ou pela lista do [Network Terminal](terminal-de-rede.md) (perguntas 113 e 131). O desenho tem seis variantes, com a aba `Log` da #90 (pergunta 214), e os sprites estão em [`tela-do-controlador-sprites.png`](../imagens/telas/tela-do-controlador-sprites.png).

| Parte | O que mostra |
|---|---|
| Cabeçalho | O quadrado na cor da rede e o nome dela. Para o dono, o clique no quadrado abre a paleta, e o botão `Rename` troca o nome por um campo de texto (perguntas 156 e 212) |
| Estado | `Online · <n> nodes` |
| Abas | `Nodes`, `Signals`, `Access` e `Log`, esta com a #90 (perguntas 214 e 215) |
| Nodes | Uma linha por nó, na ordem em que entraram na rede, com o nome, o sinal de 0 a 15 e a posição x, y e z do bloco do nó (perguntas 179 e 216). O sinal é o da lente, o mais forte entre as leituras e a emissão, e 0 num nó que não conta (pergunta 98). A posição só aparece para o dono (pergunta 149). Um nó `Unloaded` aparece em âmbar, e um `Out of range`, em vermelho (pergunta 29). Um Signal Remote aparecerá com a posição `held item`, quando o Remote existir |
| Log | Uma linha por mudança de valor de uma entrada ou de uma saída, a mais nova no topo, com a data e a hora reais do servidor em cinza, o nome do nó, o quadrado da cor e o canal, a direção e o valor à direita, como `2026-11-11 14:32:05  Iron Door  15 Red  Output  12`. Guarda as últimas `log.size` linhas da rede, só em memória, e chega só a quem está com a tela aberta (perguntas 147, 148 e 214) |
| Rodapé | Na primeira linha, os canais em uso, como `3 of 16 channels in use`, e quantos nós estão `Unloaded`, em âmbar, e `Out of range`, em vermelho, mesmo com zero, como `0 unloaded` e `0 out of range` (perguntas 219 e 236). Na segunda, `Owner: <dono>` ou, para os outros, o cadeado e `Only the owner can change this network.` |

O log substitui o gráfico dos últimos 30 segundos (pergunta 150). O conteúdo das abas `Signals` e `Access` está em [`rede-e-canais.md`](rede-e-canais.md#abas-da-tela-do-controlador). Um nó `Unloaded` continua contando para o canal com o último valor lido, e um `Out of range` não conta (perguntas 13, 23 e 29).

### Comportamento

O que a tela faz, decidido na issue #20.

| Ponto | Como é | Pergunta |
|---|---|---|
| Quem abre | Qualquer player, com a mão vazia. Para quem não é o dono, a tela é só leitura: sem `Rename`, sem paleta e sem renomear nós e canais | 22 |
| Rede `Offline` | Nada abre, e a action bar mostra o aviso. Uma tela aberta fecha se a rede for desligada | 158 e 160 |
| Linha aberta | O clique numa linha mostra os canais ativos do nó abaixo dela, com o ícone de entrada ou de saída, a cor, o canal e a direção, como `15 Red · Input`, sem nome do slot (pergunta 277). Um nó sem canal ativo mostra `No active channel`. Abrir uma linha fecha a anterior | 179, 218, 234 e 277 |
| Renomear um nó | Para o dono, o duplo clique no nome vira um campo de texto: Enter salva, Esc cancela, e vazio volta ao padrão. Um clique, no nome ou no resto da linha, abre e fecha os slots, e o duplo clique deixa a linha como estava | 144, 217 e 222 |
| Renomear a rede | O botão `Rename` troca o nome por um campo de texto. Um nome vazio é recusado, e o nome continua o mesmo | 156 |
| Renomear um canal | Na aba `Signals`, o duplo clique no nome vira um campo de texto, e vazio volta ao nome da cor | 26, 34 e 222 |
| Trocar a cor | A paleta abre sob o quadrado da cor, com as 16 cores em duas fileiras de 8 e a atual destacada. O clique numa cor a escolhe e fecha a paleta | 212 |
| Lista vazia | A aba `Nodes` de uma rede sem nós mostra `No node in this network yet.` no meio da tabela | 220 |
| Log vazio | A aba `Log` sem nenhuma mudança mostra `No signal change yet.` no meio da tabela | 248 |
| Limpar o log | Para o dono, um botão vermelho `Clear log` no canto da aba `Log` vira `Click again to clear` no primeiro clique e, no segundo, apaga o log da rede no servidor, para todos; as telas abertas recebem o log vazio. Para os outros players o botão não aparece | 264 e 265 |
| Rolagem | A roda do mouse rola a lista de nós e o log quando não cabem | — |
| Linha de entrada do log | Nasce quando a leitura de um slot de entrada muda | 147 |
| Linha de saída do log | Nasce por slot de saída quando o valor que ele emite muda, inclusive quando cai para 0 porque a rede ficou `Offline` ou `Unplaced` ou o nó saiu do alcance. Ativar ou desativar um slot de saída também gera linha, porque um slot desativado emite 0 | 243 e 246 |
| Log ao carregar | O log começa vazio, e a reaplicação das faces no primeiro tick depois de carregar a rede não gera linhas | 147 e 244 |
| Nomes no log | Os do nó e do canal no momento da mudança: renomear depois não muda as linhas antigas, e um nó removido continua com o nome na linha | 245 |
| Ao vivo | Os nomes e as linhas vão ao cliente só quando mudam, e os sinais e os estados vão à parte, também só quando mudam, para um circuito rápido não reenviar a lista inteira (regra 18). O log vai inteiro ao abrir a tela e, depois, só as linhas novas | 82 e 147 |

**O servidor confere cada ação (regras 16 e 17):** a ação só chega da tela aberta daquele controlador, ao alcance de interação de blocos do jogo e com a rede `Online`, e o domínio confere o dono e o tamanho do nome. O cliente aponta um nó pela posição dele na lista que o servidor guardou, nunca pelo ID, e a posição dos nós nem sai do servidor para quem não é o dono.

## Pendências

| Ponto | Depende de |
|---|---|
| Conteúdo das abas `Upgrades` e `Info`, que por enquanto não aparecem (pergunta 80) | Issue dos upgrades |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-12 11:58 | Tira o campo de nome do slot da Tela do Nó e o nome da linha aberta da Tela do Controlador (pergunta 277). |
| Zennd | 2026-11-12 11:11 | Adiciona o `Clear log` da aba `Log` (perguntas 264 e 265). |
| Zennd | 2026-11-12 09:22 | Descreve a aba `Log` da Tela do Controlador, da #90 (perguntas 242 a 248). |
| Zennd | 2026-11-11 17:05 | A direção e a face só mudam com o canal `Disabled` (pergunta 237). |
| Zennd | 2026-11-11 16:40 | Encurta as contagens do rodapé da Tela do Controlador (pergunta 236). |
| Zennd | 2026-11-11 16:45 | A linha aberta da Tela do Controlador mostra só os canais ativos, com o nome do slot (pergunta 234). |
| Zennd | 2026-11-11 16:40 | Redesenha a Tela do Nó com os 16 canais, o status e o nome do slot, e tira as abas laterais (perguntas 224 a 233). |
| Zennd | 2026-11-11 16:12 | O Configurator no modo `Open` também abre a Tela do Controlador (pergunta 232). |
| Zennd | 2026-11-11 15:47 | Aplica a pergunta 222: nós e canais são renomeados com duplo clique. |
| Zennd | 2026-11-11 15:40 | Descreve a Tela do Controlador entregue na #20, com a imagem nova e as perguntas 212 a 221. |
| Zennd | 2026-11-11 14:09 | Adiciona o estado `Offline` na pílula da Tela do Nó, da #88. |
| Zennd | 2026-11-11 13:46 | Descreve a escolha de rede, o nome do nó e o link da rede entregues na #87 (perguntas 198 a 208). |
| Zennd | 2026-11-11 10:24 | Aplica a pergunta 179. |
| Zennd | 2026-11-11 09:51 | Aplica a abertura pelo Configurator, a escolha de rede, o nome do nó, a lista de nós e o log no lugar do gráfico (perguntas 139 a 160). |
| Marcelo Fabiano | 2026-11-10 11:26 | Descreve o comportamento da Tela do Nó com as perguntas 78 a 85. |
| Marcelo Fabiano | 2026-11-09 15:39 | Aplica as perguntas 22 e 29 e aponta as abas `Signals` e `Access` para a página da rede. |
| Marcelo Fabiano | 2026-11-09 11:03 | Aplica as respostas das perguntas 13 e 15. |
| Marcelo Fabiano | 2026-11-09 11:20 | Descreve as telas do nó e do controlador. |
