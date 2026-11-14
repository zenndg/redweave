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
| Repositório | Mudada pela 170: `zenndg/redweave` | 12, 170 |
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
| Controlador quebrado | A rede fica salva no mundo; o item guarda o ID e, sem controlador, a rede fica `Unplaced` (146) | 24, 135, 146 |
| Nó novo | Nasce `No network` e escolhe a rede numa tela (139); todos os canais nascem inativos (226) | 25, 139, 226 |
| Nome do canal | Cor fixa e nome opcional, renomeado pelo dono | 26 |
| Signal Remote | Entrada sem posição, que volta a 0 ao sair do inventário ou do alcance | 27 |
| Estados do nó | `Unloaded` mantém o último valor; `Out of range` vale 0 | 29 |
| Quebrar o controlador | Só o dono; resiste a explosões; no criativo, quebra | 30 |
| IDs da rede e do nó | UUID, gerado fora do domínio por uma porta `IdGenerator` | 31 |
| Recusa no agregado | Resultado tipado `Outcome`; exceção só para erro de programação | 32 |
| Agregado `Network` | Mutável e protegido, com nós e slots imutáveis | 33 |
| Nome inválido | Apara as pontas e recusa vazio ou longo; no canal, vazio volta ao nome da cor | 34 |
| Nome padrão da rede | Os 8 primeiros caracteres do ID (142) | 35, 142 |
| Forma do alcance | Esfera, pela distância em linha reta | 36 |
| Última leitura | Salva com a rede, para valer depois de reiniciar o servidor | 37 |
| Estado do simulador | Na rede; o `Simulator` não tem estado, e o primeiro tick depois de carregar reaplica as saídas | 38 |
| Saídas na mesma face | A face emite a mais forte | 39 |
| Cor de rede nova | A primeira cor livre entre as redes do dono; com as 16 em uso, o branco | 40 |
| Retorno dos casos de uso | O `Outcome` do domínio, com `UNKNOWN_NETWORK` e `NO_CURRENT_NETWORK` | 41 |
| Trocar um nó de rede | Pelo nome da rede na Tela do Nó, sendo dono das duas redes (141) | 42, 141 |
| Gates da CI | Os três do `pre-push` em todo push e PR para `develop`, `homolog`, `main` e `release/*` | 43 |
| Versão das actions | Fixada pelo SHA do commit | 44 |
| Conferência extra da CI | Só o gitleaks em todo o histórico | 45 |
| Cache da CI | `cache: gradle` do `actions/setup-java`, sem o `gradle/actions` | 46 |
| Versões das actions | `checkout` v7.0.1, `setup-java` v6.2.0 e `upload-artifact` v7.0.2 | 47 |
| Ordem da Fase 1 | #14, #15, #18 e #19, o primeiro teste manual, e depois #16, #17 e o restante | 48 |
| Controlador que some | Operação sem quem pede deixa a rede `Unplaced` (146) | 49, 146 |
| Config | `network.range`, `network.name_length` e `channel.name_length`, no config do servidor; `node.slots` saiu (224) | 50, 224 |
| Clique no controlador | Mensagem na action bar até a tela da #20, sem rede atual (139) | 52, 139 |
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
| Faixas do config | Alcance de 0 a 30.000.000 e nomes de 1 a 64 | 66, 224 |
| NBT do controlador | `network` e `owner` | 67 |
| Nome em português | `Controlador de Rede` | 68 |
| Aba do criativo | Aba própria `Redweave` | 69 |
| Nó ligado ao colocar | Mudada pela 139: o nó nasce `No network` | 70, 139 |
| Face do slot no mundo | Entrada lê o vizinho daquela face; saída energiza só o bloco de apoio, com força (262) | 71, 262 |
| Blockstate do nó | `facing` e `powered`; o `powered` vira `signal` pela 99 | 72 |
| Página do nó | `no-de-sinal.md` | 73 |
| Quebrar um nó | Só o dono no survival; a queda sai da rede sem dono | 74 |
| Clique no nó | Mudada pela 154: a mão vazia mostra uma dica | 75, 154 |
| NBT do nó | `node` | 76 |
| Testes do nó | Domínio, aplicação e GameTests | 77 |
| Desenho da Tela do Nó | Sprites em pixel art, 9-slice, gerados por IA | 78 |
| Cubo da face | 3D com o bloco de apoio real, girável | 79 |
| Abas da Tela do Nó | Sem abas laterais (233) | 80, 233 |
| Distância da tela | O alcance de interação do jogo | 81 |
| Atualização ao vivo | A cada tick, só o que mudou | 82 |
| Usar um slot livre | Na primeira ativação, o canal vira entrada na face grudada (226, 228) | 83, 226, 228 |
| Liberar um slot | Saiu com os 16 slots fixos; o canal é desativado | 84, 224, 319 |
| Modo só leitura | Controles apagados e aviso com cadeado | 85 |
| Nó fora de alcance no mundo | Propriedade `out_of_range` no blockstate | 86 |
| Pontos dos slots | Os 4 primeiros canais ativos, na ordem das cores (229) | 87, 229 |
| Onde salvar as redes | Um `SavedData` único no Overworld, `redweave_networks.dat` | 88 |
| Formato do save | Codecs sobre `NbtOps`, com `version` e chaves estáveis; a leitura fica no slot | 89 |
| Restaurar a rede | `NetworkSnapshot` no domínio, com `snapshot()` e `Network.restore` | 90 |
| Rede atual no save | Mudada pela 157: o save guarda a última rede usada | 91, 157 |
| Save inválido | Descarta a menor parte com aviso; versão desconhecida vira backup e o mod começa vazio | 92 |
| Config menor que o save | A rede volta como estava; o limite vale para as próximas mudanças, com teto nas faixas da 66 | 93 |
| Testes da persistência | Domínio e GameTests; o reinício real no roteiro manual | 94 |
| Slots do nó no save | Os 16 slots fixos; `slot_count` saiu (224) | 95, 224 |
| Mundo dos GameTests | Apagado antes de cada execução | 96 |
| Save corrompido | Copiado para `redweave_networks.unreadable.backup.dat` | 97 |
| Valor da lente do nó | O mais forte entre leituras e emissão | 98 |
| Intensidade na lente | Propriedade `signal` (0 a 15), no lugar do `powered`, e tinta por nível | 99 |
| Luz do nó | Só a lente brilha; o nó não é fonte de luz | 100 |
| Issue da lente | #24 | 101 |
| Passo 20 do roteiro | Detalhado, com os passos 22 e 23 do arquivo corrompido | 102 |
| Escopo do Configurator | Rótulo flutuante e visor do modo na #80 (159) | 103, 159 |
| Configurator no controlador | Revogada pela 126 | 104, 126 |
| Copiar um nó | Slots em `redweave:node_slots`, com a face grudada relativa; o gesto é o clique no modo `Copy` (127, 143) | 105, 127 |
| Colar | Shift + clique no modo `Copy`, várias vezes, até o Shift + clique no ar (127, 143) | 106, 127 |
| Página do Configurator | `configurador.md` | 107 |
| Mensagens do Configurator | Uma por resultado, na action bar, sem as de guardar e ligar (126, 143) | 108, 126 |
| Testes do Configurator | Domínio, aplicação e GameTests, sem guardar e ligar (126) | 109, 126 |
| Label | Revogada pela 125 | 110, 125 |
| Quando entra o Label | Revogada pela 125 | 111, 125 |
| Nome da rede no tooltip | Revogada pela 126 | 112, 126 |
| Clique com item no nó e no controlador | Vai para o item; no controlador, só a mão vazia abre (140, 154) | 113, 154 |
| Nome do Label | Revogada pela 125 | 114, 125 |
| Pilha do Label | Revogada pela 125 | 115, 125 |
| Esvaziar o Label | Revogada pela 125 | 116, 125 |
| Receitas do Label | Revogada pela 125 | 117, 125 |
| Gravar e aplicar o Label | Revogada pela 125 | 118, 125 |
| Tooltip do Label | Revogada pela 125 | 119, 125 |
| Visual do Label | Revogada pela 125 | 120, 125 |
| Testes do Label | Revogada pela 125 | 121, 125 |
| Mensagem ao gravar | Revogada pela 125 | 122, 125 |
| Tipos das receitas do Label | Revogada pela 125 | 123, 125 |
| Arte do Label | Revogada pela 125 | 124, 125 |
| Label | Removido do mod e dos documentos | 125 |
| Responsabilidade do Configurator | Não guarda a rede; tem os modos `Open`, `Copy`, `Toggle` e `Wrench`, e colar num nó sem rede o liga à rede da cópia (238) | 126, 238, 241 |
| Gestos de copiar e colar | Clique copia, Shift + clique cola, no modo `Copy` | 127, 143 |
| Apagar a cópia | Shift + clique no ar, em qualquer modo | 128 |
| Controlador e rede | 1 Network Controller = 1 rede, que nasce ao colocá-lo | 129 |
| Network Terminal | Bloco aberto com a mão vazia que administra as redes do dono; não cria redes | 130 |
| Ações do Terminal | Listar, renomear, `Offline` e `Online`, limpar nós e abrir a tela do controlador | 131 |
| Excluir rede | Não há para rede com controlador: o Terminal desliga e religa; `Delete` só para `Unplaced` (166) | 132, 166 |
| Limpar nós | Tira todos os nós da rede, com confirmação; eles ficam `No network` | 133 |
| Dono do Terminal | Quem o colocou; só ele usa | 134 |
| Estados sem sinal | `Offline`, desligada no Terminal, e `Unplaced`, sem controlador no mundo | 135, 146 |
| Quebrar numa rede Offline | O dono quebra normalmente | 136 |
| Terminais por player | Um, protegido como o controlador | 137 |
| Ordem da lista do Terminal | Alfabética | 138 |
| Entrar numa rede | Tela de escolha na primeira abertura; acaba a rede atual | 139 |
| Abrir a Tela do Nó | Pelo Configurator, no modo `Open` | 140 |
| Trocar de rede | Pelo nome da rede na Tela do Nó | 141 |
| Nome padrão | Os 8 primeiros caracteres do ID, no nó e na rede | 142 |
| Modos do Configurator | `Open`, `Copy`, `Toggle` e `Wrench`, trocados com Shift + scroll | 143, 161, 164, 241 |
| Nome do nó | Renomeado na Tela do Nó e na lista do controlador; `node.name_length` | 144 |
| Limite de redes | `network.max_per_player`, 10, conferido ao colocar o controlador | 145 |
| Nome do estado sem controlador | `Unplaced`, `Não colocada` | 146 |
| Log do controlador | Só mudanças, em buffer de `log.size` linhas, só em memória | 147 |
| Hora do log | Data e hora reais do servidor, por uma porta | 148 |
| Posição dos nós | Só o dono vê | 149 |
| Gráfico de 30 segundos | Substituído pelo log | 150 |
| Quando entra o Terminal | Fase 1, antes da 0.1, depois da #20 | 151 |
| Alcance do Terminal | Todas as redes do dono, em qualquer lugar | 152 |
| Terminal de outro player | Mensagem na action bar | 153 |
| Mão vazia no nó | Dica `Use a Configurator to open this node.` | 154 |
| Nome do Terminal | `redweave:network_terminal`, `Terminal de Rede`; página `terminal-de-rede.md` | 155 |
| Renomear a rede | No Terminal e na tela do controlador | 156 |
| Redes na escolha | Só as `Online`; a última usada fica salva | 157 |
| Rede Offline no Terminal | Só a linha da lista | 158 |
| Issue #80 | Rótulo flutuante e visor do modo | 159 |
| Aviso de rede Offline | Na action bar | 160 |
| Modo Chave | Retira o nó como item limpo, que vai ao inventário ou ao chão | 161 |
| Quem usa a Chave | Só o dono: da rede, ou quem colocou o nó sem rede | 162, 165 |
| Chave numa rede Offline | Funciona | 163 |
| Blocos da Chave | Só o Signal Node | 164 |
| Dono do nó sem rede | Quem o colocou, na chave de NBT `owner` | 165 |
| Apagar rede | Só as `Unplaced`, pelo Terminal | 166 |
| Cores dos modos | `Open` azul, `Copy` verde, `Toggle` âmbar, `Wrench` vermelho | 167, 241 |
| Uso da cor do modo | Com o nome escrito na action bar e no tooltip; no visor, só a cor (271) | 168, 271 |
| Tons dos modos | `#5B9BF0`, `#3FAE5A`, `#E3A33B` (âmbar, `Toggle`) e `#D94A64` | 169, 241 |
| Repositório | `zenndg/redweave`, com o host `github.com-zenndg` | 170 |
| Slot do nó ao entrar | Na primeira ativação, o canal vira entrada na face grudada (226, 228) | 171, 226, 228 |
| Quem escolhe a rede do nó | Só quem o colocou | 172 |
| Roadmap | `ROADMAP.md` na raiz, em inglês | 180 |
| Nascimento da v0.1.0 | No fim das Fases 1 e 2, que formam uma fase só (303), depois da #26 e do roteiro manual, pela #27 | 181, 303 |
| Papel do roadmap | Visão por fase; o GitHub Projects continua a fonte da verdade | 182 |
| Issues do novo modelo | #85 a #90, e a #84 do registro | 183 |
| Nome no histórico | `Zennd`, como nos commits | 184 |
| Componente do modo | `redweave:configurator_mode`: `open`, `copy`, `toggle` e `wrench` | 173, 241 |
| Chaves novas do save | `name` no nó, `offline` na rede e `last_used` no lugar de `current`, na `version` 1 | 174 |
| Registro do Terminal | Lista `terminals` no save, e `owner` na BlockEntity | 175 |
| Rede Unplaced no Terminal | Renomear, limpar nós e `Delete` | 176 |
| Visual da rede Offline | Estado `Offline` em cinza, lente e núcleo apagados | 177 |
| Recolocar uma rede Offline | Volta `Offline` | 178 |
| Lista de nós | Nome, tipo, sinal da lente e estado; ao expandir, a posição, só para o dono, e os canais ativos (234, 281, 335) | 179, 234, 281, 335 |
| Imagens do mod | Toda tela, ícone, item e bloco tem textura, gerada pelo desenvolvedor a partir do prompt do agente | 185 |
| Onde fica a arte | `tmp/arte/{id}/`, com o `prompt.md` e a imagem pronta | 186 |
| Prompt de continuidade | Só com o contexto da sessão em 80% ou mais | 187 |
| Abrir o nó na #86 | Só o Configurator no modo `Open`; a mão vazia vale só no controlador e no Terminal | 188 |
| Dono do nó | O `owner` entra na #86, com a Chave | 189 |
| Textos dos modos | `Mode: <modo>`, `Node picked up` e as dicas por modo | 190 |
| Testes da #86 | JUnit no ciclo dos modos e GameTests no mundo | 191 |
| Sentido do scroll | Como a hotbar: para baixo avança | 192 |
| Nó sem `owner` | A Chave recusa; a mão ainda quebra | 193 |
| `LinkNode` | Fica na aplicação para a troca de rede da #87 | 194 |
| Colar sem cópia | Nada, sem mensagem | 195 |
| Escopo da cobertura | `domain/` e `application/`, no `pre-push` | 196 |
| Mínimo da cobertura | 85%, com meta de 95% | 197 |
| Desenho da escolha de rede | Prancha nova de arte | 198 |
| Sem redes Online | Aviso com o próximo passo | 199 |
| Nome do nó na troca de rede | Continua | 200 |
| Clique do dono no controlador | `<nome> · owner: <dono>`, como os outros | 201 |
| Linha da escolha de rede | Cor, nome e quantidade de nós | 202 |
| Ordem da escolha de rede | Última usada no topo, depois alfabética | 203 |
| Renomear o nó | Clique no nome vira campo de texto | 204 |
| Voltar da troca de rede | Botão `Back` | 205 |
| Nome do nó no save | Opcional, como o do canal: a chave `name` só existe quando o dono renomeia | 206 |
| Testes da #87 | No agregado `Network` e nos casos de uso públicos | 207 |
| Link da rede na Tela do Nó | Linha própria no cabeçalho, em qualquer estado com rede; a pílula mostra só o estado | 208 |
| `Offline` antes do Terminal | No domínio, no save e nos efeitos, com GameTests; o Terminal da #89 liga o botão | 209 |
| Chave `offline` no save | Só numa rede desligada | 210 |
| Aviso do limite de redes | O limite e como liberar espaço, no Terminal | 211 |
| Cor da rede na Tela do Controlador | Clique no quadrado da cor, com a paleta de 16 cores | 212 |
| Entrega da #20 | Uma issue e um PR, com as abas `Nodes`, `Signals` e `Access` | 213 |
| Prancha da Tela do Controlador | Uma só, já com a aba `Log` da #90 | 214 |
| Aba `Log` na #20 | Não aparece; a #90 a acrescenta | 215 |
| Ordem da aba `Nodes` | A ordem em que os nós entraram na rede | 216 |
| Renomear o nó na aba `Nodes` | Duplo clique no nome, para o dono; o clique abre os slots (muda a 217) | 217 e 222 |
| Linhas abertas na aba `Nodes` | Uma por vez | 218 |
| Contagens do rodapé com zero | Sempre aparecem, nas cores | 219 |
| Aba `Nodes` sem nós | Texto de ajuda no meio da tabela | 220 |
| Nome do canal na Tela do Nó | Entra na #20, com os nomes enviados só quando mudam | 221 |
| Renomear na Tela do Controlador | Duplo clique no nome do nó e do canal | 222 |
| Nome do slot | Revogada pela 277 | 223, 277 |
| Painel da Tela do Nó sem o nome | As linhas sobem e fecham o espaço | 278 |
| Slots do Signal Node | 16 fixos, um por cor, com status ativo ou inativo; saem `node.slots` e os upgrades | 224 |
| Redesenho dos slots | Dentro da #20 | 225 |
| Nó novo | Todos os canais inativos; o player ativa cada um | 226 |
| Nome do slot | Revogada pela 277 | 227, 277 |
| Ativar e desativar | Desativar guarda direção e face; só com `Disabled` se configura (237) | 228, 237 |
| Cantos da placa | Os 4 primeiros canais ativos | 229 |
| Cópia do Configurator | Os 16 slots inteiros e o ID da rede de origem (238, 277) | 230, 238, 277 |
| Arte da Tela do Nó com 16 cores | Prancha nova; a tela espera a arte | 231 |
| Configurator no controlador | O modo `Open` abre a tela, como a mão vazia | 232 |
| Aba `Slots` da Tela do Nó | Sai; a tela fica sem abas laterais | 233 |
| Linha aberta no controlador | Só os canais ativos (277) | 234, 277 |
| Saves de teste no formato antigo | Sem conversão; a rede é descartada e o teste usa um mundo novo | 235 |
| Contagens do rodapé do controlador | Sem a palavra "nodes", para caber na linha | 236 |
| Configurar um canal | Só com `Disabled`; `Enabled` trava a direção e a face | 237 |
| Colar num nó sem rede | Liga o nó à rede da cópia, para quem o colocou e é dono dela | 238 |
| Colar num nó com rede | Só aplica os slots, sem trocar a rede | 239 |
| Contagem da cópia | Todos os canais configurados | 240 |
| Modo `Toggle` do Configurator | Clique ativa os configurados; Shift + clique desativa | 241 |
| Buffer do log | Fora do agregado: `SignalLog` no domínio e um registro por rede na aplicação | 242 |
| Linha de saída do log | Por slot de saída, inclusive a queda para 0 por `Offline`, `Unplaced` ou fora de alcance | 243 |
| Log ao carregar a rede | O primeiro tick não gera linhas | 244 |
| Nomes no log | Os do momento da mudança | 245 |
| Ativar e desativar uma saída no log | Gera linha, porque o valor emitido muda | 246 |
| Testes do log | JUnit no domínio e nos casos de uso, GameTests no mundo | 247 |
| Log vazio | `No signal change yet.` no meio da tabela | 248 |
| Aba `Log` em português | `Registro` | 249 |
| Entrega do Network Terminal | Um PR; a arte vem primeiro, e a tela espera por ela | 250 |
| Visual do Network Terminal | Console de bloco cheio com visor na frente, um estado só | 251 |
| Ações na tela do Terminal | Rede selecionada e barra de botões; confirmação no segundo clique | 252 |
| Terminal perdido | Conferido ao colocar outro, só com o chunk carregado | 253 |
| Segundo Terminal | Aviso com a posição do Terminal do player | 254 |
| Textos da tela do Terminal | `Open`, `Turn off`, `Turn on`, `Clear nodes`, `Delete`, confirmação, contagem e dica | 255 |
| Terminal sem redes | Texto que ensina a criar uma rede | 256 |
| Testes do Terminal | JUnit no domínio e na aplicação, GameTests no mundo | 257 |
| Cobre das texturas do Terminal | Igual ao do Network Controller | 258 |
| `Open` do Terminal | A Tela do Controlador fica ancorada no Terminal | 259 |
| Fechar a tela aberta pelo Terminal | Esc fecha tudo | 260 |
| Contagem do topo do Terminal | Singular e plural | 261 |
| Emissão do Signal Node | Só no bloco de apoio, com força; muda a 71 | 262 |
| Onde entra a emissão nova | Na #80 | 263 |
| `Clear log` | Limpa o log da rede, só o dono, com confirmação no segundo clique, na #80 | 264 a 266 |
| Visor do Configurator | Só a cor do modo e o pixel da cópia; o nome na action bar e no tooltip | 267 e 271 |
| Rótulo sobre os nós: quando | Mão principal, até `configurator.label_range` (16) | 268 |
| Rótulo sobre os nós: sincronia | Pacote só para quem segura, só o que mudou | 269 |
| Rótulo sobre os nós: conteúdo | Duas linhas: `■ rede`, pequena, e o nome do nó, um pouco maior; sem canais nem estado | 270, 274 a 276 |
| Nome do slot | Sai antes da v0.1; ficam o nome do canal da rede e o do nó | 277 |
| Testes da #80 | JUnit no conteúdo, nas mudanças e no `Clear log`; GameTests no mundo | 272 |
| Textura `configurator_channel` | Removida | 273 |
| Signal Remote: forma e fluxo | Tablet; Shift + clique direito escolhe a rede e o nó; clique direito dispara | 279 |
| Signal Remote: lista de nós | Só nós com slot de input ativo; o tablet copia o papel desse input | 280, 282 |
| Colunas das listas de nós | Estado em texto e cor; tipo `In`, `Out`, `In/Out` ou `—`; na Tela do Controlador e no Remote, num PR próprio antes da #22 | 281, 283, 284, 290, 311 |
| Signal Remote: filtro e slot | Mostra também `Unloaded` e `Out of range`; com vários inputs, um passo escolhe o slot | 285, 286 |
| Signal Remote: vínculo | Rede, nó e slot no item; `Link lost` quando quebra; troca a qualquer momento, e o canal antigo volta a 0 | 287, 291 |
| Signal Remote: modos | Alavanca e botão, num seletor na tela; pulso no config | 288 |
| Signal Remote: sinal mais forte | Aviso no tooltip e na action bar | 289 |
| Timer vinculado | Na v0.1.0, em issue própria depois da #22; placa grudada, com textura, tela e visual de relógio moderno; escreve direto num canal ou numa borda, sem vínculo a um slot | 292, 293, 313, 375 |
| Inversor de sinal | Na v0.1.0, em issue própria; placa binária como a tocha, com entrada e saída por lado físico ou canal | 294, 296, 313 |
| Loop de sinal | Cancelado: repete o papel do timer | 295 |
| Família de módulos | `Signal Modules`: placas grudadas com base comum, que nasce na #103; entrada e saída por lado físico ou canal | 297, 300, 301, 313 |
| Módulos da Fase 2 | Agenda, Detector de player, Clima, Aleatório, Memória, Limiar, Atraso longo, Extensor de pulso, Detector de borda e Trava, com issues criadas já | 297 a 299, 302 |
| Fases 1 e 2 na v0.1.0 | Uma fase só, com ordem única por dependência e dificuldade; uma issue e um PR por item; #35 fechada e versões renumeradas | 303 |
| Ordem dos módulos | Os outros módulos dependem do Signal Inverter, que prova a base logo depois do Signal Timer | 304 |
| Divisão da #30 | Contador cedo e sem dependência, como Signal Module; o temporizador da lógica por canal sai, coberto pelo Signal Timer | 305, 308 |
| Posição da #23 | Depois de todos os blocos novos, antes das receitas | 306 |
| Refinamento máximo | Todo módulo novo passa por rodadas de perguntas até não restar furo, antes dos docs e do código | 307 |
| Lógica de sinal | Só nos Signal Modules; sai o campo `Logic` do nó; #29 cancelada; Contador vira módulo | 308 |
| Portas lógicas | Viram Signal Module de duas entradas (AND, OR, XOR, NAND), com o modo por Shift + scroll e pela tela | 309, 310, 313 |
| Porta NOT | É o Signal Inverter, que usa a regra NOT do domínio | 310 |
| Nó invisível | Sai do README; ideia para depois da v0.1.0 | 312 |
| Forma dos módulos | Placa grudada em qualquer face, como o Signal Node | 313 |
| Lado físico dos módulos | As 4 bordas da placa, com a face e 4 rotações, como no Project Red | 314 |
| Módulos e multipart | Cada placa no seu espaço na v0.1.0; a #44 considera os módulos e a migração | 315 |
| Documentação atualizada | Regra 42, crítica: toda mudança atualiza no mesmo PR os `.md` afetados | 316 |
| Regra 3 | No config: alcance, nomes, intervalos e carga; 16 canais e 16 slots fixos | 317 |
| O que ainda não existe | Marcado como planejado: grafo na Fase 3, detalhes do Remote na #22 | 318 |
| Ajustes da auditoria | `Clear slot` saiu, ponteiro da 278, sem upgrades, estados do controlador pelo código | 319 |
| Arquivo das perguntas da Fase 2 | `fase-1-sem-fio.md`, porque as Fases 1 e 2 formam a v0.1.0 | 320 |
| Entrada das portas | Binária: ligada acima de 0 | 321 |
| Saída das portas | Ligada emite 15, desligada 0 | 322 |
| Pacote das portas e dos módulos | Portas e NOT em `domain/circuit/`; módulos em `domain/module/`, na #103 | 323 |
| Forma das portas | `enum Gate` com `output(a, b)`; o NOT na classe `Not`; a chave do save entra na #31 | 324, 325 |
| Ordem das portas | `AND`, `OR`, `XOR`, `NAND`, também a do Shift + scroll | 326 |
| Testes das portas | Tabela literal com as forças 0, 1 e 15 | 327 |
| Documento das portas | `_docs/funcionalidades/portas-logicas.md` | 328 |
| Espaço da linha `Logic` | O painel e a Tela do Nó encolhem 60 px, para 90 e 190 px; a escolha de rede mostra 4 redes. Mudado pela 348 | 329, 333, 348 |
| Dica da Fase 2 no painel | Sai sem substituta | 330 |
| Prancha da Tela do Nó | Nova, gerada por IA a partir de `tmp/arte/tela_do_no/` | 331 |
| Testes da #106 | Paridade, `check-generated` e os passos 103 a 106 do roteiro manual | 332, 334 |
| Colunas da lista de nós | Nó, Tipo, Sinal e Estado; a posição vai para o topo da linha aberta, só para o dono | 335 |
| Cor do estado na lista | Só no texto da coluna de estado | 336 |
| Tipo do nó | `NodeKind` no domínio, por `Node.kind()` | 337 |
| Nó de rede `Offline` nas listas | Decidido na #22; o componente já exibe `Offline` em cinza | 338 |
| Textos das colunas novas | `Type` e `State`; `In`, `Out`, `In/Out` e `—`, e `Ent`, `Saí`, `Ent/Saí` e `—` | 339 |
| Posição na linha aberta | Sub-linha cinza `Position: x, y, z`, só para o dono | 340 |
| Testes da #107 | JUnit do `NodeKind` e do `NodeRow`, GameTest do menu e roteiro manual | 341 |
| Prancha da Tela do Controlador | Nova, gerada por IA a partir de `tmp/arte/tela_do_controlador/` | 342 |
| Terminal com o Configurator | Abre no modo `Open`, como a mão vazia, como o controlador; entra no PR #121 | 343, 344 |
| Slots do Signal Node | Continuam 16; o layout da Tela do Nó melhora numa issue própria | 345 |
| Layout da Tela do Nó | Painel em duas colunas: controles e medidor à esquerda, cubo de 40 px à direita | 346 |
| Rodapé da Tela do Nó | Canais ativos à esquerda; cadeado só para os outros, à direita | 347 |
| Tamanho da Tela do Nó | Painel de 76 px, rodapé de 20 px, tela de 168 px; escolha de rede com 3 redes | 348 |
| Ordem da reorganização da Tela do Nó | Issue nova, logo depois da #107 e antes da #22 | 349 |
| Prancha da Tela do Nó | Uma só, já com o layout novo | 350 |
| Shift + scroll no macOS | Sem rolagem vertical, vale a horizontal invertida, como a hotbar; corrigido no PR #121 | 351 |
| Valor do Signal Remote | Sempre 15 | 352 |
| Pulso do Signal Remote | `remote.pulse_ticks`: 20 ticks, de 2 a 200 | 353 |
| Alcance do Signal Remote | O `network.range`, a partir do player, na mesma dimensão | 354 |
| Saída do Signal Remote | Checagem por tick só dos tablets ligados | 355 |
| Estado do Signal Remote | No item; conta só no inventário de player online e no alcance; nada no save da rede | 356 |
| Permissões do Signal Remote | O dono vincula; quem segura dispara | 357 |
| Vários Signal Remotes | Um ID por tablet; vale o mais forte; cópias contam uma vez | 358 |
| Signal Remote e rede `Offline` | Só redes `Online` na lista; vinculado a uma desligada, não dispara | 338, 359 |
| Signal Remote no domínio | Parte `RemoteInputs` do agregado, entregue a cada tick, fora do save | 360 |
| Signal Remote na Tela do Controlador | Contagem `<n> remotes on` no rodapé | 361, 364 |
| Visual do Signal Remote | Arte nova com 4 estados; texturas e prancha antigas apagadas | 362 |
| PR do Signal Remote | Um só, na #22 | 363 |
| Tela do vínculo do Signal Remote | Vínculo atual e seletor de modo no topo, a lista do passo e `Back` | 365 |
| Tooltip do Signal Remote | Vínculo, modo, estado, a regra do mais forte e o atalho | 366 |
| Testes do Signal Remote | JUnit do domínio e dos casos de uso, GameTests do mundo e dos menus, roteiro manual | 367 |
| Nome em português do Signal Remote | `Controle de Sinal` | 368 |
| Clique durante o pulso | Recomeça o pulso; o fim fica no item | 369 |
| Action bar do disparo | Canal e estado, e o aviso da outra fonte | 370 |
| Pilha do Signal Remote | Um por slot | 371 |
| Face na Tela do Nó | Só o nome da face ao lado do cubo, sem o rótulo `Block face` | 372 |
| Rodapé da Tela do Controlador | Contagens à direita: `unloaded` e `remotes on` na linha 1, `out of range` na linha 2 | 373 |
| Artes aprovadas | Entram na #22: pranchas das telas e do tablet, 4 texturas, as antigas saem | 374 |
| Módulo na rede | Escolhe rede `Online` e canal direto na tela, sem nó; conta no alcance medido da placa | 375 |
| Entrada e saída dos módulos | Cada uma é uma borda ou um canal, nunca os dois | 376 |
| Bordas dos módulos | Fixas por módulo, orientadas pela rotação da placa | 377 |
| Dono do módulo | Quem colocou; os outros veem a tela só para leitura; canal só das redes do dono | 378 |
| Ciclo do Signal Timer | Intervalo e pulso, com o pulso menor que o intervalo; faixas no config | 379 |
| Controle do Signal Timer | `Running` ou `Stopped` na tela e a entrada `Pause`, que para e zera a saída | 380 |
| Timer descarregado ou reiniciado | Continua do ponto: progresso e estado no NBT; congela sem tick | 381 |
| Unidade do Signal Timer | Segundos com décimos, passos de 0,1 s; ticks no domínio e no save | 382 |
| Bordas do Signal Timer | Saída na frente, `Pause` atrás | 383 |
| Limites do Signal Timer | `timer.min_interval_ticks` 4 (4 a 200) e `timer.max_interval_ticks` 6000 (200 a 72000); pulso de 2 ticks ao intervalo menos 2 | 384 |
| Timer e o estado da rede | Como um nó: fora do alcance ou rede não `Online` não conta; ciclo continua; rede apagada deixa a conexão sem canal | 385, 420 |
| Timer descarregado no canal | Vale 0; sem lista de módulos, o `Unloaded` não aparece em tela | 386, 388 |
| Módulos no domínio | Conjunto por tick numa parte nova do agregado, fora do save da rede; configuração e ciclo no NBT do bloco | 387 |
| Módulos na Tela do Controlador | Só a contagem `<n> modules on` no rodapé | 388 |
| Nome do módulo | Opcional, como o do nó, com o nome do módulo como padrão | 389 |
| Signal Timer ao ser colocado | `Stopped`, saída na frente, `Pause` atrás, 1 s e 0,5 s | 390 |
| Limite do nome do módulo | `module.name_length`, 16 (1 a 64); sem nome próprio, os 8 primeiros caracteres do ID, como o nó | 391, 427 |
| Módulos no log | Entram, com o nome do módulo e o canal | 392 |
| Abrir a tela do módulo | Só com o Configurator no `Open`, como o nó | 393 |
| Quebrar o módulo | Como o nó: só o dono no survival, item simples sem configuração | 394 |
| Configurator no módulo | Mão vazia só avisa; `Toggle` alterna `Running` e `Stopped`; `Wrench` retira; `Copy` sem ação | 395 |
| Linha do módulo no log | Como a saída de um nó, com o nome do módulo; o `Pause` lido não entra | 396 |
| Rede do módulo | Uma por módulo; cada conexão em canal escolhe um dos 16 canais dela | 397, 420 |
| Ajuste do intervalo e do pulso | Só `−` e `+`: 0,1 s no clique, 1 s com Shift | 398 |
| Rotação do módulo | Pela direção do player ao colocar; Shift + clique com o Configurator no `Open` gira 90° | 399 |
| Rede e canal na tela do módulo | Escolha de rede e grade de 16 cores da Tela do Nó; seletor `Edge` ou `Channel` por conexão | 400, 420 |
| Visual do Signal Timer no mundo | Seta, mostrador e dois LEDs por estado do bloco; animação só na tela | 401 |
| Módulo numa rede `Offline` | Como o nó: `Open` e `Toggle` não agem, `Wrench` sim | 402 |
| Giro da placa | 90° no sentido horário, com som e `Output: <borda>` na action bar | 403 |
| Tela do Signal Timer | Relógio com anel e botão à esquerda; intervalo, pulso e conexões à direita; rede e cadeado no rodapé; prancha por IA | 404, 420 |
| PR da #103 | Um só, com commits atômicos | 405 |
| Testes da #103 | JUnit do ciclo, da parte nova e dos casos de uso; GameTests do mundo; roteiro manual | 406 |
| Borda de saída | 15 ou 0, com força no bloco à frente, como o repetidor | 407 |
| Início do ciclo | Do zero, com o pulso primeiro; `Pause` e `Stopped` zeram o progresso | 408 |
| Ajuste com o timer rodando | Recomeça o ciclo; pulso reduzido se não couber | 409 |
| Nome em português | `Temporizador de Sinal`; família `Módulos de Sinal` | 410 |
| Save do Signal Timer | `redweave:signal_timer`; estados `facing`, `front`, `powered`, `running`; NBT com `module_id`, `owner`, `name`, `network`, o ciclo e as conexões como `edge` ou cor | 411, 420, 428 |
| Entrada `Pause` pela borda | Como a do repetidor; acima de 0 pausa | 412 |
| `modules on` no rodapé | Linha 2, depois de `out of range` | 413 |
| `Channel` sem rede | Abre a escolha de rede e depois a grade | 414 |
| Base comum e módulo | Base: placa, dono, nome, rede, conexões, agregado, Configurator, quebra, NBT e tela comuns; módulo: bordas, regra, `Toggle`, NBT próprio, centro da tela e arte | 415, 420 |
| Colocação do módulo | Como o nó: qualquer face que não seja ar | 416 |
| Rótulo sobre os módulos | Entra na #103: nome, estado e canal da saída | 417 |
| Arte do Signal Timer | Placa nas 4 combinações de LEDs, ícone do item e prancha da tela | 418 |
| Páginas da #103 | `modulos-de-sinal.md`, a base, e `signal-timer.md`, o timer | 419 |
| Entrada e saída do módulo | Conexão, `Connection`; "porta" é só o `Gate` | 420 |
| Mensagens dos módulos | Recolhido, só quem colocou, `Toggle` com o estado, giro com a borda, mão vazia, `Copy` ainda não, `Offline` | 421 |
| Tamanho da placa do módulo | 10 × 10 e 2 de espessura, como o nó | 422 |
| Prancha da Tela do Signal Timer | Entra como está; no rodapé vale a tabela do `telas.md` | 423 |
| Contagem `modules on` | Os módulos que contam, valendo 0 ou 15 | 424 |
| Timer fora da faixa ao carregar | Ajusta à faixa nova, recomeça o ciclo e avisa no log | 425 |
| Ações do Terminal no agregado | Saem para a fachada `network.terminal()`, para o `Network.java` caber em 400 linhas | 426 |
| Nome padrão do módulo | Os 8 primeiros caracteres do ID, como o nó, porque o servidor não sabe o idioma | 427 |
| Rotação no estado do bloco | `front`, a direção real da frente, com 6 valores, no lugar de `rotation` | 428 |
| Modelo da placa nas 24 orientações | Duas versões de cada modelo, com o topo girado 0° ou 90° no UV, mais o `x` e o `y` do blockstate | 429 |
| Dicas do Configurator com os módulos | `Open`, `Toggle` e `Wrench` citam nós e módulos; o `Copy` fica igual | 430 |
| Giro do módulo numa rede `Offline` | A placa gira; só a tela e o `Toggle` ficam bloqueados | 431 |
| Som do giro do módulo | O clique do comparador, no mundo | 432 |
| Estados do timer em português | `Rodando`, `Parado` e `Pausado`; em minúsculas na action bar | 433 |
| Relógio da Tela do Signal Timer | Mostrador em sprite gerado por IA; anel de progresso e arco do pulso em código | 434 |

