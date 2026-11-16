# Sensor de Itens

O Signal Item Sensor (`redweave:signal_item_sensor`, `Sensor de Itens de Sinal` em português) é uma placa grudada num inventário, como um baú ou um barril, que emite sinal quando ele tem exatamente a quantidade escolhida de um item (perguntas 689 a 692). Ele só lê: não guarda, não filtra nem move itens, que ficam com o baú, os funis e os mods de logística (regra 2). Entra na #148, como o primeiro dos itens novos (pergunta 698). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 689 a 698 respondidas (regra 9), a implementar na #148.

## Em uma frase

O dono escolhe um item e uma quantidade; quando o inventário onde a placa está grudada tem exatamente essa quantidade daquele item, somando todos os slots, o sensor emite a força escolhida pela borda ou por um canal (perguntas 692 e 696).

## Origem

Nasceu da ideia do Receptor de Itens. Ficaram só as funções de sinal: o item esperado, a quantidade, emitir a força e a configuração só pelo dono. Saíram o slot próprio, a tela pública de depósito, o filtro, o depósito parcial, a retirada por quem depositou, a automação de entrada e de saída, o `Auto reject` e as permissões de retirada, porque são armazenamento e logística de itens (regra 2) e trazem riscos de posse e de duplicação (pergunta 689).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Item Sensor`, `redweave:signal_item_sensor`, `Sensor de Itens de Sinal` em português | 691 |
| Onde fica | Uma parte de placa do multipart, em qualquer face de um bloco com inventário: baú, barril e os de outros mods, pela capability de itens do NeoForge. Lê só o bloco de apoio, sem mexer em nada. Num bloco sem inventário, a colocação é recusada com `This needs a container.` | 690 e 694 |
| Disparo | Com exatamente N itens do tipo esperado, somando todos os slots. Com mais ou com menos, a saída desliga | 692 |
| Quantidade | De 1 até `item_sensor.max_count`, no config (veja [`limites.md`](../limites.md)); não tem teto de uma pilha, porque o inventário soma vários slots | 693 |
| Comparação | Só o tipo do item: diamante é diamante, sem olhar nome, encantamento ou dano | 697 |
| Leitura | A cada `item_sensor.check_ticks`, no config. A rede só é avaliada quando a saída muda (regra 18). Com o chunk descarregado, a contagem vale 0 (regra 6) | 695 |
| Saída | Pela borda da frente ou por um canal da rede, com a força de 1 a 15, padrão 15 | 696 |
| Tela do dono | Um slot fantasma em que o dono clica com o item para escolher, sem consumir o item; a quantidade com `−` e `+` (Shift muda 10); a força; a saída; e a contagem atual, como `7 / 10` | 696 |
| Quem configura | Só o dono, com o Configurator | 689 |
| Visual | A luz vermelha e verde dos [Emissores de Sinal](emissores.md) e o ícone do item esperado desenhado na placa | 697 |
| `Toggle` | Não faz nada | 697 |
| Dono, rede, `Open`, `Rotate` e `Wrench` | Como os [Signal Modules](modulos-de-sinal.md) | 689 |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_item_sensor/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

- **JUnit:** a regra, com a contagem exata, o item esperado e a força.
- **GameTests:** a placa num baú e num barril, a recusa sem inventário, a contagem em vários slots, o disparo só no número exato, a saída pela borda e pelo canal, o dono e o save.
- **Roteiro manual:** a tela, o slot fantasma, a arte e um inventário de outro mod (pergunta 697).

## Fora desta página

- O [Signal Lock](fechadura.md) e os outros itens novos, de #141 a #147.
- A receita, na #24, e o Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 10:12 | Cria o conceito do Signal Item Sensor, com as decisões 689 a 698. |
