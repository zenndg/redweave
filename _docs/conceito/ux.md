# UX

Cinco regras decidem qualquer dúvida de design de tela ou mecânica. Se uma tela quebra uma delas, ela muda.

1. **Ver o sinal, sempre.** Todo fio, nó e porta mostra se está ligado e com qual força, de 0 a 15, no mundo e na tela.
2. **Simples na superfície, fundo por baixo.** O caso comum resolve com um clique. O avançado, como o editor de chip e o debug por tick, existe, mas não aparece até o player procurar.
3. **Configurar onde se está.** Um nó recém-colocado já nasce num canal livre e funciona sem abrir tela. Parear é clicar um nó no outro, e shift + scroll troca o modo de uma porta.
4. **Errar barato.** Desfazer no editor, prévia de colocação antes do bloco existir e aviso claro quando um circuito oscila ou entra em loop, apontando onde.
5. **Ensinar no caminho.** Tooltips com a tabela-verdade de cada porta, uma dica de uma linha no primeiro uso de cada tela e um livro guia com exemplos prontos.

## Linguagem visual das telas

Segue a das telas do Cubit, para os dois mods conversarem no mesmo pack:

- janelas com estilo próprio, sem a textura do baú do jogo;
- abas à direita, como estado só do cliente;
- estado colorido no topo, como `● Online`, `● Unloaded` ou `● Out of range`;
- números abreviados e alinhados, e descrições que vêm do config, nunca fixas no texto;
- nada passa da borda, e um tooltip mostra o texto inteiro;
- 16 cores de canal, iguais às das lãs, e texturas emissivas quando o sinal está ativo.

## Telas principais

| Tela | Onde abre | O que faz |
|---|---|---|
| Tela do Nó | Clique no nó com a mão vazia | Slots de canal à esquerda; à direita, direção, face do bloco em 3D, canal e lógica; medidor do sinal ao vivo no rodapé |
| Tela do Controlador | Clique no Network Controller | Nome, cor e dono da rede; lista de nós por canal, com o estado ao vivo; gráfico dos últimos 30 segundos de cada canal |
| Editor de Chip | Bancada de Circuitos, na Fase 3 | Grid de arrastar e soltar, fios animados, modo teste com pausar e avançar um tick, gravação com nome e ícone |
| Livro Guia | Item | Capítulos por fase, com exemplos prontos para copiar para um chip |

## Histórico de modificações

| Pessoa | Data e hora | Modificação |
|---|---|---|
| Marcelo Fabiano | 2026-11-09 15:39 | Aplica as perguntas 22 e 29. |
| Marcelo Fabiano | 2026-11-09 10:05 | Cria o documento. |
