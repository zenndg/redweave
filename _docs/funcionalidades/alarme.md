# Alarme

O Signal Alarm (`redweave:signal_alarm`, `Alarme de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) de saída que avisa quando recebe sinal: toca um som, mostra um texto e pisca uma luz vermelha, pelo tempo e para quem o dono escolher (pergunta 643). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** implementado na #142, com as perguntas 643 a 649, 727, 732 e 737 a 744 respondidas.

## Em uma frase

Quando a entrada liga, o Signal Alarm toca um `pling` duplo do note block, mostra `Alarm <nome>: Ringing...` na action bar de quem escuta e pisca a lâmpada vermelha, até o fim da duração (perguntas 643, 644, 738 e 739).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Alarm`, `redweave:signal_alarm`, `Alarme de Sinal` em português | 643 |
| Família | Um Signal Module de saída: a entrada pela borda de trás ou por um canal, sem saída | 643 e 649 |
| Disparo | A entrada que liga começa o alarme. Ele toca na hora e a cada intervalo, até os toques; no fim da duração, tudo para, mesmo com a entrada ligada, e ele só toca de novo depois que a entrada desliga e liga | 648 |
| Som | Dois `pling` do note block juntos, um no tom normal e outro uma oitava acima, no volume máximo do jogo (1.0). Acima disso, o som só iria mais longe | 644 e 739 |
| Texto | `Alarm <nome>: Ringing...` em vermelho (`Alarme <nome>: Disparando...` em português), na action bar de quem escuta, a cada toque | 738 |
| Quem escuta | Escolha na tela: `Owner` (só o dono), `Network` (o dono e os players com um Signal Remote ligado à rede do alarme, porque a rede não tem lista de membros) ou `Everyone` (todos). Sempre dentro de `alarm.range`, e nunca um espectador. O sinal de redstone não guarda quem o causou, por isso não há "quem acionou" | 645 e 741 |
| Toques | De 1 a `alarm.max_repeats`, que é 10 | 646, 737 e 742 |
| Intervalo | Entre `alarm.min_interval_ticks` e `alarm.max_interval_ticks` | 646 e 742 |
| Duração | Escolhida na tela, até `alarm.max_duration_ticks`, mas nunca menor que os toques × o intervalo: ao aumentar os toques ou o intervalo, ela sobe junto, e o `−` para nesse mínimo. Uma duração maior deixa a luz piscando depois do último toque | 648 e 740 |
| Padrões ao colocar | 3 toques, 1 s de intervalo, 5 s de duração e `Owner` | 742 |
| Luz | A lâmpada pisca em vermelho durante toda a duração e fica apagada fora dela | 649 e 744 |
| `Toggle` | Silencia o alarme até a entrada desligar, com `<nome>: Silenced` (`<nome>: Silenciado`). Funciona com a rede `Offline`, como o Signal Lock e o Signal Lamp | 649 e 744 |
| Mão vazia | A mensagem dos módulos, `Use a Configurator to open this module.` | 744 |
| Contador | Fica fora do `modules on` da Tela do Controlador, porque não escreve num canal | 744 |
| Rótulo | O nome e, embaixo, `Ringing` ou `Quiet` e o canal de entrada | 743 |
| Cópia | O `Copy` copia quem escuta, os toques, o intervalo, a duração e a entrada, e os cola noutro Signal Alarm, levando a rede, como os outros módulos. Veja [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens) | 727 e 732 |
| Dono e Configurator | Como os Signal Modules: dono, rede, `Open`, `Rotate` e `Wrench` | 643 |

Os números ficam em [`limites.md`](../limites.md).

## Som e quem escuta

O som e o texto saem só no servidor, uma vez por toque, nunca a cada tick (regra 18). A cada toque, o servidor escolhe os players dentro de `alarm.range` que não são espectadores e que se encaixam em quem escuta, e manda a cada um, só para ele, os dois `pling` na posição do alarme e o texto na action bar. Quem não escuta não ouve nem vê nada.

## Tela

À esquerda, o estado, `Ringing` em vermelho ou `Quiet` apagado, e embaixo a duração com `−` e `+`. À direita, `Heard by` (um botão largo que passa por `Owner`, `Network` e `Everyone`, curtos para caber), `Rings` com `−` e `+`, `Interval` com `−` e `+` (Shift muda 1 s) e `Input` (`Edge` ou `Channel`). A passagem do mouse sobre o intervalo e a duração mostra os ticks. Os outros players veem a tela só para leitura. O detalhe está em [`telas.md`](telas.md#tela-do-signal-alarm).

## Mensagens

| Ação | Em inglês | Em português |
|---|---|---|
| Cada toque | `Alarm <nome>: Ringing...` | `Alarme <nome>: Disparando...` |
| `Toggle` | `<nome>: Silenced` | `<nome>: Silenciado` |

## Save

As chaves ficam no NBT da parte, fixadas na #26 (regra 15, pergunta 744):

| Chave | O que guarda |
|---|---|
| `listeners` | Quem escuta: `owner`, `network` ou `range` |
| `repeats` | Os toques |
| `interval` | O intervalo, em ticks |
| `duration` | A duração, em ticks |
| `remaining` | Os ticks que faltam do alarme em andamento |
| `rings_left` | Os toques que faltam |
| `until_ring` | Os ticks até o próximo toque |
| `last_input` | Se a entrada estava ligada no último tick, para achar a borda que liga |
| `input` | A entrada: a borda ou o canal |

O alarme em andamento fica salvo, e ao recarregar ele continua. Um valor inválido volta aos ajustes de um alarme recém-colocado, ou a nenhum alarme em andamento, com aviso no log (regra 14). Ao carregar, os ajustes são encaixados nos limites do config de agora.

## Desenho

Uma placa de 10 × 10 px com o entalhe de entrada atrás e sem seta, uma base de cobre de 6 × 6 × 1, um alto-falante com grade escura de 4 × 4 × 1 e uma lâmpada de alerta de 2 × 2 × 2 em pé ao lado. Tocando, a lâmpada usa `signal_alarm_lamp.png`, uma textura animada de dois quadros, acesa e apagada, a cada 10 ticks, emissiva: o cliente anima sozinho, sem pacote por pulso, e por isso todos os alarmes tocando piscam juntos. Quieta, ela usa a lâmpada apagada de `signal_alarm_parts.png`. O desenho segue o estado de tocar que o cliente recebe no save, nunca um dado só do servidor.

## Código

| Camada | O que tem |
|---|---|
| Domínio (`domain/module`) | `SignalAlarm`, a regra; `AlarmSettings`, os ajustes com a duração que acompanha os toques; `AlarmListeners`; `AlarmLimits`, a porta dos limites |
| Aplicação | `ChangeAlarm`: só o dono muda quem escuta, os toques, o intervalo e a duração |
| Config | `NeoForgeAlarmLimits` e a seção `alarm` do `RedweaveConfig` |
| Parte (`block/part`) | `AlarmPart`, `AlarmSave` e `AlarmCall`, que escolhe quem escuta e manda o som e o texto |
| Tela (`menu`, `client/screen`) | `AlarmMenu`, `AlarmAction`, `AlarmScreenActions`, `AlarmScreenData`, `AlarmScreenLayout`, `AlarmScreenTexts` e `AlarmScreen` |
| Datagen | `AlarmModels`, `AlarmEnglishTexts` e `AlarmPortugueseTexts` |

## Arte

A arte seguiu as perguntas 185, 186 e 699: o agente escreveu o prompt em `tmp/arte/signal_alarm/`, o desenvolvedor gerou a imagem, e o agente a conferiu. As texturas são `signal_alarm_front`, `signal_alarm_parts`, a animada `signal_alarm_lamp` e o ícone `signal_alarm`.

## Testes

- **JUnit:** `SignalAlarmTest`, com o disparo pela borda, os toques, o fim da duração com a entrada ligada, o rearme, o silêncio, os limites e a duração que acompanha os toques; e `ChangeAlarmTest`, com o dono.
- **GameTests:** `SignalAlarmGameTests`, com a duração, o rearme, o `Toggle`, quem escuta e o alcance, a entrada por canal, a tela do dono, a cópia entre alarmes e a cópia do cliente, lida do save, igual à do servidor.
- **Roteiro manual:** o som, a luz, o texto e a tela, nos passos 342 a 358 de [`roteiro-de-teste-manual.md`](../desenvolvimento/roteiro-de-teste-manual.md).

## Fora desta página

- A receita, em [`itens.md`](../itens.md#receitas) (pergunta 761).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 16:05 | A receita entra na #157 (pergunta 761). |
| Zennd | 2026-11-16 15:21 | Marca como implementado na #142, com as decisões 737 a 744: os 10 toques, o texto, o `pling` duplo, a duração que acompanha os toques, `Network`, o config, a tela, as mensagens, o save e o desenho. |
| Zennd | 2026-11-16 13:59 | O Signal Alarm entra na cópia do Configurator (pergunta 727). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 643 a 649. |
