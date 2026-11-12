# Redweave

Mod de Minecraft de redstone sem fio: nós grudados em qualquer bloco, redes com nome e cor, canais programáveis e chips de lógica, com telas fáceis de aprender e bonitas de usar. O modpack [Iron Meridian](https://github.com/marcelofabianov/iron-meridian) é um dos lugares onde ele é testado.

**Comece por:** [`README.md`](README.md), a visão geral com as fases, e [`_docs/README.md`](_docs/README.md), o mapa da documentação. As respostas em `_docs/perguntas/` são a fonte da verdade e prevalecem sobre os outros documentos.

## Estado atual

- **Etapa atual:** Fase 1, sem fio, em andamento. Já entraram o projeto NeoForge com os gates, o domínio da rede (#9 a #13), o Network Controller (#14), o Signal Node (#15), a persistência (#16), o Configurator (#17), o nó num canal livre (#18), a Tela do Nó (#19) e o novo modelo de rede das perguntas 125 a 172: a remoção do Label (#85), os modos do Configurator (#86), o nó que escolhe a rede (#87), os estados `Offline` e `Unplaced` (#88) e a Tela do Controlador (#20), que trouxe os 16 slots por cor do Signal Node e o modo `Toggle`. O log da Tela do Controlador (#90) está em PR. O trabalho entra por issue, na milestone da fase.
- **Próximos:** o Network Terminal (#89), o visor do modo do Configurator e os rótulos sobre os nós (#80) e, depois, as issues #22 a #27.
- **Limite:** não avance para as fases seguintes sem pedido explícito.

## Documentação

- **Números:** ficam em `_docs/limites.md`. As outras páginas apontam para lá.
- **Perguntas:** ficam em `_docs/perguntas/`, com um arquivo por fase. Recomendação de pergunta pendente não vale como decidida.
- **Decisões:** o resumo do que foi decidido fica em `_docs/decisoes.md`.
- **Histórico:** todo `.md` em `_docs/` termina com uma tabela de histórico de modificações, e quem altera o arquivo adiciona uma linha no topo dela. Fora de `_docs/`, os `.md` não têm essa tabela.
- **Imagens:** ficam em `_docs/imagens/`.
- **Arte do mod:** toda tela, ícone, item e bloco tem textura, e o desenvolvedor gera cada imagem numa ferramenta de IA. O agente cria `tmp/arte/{id}/`, com o ID do item, bloco ou tela, como `tmp/arte/configurator/`, e escreve ali o `prompt.md`. O desenvolvedor deixa a imagem pronta no mesmo diretório, e o agente a confere (grade, paleta e silhueta) antes de levá-la ao mod (perguntas 185 e 186).
- **Rascunhos:** conversas e ideias vão para `tmp/`, que o Git ignora.
- **Fluxo de trabalho:** o passo a passo de uma tarefa está em [`_docs/desenvolvimento/fluxo-de-trabalho.md`](_docs/desenvolvimento/fluxo-de-trabalho.md).

## Regras do mod

1. **Idioma:** o inglês é o idioma base. O que entra no jar, aparece no jogo ou acompanha o download do mod fica em inglês, como nome, IDs, comandos, config, logs e licença, e o `en_us.json` é a referência. O `pt_br.json` usa as mesmas chaves, conferidas por um teste.
2. **Só sinal:** o Redweave transporta sinal de redstone. Itens, fluidos e energia ficam com os mods de logística.
3. **Números em config:** alcance, slots por nó, canais, intervalos e limites de carga nunca ficam fixos na lógica.
4. **Receitas:** os materiais são só do jogo puro, e uma receita pode combinar itens do próprio mod. Ficam em datapack JSON, para o pack poder sobrescrever com KubeJS.
5. **Sinal é 0 a 15:** o valor de um canal é sempre um `Signal`, com o mesmo intervalo da redstone do jogo.
6. **Sem forçar chunk:** nunca carregue um chunk à força nem leia um bloco sem confirmar que ele está carregado. O que acontece com um canal cujo nó está num chunk descarregado é a pergunta 13.
7. **Estado como item:** o chip e as configurações copiadas ficam em data components do 1.21.1.
8. **Design próprio:** nada de código, textura ou modelo copiado de mods com licença fechada. Código de mods MIT pode ser reaproveitado com o aviso de copyright original.

## Stack

| Item | Versão |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.x (confira a versão do Iron Meridian) |
| Java | 21 |
| Build | MDK oficial do NeoForge para 1.21.1 (ModDevGradle) |
| Jade | Opcional, versão do Iron Meridian |
| The One Probe | Opcional, versão decidida na issue #23 (pergunta 18), porque não está no Iron Meridian |

Confira as versões no `mods/` do Iron Meridian antes de fixar ou atualizar uma dependência.

## Como trabalhar

O desenvolvedor é sênior em TypeScript, Go e PHP, e Java é secundário para ele. Valoriza SOLID, DRY, KISS, Clean Code, TDD e DDD.

- **Modding:** explique as decisões específicas de Minecraft quando não forem óbvias.
- **Mudança neste arquivo:** justifique por que é necessária e espere a autorização do desenvolvedor.
- **Encerramento:** só quando o contexto da sessão chega a 80% ou mais, com trabalho pendente, o agente deixa um prompt de continuidade em `tmp/prompt/{AAAA-MM-DD_HHhMM}_continuidade_{assunto}.md`, com a leitura obrigatória, o que foi feito e o que se espera da próxima sessão. Abaixo disso, o trabalho segue na mesma sessão (pergunta 187).

### Decisões bloqueantes

É bloqueante qualquer ponto com mais de um caminho defensável em que a escolha altere arquitetura, dependências, processo, contrato, saves ou o comportamento entregue.

1. Identifique a decisão e pare antes de implementá-la.
2. Apresente as alternativas, com a sua recomendação e a opção de recusar todas.
3. Espere a confirmação explícita.
4. Implemente e registre a resposta em `_docs/perguntas/`, no arquivo da fase, e o resumo em `_docs/decisoes.md`.

Autorização para começar um trabalho não resolve os bloqueios dele, mesmo que a decisão pareça pequena ou reversível.

## Regras de desenvolvimento

### Prioridade

Quando duas regras puxarem para lados opostos, vence a que vem antes:

```text
Segurança > Defesa > Fronteira de módulo > Contrato externo > Comportamento testado >
Design sólido > Clareza > Simplicidade > Sem repetição > Sem excesso > Domínio > Regra do Escoteiro
```

O significado de cada pilar, os checklists e a matriz de decisão estão em [`_docs/desenvolvimento/pilares.md`](_docs/desenvolvimento/pilares.md). Segurança, aqui, é a proteção do mundo do player, do servidor e, na Fase 5, do hardware ligado à ponte.

### Domínio

9. **Conceito antes do código:** uma feature só é implementada depois de documentada em `_docs/funcionalidades/` e com as decisões dela fechadas.
10. **Fronteira:** `domain/` e `application/` não importam `net.minecraft` nem `net.neoforged`. Um teste com ArchUnit reprova a violação no build. Rede, canais, portas, circuitos e simulação ficam no domínio.
11. **Value objects** são `record` que validam no construtor. Sem `get*`: `strength()`, não `getStrength()`.
12. **Interfaces:** só existem com um consumidor real e são curtas. Se um consumidor não usa todos os métodos, separe.
13. **README do domínio:** `domain/` tem um `README.md` com a linguagem do domínio, as regras e os porquês. Atualizá-lo faz parte da tarefa que muda o modelo.

### Minecraft

14. **O mod não derruba o jogo por dado inválido:** usa um valor seguro e registra no log. Só falha ao iniciar, em caso de erro de programação.
15. **Saves são contrato:** IDs, data components, NBT, chaves de config e tags não mudam sem migração.
16. **Autoridade do servidor:** toda regra roda no servidor. O cliente só exibe e pede ações.
17. **Permissões da rede:** toda ação confere quem pode alterar a rede. O servidor valida permissão e distância de todo pacote recebido do cliente.
18. **Orçamento de tick:** cada rede é avaliada uma vez por tick, só quando um canal mudou. Nada de block updates em cascata.
19. **Separação do cliente:** código comum nunca referencia classes de `client/`.
20. **Dependências opcionais:** Jade e The One Probe ficam isolados em `compat/`, e o mod abre sem eles. Dependência só de cliente não pode impedir um servidor dedicado de abrir.

### Forma do código

21. **Idioma:** arquivos de programação (como `.java` e `.sh`) ficam em inglês, incluindo comentários, logs e testes. Arquivos de conteúdo (como `.md`) ficam em português, inclusive o nome do arquivo e da pasta, assim como issues, PRs e mensagens de commit. `README.md`, `ROADMAP.md`, `AGENTS.md`, `CLAUDE.md`, `LICENSE`, `COPYING` e `pull_request_template.md` mantêm o nome, porque as ferramentas e o costume dependem dele. O `README.md` e o `ROADMAP.md` ficam em inglês, porque são a vitrine do mod (pergunta 180).
22. **Constantes em vez de literais:** nada de número mágico nem string literal solta, inclusive nos testes. Balanceamento vai no config, e o resto vira constante nomeada.
23. **Funções** têm no máximo 30 linhas e uma responsabilidade. Prefira classes pequenas e composição em vez de herança.
24. **Nomes:** classes em `PascalCase`; JSONs e IDs em `snake_case`, no singular; scripts `.sh` e arquivos `.md` em `kebab-case`. Adapters levam o que adaptam como prefixo (`NeoForgeNetworkLimits`).
25. **Comentários** são exceção e explicam o porquê. Sem código comentado e sem `TODO` sem issue.
26. **Gerados:** `src/generated/` nunca é editado à mão. Um gate reprova se o `runData` gerar diferença.
27. **Versões** ficam em `gradle.properties`, compatíveis com as do Iron Meridian.

### Testes

28. **TDD:** o teste vem antes ou junto do código. Use JUnit 5 no domínio e GameTests no que depende do mundo.
29. **Sem mock do jogo:** nunca faça mock de `Level` ou `BlockEntity`. Comportamento de mundo se testa no mundo.
30. **Cobertura:** mínimo de 85% de instruções e de ramos em `domain/` e `application/`, medido com JaCoCo e garantido pelo hook de `pre-push`, com a meta de 95%. Todo PR informa a cobertura de cada um. O restante se cobre com GameTests e um roteiro manual, porque o JaCoCo não mede os GameTests e a regra 29 proíbe mock do jogo (perguntas 196 e 197).
31. **Teste antes da `main`:** teste numa instância só com o mod e suas dependências e, depois, numa do Iron Meridian.

### Gates

32. **Commit e push:** os gates entram na Fase 1, com o projeto NeoForge. Cada ferramenta ou hook entra com um documento em `_docs/desenvolvimento/`: o que faz, por que foi escolhido, a versão e o que fazer quando reprova.

### Validação

33. **Os documentos validam a entrega:** toda entrega é conferida contra os documentos `.md`, e as respostas em `_docs/perguntas/` prevalecem sobre os demais.
34. **Validação no PR:** a descrição do PR informa os documentos conferidos e a evidência de cada um, incluindo os checklists dos pilares que se aplicam.

### Comandos do projeto

35. **Makefile:** os comandos do projeto ficam no `Makefile`. Receita com mais de 3 linhas vira um script `.sh` em `scripts/`, e o `Makefile` só o chama.

### Perguntas

36. **Toda pergunta respondida fica registrada:** qualquer pergunta feita ao desenvolvedor e respondida por ele entra em `_docs/perguntas/`, no arquivo da fase e com o próximo número livre, antes de seguir para o próximo passo. Resposta que muda outra aponta para ela. A exceção é a autorização de uma ação pontual, como commit, push ou apagar uma branch.
37. **Nenhuma das opções:** toda pergunta com opções traz, de forma explícita, a opção "Nenhuma das opções".

### Mudança no que está documentado

38. **O documentado pode mudar:** se um impedimento técnico aparecer ao avançar, a decisão pode ser alterada ou desfeita. O impedimento é mostrado com evidência, o desenvolvedor decide, e a nova resposta aponta para a antiga. O que já foi publicado continua seguindo a regra 15.

### Independência

39. **Independência:** o mod funciona numa instância de Minecraft só com ele e suas dependências. Nenhum recurso pode exigir outro mod ou modpack. Jade e The One Probe são opcionais.

### Início e fim de feature

40. **Alinhamento:** ao iniciar uma feature, o agente descreve em poucas linhas o que vamos fazer e pergunta ao desenvolvedor se tem algo mais para acrescentar, antes da rodada de decisões, da issue e da branch.
41. **Encerramento do PR:** depois do merge de um PR, nesta ordem, o agente pede autorização para apagar a branch local e a remota, apresenta no que vai atuar em seguida e pergunta se o desenvolvedor quer algum ajuste no roteiro das próximas tarefas.

## Git

- **Autorização:** nunca faça commit, push, tag, release ou PR sem autorização explícita para aquela ação. Antes, mostre os arquivos, o resumo e a mensagem proposta.
- **Merge:** somente o desenvolvedor faz.
- **Autoria:** nunca atribua autoria ou coautoria a uma IA, nem em commits, PRs, tags, releases ou comentários.
- **Assinatura:** os commits feitos na máquina do desenvolvedor são assinados com SSH. Não desligue a assinatura.
- **Remote:** `git@github.com-zenndg:zenndg/redweave.git` (pergunta 170). Não troque por HTTPS nem por `git@github.com:`, que caem na conta profissional.
- **Conta do `gh`:** esta é a conta `zenndg`. Se o `gh api user --jq .login` mostrar outra, prefixe o comando com `GH_TOKEN=$(gh auth token --user zenndg)`.

### Fluxo de branches

O projeto segue o Gitflow, com Semantic Versioning e Conventional Commits.

| Branch | Sai de | Volta para | Papel |
|---|---|---|---|
| `main` | — | — | Versões publicadas; cada merge vira uma tag |
| `release/X.Y.Z` | `homolog` | `main` e `develop` | Release fechada |
| `homolog` | `develop` | `release/X.Y.Z` | Release em teste |
| `develop` | `main` | `homolog` | Integração do trabalho em andamento |
| `feature/{issue}-{slug}` | `develop` | `develop` | Funcionalidade, melhoria, correção não urgente ou tarefa técnica |
| `hotfix/{issue}-{slug}` | `main` | `main`, `homolog` e `develop` | Correção urgente do que já foi publicado |

### Regras de Git

- **Proibido:** commit direto em `main`, `homolog` ou `develop`, exceto o commit inicial do repositório; usar `--no-verify`; misturar várias mudanças lógicas num commit.
- **Issue:** toda tarefa começa por uma issue, e a branch leva o número dela. O nome da branch fica em inglês e em `kebab-case`: `feature/12-network-controller`.
- **PR:** todo merge é feito por PR, com `Closes #n` na descrição. A branch padrão no GitHub é a `develop`.
- **Commits:** em Conventional Commits, em português, e atômicos: `feat: adiciona o Network Controller`.
- **Versão:** Semantic Versioning, com tag anotada `vX.Y.Z` na `main`. Na fase `0.x`, mudança incompatível sobe o MINOR, e o resto sobe o PATCH.
- **Backlog:** um milestone do GitHub por fase, labels por área e o board no GitHub Projects.

## Estrutura de pacotes

Proposta. Vale a partir do projeto NeoForge, na Fase 1.

```text
io.github.marcelofabianov.redweave
├── RedweaveMod             // ponto de entrada: só conecta os registros
├── domain/                 // Java puro, testado com JUnit
│   ├── signal/             // Signal (0 a 15), ChannelColor
│   ├── network/            // Network (agregado), Node, Channel, permissões, NetworkLimits (interface)
│   ├── circuit/            // portas, ligações, chips (Fases 2 e 3)
│   └── simulation/         // compilação do grafo, avaliação por tick, loops e oscilação
├── application/            // casos de uso, um por ação do player, e as portas
├── config/                 // ModConfigSpec → implementa NetworkLimits
├── registry/               // DeferredRegisters
├── block/                  // Network Controller, Signal Node, BlockEntities
├── item/                   // Configurador, Signal Remote, chips
├── menu/                   // menus das telas
├── network/                // pacotes próprios, validados no servidor
├── server/                 // estado por servidor: redes, última rede usada de cada player e casos de uso
├── compat/jade/            // só este pacote importa o Jade
├── compat/top/             // só este pacote importa o The One Probe
├── client/                 // telas, renderers
└── datagen/                // receitas, loot, traduções
```

Os testes ficam em `src/test` (JUnit) e os GameTests num pacote `gametest`.
