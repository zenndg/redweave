# Signal Randomizer

O Signal Randomizer (`redweave:signal_randomizer`, `Sorteador de Sinal` em português) é um dos [Módulos de Sinal](modulos-de-sinal.md) e uma fonte: uma placa que, a cada intervalo, sorteia um valor e o mantém na saída até o próximo sorteio (perguntas 297, 302, 475 e 486). No jogo puro, isso exige um circuito com dropper. Esta página descreve só o que é do Signal Randomizer, entregue na #109. O que todo módulo herda, como a placa, o dono, a rede, as conexões e o Configurator, está em [`modulos-de-sinal.md`](modulos-de-sinal.md). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Ligado, sorteia na hora e depois a cada intervalo um valor de 0 a 15 (`Analog`) ou 0 ou 15 (`Binary`), e mantém esse valor na saída até o próximo sorteio; parado, a saída fica em 0 (perguntas 476, 478, 480 e 482).

## Conexões

| Conexão | Borda fixa | Pode ser canal | Pergunta |
|---|---|---|---|
| `Output` | Frente | Sim | 479 |

A trás e as laterais ficam livres. A saída é `Edge` ou `Channel` (pergunta 376).

## Sorteio

| Ponto | Como é | Pergunta |
|---|---|---|
| Modos | `Analog` sorteia de 0 a 15, com a mesma chance para cada valor; `Binary` sorteia 0 ou 15, meio a meio. O modo é escolhido na tela | 476 |
| Entre sorteios | A saída mantém o valor sorteado até o próximo sorteio. Só muda no sorteio, e a rede só é avaliada quando o valor muda (regra 18) | 478 |
| Origem | Uma porta do domínio, que o adaptador implementa com o `RandomSource` do mundo, no servidor. O domínio não lê aleatoriedade, e os testes usam uma sequência fixa | 483 |
| Ao ligar | Sorteia na hora e recomeça o intervalo | 482 |
| Troca de modo, rodando | Sorteia na hora no modo novo e recomeça o intervalo. Parado, o modo só muda | 492 |

## Intervalo

| Ponto | Como é | Pergunta |
|---|---|---|
| Unidade e ajuste | Como o do timer: segundos com décimos na tela, em passos de 0,1 s; o clique em `−` ou `+` muda 0,1 s, e o Shift + clique muda 1 s. O servidor confere o valor e o limita à faixa (regra 16) | 398 e 477 |
| Faixa | De `randomizer.min_interval_ticks` a `randomizer.max_interval_ticks`, chaves próprias, com os padrões e as faixas das do timer | 477 |
| Regra comum | O ajuste à faixa e o passo são um value object comum ao timer e ao Signal Randomizer, cada um com os seus limites | 490 |
| Ajuste rodando | Mudar o intervalo recomeça o intervalo; o valor atual continua até o fim do intervalo novo, sem sorteio na hora | 493 |
| Config mudado | Ao carregar, um intervalo fora da faixa atual vai ao limite mais próximo, e o intervalo recomeça, com um aviso no log, como o timer | 425 e 490 |

## Controle

| Estado | Como se chega | A saída | Pergunta |
|---|---|---|---|
| `Stopped` | O botão da tela ou o `Toggle`. É o estado do Signal Randomizer recém-colocado | 0, e o progresso zera | 480 e 482 |
| `Running` | O botão da tela ou o `Toggle` | O valor sorteado | 480 |

O `Toggle` mostra `<nome>: Running` ou `<nome>: Stopped`, como o timer. Em português, os estados são `Rodando` e `Parado` (perguntas 433 e 486).

## Rede

Como os outros módulos: fora do alcance, numa rede que não está `Online` ou num chunk descarregado, o canal recebe 0, e com a rede apagada a saída fica sem canal até o dono escolher outro. O sorteio continua, e a borda continua funcionando (pergunta 485).

## Estados

