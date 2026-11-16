# Alarme

O Signal Alarm (`redweave:signal_alarm`, `Alarme de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) de saída que avisa quando recebe sinal: toca um som, mostra um texto e pulsa uma luz vermelha, pelo tempo escolhido pelo dono (pergunta 643). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 643 a 649 respondidas (regra 9), a implementar na #142.

## Em uma frase

Com sinal na entrada, o Signal Alarm toca o `pling` do note block, mostra `⚠ <nome>` na action bar de quem escuta e pulsa a luz vermelha, até o fim da duração escolhida (perguntas 643, 644, 647 e 648).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Alarm`, `redweave:signal_alarm`, `Alarme de Sinal` em português | 643 |
| Família | Um Signal Module de saída: a entrada pela borda de trás ou por um canal, sem saída | 643 e 649 |
| Som | O `pling` do note block | 644 |
| Quem escuta | Escolha na tela: `Owner`, `Owner and network` (o dono e quem usa a rede do alarme) ou `Everyone in range`. O sinal de redstone não guarda quem o causou, por isso não há "quem acionou" | 645 |
| Alcance | Pequeno, no config, em [`limites.md`](../limites.md) | 646 |
| Repetições e intervalo | Escolhidos na tela, dentro dos limites do config | 646 |
| Texto | `⚠ <nome>` em vermelho, na action bar de quem escuta, a cada toque | 647 |
| Duração | Escolhida na tela, com padrão de 5 s e máximo no config. Dentro dela, o som toca no intervalo até as repetições, e a luz pulsa; no fim, tudo para, mesmo com o sinal ligado, e só dispara de novo quando o sinal desliga e liga | 648 |
| Luz | Pulsa vermelha enquanto o alarme toca, e fica apagada sem sinal | 649 |
| `Toggle` | Silencia o alarme até o sinal desligar, com `<nome>: Silenced` | 649 |
| Cópia | Entra na cópia do Configurator ao nascer: o `Copy` copia a configuração e a cola noutro Signal Alarm, levando a rede, como os outros módulos. Veja [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens) | 727 e 732 |

Os números ficam em [`limites.md`](../limites.md).

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_alarm/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

JUnit da regra (a duração, as repetições, o disparo de novo só depois do sinal desligar); GameTests no mundo (quem escuta, o alcance, o `Toggle`); roteiro manual do som, da luz e do texto (pergunta 649).

## Fora desta página

- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 13:59 | O Signal Alarm entra na cópia do Configurator (pergunta 727). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 643 a 649. |
