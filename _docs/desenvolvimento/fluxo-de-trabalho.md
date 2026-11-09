# Fluxo de trabalho

Este documento mostra, passo a passo, como uma tarefa vai da issue ao merge e como uma versão é publicada. As regras ficam no [`AGENTS.md`](../../AGENTS.md). Em caso de divergência, vale o `AGENTS.md`. Adaptado do Cubit.

## Caminho de uma tarefa

```text
Alinhamento → Issue → Branch → Trabalho → Validação → Plano de commits → Commits → PR → Merge → Encerramento
```

### 1. Alinhamento

Ao iniciar uma feature, o agente descreve em poucas linhas o que vamos fazer e pergunta ao desenvolvedor se tem algo mais para acrescentar. Só depois vêm a rodada de decisões, a issue e a branch (regra 40).

### 2. Issue

Toda tarefa começa por uma issue, atribuída ao desenvolvedor, na milestone da fase e com a label da área, com o que entra e o que fica fora do escopo. Issues e PRs dividem a mesma numeração, então confirme o número antes de nomear a branch.

### 3. Branch

```bash
git fetch origin
git switch develop
git merge --ff-only origin/develop
git switch -c feature/{issue}-{slug} --no-track develop
```

### 4. Trabalho

- A funcionalidade precisa estar documentada em `_docs/funcionalidades/`, com as decisões fechadas. O teste vem antes ou junto do código.
- **Decisão bloqueante:** pare e siga o [fluxo do `AGENTS.md`](../../AGENTS.md#decisões-bloqueantes).
- **Rascunhos:** ficam em `tmp/`.
- **Histórico de modificações:** quem altera um `.md` de `_docs/` adiciona uma linha no topo da tabela de histórico.

### 5. Validação

Compare a entrega com os documentos `.md`, que são os validadores, e passe pelos checklists de [`pilares.md`](pilares.md) que se aplicam. Se a entrega e um documento divergirem, corrija a entrega. Se o documento estiver errado, pergunte ao desenvolvedor antes de mudá-lo.

### 6. Plano de commits

O agente apresenta a relação dos commits, com os arquivos e a mensagem de cada um, e espera a autorização. Cada commit trata de uma mudança lógica e deixa o projeto num estado válido.

### 7. Commits

```bash
git add {arquivos do commit}
git commit -m "tipo(escopo opcional): descrição em português"
```

Use `git add` com os arquivos do plano, nunca `git add .`.

### 8. Pull request

```bash
git push -u origin feature/{issue}-{slug}
cp .github/pull_request_template.md tmp/pr/pr-{número}.md
gh pr create --base develop --title "tipo: descrição" --body-file tmp/pr/pr-{número}.md
```

### 9. Merge

O merge é feito no GitHub, somente pelo desenvolvedor.

### 10. Encerramento

1. Confira se a issue fechou e se o card andou no board.
2. Atualize a `develop` local.
3. Peça autorização para apagar a branch da tarefa, local e remota.
4. Apresente no que vai atuar em seguida e pergunte se o desenvolvedor quer algum ajuste no roteiro (regra 41).

## O que precisa de autorização

| O agente faz sem pedir | O agente só faz com autorização |
|---|---|
| Ler, investigar e apresentar opções | Commit, push, tag, release e PR |
| Editar arquivos da tarefa aprovada, sem commit | Push forçado e qualquer reescrita de histórico |
| Rodar testes e gates | Apagar branches e arquivos |
| Criar a issue exigida por uma tarefa pedida | Mudar o `AGENTS.md` |
| Corrigir o que quebra um gate, sem mudar comportamento | Decisão bloqueante, mesmo com recomendação clara |

Todo item criado no GitHub é informado com o link, na mesma resposta.

## Publicar uma versão

```text
develop → homolog → release/X.Y.Z → main (tag vX.Y.Z)
                         └────────→ develop
```

1. **Homologação:** PR da `develop` para a `homolog`.
2. **Teste:** numa instância só com o mod e suas dependências e, depois, numa do Iron Meridian.
3. **Release fechada:** `release/X.Y.Z` a partir da `homolog`, com a versão no `gradle.properties` e o `CHANGELOG.md`, em inglês.
4. **Publicação:** PR da `release/X.Y.Z` para a `main` e, depois do merge, a tag anotada `vX.Y.Z`, com autorização.
5. **Volta:** PR da `release/X.Y.Z` para a `develop`.

## Corrigir com urgência

Só para um problema no que já foi publicado: issue, `hotfix/{issue}-{slug}` a partir da `main`, teste nas duas instâncias, PR para a `main`, tag do novo PATCH e a correção levada à `homolog` e à `develop` por PR.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
