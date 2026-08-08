# Auditoria de Código — Interpretador Tom

**Data:** 2026-08-08
**Commit:** `1f0f876`
**Build:** `javac` OpenJDK 25.0.2, saída em `/tmp/opencode/tom-out`
**Escopo:** leitura e análise de todo o `src/` + reprodução dos bugs com programas mínimos (`/tmp/opencode/audit*.tom`). Nenhuma correção foi aplicada.

## Sumário executivo

Foram confirmados **6 bugs reproduzíveis**, vários deles com impacto direto em corretude (avaliação dupla de operandos, mutação de operando por unário, erro de iteração em texto, igualdade numérica inconsistente) e em robustez (stack traces crus, string não fechada aceita silenciosamente). Além disso, a auditoria encontrou inconsistências de design e problemas de performance concentrados em: representação de números (`float`), armazenamento de arrays (`HashMap`), cópia defensiva por acesso a membro e redução de tipos genéricos sem cache.

---

## 1. Bugs confirmados

### B1 — Operandos binários avaliados duas vezes

- **Arquivo:** `src/Runtime/Evaluate/Factory/BinaryExpr/BinaryExprFactory.java:19-20`
- **Causa:** `build()` avalia `expr.left` e `expr.right` para escolher a estratégia e, em seguida, a estratégia reevalua os mesmos nós via `Interpreter.evaluate`. Efeitos colaterais ocorrem em dobro e todo operador binário custa o dobro.
- **Repro (`audit1.tom`):**
  ```toml
  funcao lado_efeito(x: inteiro): inteiro {
      escreva("lado: " + x)
      retorna x
  }
  const r: inteiro = 2 + lado_efeito(1) + lado_efeito(2)
  escreva("r = " + r)
  ```
  Saída (4 prints em vez de 2):
  ```
  lado: 1
  lado: 2
  lado: 1
  lado: 2
  r = 3
  ```
- **Impacto:** efeitos colaterais duplicados; custo 2× por operação binária.
- **Correção:** avaliar cada operando uma única vez e passar os valores prontos à estratégia; ou escolher a estratégia pelo tipo/operador sem avaliar.

### B2 — Unário negativo muta o operando

- **Arquivo:** `src/Runtime/Values/NumericValue.java:29-33` (método `opposite()`), `src/Runtime/Evaluate/Strategies/UnaryExpr/AdditiveUnaryStrategy.java:22-24`
- **Causa:** `opposite()` faz `value = -value` in-place e retorna `this`; a estratégia devolve esse mesmo objeto. Qualquer uso de `-a` altera `a`.
- **Repro (`audit2.tom`):**
  ```toml
  const a: inteiro = 5
  const b: inteiro = -a
  escreva("b = " + b)
  escreva("a = " + a)
  ```
  Saída (esperado `a = 5`):
  ```
  b = -5
  a = -5
  ```
- **Correção:** retornar um novo `NumericValue` em `opposite()`.

### B3 — `para cada` sobre texto estoura no fim

- **Arquivo:** `src/Runtime/Values/StringValue.java:73` e `:64`
- **Causa:** `iteratorSize()` retorna `value.length() + 1`, mas `iterate(i)` exige `i < length`. A última iteração (`i == length`) lança `InvalidIndexException`.
- **Repro (`audit3.tom`):**
  ```toml
  const s: texto = "abc"
  para cada (c em s) {
      escreva(c)
  }
  ```
  Saída:
  ```
  a
  b
  c
  Exception in thread "main" Entities.Exceptions.InvalidIndexException: O índice 3 é inválido.
  ```
- **Correção:** `iteratorSize()` retornar `length`, ou `iterate()` tratar `i == length` (ex.: `NullValue`).

### B4 — Igualdade numérica inconsistente

- **Arquivo:** `src/Runtime/Values/NumericValue.java:36-45` (`equals`)
- **Causa:** `equals` exige que `isInteger` coincida além do valor; como o runtime representa tudo em `float`, valores grandes colapsam no mesmo `float`.
- **Repro (`audit5.tom`):**
  ```toml
  const a: inteiro = 5
  const b: real = 5.0
  const igual: logico = a == b
  escreva(igual)   # falso  (esperado verdadeiro)
  ```
  E (`audit11.tom`):
  ```toml
  const a: inteiro = 16777217
  escreva(a)                       # 16777216 (perda de precisão silenciosa)
  escreva(16777217 == 16777216)    # verdadeiro
  ```
