# Funcionalidades

Uma página por funcionalidade, escrita antes do código e com as decisões fechadas (regra 9 do `AGENTS.md`). Cada página entra pela issue da funcionalidade.

| Fase | Funcionalidades |
|---|---|
| 1 · Sem fio | Rede e canais, Signal Node, Network Controller, Network Terminal, Configurador, Signal Remote, telas do nó e do controlador, Jade e The One Probe |
| 2 · Lógica | Signal Modules: timer, inversor, porta, contador e os outros módulos (perguntas 297 e 308 a 310) |
| 3 · Chips | Circuitos, chips, Bancada de Circuitos, editor e livro guia |
| 4 · Expansão | Fio físico, cabo agrupado, multipart, integrações e port para 26.x |
| 5 · Ponte | Ponte MQTT, segurança, protótipos com ESP32 e dashboard |

| Página | Fase | Estado |
|---|---|---|
| [`rede-e-canais.md`](rede-e-canais.md) | 1 | Comportamento da rede, dos canais, dos nós e das permissões |
| [`telas.md`](telas.md) | 1 | Conceito das telas do nó e do controlador |
| [`controlador-de-rede.md`](controlador-de-rede.md) | 1 | O Network Controller: colocar, clicar, quebrar e o item que guarda a rede |
| [`no-de-sinal.md`](no-de-sinal.md) | 1 | O Signal Node: colocar, ler, emitir, clicar e quebrar |
| [`terminal-de-rede.md`](terminal-de-rede.md) | 1 | O Network Terminal: as redes do player, desligar, religar, limpar e apagar |
| [`configurador.md`](configurador.md) | 1 | O Configurator: os modos de abrir, copiar e colar, e retirar nós |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-12 16:50 | Troca a lógica por canal e as portas em placa pelos Signal Modules (perguntas 308 a 310). |
| Zennd | 2026-11-11 09:51 | Adiciona o Network Terminal, atualiza o Configurator e tira a página da etiqueta (perguntas 125 e 155). |
| Marcelo Fabiano | 2026-11-10 16:29 | Adiciona a página da etiqueta. |
| Marcelo Fabiano | 2026-11-10 15:52 | Adiciona a página do configurador. |
| Marcelo Fabiano | 2026-11-10 09:47 | Adiciona a página do nó de sinal. |
| Marcelo Fabiano | 2026-11-10 09:06 | Adiciona a página do controlador de rede. |
| Marcelo Fabiano | 2026-11-09 15:39 | Adiciona a página da rede e dos canais. |
| Marcelo Fabiano | 2026-11-09 11:25 | Lista a página das telas. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
