# Lâmpada

O Signal Lamp (`redweave:signal_lamp`, `Lâmpada de Sinal` em português) é uma barra de LED do multipart que acende na cor do seu canal, com a intensidade da força recebida, e emite a força escolhida no bloco de apoio (pergunta 650). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #143, com as perguntas 650 a 658 e 715 a 721 respondidas.

## Em uma frase

A lâmpada escuta um único canal, tem sempre a cor dele, acende no nível de luz da força recebida, sem piscar, e acesa emite a força escolhida na tela (perguntas 650 e 651).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Lamp`, `redweave:signal_lamp`, `Lâmpada de Sinal` em português | 650 |
| Canal e cor | Um único canal de intensidade; a cor da lâmpada é sempre a cor desse canal | 650 |
| Luz | A força recebida é o nível de luz, de 0 a 15. A luz no mundo é branca, porque o jogo não tem luz colorida; a cor fica só na lâmpada, numa textura emissiva tingida. É estática: só muda quando o sinal muda. Fica na propriedade `light` do estado do `part_space` (veja [Luz no mundo](#luz-no-mundo)) | 650 e 715 |
| Força emitida | Acesa, emite a força escolhida na tela, de 1 a 15 | 651 |
| Forma | Uma parte do multipart, como o Signal Node: uma barra de LED de 12 × 4 px e 2 de espessura, centrada na face, no sentido de onde o player olha; o `Rotate` a gira 90° | 652 |
| Saída | Como o Signal Node: com força, só no bloco de apoio, e com o cabo e o pó da mesma face ao lado | 262, 574 e 653 |
| Rede | Fora do alcance, com a rede `Offline` ou sem rede, apaga e não emite, com a cor escura; o rótulo mostra `Offline` ou `Out of range` | 654 |
| Contador da rede | Não entra no `modules on` do rodapé da Tela do Controlador: a lâmpada só lê canais, como a entrada de um nó | 717 |
| Desempenho | O nível de luz muda no máximo a cada `lamp.min_light_update_ticks`, em [`limites.md`](../limites.md); o sinal emitido segue sem atraso | 655 |
| Controle | Um segundo canal opcional, da mesma rede e de qualquer cor, com o modo escolhido pelo dono: `Hold` (com sinal no controle, a lâmpada fica desligada) ou `Pulse` (cada pulso alterna). Padrão `Hold` | 656 |
| Desligada pelo controle | Apaga e não emite, com a cor escura e um indicador vermelho; o rótulo mostra `Off` | 657 |
| `Toggle` | Liga e desliga à mão, como um pulso no controle, com `<nome>: On` ou `<nome>: Off`. Funciona mesmo com a rede `Offline`, como o Signal Lock; a luz só acende quando a rede voltar | 658 e 716 |
| Padrões | Sem canal de intensidade (apagada, difusor cinza), sem controle, força 15, modo `Hold` | 719 |
| Rótulo | `Offline` ou `Out of range`, como o nó; nos outros casos, `On` ou `Off` e o canal de intensidade | 721 |
| Dono e Configurator | Como os Signal Modules: dono, rede, `Open`, `Rotate` e `Wrench` | 658 |

Os números ficam em [`limites.md`](../limites.md).

## Luz no mundo

O motor de luz do jogo roda na sua própria thread e não consegue ler o block entity do espaço, então não enxerga os dados da parte. Por isso a luz fica no estado do bloco: o `part_space` tem uma propriedade `light`, de 0 a 15, com a luz mais forte das partes dele, que o jogo lê e manda aos clientes sozinho (pergunta 715).

| Ponto | Como é | Pergunta |
|---|---|---|
| Quando muda | Só quando o valor muda, e no máximo a cada `lamp.min_light_update_ticks`; o sinal emitido segue sem atraso | 655 e 715 |
| Custo | Com o sinal parado, nada além do tick do módulo. Com o sinal mudando, no máximo uma mudança de estado e uma reconferência de luz por janela de `lamp.min_light_update_ticks` (regra 18) | 715 |
| Ao retirar | A luz da lâmpada sai do mundo na hora, mesmo com outras partes no espaço | 650 |
| Save | A propriedade `light` é contrato de save, fixada na #26 (regra 15). Mundos anteriores carregam com 0 | 715 |

## Tela

A tela, com o estado e a luz à esquerda e a força, a intensidade, o controle e o modo à direita, está em [`telas.md`](telas.md#tela-do-signal-lamp). Em português, `Lâmpada de Sinal`, `Ligada` e `Desligada`, `Luz`, `Força`, `Intensidade`, `Controle`, `Modo`, `Manter`, `Pulso` e `Nenhum` (pergunta 720).

## Mensagens

| Ação | Mensagem |
|---|---|
| `Toggle` | `<nome>: On` ou `<nome>: Off` (pergunta 658) |
| Mão vazia | `Use a Configurator to open this module.`, como os outros módulos |

## Save

Além das chaves comuns dos módulos, no NBT da parte (pergunta 718). Ficam fixadas na #26 (regra 15):

| Chave | O que guarda | Valor inválido |
|---|---|---|
| `strength` | A força emitida acesa, de 1 a 15 | 15, com aviso no log |
| `mode` | `hold` ou `pulse` | `hold`, com aviso no log |
| `off` | Se o interruptor manual está desligado | — |
| `last_control` | O controle do tick anterior, para ver só o começo de um pulso | — |
| `light` | O nível de luz que a lâmpada dá ao mundo, que o cliente também desenha | 0, com aviso no log |
| `intensity` | A cor do canal de intensidade; ausente sem canal | Sem canal, com aviso no log |
| `control` | A cor do canal de controle; ausente sem canal | Sem canal, com aviso no log |

## Desenho

| Ponto | Como é | Pergunta |
|---|---|---|
| Barra | Um trilho de 12 × 4 × 1 px, comprido ao longo da frente, com um difusor de 10 × 2 × 1 px em cima, emissivo | 652 |
| Cor | O difusor é tingido com a cor do canal de intensidade: de 40% a 100% do brilho conforme a luz, 25% apagado, e cinza sem canal. A cor vem só do que o cliente recebe, o canal e a `light` | 721 |
| Indicador | Desligada pelo controle ou pelo `Toggle`, o modelo `_off` mostra o indicador vermelho de 1 × 1 × 1, a luz do Signal Lever | 657 |
| Mira | A barra, comprida ao longo da frente; o `Rotate` a gira 90° | 652 |

## Código

| Camada | Classe | O que faz |
|---|---|---|
| Domínio | `SignalLamp`, `LampMode` | A regra: a luz, a saída, o controle `Hold` e `Pulse` e o `Toggle` |
| Aplicação | `ChangeLamp` | Só o dono muda a força e o modo e usa o `Toggle` |
| Config | `NeoForgeLampLimits` | `lamp.min_light_update_ticks` |
| Parte | `LampPart`, `LampSave`, `LampShapes` | Lê os dois canais, emite no bloco de apoio e no cabo ou pó ao lado pelo `ModuleTicks.give`, e põe a luz no estado pelo `PartSpaceBlockEntity.refreshLight` |
| Bloco | `PartSpaceBlock` | A propriedade `light` e a luz que o estado dá ao mundo |
| Menu | `LampMenu`, `LampAction`, `LampScreenActions`, `LampScreenData`, `LampScreenLayout`, `LampScreenTexts` | A tela, conferida no servidor |
| Cliente | `LampScreen`, `LampColors` | A tela e a cor do difusor |
| Datagen | `LampModels`, `LampEnglishTexts`, `LampPortugueseTexts` | Os modelos e os textos |

A saída segue a do Signal Node: com força, só no bloco de apoio (pergunta 262), e no cabo ou no pó ao lado, na mesma face (pergunta 574). A lâmpada nunca escreve num canal.

## Arte

A arte seguiu as perguntas 185 e 186: o prompt em `tmp/arte/signal_lamp/`, com a carcaça `signal_lamp_body.png` e o difusor em cinza `signal_lamp_glow.png`, que o jogo tinge com a cor do canal, como o Signal Cable.

## Testes

- **JUnit:** `SignalLampTest` e `ChangeLampTest`, com a intensidade, o controle `Hold` e `Pulse`, o `Toggle`, a saída e o dono.
- **GameTests:** `LampGameTests`, com a luz no mundo e a saída no bloco de apoio, a rede `Offline`, o controle `Hold` e `Pulse`, o `Toggle` também com a rede `Offline` (pergunta 716), outro player e o save, com a cópia do cliente desenhando a lâmpada igual ao servidor.
- **Roteiro manual:** os passos 315 a 330, com a cor, a luz à noite e o desempenho (pergunta 658).

## Fora desta página

- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 758).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 758). |
| Zennd | 2026-11-16 13:19 | Marca o Signal Lamp como implementado na #143: a luz no estado do bloco, o `Toggle` offline, o contador, o save, os padrões, os textos, o rótulo, a tela, o desenho e o código (perguntas 715 a 721). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 650 a 658. |
