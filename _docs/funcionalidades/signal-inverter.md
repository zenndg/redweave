# Signal Inverter

O Signal Inverter (`redweave:signal_inverter`, `Inversor de Sinal` em português) é o segundo dos [Módulos de Sinal](modulos-de-sinal.md) e o NOT do mod: uma placa que, como a tocha de redstone, desliga a saída quando a entrada recebe sinal e a liga quando ela não recebe (perguntas 296, 300, 310 e 447). Esta página descreve só o que é do inversor, entregue na #105. O que todo módulo herda, como a placa, o dono, a rede, as conexões e o Configurator, está em [`modulos-de-sinal.md`](modulos-de-sinal.md). A regra NOT está em [`portas-logicas.md`](portas-logicas.md), e os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Sem sinal na entrada A, a saída B emite a força escolhida, 15 por padrão; com qualquer sinal em A, B fica em 0, com o atraso da tocha e mais o delay escolhido, e um laço que oscila queima, como ela (perguntas 296, 444, 445, 833 e 834).

## Conexões

| Conexão | Borda fixa | Pode ser canal | Pergunta |
|---|---|---|---|
| `Input` (A) | Trás | Sim | 296 e 441 |
| `Output` (B) | Frente | Sim | 296 e 441 |

As laterais ficam livres. Cada conexão é `Edge` ou `Channel` (pergunta 376).

## Regra

