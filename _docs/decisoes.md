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
| Nó novo | Nasce `No network` e escolhe a rede numa tela (139); entra com 1 entrada no primeiro canal livre (171) | 25, 139, 171 |
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
| Config | `network.range`, `network.name_length`, `channel.name_length` e `node.slots`, no config do servidor | 50 |
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
| Faixas do config | Alcance de 0 a 30.000.000, nomes de 1 a 64 e slots de 1 a 9 | 66 |
| NBT do controlador | `network` e `owner` | 67 |
| Nome em português | `Controlador de Rede` | 68 |
| Aba do criativo | Aba própria `Redweave` | 69 |
| Nó ligado ao colocar | Mudada pela 139: o nó nasce `No network` | 70, 139 |
| Face do slot no mundo | Entrada lê o vizinho daquela face; saída alimenta o bloco de apoio como uma alavanca | 71 |
| Blockstate do nó | `facing` e `powered`; o `powered` vira `signal` pela 99 | 72 |
| Página do nó | `no-de-sinal.md` | 73 |
| Quebrar um nó | Só o dono no survival; a queda sai da rede sem dono | 74 |
| Clique no nó | Mudada pela 154: a mão vazia mostra uma dica | 75, 154 |
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
| Rede atual no save | Mudada pela 157: o save guarda a última rede usada | 91, 157 |
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
| Responsabilidade do Configurator | Só copiar e colar; não guarda a rede nem liga nós | 126 |
| Gestos de copiar e colar | Clique copia, Shift + clique cola, no modo `Copy` | 127, 143 |
| Apagar a cópia | Shift + clique no ar, em qualquer modo | 128 |
| Controlador e rede | 1 Network Controller = 1 rede, que nasce ao colocá-lo | 129 |
| Network Terminal | Bloco aberto com a mão vazia que administra as redes do dono; não cria redes | 130 |
| Ações do Terminal | Listar, renomear, `Offline` e `Online`, limpar nós e abrir a tela do controlador | 131 |
| Excluir rede | Não há: o Terminal desliga e religa; uma rede `Offline` só aceita o Terminal | 132 |
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
| Modos do Configurator | `Open`, `Copy` e `Wrench`, trocados com Shift + scroll | 143, 161, 164 |
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
| Cores dos modos | `Open` azul, `Copy` verde, `Wrench` vermelho | 167 |
| Uso da cor do modo | Sempre com o nome escrito, em tons próprios | 168 |
| Tons dos modos | `#5B9BF0`, `#3FAE5A` e `#D94A64` | 169 |
| Repositório | `zenndg/redweave`, com o host `github.com-zenndg` | 170 |
| Slot do nó ao entrar | 1 entrada no primeiro canal livre | 171 |
| Quem escolhe a rede do nó | Só quem o colocou | 172 |
| Roadmap | `ROADMAP.md` na raiz, em inglês | 180 |
| Nascimento da v0.1.0 | No fim da Fase 1, depois da #26 e do roteiro manual, pela #27 | 181 |
| Papel do roadmap | Visão por fase; o GitHub Projects continua a fonte da verdade | 182 |
| Issues do novo modelo | #85 a #90, e a #84 do registro | 183 |
| Nome no histórico | `Zennd`, como nos commits | 184 |
| Componente do modo | `redweave:configurator_mode`: `open`, `copy` e `wrench` | 173 |
| Chaves novas do save | `name` no nó, `offline` na rede e `last_used` no lugar de `current`, na `version` 1 | 174 |
| Registro do Terminal | Lista `terminals` no save, e `owner` na BlockEntity | 175 |
| Rede Unplaced no Terminal | Renomear, limpar nós e `Delete` | 176 |
| Visual da rede Offline | Estado `Offline` em cinza, lente e núcleo apagados | 177 |
| Recolocar uma rede Offline | Volta `Offline` | 178 |
| Lista de nós | Nome, sinal da lente e posição, com canal e direção por slot ao expandir | 179 |
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
| Nome do slot | Entra na #20, com decisões próprias antes do código | 223 |
| Painel da Tela do Nó sem o nome | As linhas sobem e fecham o espaço | 278 |
| Slots do Signal Node | 16 fixos, um por cor, com status ativo ou inativo; saem `node.slots` e os upgrades | 224 |
| Redesenho dos slots | Dentro da #20 | 225 |
| Nó novo | Todos os canais inativos; o player ativa cada um | 226 |
| Nome do slot | Do slot no nó, com `slot.name_length` de 32 | 227 |
| Ativar e desativar | Desativar guarda direção, face e nome | 228 |
| Cantos da placa | Os 4 primeiros canais ativos | 229 |
| Cópia do Configurator | Os 16 slots inteiros, com o nome | 230 |
| Arte da Tela do Nó com 16 cores | Prancha nova; a tela espera a arte | 231 |
| Configurator no controlador | O modo `Open` abre a tela, como a mão vazia | 232 |
| Aba `Slots` da Tela do Nó | Sai; a tela fica sem abas laterais | 233 |
| Linha aberta no controlador | Só os canais ativos, com o nome do slot | 234 |
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

Não há perguntas pendentes da concepção.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
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
