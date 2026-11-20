# Teclado

O Signal Keypad (`redweave:signal_keypad`, `Teclado de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) que libera o sinal com uma senha numérica: com sinal no seu canal de entrada, qualquer player digita a senha e, se ela estiver certa, o teclado emite a força escolhida (pergunta 659). Esta página descreve só o que é do teclado; o que todo módulo herda está em [`modulos-de-sinal.md`](modulos-de-sinal.md), e os termos são os do [`glossario.md`](../glossario.md).

**Estado:** entregue na #144, com as perguntas 659 a 667, 731 e 896 a 916 respondidas.

## Em uma frase

Com sinal na entrada, qualquer player clica com a mão vazia, digita a senha e, se ela estiver certa, o teclado acende a luz verde e emite a força escolhida pelo tempo aberto (perguntas 659 a 663).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Keypad`, `redweave:signal_keypad`, `Teclado de Sinal` em português; a ideia do "Password" do desenvolvedor | 659 |
| Senha | Numérica, de 1 a 6 dígitos, escolhida pelo dono. Sem senha, não abre para ninguém | 659 e 660 |
| Entrada | Só por um canal escolhido pelo dono. Sem sinal, não emite, e o clique mostra `No power.` sem abrir. Um canal indisponível, com a rede `Offline` ou `Unplaced`, sem rede ou fora do alcance, vale como sem sinal | 661 e 906 |
| Tela do player | Um teclado 3 × 4, com `Clear` e `Enter` e os dígitos como `•`, aberto por qualquer player com a mão vazia | 662 |
| Senha certa | `Access granted` em verde, e a força escolhida (1 a 15, padrão 15) pela borda da frente ou por um canal, pelo tempo aberto: padrão de 3 s, de 1 a 30 s, com os limites em [`limites.md`](../limites.md) | 663 |
| Delay | Nenhum, como o Signal Lock: a senha certa abre na hora, e o tempo aberto controla a saída | 900 |
| Tentativas | Contam por player: quem erra não tranca o teclado para os outros. Os erros do dono não contam nem geram espera ou aviso. Cada erro mostra uma notificação vermelha para quem digita | 664 e 914 |
| Espera | 3 tentativas livres: enquanto o player está nelas, 60 s sem errar zeram a contagem. Da 4ª à 10ª, a espera dobra a partir de 5 s, até o teto; depois da 10ª, fica no teto. Depois da 4ª, a contagem só zera com a senha certa, com a troca da senha ou depois de 10 min sem tentar. Durante a espera, `Wait <n> s.` em vermelho, e a tentativa nem confere a senha nem conta | 665, 905, 913 e 914 |
| Aviso ao dono | A partir da 4ª tentativa errada, uma linha no chat do dono, `⚠ <player> missed the code of <nome> (<n> tries)`; offline, um resumo ao entrar | 666 |
| Estados | `Locked` (`Trancado`) fechado e `Unlocked` (`Destrancado`) durante o tempo aberto, como o Signal Lock | 908 |
| Indicadores | Um teclado 3D na placa, a luz vermelha fechado e verde aberto e um ponto na cor do canal de entrada | 667 e 909 |
| `Toggle` | Abre pelo tempo aberto, só para o dono testar, com `<nome>: Unlocked`; funciona mesmo com a rede `Offline`, como o Signal Lock, e a saída num canal só não conta enquanto a rede está `Offline` | 667 e 906 |
| Cópia | Nunca é copiado nem colado pelo `Copy` do Configurator, como o [Signal Lock](fechadura.md): copiar nele ou colar nele mostra `This item cannot be copied.`. Veja [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens) | 731 |
| Retirada e explosão | Como o [Signal Lock](fechadura.md): só o dono, pelo `Wrench`; resiste a explosão com o bloco de apoio | 641, 667 e 708 |
| Cartão | Abre com um [Signal Card](cartao.md) vinculado, como a senha certa, com sinal na entrada. A #144 entrega a lista dos IDs aceitos, a regra e a revogação na tela; o slot que recebe o cartão chega com o item, na #145 | 669, 670, 902 e 904 |
| Proteção da porta | Protege os blocos escolhidos pelo dono, na [proteção dos blocos](protecao.md), na #146 | 672 a 676 |

Os números ficam em [`limites.md`](../limites.md).

## Segredo

O pacote do bloco manda a cada cliente o NBT das partes do espaço. O teclado separa as suas chaves em públicas e secretas, e um filtro comum do espaço tira as secretas desse pacote, então elas só existem no servidor e no save (perguntas 660, 896 e 910). O mesmo filtro tira a `key` do [Signal Lock](fechadura.md), que antes ia a todo cliente, contra a pergunta 637 (pergunta 899).

| Ponto | Como é | Pergunta |
|---|---|---|
| Forma | Hash SHA-256 da senha com um sal aleatório por teclado. Quem lê o save ou usa `/data get` vê só o hash; com 6 dígitos, quem tem o arquivo ainda pode testar todas as senhas fora do jogo, mas ninguém a lê por acaso | 897 |
| Tela do dono | Mostra `Code ••••`, nunca a senha. `Change` abre o mesmo teclado 3 × 4: o dono digita a nova e repete para confirmar. Só os dígitos vão ao servidor, que gera o sal e o hash | 660 e 905 |
| Rótulo e outros players | Nunca mostram a senha, as tentativas nem os cartões | 660 e 908 |

## Tentativas e relógio

| Ponto | Como é | Pergunta |
|---|---|---|
| Onde ficam | No NBT da parte, por UUID do player, com a contagem e o tick da última tentativa, fora do pacote ao cliente. Sobrevivem a descarregar o chunk e a reiniciar, então ninguém zera a contagem se afastando | 898 |
| Limpeza | Entradas com mais de 10 min sem tentar são apagadas, e a lista não cresce sem fim | 665 e 898 |
| Relógio da espera | A espera e os 10 min usam o tempo do mundo (`gameTime`), que corre com o chunk descarregado e atravessa reinícios: esperar longe conta | 907 |
| Tempo aberto | Conta ticks só com o chunk carregado e fica no save com o que falta, como o pulso do Signal Lock (regra 6) | 907 |

## Aviso ao dono offline

O resumo fica no save do servidor que já guarda as redes, por dono: por teclado, o nome, o último player e o total de erros. Ao entrar, o dono recebe o resumo no chat, e ele é apagado. Cada dono guarda no máximo `keypad.max_offline_reports` teclados; acima do teto, sai o teclado do resumo mais antigo (perguntas 666, 903 e 911).

## Mensagens

| Quando | Inglês | Português |
|---|---|---|
| Clique sem sinal | `No power.` | `Sem energia.` |
| Clique sem senha definida | `This keypad has no code.` | `Este teclado não tem senha.` |
| `Toggle` | `<nome>: Unlocked` | `<nome>: Destrancado` |
| Aviso ao dono | `⚠ <player> missed the code of <nome> (<n> tries)` | `⚠ <player> errou a senha de <nome> (<n> tentativas)` |

As telas estão em [`telas.md`](telas.md#tela-do-signal-keypad).

## Forma

Sobre a placa de 10 × 10 dos módulos, um teclado baixo de 6 × 6 px e 1 px de altura. No topo, uma fileira escura na frente separa o teclado da seta, e 3 × 2 teclas de 1 px ficam soltas, com espaço entre elas; na fileira de trás, a luz dos [Signal Emitters](emissores.md), de 2 × 1 px, vermelha trancado e verde aberto, e o ponto na cor do canal de entrada. A mira cobre a placa e o teclado (perguntas 667, 909 e 912).

## Save

Além do NBT comum dos [módulos](modulos-de-sinal.md#save), dentro de `data` no contêiner `redweave:part_space`. As chaves ficam fixadas na #26 (pergunta 910, regra 15):

| Chave | O que guarda | Vai ao cliente |
|---|---|---|
| `strength` | A força emitida aberto, de 1 a 15 | Sim |
| `open_ticks` | O tempo aberto, em ticks | Sim |
| `remaining` | Os ticks que faltam do tempo aberto, 0 fechado | Sim |
| `input` | A cor do canal de entrada | Sim |
| `output` | `edge` ou a cor do canal de saída | Sim |
| `code_hash` | O hash da senha; ausente sem senha | Não |
| `code_salt` | O sal da senha; ausente sem senha | Não |
| `attempts` | A lista de `{player, count, last_tick}` | Não |
| `cards` | A lista dos IDs de Signal Card aceitos | Não |

## Config

| Chave | Valor | Pergunta |
|---|---|---|
| `keypad.min_open_ticks` | 20 (1 s), de 20 a 600 | 663 e 915 |
| `keypad.max_open_ticks` | 600 (30 s), de 20 a 6000 | 663 e 915 |
| `keypad.free_window_ticks` | 1200 (60 s), de 200 a 12000 | 665, 913 e 915 |
| `keypad.free_tries` | 3, de 1 a 10 | 665 e 915 |
| `keypad.first_wait_ticks` | 100 (5 s), de 20 a 1200, dobrando a cada erro | 665 e 915 |
| `keypad.max_wait_ticks` | 6000 (5 min), de 100 a 72000 | 665 e 915 |
| `keypad.reset_ticks` | 12000 (10 min), de 1200 a 72000 | 665 e 915 |
| `keypad.max_offline_reports` | 16, de 1 a 64 | 911 e 915 |
| `card.max_links_per_item` | 8, de 1 a 64 | 669, 904 e 915 |

Os valores ficam em [`limites.md`](../limites.md).

## Receita

Na forma comum dos módulos: `·, botão de pedra-negra polida, · / barra de cobre, redstone, barra de cobre / deepslate polido, Signal Node, deepslate polido`, que dá 1 Signal Keypad (perguntas 753, 766 e 901). A grade fica em [`itens.md`](../itens.md#receitas).

## Código

| Camada | Classe | O que faz |
|---|---|---|
| Domínio | `SignalKeypad`, `KeypadCode`, `KeypadSecret`, `KeypadAttempts`, `KeypadAttempt`, `KeypadCards`, `CardId`, `KeypadTry`, `KeypadEntry`, `KeypadReport`, `KeypadReports`, `KeypadLimits`, `CardLimits` | A regra, a senha com hash, as tentativas e a espera, os cartões e o resumo do dono offline |
| Aplicação | `ChangeKeypad`, `KeypadOwnerReports`, `KeypadSalts`, `KeypadReportBook` | Só o dono muda o teclado; o resumo guardado e entregue uma vez; as portas do sal e do save |
| Config | `KeypadConfig`, `NeoForgeKeypadLimits` | As 9 chaves da pergunta 915 |
| Parte | `KeypadPart`, `KeypadSave`, `KeypadWarnings`, `KeypadJoinEvents`, `SecretPart` | A placa, o save com as chaves secretas fora do pacote ao cliente, o aviso ao dono e o resumo ao entrar |
| Servidor | `SavedKeypadReport`, `SecureKeypadSalts` | O resumo no save do servidor, no campo opcional `keypad_reports`, e o sal com `SecureRandom` |
| Menu | `KeypadMenu`, `KeypadEntryMenu`, `KeypadAction`, `KeypadCodes`, `KeypadScreenActions`, `KeypadScreenData`, `KeypadScreenLayout`, `KeypadScreenTexts` | As duas telas, conferidas no servidor; a senha viaja como tamanho × 1.000.000 + dígitos |
| Cliente | `KeypadScreen`, `KeypadEntryScreen`, `KeypadPad` | A tela do dono, o teclado do player e o teclado 3 × 4 comum |
| Datagen | `KeypadModels`, `KeypadEnglishTexts`, `KeypadPortugueseTexts` | O teclado 3D, a luz, o ponto tingido do canal e os textos |

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_keypad/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

- **JUnit:** a regra da senha certa e errada, o hash com sal, a espera exponencial por player com o relógio do mundo, a limpeza depois de 10 min, a troca da senha que zera as tentativas, os cartões aceitos e o teto, e os casos de uso, só pelo dono.
- **GameTests:** o sinal na entrada e o canal indisponível, a senha certa abrindo pelo tempo aberto, a espera, a senha, as tentativas e os cartões fora do pacote ao cliente, a `key` do Lock fora do pacote, o aviso ao dono online e o resumo ao entrar, o `Toggle` com a rede `Offline`, a retirada só pelo `Wrench` e o save.
- **Roteiro manual:** a tela do player e a do dono, a arte, os textos e o chunk descarregado (pergunta 667).

## Fora desta página

- O item do cartão e o slot que o recebe, em [`cartao.md`](cartao.md), na #145, e a proteção dos blocos, em [`protecao.md`](protecao.md), na #146.
- O Jade e o The One Probe, na #23.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-19 22:48 | Entregue na #144: as mensagens, o código e o estado (perguntas 896 a 916). |
| Zennd | 2026-11-19 22:14 | As faixas do config, com `keypad.free_tries` e `keypad.reset_ticks` (pergunta 915). |
| Zennd | 2026-11-19 22:13 | A janela das tentativas livres e a espera, e os erros do dono que não contam (perguntas 913 e 914). |
| Zennd | 2026-11-19 22:01 | O topo do teclado tem 3 × 2 teclas soltas, com a luz e o ponto atrás (pergunta 912). |
| Zennd | 2026-11-19 21:30 | Completa o conceito com as decisões 896 a 911: o segredo filtrado do cliente, o hash, as tentativas, o relógio, o aviso offline, a forma, o save, o config e a receita (#144). |
| Zennd | 2026-11-16 13:59 | O Signal Keypad nunca é copiado nem colado (pergunta 731). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 659 a 667. |
