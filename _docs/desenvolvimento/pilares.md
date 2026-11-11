# Pilares de desenvolvimento

Os pilares estão em **ordem de prioridade**. Quando dois princípios entram em conflito, vence o de maior prioridade, e toda decisão técnica deve poder ser justificada por essa hierarquia.

```text
Segurança > Defesa > Fronteira de módulo > Contrato externo > Comportamento testado >
Design sólido > Clareza > Simplicidade > Sem repetição > Sem excesso > Domínio > Regra do Escoteiro
```

As regras numeradas citadas aqui são as do [`AGENTS.md`](../../AGENTS.md). Os pilares foram adaptados dos do Cubit.

## 1. Segurança

**O que o Redweave protege é o mundo do player, o servidor e, na Fase 5, o hardware ligado à ponte.** Um mod roda com acesso total ao jogo, e uma rede sem fio alcança o mundo inteiro.

| Risco | Como o projeto se protege |
|---|---|
| Controle de acesso | Toda ação confere as permissões da rede. O servidor valida permissão e distância de todo pacote recebido (regra 17) |
| Dados recebidos do cliente | O cliente só pede ações, e o servidor decide (regra 16) |
| Cadeia de dependências | Dependência nova é decisão bloqueante. As versões são fixas, e o wrapper tem o checksum conferido |
| Segredos | Nada de segredo no repositório. Na Fase 5, a senha do broker fica só no config do servidor |
| Integridade do save | IDs, data components, NBT e chaves de config não mudam sem migração (regra 15) |
| Carga do servidor | Redes avaliadas por tick só quando mudam, com limites no config (regra 18) |
| Hardware real (Fase 5) | Ponte desligada por padrão, limite de mensagens por canal e failsafe no dispositivo |

### Checklist

- [ ] A ação confere as permissões da rede no servidor?
- [ ] O pacote recebido do cliente é validado?
- [ ] A mudança preserva os saves existentes?
- [ ] Nenhum caminho deixa uma rede sem limite de carga?

## 2. Defesa

**O código resiste a entrada inesperada, estado inválido e falha de dependência.**

- **Validar na fronteira:** NBT, data components, config e pacotes são validados antes de entrar no domínio.
- **Não confiar em dado de runtime:** o bloco do nó pode ter sumido, o chunk pode estar descarregado, e a rede salva pode não existir mais (regra 6).
- **Tratamento único:** a falha é registrada ou propagada, nunca as duas coisas.
- **Falhar seguro:** diante de dado inválido, o canal assume o estado mais restritivo, sinal 0, e registra no log (regra 14).
- **Falhar cedo ao iniciar:** um erro de programação derruba a inicialização, em vez de deixar o mod degradado.
- **Limites explícitos:** alcance, nós por rede e intervalos têm limites no config (regra 3).

### Checklist

- [ ] Todo dado externo é validado antes do domínio?
- [ ] O código trata bloco ausente e chunk descarregado?
- [ ] Um circuito com loop ou oscilação é detectado, sem travar o tick?

## 3. Fronteira de módulo

**A fronteira é a decisão que o resto da arquitetura protege.**

1. `domain/` e `application/` não importam o jogo (regra 10), e o ArchUnit reprova a violação.
2. As dependências apontam para dentro: as classes do jogo dependem do domínio, nunca o contrário.
3. Código comum nunca referencia `client/` (regra 19).
4. Dependências opcionais ficam isoladas em `compat/`, e o mod abre sem elas (regra 20).

### Checklist

- [ ] `domain/` continua sem importar o jogo?
- [ ] Nenhuma regra de sinal ficou em bloco, BlockEntity ou tela?
- [ ] O servidor continua abrindo sem classes de cliente?

## 4. Contrato externo

**Quem consome nem sempre atualiza junto.** O Redweave tem cinco contratos:

| Contrato | Quem consome | Regra |
|---|---|---|
| Saves | Mundos já criados, com redes e chips | IDs, data components e NBT mudam só com migração |
| Config | O pack, pelo packwiz | Chaves não mudam sem migração |
| Receitas e tags | KubeJS e outros mods | IDs e tags são estáveis, e as receitas são JSON |
| Sinal | Redstone do jogo e outros mods | Sempre de 0 a 15, pelas APIs de redstone do jogo |
| Tópicos MQTT (Fase 5) | Dashboards e dispositivos | Formato do tópico e da mensagem versionados |

- O contrato é definido nos documentos antes do código (regra 9).
- Arquivos gerados são versionados e nunca editados à mão (regra 26).

### Checklist

- [ ] O contrato está documentado antes do código?
- [ ] A mudança é compatível ou tem migração?

## 5. Comportamento testado

**O teste define o comportamento esperado antes da implementação.**

- O ciclo é teste falhando, código mínimo e refatoração (regra 28).
- A cobertura mínima de `domain/` e `application/` é de 85%, garantida no `pre-push`, com a meta de 95% (regra 30).
- Comportamento de mundo se testa no mundo, com GameTests, sem mock de `Level` (regra 29).
- Os testes não dependem de ordem, de relógio real nem de `sleep`.
- A correção de um bug começa pelo teste que o reproduz.

### Casos que sempre precisam de teste

