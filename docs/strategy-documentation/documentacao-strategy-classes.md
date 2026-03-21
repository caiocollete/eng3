# Strategy no DoacaoService — O que cada classe faz

Documentação das responsabilidades de cada classe/interface da implementação do padrão Strategy na validação de doações.

---

## Pacote

`src/main/java/sosanimais/com/example/app/controller/service/strategy/doacao`

---

## Interface

### `DoacaoValidationStrategy`

**Papel:** Contrato que toda estratégia de validação de doação deve implementar.

**Responsabilidade:**
- Definir o método de validação (ex.: `validate(Doacao doacao)` ou `boolean isValid(Doacao doacao)`).
- Garantir que o `DoacaoService` possa tratar todas as estratégias de forma uniforme, sem conhecer o tipo concreto.

**Não faz:** Validação de campos comuns (isso fica no service ou em um validador base). Apenas define o “como” validar para um tipo específico.

---

## Estratégias concretas

### `DoacaoDinheiroValidationStrategy`

**Papel:** Valida doações do tipo **dinheiro**.

**Responsabilidade:**
- Verificar se `valor` não é nulo.
- Verificar se `valor` é maior que zero.
- Retornar sucesso ou falha (e mensagem de erro) conforme a regra.

**Não faz:** Validar nome, email ou data (isso é comum a todos os tipos). Não persiste; apenas valida.

---

### `DoacaoProdutoValidationStrategy`

**Papel:** Valida doações do tipo **produto** (remedios, comida, produtos).

**Responsabilidade:**
- Verificar se `produto` não é nulo.
- Verificar se `produto.getNome()` existe e não está em branco.
- Pode ser reutilizada para os tipos `remedios`, `comida` e `produtos` (mesma regra).

**Não faz:** Validar valor em dinheiro nem outros tipos. Não persiste; apenas valida.

---

## Contexto / registro de estratégias

### `DoacaoValidationContext` (ou mapa no `DoacaoService`)

**Papel:** Saber qual estratégia usar para cada tipo de doação.

**Responsabilidade:**
- Manter um mapeamento `tipo` → `DoacaoValidationStrategy` (ex.: `"dinheiro"` → instância de `DoacaoDinheiroValidationStrategy`).
- Oferecer método para obter a estratégia pelo tipo (ex.: `getStrategy(String tipo)`).
- Opcionalmente normalizar o tipo (trim, toLowerCase) antes de buscar.

**Não faz:** Validar nem persistir; apenas resolve qual estratégia deve ser usada.

*Se não houver classe separada,* o próprio `DoacaoService` pode manter um `Map<String, DoacaoValidationStrategy>` e fazer esse papel.

---

## Resultado de validação (opcional)

### `DoacaoValidationResult`

**Papel:** Representar o resultado da validação (válida ou inválida + mensagem).

**Responsabilidade:**
- Guardar se a validação passou (`boolean valid`).
- Guardar mensagem de erro quando `valid == false`.
- Permitir que o service decida se persiste ou retorna erro sem depender de exceção.

**Não faz:** Executar validação; apenas carrega o resultado retornado pelas strategies.

*Alternativa:* as strategies podem lançar exceção (ex.: `IllegalArgumentException`) com mensagem; aí essa classe não é obrigatória.

---

## Service (já existente, refatorado)

### `DoacaoService`

**Papel:** Orquestrar o fluxo de registro de doação usando as strategies.

**Responsabilidade:**
- Validar campos comuns (doacao não nula, nomeDoador, email, tipo, dataDoacao).
- Obter a estratégia correspondente ao `tipo` (via contexto ou mapa).
- Se não houver estratégia para o tipo, tratar como tipo inválido e retornar erro.
- Chamar a estratégia para validar a doação.
- Se válida, chamar `doacaoDAL.save(doacao)` e retornar a doação salva.
- Se inválida, retornar `null` ou resposta de erro conforme o contrato da API.

**Não faz:** Conhecer as regras específicas de dinheiro ou produto; delega isso às strategies.

---

## Resumo em tabela

| Classe / Interface              | Faz                                                                 | Não faz                          |
|--------------------------------|---------------------------------------------------------------------|----------------------------------|
| `DoacaoValidationStrategy`      | Define contrato de validação por tipo                               | Validar ou persistir             |
| `DoacaoDinheiroValidationStrategy` | Valida valor obrigatório e > 0 para tipo dinheiro              | Validar produto ou campos comuns |
| `DoacaoProdutoValidationStrategy`   | Valida produto e nome para remedios/comida/produtos            | Validar dinheiro ou persistir    |
| `DoacaoValidationContext` (ou Map no service) | Mapeia tipo → strategy e devolve a correta           | Validar ou persistir             |
| `DoacaoValidationResult` (opcional) | Guarda válido/inválido + mensagem de erro                    | Executar validação               |
| `DoacaoService`                 | Valida comum, escolhe strategy, valida específico, persiste          | Implementar regras por tipo      |

---

## Fluxo de chamadas

1. **Controller** recebe POST e chama `DoacaoService.registrarDoacao(doacao)`.
2. **DoacaoService** valida campos comuns; se falhar, retorna erro.
3. **DoacaoService** obtém a strategy pelo `tipo` (via contexto/mapa).
4. Se não houver strategy, **DoacaoService** retorna erro “Tipo de doação inválido”.
5. **DoacaoService** chama `strategy.validate(doacao)` (ou equivalente).
6. Se a strategy indicar inválido, **DoacaoService** retorna erro.
7. **DoacaoService** chama `doacaoDAL.save(doacao)` e retorna a doação salva.