- **Impacto:** `5 == 5.0` é `falso`; `16777217 == 16777216` é `verdadeiro`. A comparação deve promover inteiro para o tipo do outro operando.
- **Correção:** comparar apenas o valor numérico (promovendo int→float), ou adotar `double`/`long`.

### B5 — Literal inteiro grande vaza `NumberFormatException`

- **Arquivo:** `src/Ast/Expressions/Literals/IntegerLiteral.java:20`
- **Causa:** `Integer.parseInt(token.value)` estoura para literais > 2³¹−1, enquanto o runtime (`NumericValue`) é `float`.
- **Repro (`audit9.tom`):**
  ```toml
  const a: inteiro = 9999999999
  escreva(a)
  ```
  Saída: stack trace cru de `NumberFormatException` (sem mensagem Tom, sem localização).
- **Correção:** usar `Float.parseFloat` (coerente com o runtime) ou `double`; isso também elimina o domínio numérico duplo `int` (AST) vs `float` (runtime) — ver I4.

### B6 — String literal não fechada engole o resto do arquivo

- **Arquivo:** `src/Lexer/Readers/StringLiteralReader.java:34-50`
- **Causa:** a leitura para no EOF sem verificar a aspa de fechamento; o token é emitido mesmo assim, consumindo tudo até o fim do arquivo (inclusive novas linhas e demais statements).
- **Repro (`audit10.tom`):**
  ```toml
  const s: texto = "abc
  const b: inteiro = 42
  escreva(s)
  escreva(b)
  ```
  Saída: nada, `exit 0` — o interpretador não acusa erro e nenhum statement posterior é executado.
- **Correção:** lançar `LexingException` quando o fechamento não for encontrado.

---

## 2. Inconsistências

| ID | Descrição |
|----|-----------|
| I1 | **Erros de runtime sem tratamento.** `Main.java:41-64` só captura `ParsingException`/`LexingException`; `InvalidIndexException`, `NumberFormatException` e `java.lang.RuntimeException` (lançado cru em `ConstructorCallStrategy.java:84`) vazam como stack trace. Há um `//TODO: fix` no código. |
| I2 | **Colisão de nome `RuntimeException`.** `Entities.Abstractions.Runtime.RuntimeException` é um `RuntimeValue` (não uma exceção Java) e conflita com `java.lang.RuntimeException`. `evaluateContinue`/`evaluateBreak` retornam a versão Tom; outros pontos lançam a do Java. |
| I3 | **Import inválido/não usado.** `javax.naming.directory.InvalidAttributeIdentifierException` em `ClassMemberStrategy.java:18` (copy-paste de API de diretórios); wildcards `Runtime.Values.*`, `Ast.Statements.*`, `Entities.Exceptions.*`. |
| I4 | **Domínio numérico duplo.** `int` no AST (`IntegerLiteral`) vs `float` no runtime (`NumericValue`) — origem de B4/B5. |
| I5 | **Proteção de acesso inconsistente.** `assignClassMember` só inspeciona o `parent` imediato; `ClassMemberStrategy.java:34-42` varre a cadeia de herança inteira. |
| I6 | **`const` não congela.** `const b = a; b[0] = 99` muta `a` (audit14). `const` só impede reassign da variável; mutação interna exige unário `congele`/`freezeMe`, aplicado manualmente. |
| I7 | **`Parser.expect` frágil.** Consome o token antes de validar; `String.format` só é aplicado quando a mensagem contém `%s` — erros com `%` literal quebrariam (`Parser.java:41-54`). |
| I8 | **Miscelânea.** `TypeKind.NativeFunction` fora do PascalCase; typo `#refion` (era `#region`) em `ReservedKeys`; o helper `Errors` existe, mas o Lexer constrói erros diretamente. |

---

## 3. Padrões de design fracos

