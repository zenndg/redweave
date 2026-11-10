# Decisões

O resumo do que foi decidido. O detalhe e o porquê ficam em [`perguntas/`](perguntas/README.md), que prevalece.

| Tema | Decisão | Pergunta |
|---|---|---|
| Escopo | Só sinal de redstone. Itens, fluidos e energia ficam com os mods de logística | 1 |
| Linguagem | Java 21 | 2 |
| Versão | 1.21.1 NeoForge e, depois, 26.x. Só os adapters mudam no port | 3 |
| Multipart | Adiado para a Fase 4 | 4 |
| Fio físico | Só na Fase 4 | 5 |
| Integrações | Jade e The One Probe primeiro, opcionais e isolados em `compat/` | 6 |
| Licença | LGPL-3.0 | 7 |
| Ponte (Fase 5) | MQTT | 8 |
| Nome | Redweave | 9 |
| Processo | O do Cubit: regras, pilares, perguntas numeradas, Gitflow, gates e GameTests | 10, 11 |
| Repositório | `marcelofabianov/redweave`, privado no início, com o board no GitHub | 12 |
| Chunk descarregado | O canal mantém o último valor do nó, e a Tela do Controlador marca o nó como `Unloaded` | 13, 29 |
| Idioma do processo | Issues, PRs e commits em português. Código, IDs e o `README.md` em inglês | 14 |
| Controle remoto | Signal Remote, com o ID `signal_remote` | 15 |
| Board | GitHub Projects é a fonte da verdade. O board do claude.ai é aposentado | 16 |
| Ferramentas de build | As versões do Cubit, com o NeoForge 21.1.265 do Iron Meridian | 17 |
| The One Probe | Opcional, na Fase 1, com a versão decidida na issue #23 | 18 |
| Hooks de git | Os do Cubit, com gitleaks | 19 |
| Licença no metadado | `LGPL-3.0-only` | 20 |
| Fronteira no ArchUnit | Estrita: `domain/` só depende de si e do Java; `application/` só de si, de `domain/` e do Java | 21 |
| Permissões | Só o dono altera a rede, sem exceção para operadores | 22, 28 |
| Alcance | Raio em blocos a partir do controlador, no config (128; 0 é ilimitado), só na mesma dimensão | 23 |
| Controlador quebrado | A rede fica salva no mundo; o item guarda o ID e, sem controlador, a rede fica `Offline` | 24 |
| Nó novo | Entra na rede atual do player, com 1 slot de entrada no primeiro canal livre | 25 |
| Nome do canal | Cor fixa e nome opcional, renomeado pelo dono | 26 |
| Signal Remote | Entrada sem posição, que volta a 0 ao sair do inventário ou do alcance | 27 |
| Estados do nó | `Unloaded` mantém o último valor; `Out of range` vale 0 | 29 |
| Quebrar o controlador | Só o dono; resiste a explosões; no criativo, quebra | 30 |
| IDs da rede e do nó | UUID, gerado fora do domínio por uma porta `IdGenerator` | 31 |
| Recusa no agregado | Resultado tipado `Outcome`; exceção só para erro de programação | 32 |
| Agregado `Network` | Mutável e protegido, com nós e slots imutáveis | 33 |
| Nome inválido | Apara as pontas e recusa vazio ou longo; no canal, vazio volta ao nome da cor | 34 |
| Nome padrão da rede | `Network` e os 4 primeiros caracteres do ID | 35 |
| Forma do alcance | Esfera, pela distância em linha reta | 36 |
| Última leitura | Salva com a rede, para valer depois de reiniciar o servidor | 37 |
| Estado do simulador | Na rede; o `Simulator` não tem estado, e o primeiro tick depois de carregar reaplica as saídas | 38 |
| Saídas na mesma face | A face emite a mais forte | 39 |
| Cor de rede nova | A primeira cor livre entre as redes do dono; com as 16 em uso, o branco | 40 |
| Retorno dos casos de uso | O `Outcome` do domínio, com `UNKNOWN_NETWORK` e `NO_CURRENT_NETWORK` | 41 |
| Trocar um nó de rede | Exige ser dono da rede antiga e da nova | 42 |
| Gates da CI | Os três do `pre-push` em todo push e PR para `develop`, `homolog`, `main` e `release/*` | 43 |
| Versão das actions | Fixada pelo SHA do commit | 44 |
| Conferência extra da CI | Só o gitleaks em todo o histórico | 45 |
| Cache da CI | `cache: gradle` do `actions/setup-java`, sem o `gradle/actions` | 46 |
| Versões das actions | `checkout` v7.0.1, `setup-java` v6.2.0 e `upload-artifact` v7.0.2 | 47 |
| Ordem da Fase 1 | #14, #15, #18 e #19, o primeiro teste manual, e depois #16, #17 e o restante | 48 |
| Controlador que some | Operação sem quem pede deixa a rede `Offline` | 49 |
| Config | `network.range`, `network.name_length`, `channel.name_length` e `node.slots`, no config do servidor | 50 |
| Clique no controlador | Mensagem na action bar até a tela da #20 | 52 |
| Redes até a #16 | Num objeto por servidor, em memória | 53 |
| Página do controlador | `controlador-de-rede.md` | 54 |
| Item com a rede de outro player | Não coloca, com aviso na action bar | 55 |
| Rede que já tem controlador | Não coloca, com aviso na action bar | 56 |
| Item com rede que não existe | Nasce uma rede nova | 57 |
| Colocado sem player real | Não coloca | 58 |
| Texturas da Fase 1 | As do desenvolvedor, extraídas de `_docs/imagens/conceito/texturas.png` | 51 |
| Nó fora de alcance | Vale o `visual.md`, com a textura refeita | 59 |
| Controlador sem nós | `top_on` sem a camada emissiva | 60 |
| Pontos de slot do nó | Textura `signal_node_slot` tingida pelo canal | 61 |
| Testes do controlador | Domínio, aplicação e GameTests | 62 |
| Estado por servidor | Pacote `server/`, com o `RedweaveServer` | 63 |
| Blockstate do controlador | `facing` e `linked` | 64 |
| Componente da rede | `redweave:network` | 65 |
| Faixas do config | Alcance de 0 a 30.000.000, nomes de 1 a 64 e slots de 1 a 9 | 66 |
| NBT do controlador | `network` e `owner` | 67 |
| Nome em português | `Controlador de Rede` | 68 |
| Aba do criativo | Aba própria `Redweave` | 69 |
| Nó ligado ao colocar | Na #15, pelo `PlaceNode`; a #18 fecha junto | 70 |
| Face do slot no mundo | Entrada lê o vizinho daquela face; saída alimenta o bloco de apoio como uma alavanca | 71 |
| Blockstate do nó | `facing` e `powered`; o `powered` vira `signal` pela 99 | 72 |
| Página do nó | `no-de-sinal.md` | 73 |
| Quebrar um nó | Só o dono no survival; a queda sai da rede sem dono | 74 |
| Clique no nó | Mensagem na action bar até a tela da #19 | 75 |
| NBT do nó | `node` | 76 |
| Testes do nó | Domínio, aplicação e GameTests | 77 |
| Desenho da Tela do Nó | Sprites em pixel art, 9-slice, gerados por IA | 78 |
| Cubo da face | 3D com o bloco de apoio real, girável | 79 |
| Abas da Tela do Nó | Só `Slots` por enquanto | 80 |
| Distância da tela | O alcance de interação do jogo | 81 |
| Atualização ao vivo | A cada tick, só o que mudou | 82 |
| Usar um slot livre | Entrada no primeiro canal livre | 83 |
| Liberar um slot | Botão `Clear slot` com confirmação | 84 |
| Modo só leitura | Controles apagados e aviso com cadeado | 85 |
| Nó fora de alcance no mundo | Propriedade `out_of_range` no blockstate | 86 |
| Pontos dos slots | Os 4 primeiros slots, tingidos pelo canal | 87 |
| Onde salvar as redes | Um `SavedData` único no Overworld, `redweave_networks.dat` | 88 |
| Formato do save | Codecs sobre `NbtOps`, com `version` e chaves estáveis; a leitura fica no slot | 89 |
| Restaurar a rede | `NetworkSnapshot` no domínio, com `snapshot()` e `Network.restore` | 90 |
| Rede atual no save | Salva no mesmo arquivo; entrada sem rede é descartada | 91 |
| Save inválido | Descarta a menor parte com aviso; versão desconhecida vira backup e o mod começa vazio | 92 |
| Config menor que o save | A rede volta como estava; o limite vale para as próximas mudanças, com teto nas faixas da 66 | 93 |
| Testes da persistência | Domínio e GameTests; o reinício real no roteiro manual | 94 |
| Slots do nó no save | Chave `slot_count`, de 1 a 9; `slots` só com os em uso | 95 |
| Mundo dos GameTests | Apagado antes de cada execução | 96 |
| Save corrompido | Copiado para `redweave_networks.unreadable.backup.dat` | 97 |
| Valor da lente do nó | O mais forte entre leituras e emissão | 98 |
| Intensidade na lente | Propriedade `signal` (0 a 15), no lugar do `powered`, e tinta por nível | 99 |
| Luz do nó | Só a lente brilha; o nó não é fonte de luz | 100 |
| Issue da lente | #24 | 101 |
| Passo 20 do roteiro | Detalhado, com os passos 22 e 23 do arquivo corrompido | 102 |
| Escopo do Configurator | Item e ações na #17; visor ao vivo e rótulo sobre os nós em issue própria | 103 |
| Configurator no controlador | Só o dono guarda a rede; a rede atual não muda | 104 |
| Copiar um nó | Slots em `redweave:node_slots`, com a face grudada relativa | 105 |
| Colar | Cola várias vezes até o Shift + clique no ar; o nó fica na rede dele | 106 |
| Página do Configurator | `configurador.md` | 107 |
| Mensagens do Configurator | Uma por resultado, na action bar, e tooltip com a rede e a cópia | 108 |
| Testes do Configurator | Domínio, aplicação e GameTests | 109 |
| Label | Item que guarda uma configuração de nó, renomeável e copiável | 110 |
| Quando entra o Label | Issue própria logo depois da #17 | 111 |
| Nome da rede no tooltip | Gravado em `redweave:network_name` ao guardar a rede | 112 |
| Clique com item no nó e no controlador | Vai para o item; só a mão vazia abre a tela ou mostra a mensagem | 113 |
| Nome do Label | `redweave:label`, `Label` e `Etiqueta`; página `etiqueta.md` | 114 |
| Pilha do Label | Até 16; gravados iguais se juntam | 115 |
| Esvaziar o Label | Sozinho na grade de crafting | 116 |
| Receitas do Label | Cópia e esvaziar na #81; o Label vazio na #24 | 117 |
| Gravar e aplicar o Label | Grava um da pilha com o Configurator; aplicar não gasta | 118 |
| Tooltip do Label | Um slot por linha, na cor do canal | 119 |
| Visual do Label | Etiqueta de papel com ilhós de cobre; `label` e `label_written` | 120 |
| Testes do Label | GameTests, e JUnit só para regra nova | 121 |
| Mensagem ao gravar | `Copied <n> slots to a Label`; o Configurator guarda a cópia também | 122 |
| Tipos das receitas do Label | `redweave:label_copy` e `redweave:label_clear` | 123 |
| Arte do Label | Gerada pelo desenvolvedor por IA; a #81 espera | 124 |

