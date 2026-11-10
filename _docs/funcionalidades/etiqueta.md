# Etiqueta

O Label (`redweave:label`) é uma etiqueta que guarda a configuração de um nó para reutilizar: o player grava, dá um nome na bigorna e aplica em quantos nós quiser. Esta página descreve o item na Fase 1, com a issue #81. A cópia e as regras de colar são as do [Configurator](configurador.md), e os termos são os do [`glossario.md`](../glossario.md).

## Em uma frase

Com o Configurator na mão e Labels vazios na outra, Shift + clique num nó grava a configuração num Label; com o Label na mão, um clique em outro nó a aplica.

## Ações

Toda ação roda no servidor, que confere o dono da rede (regras 16 e 17).

| Ação | O que acontece | Pergunta |
|---|---|---|
| Configurator na mão principal, Labels vazios na outra, Shift + clique num nó | Um Label sai da pilha gravado com os slots do nó e vai para o inventário, ou cai no chão se ele estiver cheio. O Configurator também guarda a cópia, e a action bar mostra `Copied <n> slots to a Label` | 118 e 122 |
| Configurator na mão principal, Label gravado na outra, Shift + clique num nó | O Label não muda; a cópia vai para o Configurator, como sem o Label (pergunta 105) | 118 |
| Label gravado na mão, clique num nó | Aplica a configuração com as regras de colar do Configurator, e o Label não é gasto | 106 e 118 |
| Bigorna | Dá um nome ao Label, como "Porta da base", como em qualquer item do jogo | 110 |
| Grade de crafting: um Label gravado com Labels vazios | Os vazios saem gravados iguais ao primeiro, que volta para a grade | 110 e 117 |
| Grade de crafting: um Label gravado sozinho | Sai um Label vazio | 116 |

Um clique com o Label vazio num nó não faz nada. Gravar copia a configuração de qualquer nó, como o Configurator, e um nó sem rede é recusado com `No network`.

### Aplicar

São as regras de colar da pergunta 106: todos os slots do nó são trocados, um slot na face grudada acompanha a face do nó de destino, os slots que passam do tamanho do nó ficam de fora, e só o dono da rede do nó aplica. As mensagens são as do Configurator: `Pasted <n> slots`, `Pasted <n> slots, <m> left out`, `Only the owner of <rede> can change this node.` e `No network`.

## Tooltip

| Label | O que mostra | Pergunta |
|---|---|---|
| Gravado | Uma linha por slot, na cor do canal, como `Red · Input · East`, e a dica `Click a node to apply.` | 119 |
| Vazio | `Empty` e `Shift + click a node with a Configurator to write it.` | 119 |

O nome dado na bigorna aparece como título do item.

## Item

| Parte | O que é |
|---|---|
| ID | `redweave:label` |
| Nome | `Label`, e `Etiqueta` em português (pergunta 114) |
| Pilha | Até 16. Os gravados só se juntam com a mesma configuração e o mesmo nome (pergunta 115) |
| Aba do criativo | `Redweave` (pergunta 69) |
| Data component | `redweave:node_slots`, o mesmo da cópia do Configurator (pergunta 105) |
| Texturas | `label`, vazio, e `label_written`, gravado: uma etiqueta de papel bege com ilhós de cobre e um cordão; gravada, com uma faixa de redstone acesa e linhas escritas (perguntas 120 e 124). A prancha está em [`texturas-label.png`](../imagens/conceito/texturas-label.png) |

### Receitas

A cópia e o esvaziar na grade entram nesta issue, em `data/redweave/recipe/label_copy.json` e `label_clear.json`, com os tipos `redweave:label_copy` e `redweave:label_clear` (pergunta 123), para o pack poder sobrescrever com KubeJS (regra 4 e pergunta 117). A receita do Label vazio fica na #24, com as outras.

## Fora desta funcionalidade

- A receita do Label vazio (issue #24).

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-10 16:46 | Aponta para a prancha das texturas do Label. |
| Marcelo Fabiano | 2026-11-10 16:30 | Aplica as perguntas 122 e 123. |
| Marcelo Fabiano | 2026-11-10 16:29 | Cria o documento, com as respostas das perguntas 110, 111 e 114 a 121. |
