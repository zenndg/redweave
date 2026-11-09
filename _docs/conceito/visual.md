# Visual

A identidade do Redweave e o conceito visual dos blocos, itens e telas. As imagens ficam em [`_docs/imagens/`](../imagens/), em PNG e SVG. Tudo aqui é conceito: as texturas do jogo são de 16×16 e são feitas a partir destas imagens.

![Identidade visual](../imagens/identidade/identidade-visual.png)

## Direção

- **Materiais do jogo puro:** deepslate polido no corpo, cobre nas molduras e redstone no que está ativo. Os blocos e itens parecem da mesma família e combinam com o Iron Meridian.
- **O sinal sempre visível:** o que está ligado acende em vermelho, com brilho, e a força aparece num medidor de 15 barras (pilar 1 de [`ux.md`](ux.md)).
- **Canais pelas cores das lãs:** as 16 cores que o player já conhece no jogo.
- **Telas da família do Cubit:** metal escuro com chanfro, sem a textura do baú, para os dois mods conversarem no mesmo pack.

## Logo

| Versão | Arquivo |
|---|---|
| Logo para fundo escuro | [`logo-escuro.png`](../imagens/identidade/logo-escuro.png) |
| Logo para fundo claro | [`logo-claro.png`](../imagens/identidade/logo-claro.png) |
| Logo em vetor | [`svg/logo.svg`](../imagens/identidade/svg/logo.svg) |
| Símbolo em vetor | [`svg/simbolo.svg`](../imagens/identidade/svg/simbolo.svg) |
| Símbolo em 512 px | [`simbolo-512.png`](../imagens/identidade/simbolo-512.png) |

- **Símbolo:** dois fios que se cruzam e ligam quatro nós. O de redstone passa por cima do de cobre, como num tecido, que é o que o nome diz.
- **Palavra:** **Red** em vermelho e **weave** em branco, ou em grafite no fundo claro, em Rajdhani 700.
- **Lema:** *Weave your redstone.*
- **Uso:** README, páginas do CurseForge e do Modrinth e o livro guia. O `logo.svg` usa a fonte Rajdhani: para uma imagem que não dependa da fonte instalada, use os PNGs.

## Ícone do mod

| Tamanho | Arquivo | Uso |
|---|---|---|
| 128 px | [`icone-128.png`](../imagens/identidade/icone-128.png) | Ícone do projeto no CurseForge e no Modrinth |
| 64 px | [`icone-64.png`](../imagens/identidade/icone-64.png) | Versão sem os nós, para tamanhos médios |
| 32 px | [`icone-32.png`](../imagens/identidade/icone-32.png) | Só os dois fios |
| 16 px | [`icone-16.png`](../imagens/identidade/icone-16.png) ([ampliado](../imagens/identidade/icone-16-ampliado.png)) | Lista de mods do jogo e base do item do livro guia, desenhado pixel a pixel |

O símbolo perde detalhe conforme diminui: os nós somem em 64 px, e em 16 px ele vira um desenho em pixels, não uma redução.

## Tipografia

| Papel | Fonte | Onde |
|---|---|---|
| Logo e títulos | Rajdhani 700 | Logo, README, páginas das plataformas, pranchas de conceito |
| Texto | IBM Plex Sans | Documentos e pranchas |
| Dados | IBM Plex Mono | IDs e números nas pranchas |
| Dentro do jogo | A fonte do Minecraft | Telas, tooltips e textos. Nas pranchas, a VT323 a simula |

## Paleta

![Paleta e canais](../imagens/conceito/paleta-e-canais.png)

| Material | Cor | Uso |
|---|---|---|
| Deepslate polido | `#3A3E45` | Corpo dos blocos e itens |
| Cobre | `#C27B4C` | Molduras, cabos e botões |
| Redstone | `#E2392F` | Sinal ativo, núcleo e lentes |
| Âmbar | `#E3A33B` | Nó fora de alcance, se a pergunta 13 confirmar |
| Tela | `#0E1116` | Visores e painéis de LED |

## Canais

As 16 cores das lãs do jogo, na ordem do jogo. O número é o do canal, de 01 a 16. Nas telas, o nome aparece em inglês.

| Canal | Cor | Nome no jogo | Hex |
|---|---|---|---|
| 01 | Branco | White | `#F9FFFE` |
| 02 | Laranja | Orange | `#F9801D` |
| 03 | Magenta | Magenta | `#C74EBD` |
| 04 | Azul-claro | Light Blue | `#3AB3DA` |
| 05 | Amarelo | Yellow | `#FED83D` |
| 06 | Lima | Lime | `#80C71F` |
| 07 | Rosa | Pink | `#F38BAA` |
| 08 | Cinza | Gray | `#474F52` |
| 09 | Cinza-claro | Light Gray | `#9D9D97` |
| 10 | Ciano | Cyan | `#169C9C` |
| 11 | Roxo | Purple | `#8932B8` |
| 12 | Azul | Blue | `#3C44AA` |
| 13 | Marrom | Brown | `#835432` |
| 14 | Verde | Green | `#5E7C16` |
| 15 | Vermelho | Red | `#B02E26` |
| 16 | Preto | Black | `#1D1D21` |

O canal 15 usa o vermelho da lã. O vermelho de redstone, `#E2392F`, fica para o brilho do sinal, para os dois não se confundirem.

## Sinal

O sinal vai de 0 a 15 (regra 5 do `AGENTS.md`) e aparece como um medidor de 15 barras, que crescem em altura e intensidade. Sinal 0 fica apagado. A partir de 12, as barras brilham. O medidor aparece no Network Controller, no Configurator e nas telas.

## Elementos de interface

| Elemento | Como é |
|---|---|
| Estado | Etiqueta com ponto colorido: `Online` em verde, `Out of range` em âmbar e `No network` em cinza |
| Canal | Quadrado na cor do canal, com o número e o nome em inglês, como `15 Red` |
| Sinal | O medidor de 15 barras e o número |
| Direção | Seletor de duas opções, `Input` e `Output`, com a escolhida em cobre |
| Botões | Principal em cobre, secundário em metal escuro, os dois com sombra interna embaixo |
| Janela | Metal escuro com chanfro claro em cima e à esquerda e escuro embaixo e à direita, como no Cubit |
| Abas | À direita da janela, como estado só do cliente |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 11:10 | Adiciona a identidade visual, a paleta, os canais e os elementos de interface. |
