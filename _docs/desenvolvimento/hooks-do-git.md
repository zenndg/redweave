# Hooks do Git

Os hooks ficam em `scripts/development/git-hooks/` e rodam em todo commit e todo push. São os do Cubit, adaptados (pergunta 19).

## Como ativar

Rode uma vez em cada clone:

```bash
make setup
```

O comando chama o `scripts/development/setup-dev.sh`, que baixa o gitleaks para `.tools/`, confere o checksum e aponta o `core.hooksPath` do repositório para os hooks. Nada é instalado fora do projeto. Por enquanto, só funciona em Linux x86_64.

## O que cada hook faz

| Hook | Gate | O que reprova |
|---|---|---|
| `pre-commit` | Branch | Commit fora de `feature/{issue}-{slug}`, `hotfix/{issue}-{slug}` ou `release/X.Y.Z` |
| `pre-commit` | Histórico | Arquivo `.md` em `_docs/` alterado sem linha nova na tabela de histórico de modificações. Os `.md` fora de `_docs/` não têm essa tabela |
| `pre-commit` | Formatação | Arquivo `.java` fora do padrão, quando há um `.java` no commit |
| `pre-commit` | gitleaks | Segredo nas alterações preparadas para o commit |
| `commit-msg` | Mensagem | Título fora do Conventional Commits |
| `commit-msg` | Autoria | Coautoria ou atribuição a uma IA. Citar uma ferramenta de IA como assunto da mudança é permitido |
| `pre-push` | Build | Compilação, testes, arquitetura, cobertura, formatação ou análise estática reprovados, por meio de `make check` |

Os GameTests e a conferência do `src/generated` entram no `pre-push` com a issue #5.

O `pre-push` roda todos os gates também ao apagar uma branch remota, porque o script não diferencia esse caso.

Os hooks rodam só na máquina local. Eles não bloqueiam merges feitos pelo GitHub nem um push direto de outra máquina. A CI, na issue #2, repete os gates no GitHub.

## gitleaks

- **O que faz:** procura senhas, tokens e chaves nas alterações antes do commit.
- **Por que foi escolhido:** é um único binário, sem depender de Java ou do Gradle, e é o mesmo do Cubit.
- **Versão:** 8.30.1, fixada em `scripts/development/setup-dev.sh` com o SHA-256 oficial. Para atualizar, troque a versão e o checksum juntos.

## Quando um gate reprova

- **Branch:** crie a issue e a branch dela, e faça o commit lá. As alterações não commitadas acompanham a troca de branch.
- **Formatação:** rode `make format` e adicione os arquivos ao commit de novo.
- **Histórico:** adicione uma linha no topo da tabela do arquivo, com o nome da pessoa, a data e a hora (`AAAA-MM-DD HH:MM`) e uma frase direta do que foi feito.
- **gitleaks:** remova o segredo do arquivo e troque-o, se ele já tiver sido exposto. O relatório oculta o valor. Em caso de falso positivo, consulte o desenvolvedor antes de criar qualquer exceção.
- **gitleaks não encontrado:** rode `make setup`.
- **Mensagem:** reescreva o título no formato `tipo(escopo opcional): descrição`, em português.
- **Autoria:** remova a atribuição.
- **Build:** veja [`ferramentas-de-build.md`](ferramentas-de-build.md#quando-algo-reprova).

Nunca use `--no-verify`.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 13:52 | Inclui a arquitetura no gate de push. |
| Marcelo Fabiano | 2026-11-09 13:28 | Adiciona os hooks de commit e de push, com o gitleaks. |
