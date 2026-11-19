# Signal Flip-Flop

O Signal Flip-Flop (`redweave:signal_flip_flop`, `Flip-Flop de Sinal` em português) é o módulo de memória dos [Módulos de Sinal](modulos-de-sinal.md): um T flip-flop, uma placa em que cada pulso na entrada alterna a saída, e com ele um botão vira interruptor (perguntas 297, 302 e 877). Esta página descreve só o que é do flip-flop, entregue na #110. O que todo módulo herda, como a placa, o dono, a rede, as conexões, o delay e o Configurator, está em [`modulos-de-sinal.md`](modulos-de-sinal.md), e os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Cada vez que a entrada A passa de 0 para acima de 0, a saída B alterna entre desligada e a força escolhida, 15 por padrão, e o estado sobrevive ao reinício do servidor (perguntas 879, 880 e 881).

## Conexões

| Conexão | Borda fixa | Pode ser canal | Pergunta |
|---|---|---|---|
| `Input` (A) | Trás | Sim | 878 |
| `Output` (B) | Frente | Sim | 878 |

As laterais ficam livres, sem reset nem saída invertida. Cada conexão é `Edge` ou `Channel` (pergunta 376).

## Regra

| Ponto | Como é | Pergunta |
|---|---|---|
| Gatilho | A borda de subida: A passa de 0 para acima de 0, e B alterna. Segurar o sinal não alterna de novo; só soltar e ligar outra vez. Descer a 0 não muda nada | 879 |
| Força | De 1 a 15, com `−` e `+` na tela, 15 por padrão, como no Signal Inverter. Mudar a força com B ligado muda B na hora, também com mudanças esperando na linha (pergunta 843) | 880 |
| Atraso | Nenhum além do tick do módulo, como o controle `Pulse` da Signal Lamp: a borda lida no tick alterna B nesse mesmo tick. Pela borda, A é lida no tick; pelo canal, já vale o valor do tick anterior (pergunta 387). Sem os 2 ticks do inversor | 892 |
| Delay | De 0 a 15 s, em segundos inteiros, somado ao tick do módulo; com 0 s, sem atraso. O estado muda na hora, e só a saída espera (perguntas 845 e 851). As regras comuns estão em [`modulos-de-sinal.md`](modulos-de-sinal.md#delay-na-saída) | 830 a 854 |
| Ao ser colocado | Nasce `Off`. Uma entrada já ligada ao colocar não alterna: conta como já ligada | 881 |
| Ao carregar | O save guarda B e o último valor lido de A, então carregar o chunk ou reiniciar o servidor com A ligada não conta como borda. Sem o valor de A no save, A conta como ainda não lida, como ao colocar | 881, 886 e 893 |
| Canal indisponível | Com A num canal que fica indisponível (rede `Offline` ou `Unplaced`, sem rede, rede apagada ou fora do alcance), A fica congelada no último valor lido, sem borda: desligar e ligar a rede não muda o estado. Completa a pergunta 385 | 889 |
| Queima | Nenhuma. Ligado na própria saída, ele estabiliza em `Off`: B liga, A sobe e B desliga, A desce e nada alterna. A frequência só vem de fontes que já têm limite, a linha de delay tem o teto `module.max_delay_changes`, e um laço com um inversor queima no inversor | 883 |
| `Toggle` | Alterna B, como uma borda em A, só pelo dono, com `<nome>: On` ou `<nome>: Off` na action bar. Numa rede `Offline`, continua alternando, como a Signal Lamp e o Signal Lever; o canal só não conta | 882 |

## Estados

A tela e o rótulo mostram o estado guardado de B, que muda na hora, mesmo com delay (perguntas 851 e 890):

| Estado | Quando | Em português |
|---|---|---|
| `On` | B ligado, emitindo a força | `Ligado` |
| `Off` | B desligado | `Desligado` |

## Tela

Abre com o Configurator no `Open` (pergunta 393). É a tela do Signal Inverter, com as partes comuns de [`modulos-de-sinal.md`](modulos-de-sinal.md#tela-comum) (pergunta 884):

| Parte | Como é |
|---|---|
| Topo | `Signal Flip-Flop` e o nome do módulo, renomeável pelo dono |
| Esquerda | Um diagrama grande A → T → B, com cada lado aceso quando tem sinal, e embaixo o estado: `● On` ou `● Off` |
| Direita | `Strength` e `Delay`, cada um entre `−` e `+`, e `Input` e `Output`, cada uma com `Edge` ou `Channel`, e a grade de 16 cores |
| Rodapé | A rede e, para quem não é o dono, o cadeado |
| Rede `Offline` | A tela não abre, como a do nó (pergunta 402) |

A moldura tem os 186 px das telas de módulo (pergunta 853). O desenho está em [`telas.md`](telas.md#tela-do-signal-flip-flop).

## No mundo

| Ponto | Como é | Pergunta |
|---|---|---|
| Textura | A seta para a frente, o entalhe de A atrás e um `T` no meio, de T flip-flop | 888 |
| Campos da parte | Os comuns, `front` e `powered`, que acende a seta em vermelho com B ligado (pergunta 560) | 888 |
| Modelo | Como o do inversor: o topo girado 0° ou 90° no UV cobre as 24 orientações (pergunta 429) | 888 |

## Cópia

O `Copy` do Configurator leva a rede, as conexões, a força e o delay, como nos outros módulos, e nunca o estado de B, o último valor de A nem a linha pendente. O módulo colado mantém o próprio estado (perguntas 841 e 887). O detalhe está em [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens).

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), dentro de `data` no contêiner `redweave:part_space` (perguntas 886 e 893). As chaves ficam fixadas na #26 (regra 15):

| Chave | O que guarda | Sem a chave ou inválida |
|---|---|---|
| `input` | A conexão `Input`: `edge` ou a chave da cor do canal | `edge` |
| `output` | A conexão `Output`: `edge` ou a chave da cor do canal | `edge` |
| `on` | Se B está ligado, um booleano | `Off` |
| `input_on` | Se o último valor lido de A estava acima de 0, um booleano; só vai ao save depois da primeira leitura | Ainda não lida: o primeiro tick só lê A, sem alternar (pergunta 893) |
| `strength` | A força, de 1 a 15 | 15, com aviso no log (regra 14) |
| `output_delay_ticks` | O delay em ticks | 0 |
| `delay_line` | As mudanças que esperam o delay, cada uma `{signal, ticks}` | Vazia |

## Config

O flip-flop não tem chave própria (pergunta 883). Usa as comuns dos módulos:

| Chave | Valor | Pergunta |
|---|---|---|
| `module.max_output_delay_ticks` | 300 (15 s), de 0 a 1200 | 837 |
| `module.max_delay_changes` | 32, de 2 a 256 | 847 |

Os valores ficam em [`limites.md`](../limites.md).

## Código

| Camada | Classe | O que faz |
|---|---|---|
| Domínio | `SignalFlipFlop`, `FlipFlopInput` | A regra: a borda de subida, a alternância, a força, a entrada congelada e o `Toggle`; a última leitura da entrada, `UNREAD`, `OFF` ou `ON` |
| Aplicação | `ChangeFlipFlop`, `ReadModuleChannel.reading` | Só o dono muda a força e usa o `Toggle`; a leitura de um canal, vazia quando ele está indisponível (pergunta 889) |
| Parte | `FlipFlopPart`, `FlipFlopSave`, `ModuleTicks.reading` | Lê A, ou nada com o canal indisponível, escreve B a cada tick, com o delay da base, e o save |
| Menu | `FlipFlopMenu`, `FlipFlopAction`, `FlipFlopScreenActions`, `FlipFlopScreenData`, `FlipFlopScreenLayout`, `FlipFlopScreenTexts` | A tela, conferida no servidor |
| Cliente | `FlipFlopScreen`, `FlipFlopDiagram` | A tela e o diagrama A → T → B |
| Datagen | `RedweaveBlockStates`, `FlipFlopEnglishTexts`, `FlipFlopPortugueseTexts`, `RedweaveRecipes` | Os modelos, os textos em inglês e em português e a receita |

## Receita

Na forma comum dos módulos: `·, pistão grudento, · / barra de cobre, redstone, barra de cobre / deepslate polido, Signal Node, deepslate polido`, que dá 1 Signal Flip-Flop. O pistão grudento é o coração do T flip-flop clássico do jogo puro (perguntas 753, 766 e 885). A grade fica em [`itens.md`](../itens.md#receitas).

## Arte

Em `tmp/arte/signal_flip_flop/`: as 2 texturas do topo (apagada e acesa) e o ícone do item. Em `tmp/arte/tela_do_signal_flip_flop/`: a prancha da tela. O agente escreve os `prompt.md`, o desenvolvedor gera as imagens, e o agente as confere antes de levá-las ao mod (perguntas 185, 186 e 888).

## Testes

- **JUnit:** a borda de subida, o sinal segurado que não alterna, a descida sem efeito, a força e os limites, o nascimento em `Off`, a entrada já ligada ao colocar, o carregamento com a entrada ligada, a entrada congelada com o canal indisponível, o `Toggle` e o caso de uso, só pelo dono.
- **GameTests:** colocar em `Off`, a borda pela entrada e pelo canal, o botão que vira interruptor, a saída num canal lida por um nó, o laço na própria saída que estabiliza em `Off`, o `Toggle`, também com a rede `Offline`, a rede que volta sem alternar, o dono, o NBT depois de salvar e carregar, com a entrada ligada sem alternar, o save sem as chaves, o delay com a linha salva, a cópia sem o estado e a receita.
- **Roteiro manual:** a tela, a arte, os textos e o chunk descarregado.

## Fora desta página

- O Jade e o The One Probe, na #23.
- O latch SR, a outra memória, na #111.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-19 20:40 | A seção do código nomeia as classes entregues (#110). |
| Zennd | 2026-11-19 20:30 | Sem `input_on`, a entrada conta como ainda não lida (pergunta 893). |
| Zennd | 2026-11-19 20:15 | Cria o documento, com as decisões 877 a 892 da #110. |
