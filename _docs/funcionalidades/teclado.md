# Teclado

O Signal Keypad (`redweave:signal_keypad`, `Teclado de Sinal` em português) é um [Signal Module](modulos-de-sinal.md) que libera o sinal com uma senha numérica: com sinal no seu canal de entrada, qualquer player digita a senha e, se ela estiver certa, o teclado emite a força escolhida (pergunta 659). Os termos são os do [`glossario.md`](../glossario.md).

**Estado:** conceito fechado, com as perguntas 659 a 667 respondidas (regra 9), a implementar na #144.

## Em uma frase

Com sinal na entrada, qualquer player clica com a mão vazia, digita a senha e, se ela estiver certa, o teclado acende a luz verde e emite a força escolhida pelo tempo aberto (perguntas 659 a 663).

## Conceito

| Ponto | Como é | Pergunta |
|---|---|---|
| Nome e ID | `Signal Keypad`, `redweave:signal_keypad`, `Teclado de Sinal` em português; a ideia do "Password" do desenvolvedor | 659 |
| Senha | Numérica, de 1 a 6 dígitos, escolhida pelo dono | 659 |
| Segredo | Só no NBT do servidor, sem nunca ir ao cliente: nem rótulo, nem tela, nem Jade. O dono vê `••••` e troca a senha, mas não a lê. Sem senha, não abre para ninguém | 660 |
| Entrada | Só por um canal escolhido pelo dono. Sem sinal, não emite, e o clique mostra `No power.` sem abrir | 661 |
| Tela | Um teclado 3 × 4, com `Clear` e `Enter` e os dígitos como `•`, aberto por qualquer player com a mão vazia | 662 |
| Senha certa | `Access granted` em verde, e a força escolhida (1 a 15, padrão 15) pela borda da frente ou por um canal, pelo tempo aberto: padrão de 3 s, com os limites em [`limites.md`](../limites.md) | 663 |
| Tentativas | Contam por player: quem erra não tranca o teclado para os outros. Cada erro mostra uma notificação vermelha para quem digita | 664 |
| Espera | 3 tentativas livres por janela; da 4ª à 10ª, a espera dobra a partir de 5 s, até o teto; depois da 10ª, fica no teto. Zera com a senha certa ou depois de 10 min sem tentar. Durante a espera, `Wait <n> s.` em vermelho. Os números estão em [`limites.md`](../limites.md) | 665 |
| Aviso ao dono | A partir da 4ª tentativa errada, uma linha no chat do dono, `⚠ <player> missed the code of <nome> (<n> tries)`; offline, um resumo ao entrar | 666 |
| Indicadores | Um teclado 3D na placa, a luz vermelha fechado e verde aberto e um ponto na cor do canal de entrada | 667 |
| `Toggle` | Abre pelo tempo aberto, só para o dono testar, com `<nome>: Unlocked` | 667 |
| Cópia | Nunca é copiado nem colado pelo `Copy` do Configurator, como o [Signal Lock](fechadura.md): copiar nele ou colar nele mostra `This item cannot be copied.`. Veja [`configurador.md`](configurador.md#copiar-e-colar-todos-os-itens) | 731 |
| Retirada e explosão | Como o [Signal Lock](fechadura.md): só o dono, pelo `Wrench`; resiste a explosão com o bloco de apoio | 641 e 667 |
| Cartão | Abre com um [Signal Card](cartao.md) vinculado, como a senha certa, com sinal na entrada | 669 e 670 |
| Proteção da porta | Protege os blocos escolhidos pelo dono, na [proteção dos blocos](protecao.md) | 672 a 676 |

Os números ficam em [`limites.md`](../limites.md).

## Arte

A arte segue as perguntas 185 e 186: o agente escreve o prompt em `tmp/arte/signal_keypad/`, o desenvolvedor gera a imagem, e o agente a confere.

## Testes

JUnit da regra e da espera (os dígitos, a senha certa e errada, a espera exponencial por player); GameTests no mundo (o sinal na entrada, a senha nunca indo ao cliente, o aviso ao dono, a retirada); roteiro manual da tela e da arte (pergunta 667).

## Fora desta página

- O cartão, em [`cartao.md`](cartao.md), e a proteção dos blocos, em [`protecao.md`](protecao.md).
- A receita, na #24.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-16 13:59 | O Signal Keypad nunca é copiado nem colado (pergunta 731). |
| Zennd | 2026-11-16 09:34 | Cria o conceito, com as decisões 659 a 667. |
