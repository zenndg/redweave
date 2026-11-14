# Weather Detector

O Weather Detector (`redweave:weather_detector`, `Detector de Clima` em português) é um dos [Módulos de Sinal](modulos-de-sinal.md) e uma fonte: uma placa sem entrada de sinal, que liga a saída conforme o clima da dimensão onde está (perguntas 297, 302, 457 e 469). Esta página descreve só o que é do detector, entregue na #108. O que todo módulo herda, como a placa, o dono, a rede, as conexões e o Configurator, está em [`modulos-de-sinal.md`](modulos-de-sinal.md). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

No modo escolhido, `Rain`, `Thunder` ou `Clear`, a saída emite 15 enquanto o clima da dimensão bate com o modo e 0 quando não bate, e numa dimensão sem clima fica sempre em 0 (perguntas 458 a 460 e 464).

## Conexões

| Conexão | Borda fixa | Pode ser canal | Pergunta |
|---|---|---|---|
| `Output` | Frente | Sim | 461 |

A trás e as laterais ficam livres. A saída é `Edge` ou `Channel` (pergunta 376).

## Regra

| Ponto | Como é | Pergunta |
|---|---|---|
| Leitura | O clima da dimensão da placa (`isRaining` e `isThundering`), que vale em qualquer lugar, até no subsolo e dentro da base, sem depender de céu aberto nem do bioma | 458 |
| Modos | `Rain` liga com chuva ou tempestade; `Thunder`, só com tempestade; `Clear`, com céu limpo | 459 |
| Força | Binária: 15 quando o modo bate e 0 quando não bate | 460 |
| Sem clima | Numa dimensão sem luz do céu (`hasSkyLight` falso), como o Nether e o End, onde o jogo não faz o clima avançar, a saída fica em 0 em todos os modos | 464 |
| Frequência | Lê o clima a cada tick, sem chave no config; a rede só é avaliada quando a saída muda (regra 18) | 468 |
| Ao ser colocado | No modo `Rain`, com a saída na borda da frente. Não tem estado de ligado: emite já no primeiro tick, conforme o clima | 463 |
| `Toggle` | Troca o modo, `Rain` → `Thunder` → `Clear` → `Rain`, com `<nome>: <modo>` na action bar, como `<nome>: Thunder`; só o dono | 462 |
| Rede | Como os outros módulos: fora do alcance, numa rede que não está `Online` ou num chunk descarregado, o canal recebe 0, e com a rede apagada a saída fica sem canal até o dono escolher outro. A borda continua funcionando | 466 |

## Estados

A tela e o rótulo mostram o estado da saída (perguntas 417 e 465):

| Estado | Quando | Em português |
|---|---|---|
| `On` | A saída emite 15 | `Ligado` |
| `Off` | A saída está em 0 | `Desligado` |
| `No weather` | Numa dimensão sem clima | `Sem clima` |

O rótulo fica como `On · 15 Red`, e o modo aparece na tela.

## Modos

| Modo | Liga com | Em português | Símbolo na placa |
|---|---|---|---|
| `Rain` | Chuva ou tempestade | `Chuva` | Nuvem com gota |
| `Thunder` | Só tempestade | `Tempestade` | Raio |
| `Clear` | Céu limpo | `Céu limpo` | Sol |

## Tela

Abre com o Configurator no `Open` (pergunta 393). Como a do Signal Inverter, com as partes comuns de [`modulos-de-sinal.md`](modulos-de-sinal.md#tela-comum) (pergunta 470):

| Parte | Como é |
|---|---|
| Topo | `Weather Detector` e o nome do módulo, renomeável pelo dono |
| Esquerda | Um ícone grande do clima atual, um sprite de 32 × 32 (sol, nuvem com chuva, nuvem com raio ou nuvem apagada sem clima), e, embaixo, a pílula do estado (pergunta 474) |
| Direita | `Mode`, com `Rain`, `Thunder` e `Clear`, e `Output`, com `Edge` ou `Channel` e a grade de 16 cores |
| Rodapé | A rede e, para quem não é o dono, o cadeado |

O desenho está em [`telas.md`](telas.md#tela-do-weather-detector), com a prancha `_docs/imagens/telas/tela-do-weather-detector.png`.

## No mundo

| Ponto | Como é | Pergunta |
|---|---|---|
| Textura | A seta centrada, para a frente, vermelha com a saída ligada, e no meio o símbolo do modo | 471 |
| Estados do bloco | `facing`, `front`, `powered` e `mode` (`rain`, `thunder` ou `clear`), fixados na #26 (regra 15) | 471 |
| Modelo | Como o dos outros módulos: o topo girado 0° ou 90° no UV cobre as 24 orientações | 429 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save) (pergunta 467):

| Chave | O que guarda |
|---|---|
| `mode` | O modo: `rain`, `thunder` ou `clear` |
| `output` | A conexão `Output`: `edge` ou a chave da cor do canal |

O clima não é salvo: é lido de novo no primeiro tick. Um `mode` danificado volta a `rain`, com um aviso no log (regra 14).

## Config

Nenhuma chave nova: o detector não tem número a ajustar (pergunta 468).

## Arte

Em `tmp/arte/weather_detector/`: as 6 texturas do topo (3 modos, com a seta apagada ou acesa) e o ícone do item. Em `tmp/arte/weather_detector_screen/`: os 4 ícones do clima da tela, de 32 × 32 (pergunta 474). Em `tmp/arte/tela_do_weather_detector/`: a prancha da tela. O agente escreve os `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere antes de levá-las ao mod (perguntas 185, 186, 470 e 471).

## Testes

- **JUnit:** a regra, os 3 modos com cada clima e o `No weather`.
- **GameTests:** os que mudam o clima, que é global ao mundo, ficam num lote (`batch`) próprio, para não afetar os testes que rodam em paralelo; os demais cobrem colocar e emitir, o `Toggle`, o dono, o NBT, a saída num canal e o menu.
- **Roteiro manual:** a tela, a arte, os textos e o Nether (pergunta 472).

## Fora desta página

- A receita, na #24, e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 17:10 | Aponta para a prancha da tela no `telas.md` (pergunta 470). |
| Zennd | 2026-11-14 16:55 | Desenha o ícone do clima com sprites (pergunta 474). |
| Zennd | 2026-11-14 16:40 | Cria o documento, com as decisões 457 a 473 da #108. |
