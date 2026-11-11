# Terminal de rede

O Network Terminal (`redweave:network_terminal`) é o bloco que administra todas as redes de um player. Esta página descreve o bloco na Fase 1, numa issue própria depois da Tela do Controlador (#20), antes da 0.1 (pergunta 151). A rede está em [`rede-e-canais.md`](rede-e-canais.md), e o Network Controller, onde cada rede nasce, em [`controlador-de-rede.md`](controlador-de-rede.md). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Um clique com a mão vazia no Terminal mostra todas as redes do dono, de onde ele as renomeia, desliga, religa, limpa e abre.

## O que o Terminal não faz

Não cria redes. A rede nasce ao colocar um Network Controller, e cada controlador é uma rede (perguntas 129 e 130).

## Colocar e quebrar

| Ponto | Como é | Pergunta |
|---|---|---|
| Dono | Quem colocou o bloco. Só ele usa o Terminal | 134 |
| Quantidade | Um por player no servidor. Colocar um segundo é recusado, com aviso na action bar | 137 |
| Quebrar | Só o dono, no survival. Explosões não o destroem. No criativo, quebra, como o Network Controller (pergunta 30) | 137 |
| Chave do Configurator | Não retira o Terminal | 164 |
| Registro | O servidor guarda o Terminal de cada player no save das redes, numa lista `terminals` com `player` e o bloco, e a BlockEntity guarda o dono com a chave de NBT `owner`, para o limite valer também com o chunk descarregado | 175 |

## Clicar

| Quem clica | O que acontece | Pergunta |
|---|---|---|
| O dono, com a mão vazia | Abre a tela do Terminal | 130 |
| Outro player | Nada abre, e a action bar mostra `Only the owner can use this terminal.` | 153 |

O Terminal alcança todas as redes do dono, em qualquer dimensão e a qualquer distância, porque lê o save das redes, sem carregar chunk (regra 6 e pergunta 152). O servidor confere o dono e a distância de interação com o bloco em cada ação (regras 16 e 17).

## Tela

Uma lista das redes do dono, em ordem alfabética pelo nome, com o estado de cada uma ao lado e a quantidade de nós (perguntas 131 e 138).

| Ação | Em quais redes | O que faz | Pergunta |
|---|---|---|---|
| Abrir | `Online` | Abre a tela do Network Controller da rede | 131 e 158 |
| Renomear | Todas | Troca o nome, com o limite de `network.name_length` | 156, 158 e 176 |
| `Offline` | `Online` | Desliga a rede | 132 |
| `Online` | `Offline` | Religa a rede | 132 e 135 |
| Limpar nós | Todas | Tira todos os nós da rede, que perdem os slots e ficam `No network` no mundo. Pede confirmação no segundo clique, como o `Clear slot` | 133 |
| `Delete` | `Unplaced` | Apaga a rede de vez, com confirmação. Os nós dela ficam `No network` | 166 |

Uma rede `Offline` aparece só como a linha da lista: a tela do controlador dela não abre, nem pelo Terminal (pergunta 158). Uma rede `Unplaced` não pode ser religada até o controlador voltar ao mundo (pergunta 135).

## Estados que o Terminal controla

| Estado | Quando | Efeito | Pergunta |
|---|---|---|---|
| `Online` | Controlador no mundo e rede ligada | A rede funciona | 135 |
| `Offline` | Desligada no Terminal | Todos os canais valem 0. O controlador e os nós dela não aceitam interação do player, e o Configurator não abre, não copia e não cola neles. O dono ainda quebra os blocos, e a Chave funciona | 132, 136 e 163 |
| `Unplaced` | O controlador saiu do mundo | Todos os canais valem 0. Só sai pelo controlador recolocado ou pelo `Delete` | 135, 146 e 166 |

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:network_terminal`, o mesmo do bloco. É contrato e fica fixado na #26 (pergunta 155) |
| Nome | `Network Terminal`, e `Terminal de Rede` em português |
| Aba do criativo | `Redweave` (pergunta 69) |

## Pendências

| Ponto | Depende de |
|---|---|
| O visual do bloco e da tela | Issue do Terminal, como as pranchas das perguntas 51 e 78 |
| A receita | Issue #24 |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 10:24 | Aplica as perguntas 175 e 176. |
| Zennd | 2026-11-11 09:51 | Cria o documento, com as respostas das perguntas 129 a 166. |