Não há perguntas pendentes da concepção.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 16:42 | Registra a decisão 124. |
| Marcelo Fabiano | 2026-11-10 16:30 | Registra as decisões 122 e 123. |
| Marcelo Fabiano | 2026-11-10 16:28 | Registra as decisões 118 a 121. |
| Marcelo Fabiano | 2026-11-10 16:27 | Registra as decisões 114 a 117. |
| Marcelo Fabiano | 2026-11-10 16:06 | Registra a decisão 113. |
| Marcelo Fabiano | 2026-11-10 15:49 | Registra a decisão 112. |
| Marcelo Fabiano | 2026-11-10 15:48 | Registra as decisões 110 e 111. |
| Marcelo Fabiano | 2026-11-10 15:46 | Registra as decisões 107 a 109. |
| Marcelo Fabiano | 2026-11-10 15:45 | Registra as decisões 103 a 106. |
| Marcelo Fabiano | 2026-11-10 15:23 | Registra as decisões 98 a 102. |
| Marcelo Fabiano | 2026-11-10 14:07 | Registra as decisões 96 e 97. |
| Marcelo Fabiano | 2026-11-10 13:43 | Registra a decisão 95. |
| Marcelo Fabiano | 2026-11-10 13:10 | Registra as decisões 92 a 94. |
| Marcelo Fabiano | 2026-11-10 13:08 | Registra as decisões 88 a 91. |
| Marcelo Fabiano | 2026-11-10 11:26 | Registra as decisões 86 e 87. |
| Marcelo Fabiano | 2026-11-10 11:00 | Registra as decisões 78 a 85. |
| Marcelo Fabiano | 2026-11-10 09:45 | Registra as decisões 70 a 77. |
| Marcelo Fabiano | 2026-11-10 09:29 | Registra as decisões 67 a 69. |
| Marcelo Fabiano | 2026-11-10 09:20 | Registra as decisões 64 a 66. |
| Marcelo Fabiano | 2026-11-10 09:14 | Registra a decisão 63. |
| Marcelo Fabiano | 2026-11-10 09:06 | Registra a decisão 62. |
| Marcelo Fabiano | 2026-11-10 09:05 | Atualiza a decisão 59. |
| Marcelo Fabiano | 2026-11-10 08:59 | Registra as decisões 51 e 59 a 61. |
| Marcelo Fabiano | 2026-11-10 08:54 | Registra as decisões 55 a 58. |
| Marcelo Fabiano | 2026-11-10 08:52 | Registra as decisões 53 e 54. |
| Marcelo Fabiano | 2026-11-10 08:47 | Registra as decisões 49, 50 e 52. |
| Marcelo Fabiano | 2026-11-10 08:30 | Registra a decisão 48. |
| Marcelo Fabiano | 2026-11-10 08:09 | Registra as decisões 43 a 47. |
| Marcelo Fabiano | 2026-11-09 17:21 | Registra as decisões 40 a 42. |
| Marcelo Fabiano | 2026-11-09 16:57 | Registra as decisões 38 e 39. |
| Marcelo Fabiano | 2026-11-09 16:39 | Registra as decisões 36 e 37. |
| Marcelo Fabiano | 2026-11-09 16:27 | Registra as decisões 32 a 35. |
| Marcelo Fabiano | 2026-11-09 16:10 | Registra a decisão 31. |
| Marcelo Fabiano | 2026-11-09 15:39 | Registra as decisões 22 a 30. |
| Marcelo Fabiano | 2026-11-09 13:56 | Registra a decisão 21. |
| Marcelo Fabiano | 2026-11-09 11:50 | Registra as decisões 17 a 20. |
| Marcelo Fabiano | 2026-11-09 10:34 | Registra as decisões 13 a 16. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