- **D1 — God classes:** `Statements` (405 linhas), `Environment` (409), `TypeChecker` (switch gigante em `check`), `SymbolType`.
- **D2 — Duplicação:** `reduceParameters` quase idêntico em `SymbolType` e `ClassType`; padrão `containsKey + get` repetido em `assignIndex`/`assignMember`/`assignClassMember`; `ErrorOr.empty()` e `propagateError()` são o mesmo método com nomes diferentes.
- **D3 — `ErrorOr`:** campos públicos mutáveis (`value`, `error`) em vez de `final`.
- **D4 — Cópia defensiva por acesso a membro:** `ClassMemberStrategy.java:46` copia o membro (deep copy) a cada leitura. Além do custo, escrita aninhada (`g.campo.x = 5`) atua sobre a cópia.
- **D5 — `Main.java`:** falta `return` após `REPL.run()` (fall-through → `AIOOBE` se o REPL terminar); `System.exit` dentro de catch.
- **D6 — REPL:** `mustStop()` sempre `false` (loop infinito); `split("")` + `toCharArray()[0]` para ler caractere; `readLine()` pode devolver `null` → NPE em `isBlank()`; linhas concatenadas sem `\n`.
- **D7 — Amarração a JDK 25:** `void main` (source-file mode), `java.lang.IO` (JEP 501) e `Reader.readAllAsString()` não existem em JDK < 25.

---

## 4. Performance (com soluções)

| ID | Problema | Local | Solução |
|----|----------|-------|---------|
| P1 | Avaliação dupla de operandos (B1) | `BinaryExprFactory.java:19-20` | Avaliar uma vez e passar valores à estratégia. |
| P2 | Arrays como `HashMap<Integer,…>` — boxing por acesso; ordem de iteração por bucket (audit12 passou por acaso; índices colidindo tipo `{0,16}` iteram fora de ordem) | `ArrayValue.java:13` | `ArrayList` com índice direto (ou `LinkedHashMap`). |
| P3 | Iteração de objeto O(n²) — reconstrói `entrySet().stream().toList()` + `HashMap` duplo por iteração | `ObjectValue.java:116-122` | Iterador com cursor. |
| P4 | Cópia defensiva em toda leitura de membro (D4) | `ClassMemberStrategy.java:46` | Remover cópia na leitura; copiar só na escrita. |
| P5 | `ClassValue.copy()` recursa na cadeia de pais + `bindTypeArguments` varre a cadeia de novo | `ClassValue.java:188-205` | Cachear redução de tipos; copiar com lazy/estrutura compartilhada. |
| P6 | `Type.reduce` re-parseia e aloca listas a cada chamada | `SymbolType.java:68`, `ClassType.java:45` | Memoização por (tipo, escopo). |
| P7 | Concatenação de string por caractere em loop | `OperatorReader.java:39` | Montar com char array / índice. |
| P8 | Um `Environment` novo por iteração de `para cada` | `Statements.java:341` | Reuso de escopo ou declaração explícita. |
| P9 | Números em `float` — perda de precisão e comparação inconsistente (B4) | `NumericValue.java` | `double` (mínimo) ou `BigDecimal`/`long` conforme semântica. |

---

## 5. Questões de semântica a decidir

- **Q1 — `const`:** deve ter semântica de referência (atual) ou cópia profunda?
- **Q2 — Cópia:** `copy()` é profundo, mas usado de forma seletiva (instanciação de classe, acesso a membro). Confirmar se a perda de escrita aninhada (D4) é aceitável.
- **Q3 — `ObjectValue.bool()`:** usa `ValueType.Null` para "todas as props nulas" — objeto com uma prop nula e outra real conta como verdadeiro. Verificar intenção.

---

## Apêndice — Arquivos de reprodução

Os programas mínimos usados estão em `/tmp/opencode/audit1.tom` … `audit14.tom` (mapeados nas seções acima). Para reproduzir:

```bash
javac -d /tmp/opencode/tom-out -cp /tmp/opencode/tom-out $(find src -name "*.java")
java -cp /tmp/opencode/tom-out Main /tmp/opencode/audit1.tom
```

Suíte de validação de generics (passando, do trabalho da sessão anterior): `/tmp/opencode/generic.tom`, `inherit.tom`, `nested.tom`, `edge.tom`, `unionclass.tom`, `genericfun.tom`, `classfun.tom`.