- **Sinal:** limites 0 e 15, e valores fora do intervalo rejeitados.
- **Canal:** com várias entradas, vale o sinal mais forte.
- **Rede:** nó entra, sai, muda de canal e de rede sem deixar canal órfão.
- **Simulação:** avaliação só com canal alterado, ordem topológica, loop e oscilação detectados.
- **Item:** copiar configuração e gravar chip preservam todo o estado.

### Checklist

- [ ] O teste veio antes do código?
- [ ] A cobertura de `domain/` e `application/` está em 85% ou mais, perto da meta de 95%?
- [ ] Sucesso, erro e bordas estão cobertos?

## 6. Design sólido

| Princípio | Aplicação |
|---|---|
| Responsabilidade única | Uma classe tem um motivo para mudar. O bloco adapta, e o domínio decide |
| Aberto e fechado | Uma porta ou lógica nova entra sem mudar as regras existentes |
| Substituição de Liskov | Toda implementação de interface respeita o contrato dela |
| Segregação de interfaces | Interfaces são pequenas e definidas por quem consome (regra 12) |
| Inversão de dependência | O domínio define a interface, e o código do jogo a implementa, como `NetworkLimits` |

### Checklist

- [ ] Cada classe tem uma responsabilidade clara?
- [ ] O domínio depende só de interfaces próprias?

## 7. Clareza

**Código é lido mais vezes do que é escrito.**

- O nome revela a intenção: `Channel`, não `ChannelManager`. Sem `data`, `info` ou `manager`.
- Funções têm no máximo 30 linhas (regra 23) e complexidade ciclomática de no máximo 10.
- Blocos e BlockEntities adaptam e delegam. Regra de sinal neles é violação.
- Prefira retorno antecipado a `else` aninhado.
- Constantes em vez de números mágicos (regra 22) e comentários só para o porquê (regra 25).

### Checklist

- [ ] Os nomes revelam a intenção?
- [ ] Cada função faz uma coisa só?
- [ ] O código passa nos gates sem supressões injustificadas?

## 8. Simplicidade

**A solução mais simples que atende o requisito atual é a correta.**

- A abstração precisa de um motivo real.
- Prefira a biblioteca padrão e a API do NeoForge a uma dependência nova.
- Cache e otimização precisam de um problema medido, com o spark.

### Checklist

- [ ] É a solução mais simples que resolve corretamente?
- [ ] A complexidade é proporcional ao problema?

## 9. Sem repetição

**Conhecimento tem uma única representação no sistema.**

- A validação fica no value object, e os números ficam no config.
- A regra vale para conhecimento, não para linhas: dois trechos parecidos com motivos diferentes para mudar continuam separados.

### Checklist

- [ ] A validação ficou num lugar só?
- [ ] A repetição encontrada é de conhecimento ou é acidental?

## 10. Sem excesso

**Não implemente o que não é necessário agora.**

- Nada de recurso "por precaução", nem de código de fases futuras.
- Uma opção de config entra junto com o código que a lê.
- Questão em aberto é bloqueante: pergunte antes de escolher.

### Checklist

- [ ] Todo código é necessário para a tarefa atual?
- [ ] Nenhuma questão em aberto foi resolvida sem consulta?

## 11. Domínio

**Se o domínio aceitou o dado, o dado é válido. Se o dado é inválido, o domínio o rejeitou.**

- **Linguagem única:** os nomes do domínio são os mesmos no código, nos documentos e no [`glossario.md`](../glossario.md).
- **Value objects:** conceitos como sinal e cor de canal não são primitivos soltos. São `record` imutáveis que validam no construtor (regra 11).
- **Mudança por comportamento:** o estado muda por métodos que guardam as regras, nunca por setters.
- **Sem dependências ocultas:** o domínio não lê relógio, config nem o jogo. Quem chama fornece os dados.
- **README:** `domain/` documenta a linguagem, as regras e os porquês (regra 13).

### Checklist

- [ ] Todo conceito do domínio usa um value object?
- [ ] O construtor rejeita valores inválidos?
- [ ] O `README.md` do domínio está atualizado?

## 12. Regra do Escoteiro

**Deixe o código melhor do que o encontrou.**

| Situação | O que fazer |
|---|---|
| Um gate reprova em arquivo que você não alterou | Corrigir no mesmo PR |
| Um teste de outra área quebra | Investigar e corrigir; um bug real ganha issue própria |
| Documento desatualizado em arquivo que você leu | Perguntar ao desenvolvedor e, com a resposta, atualizar no mesmo PR |

Isso não autoriza refatorar um arquivo inteiro nem mudar o comportamento de outra funcionalidade.

## Matriz de decisão

| Situação | Quem vence | Decisão |
|---|---|---|
| Confiar na distância informada pelo cliente simplificaria o código | Segurança vence Simplicidade | O servidor calcula a distância |
| Assumir que o nó salvo ainda existe deixaria o método mais curto | Defesa vence Clareza | O código trata o nó ausente |
| Colocar a regra do canal no bloco evitaria uma classe | Fronteira vence Simplicidade | A regra fica em `domain/` |
| Renomear uma chave de config deixaria o nome melhor | Contrato vence Clareza | O nome só muda com migração |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-11 11:18 | Estende a cobertura à aplicação, com a meta de 95% (perguntas 196 e 197). |
| Marcelo Fabiano | 2026-11-09 10:05 | Adota os pilares do Cubit, adaptados ao Redweave. |
