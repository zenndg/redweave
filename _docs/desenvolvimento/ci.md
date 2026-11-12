# CI

A CI roda no GitHub Actions, em `.github/workflows/ci.yml`, e repete no GitHub os gates do `pre-push` ([`hooks-do-git.md`](hooks-do-git.md)). Ela pega o que os hooks locais não alcançam, como um commit feito pelo GitHub ou por outra máquina. As decisões estão nas perguntas 43 a 47 de [`fase-1-sem-fio.md`](../perguntas/fase-1-sem-fio.md).

## Quando roda

Em todo push e todo PR para `develop`, `homolog`, `main` e `release/*` (pergunta 43). O push de uma branch de feature não dispara a CI, só o PR dela, então nada roda em dobro. Um push novo na mesma branch cancela a execução anterior, para não gastar minutos com um commit já substituído.

O repositório é privado, e o plano gratuito dá 2.000 minutos por mês. Nesse plano, um repositório privado não tem proteção de branch, então a CI informa o resultado, e quem bloqueia de fato continua sendo o `pre-push`.

## O que roda

| Job | Passo | Comando | O que reprova |
|---|---|---|---|
| Gates | Check | `make check` | Compilação, testes, arquitetura, cobertura, formatação ou análise estática |
| Gates | GameTests | `make gametest` | GameTest reprovada ou mod que não carrega no servidor |
| Gates | Generated files | `make check-generated` | `src/generated` diferente do que a geração produz |
| Secrets | gitleaks | `make setup` e `make scan-secrets` | Segredo em qualquer commit do histórico (pergunta 45) |

Os comandos são os mesmos do `Makefile` (regra 35 do `AGENTS.md`), então o que passa na máquina passa na CI. Quando o job Gates reprova, o `build/gametest.log` e os relatórios de `build/reports/` ficam no artefato `reports` da execução.

O job Secrets usa o mesmo `setup-dev.sh` dos clones, com a versão e o checksum do gitleaks fixados num lugar só. O `make setup` também liga os hooks no clone da CI, o que não tem efeito ali.

## Actions

Cada action fica fixada pelo SHA do commit, com a tag num comentário ao lado (pergunta 44), para uma tag movida não trocar o código que roda. As versões são da pergunta 47.

| Action | Versão | O que faz | Por que foi escolhida |
|---|---|---|---|
| `actions/checkout` | v7.0.1 | Baixa o repositório, sem guardar a credencial no clone. O job Secrets baixa todo o histórico | É a oficial do GitHub |
| `actions/setup-java` | v6.2.0 | Instala o JDK 21 Temurin e faz o cache do Gradle | É a oficial do GitHub. O cache guarda `~/.gradle/caches`, onde fica o Minecraft preparado (`neoformruntime`), e o wrapper, com a chave pelo hash dos `*.gradle*` e do `gradle-wrapper.properties` (pergunta 46) |
| `actions/upload-artifact` | v7.0.2 | Guarda o log e os relatórios quando um gate reprova | É a oficial do GitHub |

O `gradle/actions` ficou de fora: o cache padrão da v6 dele é proprietário, e o cache aberto faz o mesmo que o do `setup-java`.

**Atualizar uma action:** pegue o SHA do commit da tag nova (`gh api repos/{dono}/{action}/git/ref/tags/{tag}`; se o tipo for `tag`, siga o `object.sha` até o commit) e troque o SHA e o comentário juntos.

## Quando algo reprova

- **Check, GameTests ou Generated files:** rode o mesmo comando na máquina e veja [`ferramentas-de-build.md`](ferramentas-de-build.md#quando-algo-reprova). O log e os relatórios estão no artefato `reports` da execução.
- **Passa na máquina e reprova na CI:** confira se o arquivo que falta está no commit, como um arquivo de `src/generated`, e se o `git status` está limpo.
- **gitleaks:** o relatório oculta o valor e mostra o commit e o arquivo. Troque o segredo, porque ele já foi exposto, e só então pense em reescrever o histórico, que exige autorização. Em caso de falso positivo, consulte o desenvolvedor antes de criar qualquer exceção.
- **Primeira execução lenta ou cache perdido:** a chave do cache muda com qualquer `*.gradle*` ou com o wrapper, e aí o Minecraft é preparado de novo. É esperado.

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-12 17:18 | Usa o nome `Generated files` do passo da CI, como no `ci.yml` (pergunta 43). |
| Marcelo Fabiano | 2026-11-10 08:09 | Cria o documento, com a CI no GitHub Actions. |