Não há perguntas pendentes da concepção.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 12:52 | Registra a decisão 434, da #103. |
| Zennd | 2026-11-14 12:35 | Registra a decisão 433, da #103. |
| Zennd | 2026-11-14 12:34 | Registra a decisão 432, da #103. |
| Zennd | 2026-11-14 12:31 | Registra as decisões 430 e 431, da #103. |
| Zennd | 2026-11-14 12:08 | Registra a decisão 429, da #103. |
| Zennd | 2026-11-14 11:13 | Registra a decisão 428, da #103. |
| Zennd | 2026-11-14 10:44 | Registra a decisão 427, da #103. |
| Zennd | 2026-11-14 10:35 | Registra a decisão 426, da #103. |
| Zennd | 2026-11-14 10:24 | Registra as decisões 424 e 425, da #103. |
| Zennd | 2026-11-14 10:19 | Registra a decisão 423, da #103. |
| Zennd | 2026-11-14 10:01 | Registra a decisão 422, da #103. |
| Zennd | 2026-11-14 09:47 | Registra a decisão 421, da #103. |
| Zennd | 2026-11-14 09:46 | Corrige o `Unloaded` do timer descarregado pela decisão 388. |
| Zennd | 2026-11-14 09:44 | Registra a decisão 420 e troca "porta" por "conexão" nas decisões da #103. |
| Zennd | 2026-11-14 09:41 | Registra a decisão 419, da #103. |
| Zennd | 2026-11-14 09:37 | Registra as decisões 415 a 418, da #103. |
| Zennd | 2026-11-14 09:35 | Registra as decisões 411 a 414, da #103. |
| Zennd | 2026-11-14 09:30 | Registra as decisões 407 a 410, da #103. |
| Zennd | 2026-11-14 09:29 | Registra as decisões 403 a 406, da #103. |
| Zennd | 2026-11-14 09:27 | Registra as decisões 399 a 402, da #103. |
| Zennd | 2026-11-14 09:22 | Registra as decisões 395 a 398, da #103. |
| Zennd | 2026-11-14 09:20 | Registra as decisões 391 a 394, da #103. |
| Zennd | 2026-11-14 09:17 | Registra as decisões 387 a 390, da #103. |
| Zennd | 2026-11-14 09:15 | Registra as decisões 383 a 386, da #103. |
| Zennd | 2026-11-14 09:13 | Registra as decisões 379 a 382, da #103. |
| Zennd | 2026-11-14 09:08 | Registra as decisões 375 a 378, da #103. |
| Zennd | 2026-11-12 21:36 | Registra as decisões 372 a 374. |
| Zennd | 2026-11-12 20:48 | Registra as decisões 368 a 371. |
| Zennd | 2026-11-12 20:38 | Registra as decisões 364 a 367. |
| Zennd | 2026-11-12 20:37 | Registra as decisões 360 a 363. |
| Zennd | 2026-11-12 20:34 | Registra as decisões 356 a 359. |
| Zennd | 2026-11-12 20:01 | Registra as decisões 352 a 355. |
| Zennd | 2026-11-12 19:38 | Registra a decisão 351. |
| Zennd | 2026-11-12 19:33 | Registra as decisões 347 a 350. |
| Zennd | 2026-11-12 19:31 | Registra a decisão 346. |
| Zennd | 2026-11-12 19:21 | Registra as decisões 343 a 345. |
| Zennd | 2026-11-12 19:02 | Registra a decisão 342. |
| Zennd | 2026-11-12 18:57 | Registra as decisões 339 a 341. |
| Zennd | 2026-11-12 18:56 | Registra as decisões 335 a 338 e atualiza a lista de nós. |
| Zennd | 2026-11-12 18:47 | Registra a decisão 334. |
| Zennd | 2026-11-12 18:43 | Registra a decisão 333. |
| Zennd | 2026-11-12 18:42 | Registra as decisões 329 a 332. |
| Zennd | 2026-11-12 18:24 | Registra as decisões 325 a 328. |
| Zennd | 2026-11-12 18:22 | Registra as decisões 321 a 324. |
| Zennd | 2026-11-12 17:18 | Atualiza os resumos mudados por respostas novas, como o `Toggle`, os 16 canais inativos e a saída do nome do slot (perguntas 166, 224, 226, 229, 233, 237, 238, 241, 262, 271, 277, 281, 303, 308, 310 e 313). |
| Zennd | 2026-11-12 17:27 | Registra as decisões 304 a 320. |
| Zennd | 2026-11-12 13:25 | Registra as decisões 279 a 303. |
| Zennd | 2026-11-12 12:01 | Registra a decisão 278. |
| Zennd | 2026-11-12 11:51 | Registra a decisão 277. |
| Zennd | 2026-11-12 11:45 | Registra as decisões 274 a 276. |
| Zennd | 2026-11-12 11:27 | Registra a decisão 273. |
| Zennd | 2026-11-12 11:11 | Registra a decisão 272. |
| Zennd | 2026-11-12 11:10 | Registra a decisão 271. |
| Zennd | 2026-11-12 11:08 | Registra as decisões 267 a 270. |
| Zennd | 2026-11-12 10:57 | Registra as decisões 264 a 266. |
| Zennd | 2026-11-12 10:56 | Registra as decisões 262 e 263. |
| Zennd | 2026-11-12 10:30 | Registra a decisão 261. |
| Zennd | 2026-11-12 10:25 | Registra as decisões 259 e 260. |
| Zennd | 2026-11-12 10:07 | Registra a decisão 258. |
| Zennd | 2026-11-12 09:54 | Registra a decisão 257. |
| Zennd | 2026-11-12 09:52 | Registra as decisões 253 a 256. |
| Zennd | 2026-11-12 09:49 | Registra as decisões 251 e 252. |
| Zennd | 2026-11-12 09:48 | Registra a decisão 250. |
| Zennd | 2026-11-12 09:30 | Registra a decisão 249. |
| Zennd | 2026-11-12 09:25 | Registra a decisão 248. |
| Zennd | 2026-11-12 09:20 | Registra as decisões 246 e 247. |
| Zennd | 2026-11-12 09:18 | Registra as decisões 242 a 245. |
| Zennd | 2026-11-11 17:30 | Registra as decisões 240 e 241. |
| Zennd | 2026-11-11 17:08 | Registra as decisões 238 e 239. |
| Zennd | 2026-11-11 16:52 | Registra a decisão 237. |
| Zennd | 2026-11-11 16:40 | Registra a decisão 236. |
| Zennd | 2026-11-11 16:34 | Registra a decisão 235. |
| Zennd | 2026-11-11 16:30 | Registra as decisões 233 e 234. |
| Zennd | 2026-11-11 16:12 | Registra a decisão 232. |
| Zennd | 2026-11-11 16:08 | Registra a decisão 231. |
| Zennd | 2026-11-11 16:06 | Registra a decisão 229. |
| Zennd | 2026-11-11 16:02 | Registra as decisões 227, 228 e 230. |
| Zennd | 2026-11-11 15:58 | Registra as decisões 224 a 226. |
| Zennd | 2026-11-11 15:52 | Registra a decisão 223. |
| Zennd | 2026-11-11 15:47 | Registra a decisão 222. |
| Zennd | 2026-11-11 15:24 | Registra a decisão 221. |
| Zennd | 2026-11-11 15:08 | Registra as decisões 219 e 220. |
| Zennd | 2026-11-11 14:52 | Registra as decisões 216 a 218. |
| Zennd | 2026-11-11 14:40 | Registra a decisão 215. |
| Zennd | 2026-11-11 14:21 | Registra as decisões 212 a 214. |
| Zennd | 2026-11-11 13:59 | Registra as decisões 209 a 211. |
| Zennd | 2026-11-11 13:20 | Registra as decisões 206 a 208. |
| Zennd | 2026-11-11 11:52 | Registra as decisões 198 a 205. |
| Zennd | 2026-11-11 11:18 | Registra as decisões 196 e 197. |
| Zennd | 2026-11-11 11:06 | Registra a decisão 195. |
| Zennd | 2026-11-11 11:04 | Registra a decisão 194. |
| Zennd | 2026-11-11 11:01 | Registra as decisões 188 a 193. |
| Zennd | 2026-11-11 10:53 | Registra a decisão 187. |
| Zennd | 2026-11-11 10:48 | Registra a decisão 186. |
| Zennd | 2026-11-11 10:32 | Registra a decisão 185. |
| Zennd | 2026-11-11 10:24 | Registra as decisões 173 a 179. |
| Zennd | 2026-11-11 10:45 | Registra a decisão 184. |
| Zennd | 2026-11-11 10:40 | Registra as decisões 180 a 183. |
| Zennd | 2026-11-11 09:51 | Registra as decisões 125 a 172 e atualiza as que elas mudam. |
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
