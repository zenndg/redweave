# Signal Remote

O Signal Remote (`redweave:signal_remote`, `Controle de Sinal` em português) é um item de mão com aparência de tablet, vinculado a um slot de input de um nó, que liga e desliga o canal desse slot de longe (perguntas 15, 279, 282 e 368). Esta página descreve o item na Fase 1, entregue na #22. A rede está em [`rede-e-canais.md`](rede-e-canais.md#signal-remote), e as telas, em [`telas.md`](telas.md). Os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

O dono vincula o tablet a um slot de input com Shift + clique direito, e quem o segura liga, desliga ou pulsa o canal desse slot com o clique direito, como uma alavanca sem posição no mundo (perguntas 27, 357 e 288).

## Na rede

| Ponto | Como é | Pergunta |
|---|---|---|
| O que é | Uma fonte de entrada sem posição no mundo, com ID próprio, que copia o papel do slot de input vinculado e entra no canal dele | 27 e 282 |
| Valor | Sempre 15 ligado, 0 desligado | 352 |
| Sinal mais forte | O tablet entra na regra do mais forte junto com as leituras dos nós, então ele não desliga um canal que outra fonte mantém ligado | 289 |
| Troca de canal | O vínculo é o slot, não o canal: se o slot muda de canal, o tablet passa a agir no canal novo | 287 |
| No domínio | Uma parte nova do agregado, `RemoteInputs`, que recebe a cada tick o conjunto dos tablets ligados, com o ID, o nó e o slot. A rede só fica pendente se um canal mudar | 360 |
| Save | Nada vai para o save da rede: o estado fica no item | 356 |

## O item

| Ponto | Como é | Pergunta |
|---|---|---|
| Pilha | Um por slot, como as ferramentas do jogo | 371 |
| ID | Ganho no primeiro vínculo. Cópias com o mesmo ID, como no criativo, contam como uma fonte só | 358 |
| Vínculo | A rede, o nó e o slot, num data component (regra 7) | 287 |
| Modo | `Lever` ou `Button`, num data component, escolhido na tela do vínculo | 288 e 365 |
| Estado | Ligado ou desligado no modo alavanca, e o tick do jogo em que o pulso acaba no modo botão, num data component | 356 e 369 |
| Visual | 4 estados, trocados pelo data component: sem vínculo, desligado, ligado e `Link lost`. As texturas são `signal_remote`, `signal_remote_off`, `signal_remote_on` e `signal_remote_lost`, escolhidas pela propriedade de item `look` (pergunta 374) | 362 |

## Vincular

1. **Shift + clique direito** abre a tela do vínculo, a qualquer momento (perguntas 279 e 291).
2. **Redes:** a lista das redes `Online` do player, no componente do Network Terminal (perguntas 279 e 359).
3. **Nós:** a tabela de nós da rede escolhida, só com os nós que têm slot de input ativo, com as colunas de tipo e estado. Nós `Unloaded` e `Out of range` aparecem, com o estado (perguntas 282, 283, 284 e 285).
4. **Slots:** se o nó tiver mais de um slot de input ativo, a lista deles, pela cor e pelo nome do canal; com um só, o passo é pulado (pergunta 286).
5. O vínculo novo substitui o antigo. A contribuição no canal antigo volta a 0 na hora, o tablet começa desligado, e a action bar confirma, como `Linked: ■ Base Norte · Alavanca do portão · Red` (pergunta 291).

No topo da tela ficam o vínculo atual, ou `No link`, e o seletor `Lever` e `Button`. O rodapé tem `Back`, que volta um passo (pergunta 365).

**Quem vincula:** só o dono da rede. As listas só mostram as redes do player, e o servidor confere o dono a cada passo (regras 16 e 17, pergunta 357).

## Disparar

| Ponto | Como é | Pergunta |
|---|---|---|
| Quem | Qualquer player que segure um tablet vinculado, para o dono poder entregar o controle a um amigo | 357 |
| Como | O clique direito, no ar ou num bloco sem interação própria | 279 |
| Modo alavanca | Cada clique liga ou desliga | 288 |
| Modo botão | Cada clique manda um pulso de `remote.pulse_ticks` ticks, 20 por padrão, como o botão de pedra. Um clique durante o pulso o recomeça | 353 e 369 |
| Action bar | `15 Red: On`, `15 Red: Off` ou `15 Red: Pulse`, com o nome do canal da rede. `On` fica em negrito e verde (`#6BD48F`), e `Off`, em negrito e vermelho (`#E2392F`); `Pulse` fica sem cor. Com o canal já ligado por outra fonte, `15 Red is held on by another source.` | 289, 370, 436 e 437 |
| Sem vínculo válido | O tablet mostra `Link lost` e não dispara, quando o nó saiu da rede, o slot foi desativado ou virou output | 287 |
| Rede `Offline` | Não dispara, e a action bar diz `<rede> is offline.` | 359 |

## Quando conta

A cada tick, o servidor soma só os tablets ligados ou em pulso que estão no inventário de um player online, na mesma dimensão do controlador e dentro do `network.range` dele, medido da posição do player (perguntas 27, 354, 355 e 356).

| Situação | Conta? |
|---|---|
| No inventário do player, no alcance | Sim |
| Guardado num baú, largado no chão ou com o player fora do jogo | Não, e volta a contar ligado quando voltar ao inventário |
| Player fora do alcance ou em outra dimensão | Não |
| Rede `Offline` ou `Unplaced` | Não, como todos os canais da rede (perguntas 24 e 135) |

O custo cresce com os players online, não com os nós (regra 18).

## Tooltip

`Link: ■ <rede> · <nó> · <canal>` ou `No link`; `Mode: Lever` ou `Button`; `On` ou `Off`, em negrito, verde ou vermelho (perguntas 436 e 437); e, em cinza, `The strongest signal on a channel wins.` e `Shift + right-click to link.` (pergunta 366).

## Na Tela do Controlador

O rodapé ganha `<n> remotes on`, com os tablets que contam agora na rede, ao lado de `unloaded` e `out of range`. A aba `Nodes` continua só com nós (perguntas 361 e 364).

## Testes

- **JUnit:** o `RemoteInputs`, com o mais forte junto com as leituras, o slot que deixa de ser input, a rede `Offline`, fora do alcance e cópias com o mesmo ID, e os casos de uso.
- **GameTests:** o vínculo só pelo dono, o disparo em alavanca e em botão com o pulso, a saída do inventário, o `Link lost` e os menus com `FakePlayerFactory`.
- **Roteiro manual:** as telas, a arte e os textos (pergunta 367).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-14 13:34 | Destaca o estado em negrito e cor (perguntas 436 e 437). |
| Zennd | 2026-11-12 21:39 | Aponta as texturas dos 4 estados do tablet (pergunta 374). |
| Zennd | 2026-11-12 20:48 | Cria o documento, com as decisões 352 a 371 da #22. |
