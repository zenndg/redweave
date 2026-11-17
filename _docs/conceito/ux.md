# UX

Cinco regras decidem qualquer dúvida de design de tela ou mecânica. Se uma tela quebra uma delas, ela muda.

1. **Ver o sinal, sempre.** Todo fio, nó e porta mostra se está ligado e com qual força, de 0 a 15, no mundo e na tela.
2. **Simples na superfície, fundo por baixo.** O caso comum resolve com um clique. O avançado, como o editor de chip e o debug por tick, existe, mas não aparece até o player procurar.
3. **Configurar onde se está.** Um nó recém-colocado entra na rede com um clique, com a última rede usada já destacada, e nasce com os 16 canais inativos, prontos para ativar. Shift + scroll troca o modo do Configurator e o de uma porta.
4. **Errar barato.** Desfazer no editor, prévia de colocação antes do bloco existir e aviso claro quando um circuito oscila ou entra em loop, apontando onde.
5. **Ensinar no caminho.** Tooltips com a tabela-verdade de cada porta, uma dica de uma linha no primeiro uso de cada tela e um livro guia com exemplos prontos.

## Linguagem visual das telas

Segue a das telas do Cubit, para os dois mods conversarem no mesmo pack:

- janelas com estilo próprio, sem a textura do baú do jogo;
- abas numa fileira horizontal sob o cabeçalho, como estado só do cliente, e a Tela do Nó sem abas laterais;
- estado colorido no topo, como `● Online`, `● Unloaded` ou `● Out of range`;
- números abreviados e alinhados, e descrições que vêm do config, nunca fixas no texto;
- nada passa da borda, e um tooltip mostra o texto inteiro;
- 16 cores de canal, iguais às das lãs, e texturas emissivas quando o sinal está ativo.

## Telas principais

| Tela | Onde abre | O que faz |
|---|---|---|
| Tela do Nó | Clique no nó com o Configurator no modo `Open` | Na primeira vez, a escolha de rede; depois, o nome do nó e a rede no cabeçalho, a fileira das 16 cores e, no painel do canal, em duas colunas, `Status`, direção e o medidor do sinal ao vivo à esquerda e a face do bloco em 3D à direita |
| Tela do Controlador | Clique no Network Controller com a mão vazia ou com o Configurator no modo `Open` | Nome, cor e dono; abas `Components` (nós e módulos), `Signals` (nomes dos canais), `Access` e `Log`, com `Clear log` para o dono |
| Tela do Terminal | Clique no Network Terminal com a mão vazia ou com o Configurator no modo `Open` | As redes do dono, em ordem alfabética, com estado e quantidade de nós; abrir, renomear, desligar, religar, limpar nós e apagar as redes `Unplaced` |
| Editor de Chip | Bancada de Circuitos, na Fase 3 | Grid de arrastar e soltar, fios animados, modo teste com pausar e avançar um tick, gravação com nome e ícone |
| Livro Guia | Item | Capítulos por fase, com exemplos prontos para copiar para um chip |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Zennd | 2026-11-17 11:11 | A aba `Nodes` vira `Components` (pergunta 809). |
| Zennd | 2026-11-12 19:51 | A Tela do Nó em duas colunas, com o medidor no painel (pergunta 346). |
| Zennd | 2026-11-12 19:23 | O Network Terminal abre também com o Configurator no modo `Open` (pergunta 343). |
| Zennd | 2026-11-12 17:18 | Atualiza o nó que nasce inativo, as abas e as telas do Nó, do Controlador e do Terminal (perguntas 166, 224, 226, 232 e 233). |
| Zennd | 2026-11-12 16:50 | Tira a lógica da Tela do Nó (pergunta 308). |
| Zennd | 2026-11-11 09:51 | Aplica a escolha de rede, os modos do Configurator, o log e a Tela do Terminal (perguntas 130 a 150). |
| Marcelo Fabiano | 2026-11-09 15:39 | Aplica as perguntas 22 e 29. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