A tela e o rótulo mostram `Running` ou `Stopped`, e o rótulo fica como `Running · 15 Red`. O valor atual aparece na tela (pergunta 484).

## Tela

Abre com o Configurator no `Open` (pergunta 393). Como a do timer, com as partes comuns de [`modulos-de-sinal.md`](modulos-de-sinal.md#tela-comum) (pergunta 487):

| Parte | Como é |
|---|---|
| Topo | `Signal Randomizer` e o nome do módulo, renomeável pelo dono |
| Esquerda | O valor atual grande, de 0 a 15, com a fonte ampliada como o tempo do relógio do timer, e o botão `● Running` ou `Stopped` |
| Direita | `Mode`, com `Analog` e `Binary`; `Interval`, com `−` e `+`; e `Output`, com `Edge` ou `Channel` e a grade de 16 cores |
| Rodapé | A rede e, para quem não é o dono, o cadeado |

O desenho está em [`telas.md`](telas.md#tela-do-signal-randomizer), com a prancha `_docs/imagens/telas/tela-do-signal-randomizer.png`.

## No mundo

| Ponto | Como é | Pergunta |
|---|---|---|
| Textura | A seta centrada, para a frente, vermelha com a saída acima de 0; um dado pequeno no meio; e o LED verde de `Running`, como o timer | 488 |
| Campos da parte | `facing`, `front`, `powered` e `running`, fixados na #26 (regra 15). Desde a #44, são campos da parte, não estados do bloco: `facing` sai da `face` da parte no contêiner, e os outros ficam no save, dentro de `data` (perguntas 534, 559 e 560) | 488 |
| Modelo | Como o dos outros módulos: o topo girado 0° ou 90° no UV cobre as 24 orientações | 429 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), o Signal Randomizer continua do ponto, como o timer (perguntas 381 e 481):

| Chave | O que guarda |
|---|---|
| `mode` | O modo: `analog` ou `binary` |
| `running` | Se está ligado |
| `interval_ticks` | O intervalo, em ticks |
| `elapsed_ticks` | O progresso do intervalo, em ticks |
| `value` | O valor sorteado que está na saída, de 0 a 15 |
| `output` | A conexão `Output`: `edge` ou a chave da cor do canal |

Num chunk descarregado, o Signal Randomizer congela e volta de onde parou, com o mesmo valor.

## Config

| Chave | Valor | Pergunta |
|---|---|---|
| `randomizer.min_interval_ticks` | 4, de 4 a 200 | 477 |
| `randomizer.max_interval_ticks` | 6000 (5 min), de 200 a 72000 (1 h) | 477 |

Os valores ficam em [`limites.md`](../limites.md).

## Arte

Em `tmp/arte/signal_randomizer/`: as 4 texturas do topo (a seta apagada ou acesa, com o LED apagado ou aceso) e o ícone do item. Em `tmp/arte/tela_do_signal_randomizer/`: a prancha da tela. O agente escreve os `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere antes de levá-las ao mod (perguntas 185, 186, 487 e 488).

## Testes

- **JUnit:** a regra, com a porta de sorteio fixa (os dois modos, o sorteio ao ligar, o intervalo, a saída mantida e parado em 0), o intervalo comum e o caso de uso do dono.
- **GameTests:** colocar parado, o `Toggle` ligando e sorteando, a saída num canal, o dono, o NBT e o menu.
- **Roteiro manual:** a tela, a arte, os textos e a variação real do sorteio (pergunta 489).

## Fora desta página

- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 757), e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 757). |
| Zennd | 2026-11-15 09:35 | Leva o visual para os campos da parte no contêiner, sem estados do bloco (perguntas 534, 559 e 560). |
| Zennd | 2026-11-14 19:34 | Leva os estados do bloco para a parte (pergunta 534). |
| Zennd | 2026-11-14 18:16 | Aponta para a prancha da tela no `telas.md` (pergunta 487). |
| Zennd | 2026-11-14 17:46 | Cria o documento, com as decisões 475 a 493 da #109. |
