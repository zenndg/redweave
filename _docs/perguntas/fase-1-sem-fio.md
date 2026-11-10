# Fase 1 · Sem fio

Perguntas desta fase. A mais nova fica no topo de cada tabela.

## Pendentes

| # | Pergunta | Opções | Recomendação |
|---|---|---|---|

## Respondidas

| # | Pergunta | Resposta |
|---|---|---|
| 69 | Em que aba do inventário criativo fica o item do Network Controller? | Numa aba própria, `Redweave`, com o ícone do controlador, que recebe cada item novo da Fase 1 |
| 68 | Como o nome do Network Controller aparece no `pt_br.json`? | `Controlador de Rede`, traduzido como o jogo faz com os próprios blocos, e as mensagens também em português |
| 67 | Com quais chaves de NBT a BlockEntity do controlador salva a rede e o dono? | `network`, com o `NetworkId`, e `owner`, com o UUID do dono, que vai também ao cliente para a quebra não aparecer a quem não é dono. São contrato de save e ficam fixadas na issue #26 |
| 66 | Quais limites mínimo e máximo valem para as chaves de config? | `network.range` de 0 a 30.000.000, a borda do mundo; `network.name_length` e `channel.name_length` de 1 a 64; `node.slots` de 1 a 9, o máximo com upgrade. Um valor fora do intervalo volta ao padrão, e o mod registra no log. Detalha a pergunta 50 |
| 65 | Qual é o ID do data component que guarda a rede no item do Network Controller? | `redweave:network`, com o `NetworkId`. O mesmo componente serve à rede guardada no Configurador (#17), porque é o mesmo conceito |
| 64 | Quais propriedades de blockstate o Network Controller tem? | `facing`, nas 4 direções horizontais, com a frente voltada para quem colocou, e `linked`, verdadeiro quando o bloco tem rede, que escolhe o topo `top_on` ou `top`. O estado `Sem nós` vem com a camada emissiva da #24, sem propriedade nova |
| 63 | Onde fica o objeto por servidor que guarda as redes até a #16? | Num pacote novo, `server/`, com o `RedweaveServer`, a raiz de composição por servidor, as redes em memória e o gerador de IDs. A #16 troca só as redes em memória pelo `SavedData`. A estrutura de pacotes do `AGENTS.md` ganha a linha do pacote. Detalha a pergunta 53 |
| 62 | Em quais fronteiras ficam os testes do Network Controller? | Nas três camadas. No domínio, com JUnit: `placeController` recusa com `ALREADY_PLACED` quando já há controlador (pergunta 56), e `loseController` deixa a rede `Offline` sem quem pede (pergunta 49). Na aplicação, com JUnit: os casos de uso de colocar, quebrar e perder o controlador. No mundo, com GameTests: colocar, quebrar como dono e soltar o item com o ID, outro player não quebra, explosão não destrói e recolocar volta `Online` |
| 61 | De onde vêm os quatro pontos de slot nos cantos do Signal Node? | Da textura branca `signal_node_slot`, tingida com a cor do canal em quatro elementos do modelo, nos cantos. O modelo fica com a #15 |
| 60 | Qual textura mostra o Network Controller no estado `Sem nós`, com o núcleo aceso fraco? | A `network_controller_top_on` sem a camada emissiva `network_controller_top_glow`, então o núcleo fica fraco no escuro, sem textura nova |
| 59 | A textura `signal_node_front_out_of_range` tem lente âmbar e anel de cobre, e o `visual.md` pede lente apagada e anel tracejado vermelho. Qual vale? | O `visual.md`. O desenvolvedor redesenhou a textura, com a lente apagada e o anel tracejado vermelho, em `_docs/imagens/conceito/texturas-out-of-range-e-button-on.png`, junto com o `signal_remote_button_on`, o Remote em modo botão com a antena e o LED acesos durante o pulso |
| 51 | Qual é o visual do Network Controller até a #24 trazer a textura final? | As texturas do desenvolvedor, enviadas numa prancha 16×16 ampliada 8×, em `_docs/imagens/conceito/texturas.png`, e extraídas para `assets/redweave/textures/block/` e `item/`. Valem para todos os blocos e itens da Fase 1 |
| 58 | Quem é o dono de um Network Controller colocado sem um player real, como pelo FakePlayer de outro mod? | Ninguém: o bloco não é colocado. Só um player real cria ou recoloca uma rede, porque o dono a altera depois pela tela |
| 57 | O que acontece ao colocar um controlador cujo item guarda o ID de uma rede que não existe? | Nasce uma rede nova, como com um controlador sem ID, e ela vira a rede atual do player. Acontece, por exemplo, depois de reiniciar o servidor antes da #16 |
| 56 | O que acontece ao colocar um controlador cujo item guarda uma rede que já tem um controlador colocado, como um item duplicado no criativo? | O bloco não é colocado, o item fica na mão, e a action bar mostra `This network already has a controller.` O primeiro controlador continua sendo o da rede |
| 55 | O que acontece quando um player coloca um controlador cujo item guarda a rede de outro player? | O bloco não é colocado, o item fica na mão, e a action bar mostra `Only the owner can place this network.` Detalha a pergunta 30 |
| 54 | Como se chama a página da funcionalidade do Network Controller? | `controlador-de-rede.md`, em português, como pede a regra 21. O texto usa o nome do bloco, Network Controller |
| 53 | Até a persistência da #16, onde ficam as redes no servidor? | Num objeto por servidor, criado ao abrir o mundo e descartado ao fechar, com as redes e a rede atual de cada player. A #16 o troca pelo `SavedData`. Até lá, um controlador que sobra no mundo depois de reiniciar fica sem rede e mostra `No network` no clique. Detalha a pergunta 48 |
| 52 | O que o player vê ao clicar no Network Controller com a mão vazia, antes da tela da #20? | Uma mensagem traduzida na action bar. Para o dono, a rede passa a ser a atual, e a mensagem mostra o nome dela, como `Current network: Network 3f2a`. Para os outros, a mensagem mostra o nome da rede e o dono, sem trocar a rede atual. A Tela do Controlador substitui a mensagem |
| 50 | Quais chaves de config entram com o Network Controller? | As quatro que o `NetworkLimits` lê, no config do servidor (`redweave-server.toml`), que vale por mundo e é enviado aos clientes: `network.range` (128), `network.name_length` (32), `channel.name_length` (16) e `node.slots` (4). Os 9 slots com upgrade entram com o upgrade. Detalha a regra 3 |
| 49 | O que acontece com a rede quando o Network Controller some do mundo sem o dono quebrá-lo, como no criativo, por comando ou por outro mod? | O domínio ganha uma operação sem quem pede, para o controlador que sumiu: a rede fica `Offline`. Sem item, ela continua salva `Offline`, como uma rede cujo item se perdeu. A retirada pelo dono continua conferindo o dono. Detalha as perguntas 24 e 30 |
| 48 | Em que ordem seguem as issues da Fase 1, para chegar ao primeiro teste manual? | Antecipando a #18 e a #19: #14 Network Controller, #15 Signal Node, #18 nó num canal livre e #19 Tela do Nó, e então o primeiro teste manual de alavanca, rede e lâmpada. Depois, #16 persistência, #17 Configurador e o restante. Até a #16, a rede some ao reiniciar o servidor |
| 47 | Quais versões das actions a CI fixa? | As últimas estáveis em 2026-11-10: `actions/checkout` v7.0.1, `actions/setup-java` v6.2.0 e `actions/upload-artifact` v7.0.2, cada uma pelo SHA do commit (pergunta 44) |
| 46 | Como a CI faz o cache do Gradle e do Minecraft preparado? | Pelo `cache: gradle` do `actions/setup-java`, sem o `gradle/actions`: é uma dependência a menos, com licença MIT, e evita o cache proprietário que a v6 do `gradle/actions` usa por padrão. O cache guarda `~/.gradle/caches`, onde fica o `neoformruntime`, e o wrapper |
| 45 | Que conferências a CI faz além dos gates do `pre-push`? | Só o gitleaks em todo o histórico, com a mesma versão e o mesmo checksum do `setup-dev.sh`, para pegar um segredo que entrou sem passar pelos hooks. As mensagens e o nome da branch do PR não são conferidos na CI |
| 44 | Como a CI fixa a versão das actions de terceiros? | Pelo SHA do commit, com a tag num comentário ao lado, para uma tag movida não trocar o código que roda |
| 43 | Quais gates a CI roda em cada evento? | Os três do `pre-push` (`make check`, `make gametest` e `make check-generated`) em todo push e todo PR para `develop`, `homolog`, `main` e `release/*` |
| 42 | Para trocar um nó de rede com o Configurador, quem precisa ser o dono? | Das duas redes, a antiga e a nova, para ninguém tirar o nó da rede de outro player. Um nó sem rede entra em qualquer rede do próprio player. Detalha a pergunta 22 |
| 41 | O que os casos de uso devolvem? | O mesmo `Outcome` do domínio, com `UNKNOWN_NETWORK` e `NO_CURRENT_NETWORK`. O `PlaceNode` devolve também o ID do nó criado |
| 40 | Que cor recebe uma rede recém-criada? | A primeira cor, na ordem das lãs, que nenhuma outra rede do mesmo dono usa. Com as 16 em uso, o branco. O dono troca depois |
| 39 | Um nó com dois slots de saída na mesma face, em canais diferentes: que sinal o bloco recebe? | O mais forte entre as saídas da face, como dois fios de redstone no mesmo bloco. A mudança que o simulador devolve é por face do nó |
| 38 | Onde ficam a marca de pendente de avaliação e o último valor emitido por cada saída? | Na rede, e o `Simulator` é um serviço de domínio sem estado, que recebe a rede e devolve as mudanças. O último valor emitido não vai para o save: ao carregar, a rede começa pendente, e o primeiro tick reaplica todas as saídas |
| 37 | Onde fica a última leitura de cada entrada, que mantém o canal de um nó `Unloaded`? | Salva com a rede, no mundo. Depois de reiniciar o servidor, um nó num chunk descarregado continua contribuindo com o valor que tinha, como a redstone congelada do jogo. Detalha a pergunta 13 |
| 36 | Que forma tem o alcance do Network Controller? | Esfera: a distância em linha reta até o bloco do nó, comparada pelos quadrados, sem raiz nem arredondamento. Detalha a pergunta 23 |
| 35 | Qual nome uma rede recebe ao ser criada? | `Network` seguido dos 4 primeiros caracteres do ID, como `Network 3f2a`, em inglês. Quem chama monta o nome, e o domínio só valida. O dono renomeia depois |
| 34 | O que acontece com um nome de rede ou de canal vazio, ou maior que o limite do config? | Os espaços das pontas são removidos. Vazio ou acima do limite, a operação é recusada com `INVALID_NAME`, e o nome fica como estava. No canal, um nome vazio volta ao nome da cor, porque o nome do canal é opcional. A tela limita o campo ao mesmo tamanho |
| 33 | O agregado `Network` é mutável ou imutável? | Mutável e protegido: uma entidade cujas operações mudam o estado validando as invariantes. As consultas devolvem cópias imutáveis ou value objects, e os nós e os slots são value objects trocados inteiros |
| 32 | Como o agregado `Network` recusa um pedido esperado, como o de quem não é o dono? | Com um resultado tipado: cada operação devolve um `Outcome` (`ACCEPTED`, `NOT_OWNER`, `INVALID_NAME` e outros), e quem chama decide o que mostrar. Exceção fica só para erro de programação |
| 31 | Qual é a forma de `NetworkId` e `NodeId`, e quem os gera? | UUID nos dois, como o jogo faz com entidades e players. O domínio só recebe o ID: quem chama gera com `UUID.randomUUID()` por uma porta `IdGenerator`, com uma versão fixa nos testes |
| 30 | Quem pode quebrar o Network Controller de outro player? | Só o dono. Para os outros, o bloco não quebra no survival, e explosões não o destroem. No criativo, quebra normalmente |
| 29 | O nó num chunk descarregado e o nó fora do raio precisam de nomes diferentes. Como ficam? | `Unloaded` (Descarregado), em âmbar, mantém o último valor. `Out of range` (Fora de alcance), em vermelho, não conta e vale 0. Ajusta o nome usado na resposta 13, sem mudar o comportamento dela |
| 28 | Um operador do servidor pode alterar ou apagar a rede de outro player? | Não. Só o dono, sem exceção. Detalha a pergunta 22 |
| 27 | Como o Signal Remote entra na rede? | Como uma fonte de entrada sem posição no mundo, com ID próprio, que entra no sinal mais forte do canal. Volta a 0 ao sair do inventário ou ficar fora do alcance, para o canal não ficar preso ligado. Os detalhes do item ficam na issue do Remote |
| 26 | O canal tem só a cor, ou o player pode dar um nome a ele? | Cor fixa e nome opcional. O nome padrão é o da cor, como `Red`, e o dono pode renomeá-lo na aba `Signals`, com o limite de caracteres no config |
| 25 | Em qual rede e em qual canal entra um Signal Node recém-colocado? | Na rede atual do player: a última que ele criou ou abriu no controlador, guardada no servidor. Entra com 1 slot de entrada na face em que foi grudado, no primeiro canal livre. Sem rede atual, fica `No network`. O Configurador troca a rede depois |
| 24 | O que acontece com a rede quando o Network Controller é quebrado? | A rede fica salva no mundo, no servidor, e não no bloco, então funciona com o chunk do controlador descarregado. Quebrado, o item guarda o ID da rede num data component. Sem o controlador, a rede fica `Offline`: os canais vão a 0 e os nós mostram `No network`. Recolocado, ela volta igual |
| 23 | Até onde um nó pode estar do Network Controller? | Na mesma dimensão, dentro de um raio em blocos a partir do controlador, no config, com valor inicial de 128. O valor 0 significa ilimitado. Fora do raio, o nó não conta para o canal. Outra dimensão nunca alcança |
| 22 | Quem pode fazer o quê numa rede? | Só o dono altera a rede: liga nós, configura slots, renomeia a rede e os canais e liga o Signal Remote. Os demais players só veem as telas, em modo de leitura |
| 21 | Até onde vai a fronteira de `domain/` e `application/` no ArchUnit? | Estrita, como no Cubit: `domain/` só depende de si e do Java, e `application/` só de si, de `domain/` e do Java. Um caso de uso usa um adapter do mod só por uma porta. Detalha a regra 10 do `AGENTS.md` |
| 20 | Qual identificador SPDX de licença vai no `neoforge.mods.toml` e nas páginas do mod? | `LGPL-3.0-only`: só a versão 3 da LGPL. Detalha a pergunta 7 |
| 19 | Quais hooks de git entram? | Todos os do Cubit, adaptados: branch, histórico dos `.md` de `_docs/`, formatação, gitleaks, Conventional Commits e atribuição a IA no commit; `check`, GameTests e datagen no push. O gitleaks fica fixado com checksum em `.tools/` |
| 18 | O The One Probe não está no Iron Meridian. Como fica? | Continua opcional e na Fase 1. A versão é a última estável para 1.21.1, decidida quando a issue #23 começar. O mod abre sem ele, conferido pela GameTest da issue #5 |
| 17 | Quais versões de ferramentas a base do projeto fixa? | As mesmas do Cubit: Gradle 9.2.1 com checksum, ModDevGradle 2.0.148, NeoForge 21.1.265 (a do Iron Meridian), Parchment 2024.11.17, JUnit 6.1.3, ArchUnit 1.5.1, JaCoCo 0.8.16, Spotless 8.9.0 com google-java-format 2.0.0 e Checkstyle 14.4.0. Cada uma entra com a issue que a usa |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 09:29 | Responde as perguntas 67 a 69. |
| Marcelo Fabiano | 2026-11-10 09:20 | Responde as perguntas 64 a 66. |
| Marcelo Fabiano | 2026-11-10 09:14 | Responde a pergunta 63. |
| Marcelo Fabiano | 2026-11-10 09:06 | Responde a pergunta 62. |
| Marcelo Fabiano | 2026-11-10 09:05 | Atualiza a resposta 59 com a textura refeita. |
| Marcelo Fabiano | 2026-11-10 08:59 | Responde as perguntas 51 e 59 a 61. |
| Marcelo Fabiano | 2026-11-10 08:54 | Responde as perguntas 55 a 58. |
| Marcelo Fabiano | 2026-11-10 08:52 | Responde as perguntas 53 e 54. |
| Marcelo Fabiano | 2026-11-10 08:47 | Responde as perguntas 49, 50 e 52 e registra a 51 como pendente. |
| Marcelo Fabiano | 2026-11-10 08:30 | Responde a pergunta 48. |
| Marcelo Fabiano | 2026-11-10 08:09 | Responde as perguntas 43 a 47. |
| Marcelo Fabiano | 2026-11-09 17:21 | Responde as perguntas 40 a 42. |
| Marcelo Fabiano | 2026-11-09 16:57 | Responde as perguntas 38 e 39. |
| Marcelo Fabiano | 2026-11-09 16:39 | Responde as perguntas 36 e 37. |
| Marcelo Fabiano | 2026-11-09 16:27 | Responde as perguntas 32 a 35. |
| Marcelo Fabiano | 2026-11-09 16:10 | Responde a pergunta 31. |
| Marcelo Fabiano | 2026-11-09 15:39 | Responde as perguntas 22 a 30. |
| Marcelo Fabiano | 2026-11-09 13:56 | Responde a pergunta 21. |
| Marcelo Fabiano | 2026-11-09 11:50 | Responde as perguntas 17 a 20. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
