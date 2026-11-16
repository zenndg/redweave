# Funcionalidades

Uma página por funcionalidade, escrita antes do código e com as decisões fechadas (regra 9 do `AGENTS.md`). Cada página entra pela issue da funcionalidade.

| Fase | Funcionalidades |
|---|---|
| 1 · Sem fio | Rede e canais, Signal Node, Network Controller, Network Terminal, Configurador, Signal Remote, telas do nó e do controlador, Jade e The One Probe |
| 2 · Lógica | Signal Modules: timer, inversor, porta, contador e os outros módulos (perguntas 297 e 308 a 310); o multipart e o Signal Cable, que entram na `v0.1.0` (pergunta 516) |
| 3 · Chips | Circuitos, chips, Bancada de Circuitos, editor e livro guia |
| 4 · Expansão | Integrações e port para 26.x. O multipart e o Signal Cable foram para a `v0.1.0` (pergunta 516), e o cabo agrupado saiu (pergunta 519) |
| 5 · Ponte | Ponte MQTT, segurança, protótipos com ESP32 e dashboard |

| Página | Fase | Estado |
|---|---|---|
| [`rede-e-canais.md`](rede-e-canais.md) | 1 | Comportamento da rede, dos canais, dos nós e das permissões |
| [`telas.md`](telas.md) | 1 | Conceito das telas do nó e do controlador |
| [`controlador-de-rede.md`](controlador-de-rede.md) | 1 | O Network Controller: colocar, clicar, quebrar e o item que guarda a rede |
| [`no-de-sinal.md`](no-de-sinal.md) | 1 | O Signal Node: colocar, ler, emitir, clicar e quebrar |
| [`terminal-de-rede.md`](terminal-de-rede.md) | 1 | O Network Terminal: as redes do player, desligar, religar, limpar e apagar |
| [`configurador.md`](configurador.md) | 1 | O Configurator: os modos de abrir, copiar e colar, e retirar nós |
| [`signal-remote.md`](signal-remote.md) | 1 | O Signal Remote: o tablet que liga um slot de input de longe, o vínculo, o disparo e quando ele conta |
| [`modulos-de-sinal.md`](modulos-de-sinal.md) | 2 | A base comum dos Signal Modules: a placa, o dono, a rede, as conexões, o Configurator, a tela comum e o save |
| [`signal-timer.md`](signal-timer.md) | 2 | O Signal Timer: o ciclo de intervalo e pulso, o controle, a tela e o save |
| [`signal-inverter.md`](signal-inverter.md) | 2 | O Signal Inverter, o NOT do mod: o atraso, a queima, a tela e o save |
| [`weather-detector.md`](weather-detector.md) | 2 | O Weather Detector: os modos de clima, a dimensão sem clima, a tela e o save |
| [`signal-randomizer.md`](signal-randomizer.md) | 2 | O Signal Randomizer: os modos do sorteio, o intervalo, o controle, a tela e o save |
| [`multipart.md`](multipart.md) | 2 | O multipart de todo o mod: o bloco contêiner, as partes, a ocupação por face, a mira e o save |
| [`signal-cable.md`](signal-cable.md) | 2 | O Signal Cable: o item tingido, a forma, a ligação por cor e por dono, o sinal como o fio do jogo e o cálculo por trecho |
| [`alavanca.md`](alavanca.md) | 2 | O Signal Lever, entregue na #130: fonte ligada e desligada pelo dono, com a força na tela, uma luz vermelha e verde e saída sem fio ou pela redstone |
| [`botao.md`](botao.md) | 2 | O Signal Button, entregue na #131: fonte apertada pelo dono, que volta sozinha depois do tempo ligado, com a força e o tempo na tela, uma luz vermelha e verde e saída sem fio ou pela redstone |
| [`placa-de-pressao.md`](placa-de-pressao.md) | 2 | O Signal Pressure Plate, entregue na #132: fonte só no chão, ligada enquanto algo do modo (`Players`, `Passive`, `Hostile` ou `Items`) está sobre ela, com a força e o modo na tela, uma luz vermelha e verde e saída sem fio ou pela redstone |
| [`portas-logicas.md`](portas-logicas.md) | 2 | As portas AND, OR, XOR e NAND e a regra NOT no domínio, que o módulo de porta e o Signal Inverter usam |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-15 15:09 | Marca a placa de pressão como entregue na #132. |
| Zennd | 2026-11-15 14:04 | Marca o botão como entregue na #131. |
| Zennd | 2026-11-15 13:19 | Marca a alavanca como entregue na #130. |
| Zennd | 2026-11-14 21:23 | Ajusta a descrição da alavanca ao indicador da pergunta 554. |
| Zennd | 2026-11-14 21:14 | Adiciona os conceitos da alavanca, do botão e da placa de pressão (pergunta 548). |
| Zennd | 2026-11-14 19:14 | Adiciona as páginas do multipart e do Signal Cable, e tira o fio da Fase 4 (perguntas 516 e 519). |
| Zennd | 2026-11-14 17:46 | Adiciona a página do Signal Randomizer (#109). |
| Zennd | 2026-11-14 16:40 | Adiciona a página do Weather Detector (#108). |
| Zennd | 2026-11-14 14:31 | Adiciona a página do Signal Inverter (#105). |
| Zennd | 2026-11-14 09:46 | Adiciona as páginas dos Módulos de Sinal e do Signal Timer (#103). |
| Zennd | 2026-11-12 20:48 | Adiciona a página do Signal Remote (#22). |
| Zennd | 2026-11-12 18:26 | Adiciona a página das portas lógicas (pergunta 328). |
| Zennd | 2026-11-12 16:50 | Troca a lógica por canal e as portas em placa pelos Signal Modules (perguntas 308 a 310). |
| Zennd | 2026-11-11 09:51 | Adiciona o Network Terminal, atualiza o Configurator e tira a página da etiqueta (perguntas 125 e 155). |
| Marcelo Fabiano | 2026-11-10 16:29 | Adiciona a página da etiqueta. |
| Marcelo Fabiano | 2026-11-10 15:52 | Adiciona a página do configurador. |
| Marcelo Fabiano | 2026-11-10 09:47 | Adiciona a página do nó de sinal. |
| Marcelo Fabiano | 2026-11-10 09:06 | Adiciona a página do controlador de rede. |
| Marcelo Fabiano | 2026-11-09 15:39 | Adiciona a página da rede e dos canais. |
| Marcelo Fabiano | 2026-11-09 11:25 | Lista a página das telas. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
