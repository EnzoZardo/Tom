# Tom Lang

<img src="tom-logo.svg" alt="tom-logo.svg" style="width:200px; border-radius:20px"/>
<br/>

Linguagem simples de programação (Tom Lang)

Estou me baseando na seguinte playlist para criar meu próprio interpretador:
https://www.youtube.com/watch?v=8VB5TY1sIRo&list=PL_2VhOvlMk4UHGqYCLWc6GO8FaPl8fQTh
  0
1Como o projeto está em andamento, deixo claro que ainda preciso ver coisas como condicionais, loop's, operadores unários, overload de operações, entre muitas outras coisas que terão de ser estudadas sem me basear na playlist informada, pois ela não contempla isso.

### Atualizações 3.4.26
Playlist finalizada, agora estou por minha conta. 
Os próximos passos serão: 
- A finalização da decisão da sintaxe;
- A adição de tipos como texto e caracteres, além da divisão entre número inteiro e real;
- Adição de funções para conversão de tipos primitivos;
- Adição de statements de if e enquanto.

### Atualizações 18.4.26
- Adicionados tipos e suas respectivas validações;
- O código foi separado em pastas diferentes para melhor organização;
- Limpeza e organização de código foi feita e implementadas operações antes faltantes;
Agora é necessário:
- Implementar arrays, enums e propriedades computadas em objetos.

### Atualizações 20.4.26
- Adicionados arrays e suas indexações
- Adicionando reassign em object keys
- Finalização da refatoração inicial
- Divisão de string por inteiro que resulta em uma lista
- É necessário agora adicionar uma validação somente para membros de objetos e arrays poderem receber o assign na função de assignMember

### Atualizações 22.4.26
- Adicionando reassign em array index
- Adicionando novo escopo
- Agora expressões simples são aceitas no meio do código
- É necessário adicionar ; no final das expressões pq tá ficando complicado

### Atualizações 25.4.26
- Adicionando indexação em string (mas não reatribuição de indíce)
- Adicionados if's e else's
- Adicionado enquanto 
- Criação de logo da linguagem (em homenagem ao gato)

### Atualizações 25.4.26 - Parte 2
- Adicionando foreach
- É necessário criar o operador binário "em" para textos, números, arrays e objetos
- É necessário criar classes
- É necessário incrementar a tipagem para termos expressões binárias como "ou"
- É necessário criar o tipo caractere

### Atualizações 26.4.26
- Comentários
- Objetos nativos

### Atualizações 27.4.26
- REPL bem primitivo
- É necessário adicionar o operador unário "congela" para objetos e listas e evaluar o operador binário "em"
- 
### Atualizações 10.5.26
- Adição de operador unário "congele"
- Alteração de result para melhores mensagens de erro.

### Atualizações 13.5.26
- Adição de retorne, continue e pare

### Atualizações 7.6.26
- Adição de operações básicas de classes, como declarações, intancias, etc
- É necessário verificar os níveis de proteção de cada membro ao executar funções e também não se pode alterar para todas as classes quando se altera uma (não entendi o pq isso aconteceu)

### Atualizações 8.6.26
- Adição de chamada com referência para classe mãe do método
- Iniciando criação de strategies para assignment

### Atualizações 9.6.26
- Adição de chamadas com referência para o valor do membro da classe, não necessariamente seu valor "ClassMember"

### Atualizações 13.6.26
- Adição de strategies para member expressions

### Atualizações 16.6.26
- Adição de strategies para call expressions
- Averiguação para a criação de um fluxo todo com result pattern
- Adição de verificação de níveis de proteção
- O código precisa de uma boa refatorada

### Atualizações 17.6.26
- Adição de inclua para ser nosso import
- Adição de parse de herança, falta apenas evaluar