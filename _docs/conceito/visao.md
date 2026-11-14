# Visão

O Redweave é um mod de redstone para Minecraft 1.21.1 NeoForge em que o player monta automações complexas com nós sem fio, canais programáveis e chips de lógica, tudo por telas fáceis e bonitas.

## Para quem

- O player que acha a redstone do jogo poderosa, mas trabalhosa.
- O player técnico que quer lógica de verdade, como portas, temporizadores, contadores e memória, sem montar circuitos de 200 blocos.

## A promessa

- **Fácil de começar:** o primeiro circuito útil, uma porta que abre quando dois botões estão ligados, sai em menos de 2 minutos, sem wiki.
- **Bonito de olhar:** sinais visíveis, cores por canal e texturas que acendem.
- **Orgulho do que fez:** circuitos ganham nome e ícone e viram chips reutilizáveis.
- **Aprende fazendo:** cada tela ensina a próxima ideia, do interruptor sem fio até a lógica sequencial.

## Escopo

Só sinal de redstone (pergunta 1). Itens, fluidos e energia ficam com os mods de logística.

## Fases

| Fase | Tema | Entrega |
|---|---|---|
| 1 · Sem fio (MVP) | Sinal em qualquer lugar | Signal Node, Network Controller, Network Terminal, Configurador, Signal Remote, simulador, Jade e The One Probe |
| 2 · Lógica | Lógica que se vê | Os Signal Modules: Signal Timer, Signal Inverter, porta, contador, memória, trava e os outros módulos de fonte e de transformação; o multipart e o Signal Cable, o cabo físico de 16 cores (pergunta 516) |
| 3 · Chips | Programar a redstone | Bancada de Circuitos, editor com debug por tick, chips como item e livro guia |
| 4 · Expansão | Integrações | Integrações e port para 26.x. O fio físico e o multipart foram para a `v0.1.0` (pergunta 516), e o cabo agrupado saiu (pergunta 519) |
| 5 · Ponte | Além do jogo | Ponte MQTT para dashboards e hardware real |

Toda versão é jogável e testada no Iron Meridian. As Fases 1 e 2 saem juntas na v0.1.0, como uma fase só (pergunta 303), e a ordem do trabalho fica no [`ROADMAP.md`](../../ROADMAP.md).

## Ponte com o mundo real

Na Fase 5, um nó de ponte publica as mudanças de canal num broker MQTT e assina comandos de fora (pergunta 8). Com isso, um dashboard web mostra o histórico dos canais, um ESP32 com relé segue um canal e acende uma lâmpada real, e o Signal Remote dirige um carrinho de brinquedo por um ESP32 com ponte H.

A ponte é só mais um adapter, com as portas `EventPublisher` e `CommandReceiver`, e o core não muda. Segurança é requisito: desligada por padrão, só no servidor, broker com autenticação, limite de mensagens por canal e failsafe no hardware.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 19:14 | Leva o multipart e o Signal Cable para a `v0.1.0` e tira o cabo agrupado (perguntas 516 e 519). |
| Zennd | 2026-11-12 16:50 | Junta as Fases 1 e 2 na v0.1.0 e troca a lógica por canal e as portas em placa pelos Signal Modules (perguntas 303 e 308 a 310). |
| Zennd | 2026-11-11 09:51 | Adiciona o Network Terminal à Fase 1 (pergunta 130). |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
