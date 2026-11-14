# Signal Timer

O Signal Timer (`redweave:signal_timer`, `Temporizador de Sinal` em português) é o primeiro dos [Módulos de Sinal](modulos-de-sinal.md): uma placa que liga a saída por um pulso a cada intervalo (perguntas 292, 293, 300 e 410). Esta página descreve só o que é do timer, entregue na #103. O que todo módulo herda, como a placa, o dono, a rede, as conexões e o Configurator, está em [`modulos-de-sinal.md`](modulos-de-sinal.md). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

A cada intervalo, o timer liga a saída por um pulso, pela borda da frente ou num canal, e para enquanto a entrada `Pause` recebe sinal (perguntas 379, 380 e 383).

## Conexões

| Conexão | Borda fixa | Pode ser canal | Pergunta |
|---|---|---|---|
| `Output` | Frente | Sim | 383 |
| `Pause` | Trás | Sim | 380 e 383 |

As laterais ficam livres. Cada conexão é `Edge` ou `Channel` (pergunta 376).

## Ciclo

| Ponto | Como é | Pergunta |
|---|---|---|
| Forma | Intervalo e pulso: a cada X, liga por Y, com o pulso sempre menor que o intervalo | 379 |
| Começo | Do zero, com o pulso primeiro: o timer liga por Y logo no início e espera o resto do intervalo, então o primeiro disparo é imediato | 408 |
| Unidade | Segundos com décimos na tela, como `1.5 s`, em passos de 0,1 s, ou 2 ticks. O domínio e o save guardam ticks inteiros, e um tooltip mostra os ticks | 382 |
| Ajuste | Botões `−` e `+`: o clique muda 0,1 s, e o Shift + clique muda 1 s. O servidor confere o valor e o limita à faixa (regra 16) | 398 |
| Faixas | O intervalo vai de `timer.min_interval_ticks` a `timer.max_interval_ticks`, e o pulso, de 2 ticks até o intervalo menos 2 | 384 |
| Ajuste com o timer rodando | Mudar o intervalo ou o pulso recomeça o ciclo. Um pulso que não cabe no intervalo novo é reduzido para o intervalo menos 2 ticks | 409 |
| Config mudado | Ao carregar, um intervalo fora da faixa atual vai ao limite mais próximo, o pulso passa a caber, e o ciclo recomeça, com um aviso no log | 425 |

## Controle

| Estado | Como se chega | A saída | Pergunta |
|---|---|---|---|
| `Stopped` | O botão da tela ou o `Toggle`. É o estado do timer recém-colocado | 0, e o progresso zera | 380, 390 e 395 |
| `Running` | O botão da tela ou o `Toggle` | Segue o ciclo | 380 e 395 |
| `Paused` | `Running` com a entrada `Pause` acima de 0 | 0, e o progresso zera. Quando o `Pause` solta, o ciclo recomeça do zero, com o pulso | 380, 408 e 412 |

Um `Pause` lido de um canal fora do alcance ou numa rede que não está `Online` vale 0 (pergunta 385).

Em português, os estados são `Rodando`, `Parado` e `Pausado` na tela e no rótulo, e em minúsculas na action bar, como `<nome>: rodando` (pergunta 433).

## Ao ser colocado

O timer nasce `Stopped`, com a saída na borda da frente, o `Pause` na de trás, intervalo de 1 s e pulso de 0,5 s, sem rede (pergunta 390).

## Descarregado e reiniciado

O progresso do ciclo e o estado ficam no NBT do bloco. Num chunk descarregado, o timer congela, porque o bloco não recebe tick, e volta exatamente de onde parou; ao reiniciar o servidor, também. Enquanto está descarregado, um timer com a saída num canal vale 0 nele (perguntas 293, 381 e 386).

## Tela

Abre com o Configurator no `Open` (pergunta 393). As partes comuns estão em [`modulos-de-sinal.md`](modulos-de-sinal.md#tela-comum), e o desenho, em [`telas.md`](telas.md#tela-do-signal-timer).

| Parte | Como é | Pergunta |
|---|---|---|
| Topo | O nome, renomeável como na Tela do Nó | 404 |
| Esquerda | O relógio moderno, com o anel de progresso que enche no intervalo e o arco que marca o pulso, e embaixo o botão `Running` ou `Stopped` | 293 e 404 |
| Direita | `Interval` e `Pulse`, com `−` e `+`, e as conexões `Output` e `Pause`, com `Edge` ou `Channel` | 404 |
| Rodapé | A rede e, para quem não é o dono, o cadeado | 404 |

## No mundo

| Ponto | Como é | Pergunta |
|---|---|---|
| Textura | A seta da saída, para a frente, e o mostrador do relógio | 401 |
| LEDs | Dois, por estados do bloco: `powered` acende no pulso, e `running` marca o timer rodando | 401 |
| Modelo | O blockstate só gira em `x` e `y`, então cada combinação dos LEDs tem duas versões, com o topo girado 0° ou 90° no UV, para cobrir as 24 orientações de `facing` e `front` | 429 |
| Animação | Só na tela. A placa não tem renderer próprio, para muitos timers não custarem desempenho no cliente | 401 |
| Rótulo | Com o Configurator na mão: o nome e, embaixo, `Running`, `Stopped` ou `Paused`, e o canal da saída, se houver | 417 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save) (pergunta 411):

| Chave | O que guarda |
|---|---|
| `running` | Se o timer está `Running` |
| `interval_ticks` | O intervalo, em ticks |
| `pulse_ticks` | O pulso, em ticks |
| `elapsed_ticks` | O progresso do ciclo, em ticks |
| `output` | A conexão `Output`: `edge` ou a chave da cor do canal |
| `pause` | A conexão `Pause`: `edge` ou a chave da cor do canal |

Os estados do bloco são `facing`, `front`, `powered` e `running` (pergunta 428).

## Config

| Chave | Valor | Pergunta |
|---|---|---|
| `timer.min_interval_ticks` | 4, de 4 a 200 | 384 |
| `timer.max_interval_ticks` | 6000 (5 min), de 200 a 72000 (1 h) | 384 |

Atrasos maiores ficam com o módulo de atraso longo (#116). Os valores ficam em [`limites.md`](../limites.md).

## Arte

Em `tmp/arte/signal_timer/`: a textura da placa de 16 px, nas 4 combinações de `powered` e `running`, e o ícone do item, que é a placa. Em `tmp/arte/tela_do_signal_timer/`: a prancha da tela. O agente escreve os `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere antes de levá-las ao mod (perguntas 185, 186 e 418).

## Testes

- **JUnit:** o ciclo (intervalo, pulso, `Pause`, `Running`, faixas e passo de 2 ticks).
- **GameTests:** a saída pela borda, o `Pause` pela borda, a saída num canal lida por um nó, o `Toggle` e o NBT depois de salvar e carregar.
- **Roteiro manual:** a tela, a arte, os textos e o chunk descarregado (pergunta 406).

## Fora desta página

- A receita, na #24, e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 12:35 | Adiciona os estados em português (pergunta 433). |
| Zennd | 2026-11-14 12:08 | Adiciona o modelo nas 24 orientações (pergunta 429). |
| Zennd | 2026-11-14 11:13 | Troca `rotation` por `front` (pergunta 428). |
| Zennd | 2026-11-14 10:24 | Adiciona o ajuste à faixa ao carregar (pergunta 425). |
| Zennd | 2026-11-14 09:46 | Cria o documento, com as decisões 379 a 418 da #103. |
