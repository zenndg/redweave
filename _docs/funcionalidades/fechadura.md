# Fechadura

O Signal Lock (`redweave:signal_lock`, `Fechadura de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) de transformação que só abre com a força exata do sinal: ele espera uma força única, a chave, e emite a força definida quando a entrada bate com ela (pergunta 633). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #141, com as perguntas 633 a 642 e 707 a 714 respondidas (regra 9).

## Em uma frase

Com a força da chave na entrada, o Signal Lock abre, acende a luz verde e emite a força escolhida; com qualquer outra força, fica trancado (perguntas 633 e 634).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Lock`, `redweave:signal_lock`, `Fechadura de Sinal` em português, para não confundir com o `Latch` da #111 | 633 |
| Família | Um Signal Module de transformação, com a base comum dos módulos | 633 |
| Regra | Abre só com a força exata da chave; com outras forças, nada | 633 |
| Saída | Ligada enquanto a entrada estiver na força da chave, e desligada quando ela muda, como uma porta lógica | 634 |
| Conexões | A entrada pela borda de trás ou por um canal, e a saída pela borda da frente ou por um canal, escolhidas na tela, como no Signal Inverter | 635 |
| Padrões | A chave e a força emitida começam em 15 e se ajustam de 1 a 15 na tela. A força 0 nunca abre | 636 |
| Segredo | Só o dono vê a chave. O rótulo mostra só `Locked` ou `Unlocked`, e os outros players veem `?` na tela | 637 |
| Visual | A luz dos [Signal Emitters](emissores.md), vermelha trancado e verde aberto, e um cadeado 3D pequeno na placa, que abre quando a chave bate | 638 |
| `Toggle` | Abre por um pulso, com `<nome>: Unlocked`. O dono escolhe a duração na tela: padrão de 1 s, de 2 ticks a 10 s, com os limites em [`limites.md`](../limites.md). Funciona com a rede `Offline`, como os emissores, e o canal só não conta enquanto ela está `Offline`. Com um pulso em andamento, não reinicia, e a mensagem aparece de novo | 639, 640, 707, 711 e 712 |
| Retirada | Só o dono, só com o `Wrench` do Configurator. Ninguém quebra à mão no survival, nem o dono: a quebra não avança e o servidor recusa, com `Only the owner can remove this with a Configurator.` na action bar; no criativo, qualquer um | 641 |
| Explosão | Resiste e protege o próprio bloco de apoio. A proteção vale para o espaço de bloco inteiro, com as outras partes dele, como um cabo ou um nó | 641 e 708 |
| Mão vazia | Mostra `Use a Configurator to open this module.`, como os outros módulos; a fechadura não abre à mão | 709 |
| Mira | A placa e o cadeado, de 4 × 4 px e 6 px de altura | 714 |
| Cartão | Abre com um [Signal Card](cartao.md) vinculado, como o pulso do `Toggle` | 669 e 670 |
| Proteção da porta | Protege os blocos escolhidos pelo dono, na [proteção dos blocos](protecao.md) | 672 a 676 |
| Ordem | No passo 18 do roadmap, depois do Signal Item Sensor e antes da Signal Lamp e do módulo de memória (#110) | 642, 698 e 706 |

Os números ficam em [`limites.md`](../limites.md).

## Tela

Abre com o Configurator no modo `Open`. O detalhe está em [`telas.md`](telas.md#tela-do-signal-lock).

| Parte | Como é | Pergunta |
|---|---|---|
| Esquerda | `Unlocked` grande, em verde, ou `Locked` apagado, e embaixo o pulso do `Toggle`, em segundos com décimos, entre `−` e `+` (passos de 0,1 s; com Shift, 1 s) | 640 |
| `Key` | A chave, de 1 a 15, entre `−` e `+`. Só o dono a vê: para os outros players, a tela mostra `?` | 636 e 637 |
| `Strength` | A força emitida, de 1 a 15, entre `−` e `+` | 636 |
| `Input` e `Output` | O seletor `Edge` ou `Channel` de cada ponta, como o Signal Inverter | 635 |

O servidor só manda a chave ao cliente do dono: os outros recebem a chave vazia, e o rótulo nunca a mostra (pergunta 637, regras 16 e 17).

## Mensagens

| Quando | Inglês | Português |
|---|---|---|
| `Toggle` | `<nome>: Unlocked` | `<nome>: Aberta` |
| Quebrar à mão no survival | `Only the owner can remove this with a Configurator.` | `Só o dono pode retirar isto com um Configurator.` |
| Mão vazia | `Use a Configurator to open this module.` | A mensagem dos módulos, traduzida |
| Rótulo | `Locked` ou `Unlocked` e o canal da saída | `Trancada` ou `Aberta` |

## Save

As chaves ficam no NBT da parte, dentro de `data`, além das chaves comuns dos módulos, e são fixadas na #26 (pergunta 710, regra 15):

| Chave | O que guarda | Valor inválido |
|---|---|---|
| `key` | A chave, de 1 a 15 | Volta a 15, com aviso no log (regra 14) |
| `strength` | A força emitida, de 1 a 15 | Volta a 15, com aviso no log |
| `pulse` | Os ticks do pulso do `Toggle` | Volta a 20 ticks, com aviso no log |
| `remaining` | Os ticks que faltam de um pulso em andamento, 0 sem pulso | Volta a 0, com aviso no log |
| `input` e `output` | As duas pontas: a borda ou o canal | — |

A força lida na entrada não vai para o save: o próximo tick a lê de novo.

## Desenho

- A placa com a moldura de cobre, a seta na frente e o entalhe de entrada atrás, como o Signal Inverter. A seta fica vermelha com a saída ligada.
- Um cadeado de cobre de 4 × 2 × 3 px em pé no meio da placa, com o buraco de chave e a alça de aço. Trancado, a alça fica fechada sobre o corpo; aberto, ela sobe 1 px e gira um quarto de volta sobre uma perna (pergunta 638).
- A luz dos [Signal Emitters](emissores.md), emissiva, vermelha trancado e verde aberto. A luz e o cadeado seguem o `powered` que o cliente recebe, e não a força lida, que só o servidor conhece.

## Proteção

- **Quebra:** a parte é uma `SecuredPart`: no survival, ninguém a quebra à mão, nem o dono, e só o `Wrench` do dono a retira. No criativo, qualquer um a quebra (pergunta 641).
- **Explosão:** antes de uma explosão quebrar os blocos, a lista dela perde os espaços com uma `SecuredPart` e os blocos de apoio delas, olhando só os blocos carregados (perguntas 641 e 708, regra 6).
- **Limite:** outro player ainda pode quebrar à mão o bloco de apoio, e aí a fechadura cai. Isso é da [proteção dos blocos](protecao.md), na #146.

## Código

| Camada | Classe | O que faz |
|---|---|---|
| Domínio | `domain/module/SignalLock`, `LockPulse` e `LockLimits` | A regra: aberta com a força exata da chave ou durante o pulso, e a entrada 0 nunca abre; a chave e a força de 1 a 15; o pulso preso aos limites do config |
| Aplicação | `application/ChangeLock` | Só o dono muda a chave, a força e o pulso |
| Config | `config/NeoForgeLockLimits` | `lock.min_pulse_ticks` e `lock.max_pulse_ticks` |
| Parte | `block/part/LockPart`, `LockSave` e `SecuredPart` | A placa, que lê a entrada, escreve a saída e conta o pulso a cada tick; o save; e a marca das partes que não se quebram à mão |
| Bloco | `block/SecuredExplosions` e `PartSpaceBlock.attack` | A explosão que poupa o espaço e o bloco de apoio, e a mensagem de quem tenta quebrar à mão |
| Tela | `menu/Lock*` e `client/screen/LockScreen` | O menu, que só manda a chave ao dono, e a tela |
| Datagen | `datagen/LockModels` e os textos | Os modelos da placa, com o cadeado fechado e aberto, e os textos em inglês e português |

## Arte

A arte seguiu as perguntas 185, 186 e 699: o agente escreveu o prompt em `tmp/arte/signal_lock/`, o desenvolvedor gerou as imagens, e o agente as conferiu. As texturas são `signal_lock_front`, `signal_lock_front_on` e `signal_lock_parts`, em `textures/block/`, e o ícone `signal_lock`, em `textures/item/`. A luz é a do Signal Lever, e a lateral, a do Signal Timer.

## Testes

- **JUnit:** a regra, com a chave exata, a força 0, a força emitida e o pulso, e o caso de uso do dono.
- **GameTests:** a chave exata abrindo e as vizinhas não, a força emitida, a entrada e a saída pela borda e pelo canal, o pulso do `Toggle` e a rede `Offline`, a chave que não vai para outro player, a quebra recusada no survival e aceita no criativo, o `Wrench` do dono, a explosão, o save e a aba criativa.
- **Roteiro manual:** a tela, o cadeado aberto e fechado, a luz e os textos, nos passos do [roteiro](../desenvolvimento/roteiro-de-teste-manual.md#fase-2-o-signal-lock) (pergunta 642).

## Fora desta página

- O cartão, em [`cartao.md`](cartao.md), e a proteção dos blocos, em [`protecao.md`](protecao.md).
- O `Threshold` (#115), que liga acima de um limiar; o Signal Lock testa igualdade.
- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 12:22 | Marca como implementado na #141, com a tela, as mensagens, o save, o desenho, a proteção, o código e os testes (perguntas 707 a 714). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 633 a 642. |
