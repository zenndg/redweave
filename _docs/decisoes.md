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

Não há perguntas pendentes da concepção.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
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