| Ponto | Como é | Pergunta |
|---|---|---|
| Resultado | Binário, pela regra NOT do domínio (#28): A acima de 0 dá 0 em B, e A em 0 dá a força | 296, 310 e 834 |
| Força | De 1 a 15, com `−` e `+` na tela, 15 por padrão, como nos emissores. Mudar a força muda B na hora, também com mudanças esperando na linha | 834 e 843 |
| Atraso | 2 ticks, como a tocha: a mudança em A aparece em B dois ticks depois, pela borda ou pelo canal, que já lê o valor do tick anterior | 444 e 446 |
| Delay | De 0 a 15 s, em segundos inteiros, somado ao atraso da tocha: com 0 s, o inversor fica como antes da #171. Vale ao ligar e ao desligar, e um pulso curto passa inteiro. As regras comuns estão em [`modulos-de-sinal.md`](modulos-de-sinal.md#delay-na-saída) | 830 a 833 e 839 |
| Ao ser colocado | Emite na hora, como a tocha: sem sinal em A, B emite a força, 15 num inversor novo, sem esperar o delay. O inversor não tem estado de ligado ou parado | 442 e 834 |
| Queima | Mais de `inverter.burnout_toggles` trocas de B em `inverter.burnout_window_ticks` queimam o inversor: B vai a 0 e fica assim por `inverter.burnout_ticks`, e volta sozinho. A troca conta quando entra na linha de atraso, antes do delay, então um laço queima tão rápido quanto sem delay, e queimar esvazia a linha. Protege o tick do servidor de um laço e limita a linha (pilar Defesa e regra 18) | 445, 449 e 835 |
| `Toggle` | Nada: a action bar mostra `Signal Inverter has nothing to toggle.` (`O Inversor de Sinal não tem o que alternar.`) | 443 e 447 |

## Estados

A tela e o rótulo mostram o estado da saída B (perguntas 417 e 448):

| Estado | Quando | Em português |
|---|---|---|
| `On` | B emite a força | `Ligado` |
| `Off` | B está em 0 | `Desligado` |
| `Burnt out` | Queimado, até o fim de `inverter.burnout_ticks` | `Queimado` |

## Tela

Abre com o Configurator no `Open` (pergunta 393). Como a do timer, sem o relógio, com as partes comuns de [`modulos-de-sinal.md`](modulos-de-sinal.md#tela-comum) (pergunta 451):

| Parte | Como é |
|---|---|
| Topo | `Signal Inverter` e o nome do módulo, renomeável pelo dono |
| Esquerda | Um diagrama grande A → NOT → B, com o estado de cada lado |
| Direita | `Strength` e `Delay`, cada um entre `−` e `+`, e `Input` e `Output`, cada uma com `Edge` ou `Channel`, e a grade de 16 cores |
| Rodapé | A rede e, para quem não é o dono, o cadeado |

O desenho está em [`telas.md`](telas.md#tela-do-signal-inverter).

## No mundo

| Ponto | Como é | Pergunta |
|---|---|---|
| Textura | A seta centrada, para a frente, o entalhe de A atrás e, no meio, o símbolo do NOT, um triângulo com um círculo na ponta | 452 |
| Campos da parte | `facing`, `front`, `powered`, que acende a seta em vermelho com B ligado, e `burnt`, que troca a seta pela cinza-escura. Com `burnt`, `powered` fica falso. Desde a #44, são campos da parte, não estados do bloco: `facing` sai da `face` da parte no contêiner, `front` e `powered` ficam no save, e `burnt` sai do tempo de queima salvo (perguntas 534, 559 e 560) | 452 e 455 |
| Modelo | Como o do timer: o topo girado 0° ou 90° no UV cobre as 24 orientações | 429 |

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save) (pergunta 450):

| Chave | O que guarda |
|---|---|
| `input` | A conexão `Input`: `edge` ou a chave da cor do canal |
| `output` | A conexão `Output`: `edge` ou a chave da cor do canal |
| `burnout_ticks` | Os ticks que faltam de queima, para um reinício não soltar um laço queimado |
| `strength` | A força, de 1 a 15; ausente nos saves antigos, que abrem com 15 (pergunta 834) |
| `output_delay_ticks` | O delay em ticks; ausente nos saves antigos, que abrem com 0 (pergunta 844) |
| `delay_line` | As mudanças que esperam o delay, cada uma `{on, ticks}`, com os ticks que faltam (perguntas 836 e 844) |

O histórico de trocas do burnout não é salvo. Os 2 ticks da tocha também não: ao carregar, a entrada é lida de novo, e a linha de atraso continua de onde parou (muda a pergunta 450 pela 836).

## Config

| Chave | Valor | Pergunta |
|---|---|---|
| `inverter.burnout_toggles` | 8, de 2 a 64 | 449 |
| `inverter.burnout_window_ticks` | 60, de 20 a 1200 | 449 |
| `inverter.burnout_ticks` | 160, de 20 a 6000 | 449 |
| `module.max_output_delay_ticks` | 300 (15 s), de 0 a 1200, comum aos módulos com delay | 837 |

Os valores ficam em [`limites.md`](../limites.md).

## Arte

Em `tmp/arte/signal_inverter/`: as 3 texturas do topo (apagado, ligado e queimado) e o ícone do item. Em `tmp/arte/tela_do_signal_inverter/`: a prancha da tela. O agente escreve os `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere antes de levá-las ao mod (perguntas 185, 186, 451 e 452).

## Testes

- **JUnit:** a regra (NOT, atraso de 2 ticks, queima com a janela e a recuperação, limites), a força, o delay somado, o pulso curto, a mudança do delay com pendentes, a queima que esvazia a linha e os casos de uso; `OutputDelay` e `DelayLine` a 100%.
- **GameTests:** colocar e emitir na hora, a entrada pela borda, a saída num canal lida por um nó, o laço que queima, o `Toggle` sem efeito, o dono e o NBT depois de salvar e carregar; a força, o delay no mundo, a linha salva e carregada, o save antigo com força 15 e delay 0, e a cópia do Configurator com o delay e a força.
- **Roteiro manual:** a tela, a arte, os textos e o chunk descarregado (pergunta 453).

## Fora desta página

- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 755), e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-17 13:15 | Inclui o delay na saída e a força ajustável, com a linha de atraso salva (perguntas 830 a 844, #171). |
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 755). |
| Zennd | 2026-11-15 09:35 | Leva o visual para os campos da parte no contêiner, sem estados do bloco (perguntas 534, 559 e 560). |
| Zennd | 2026-11-14 19:34 | Leva os estados do bloco para a parte (pergunta 534). |
| Zennd | 2026-11-14 15:10 | Aponta para a prancha da tela no `telas.md` (pergunta 451). |
| Zennd | 2026-11-14 14:31 | Cria o documento, com as decisões 441 a 455 da #105. |
