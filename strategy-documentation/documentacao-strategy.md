# Implementação de Strategy no `DoacaoService`

## Objetivo

Refatorar o `DoacaoService` para aplicar o padrão **Strategy** na validação de doações por tipo (`dinheiro`, `remedios`, `comida`, `produtos`), removendo condicionais extensos e facilitando manutenção/testes.

---

## Problema atual

Hoje, o método `registrarDoacao` contém validações com múltiplos `if` baseados no campo `tipo`.  
Isso gera:

- alto acoplamento da regra em um único método
- dificuldade para adicionar novos tipos de doação
- baixa coesão
- testes mais complexos

---

## Solução proposta (Strategy)

Separar cada regra de validação em uma estratégia específica.

### Estrutura sugerida

Criar pacote:

- `src/main/java/sosanimais/com/example/app/controller/service/strategy/doacao`

Classes/interfaces:

1. `DoacaoValidationStrategy` (interface)
2. `DoacaoDinheiroValidationStrategy`
3. `DoacaoProdutoValidationStrategy` (cobre `remedios`, `comida`, `produtos`)
4. `DoacaoValidationContext` (ou resolver via `Map<String, DoacaoValidationStrategy>` dentro do service)
5. Exceção/resultado de validação (opcional):
   - `DoacaoValidationResult` **ou**
   - lançar `IllegalArgumentException` com mensagem clara

---

## Regras de validação por Strategy

### 1) Regras comuns (base)

Aplicadas antes de delegar para estratégia específica:

- `doacao != null`
- `nomeDoador` obrigatório
- `email` obrigatório
- `tipo` obrigatório
- `dataDoacao` obrigatória

### 2) `Dinheiro`

- `valor != null`
- `valor > 0`

### 3) `Remedios`, `Comida`, `Produtos`

- `produto != null`
- `produto.nome` obrigatório

---

## Passo a passo de implementação

1. Criar a interface de estratégia com método de validação.
2. Implementar estratégias concretas por tipo.
3. No `DoacaoService`, criar registro de estratégias por chave de tipo:
   - `dinheiro` -> `DoacaoDinheiroValidationStrategy`
   - `remedios`/`comida`/`produtos` -> `DoacaoProdutoValidationStrategy`
4. Manter validações comuns no service (ou extrair para helper).
5. Substituir os `if` de tipo por:
   - resolver estratégia pelo `tipo`
   - validar
   - persistir via `doacaoDAL.save(doacao)`
6. Definir comportamento para tipo inválido:
   - retornar erro claro (`"Tipo de doação inválido: X"`)

---

## Exemplo de fluxo final no `DoacaoService`

1. Valida campos comuns
2. Busca estratégia por `tipo`
3. Executa validação específica
4. Persiste doação
5. Retorna objeto salvo

---

## Critérios de aceite

- Não há `if`/`else` por tipo dentro de `registrarDoacao`
- Cada tipo de doação tem validação em classe própria
- Tipos inválidos retornam erro legível
- Inclusão de novo tipo exige apenas:
  - nova Strategy
  - registro no mapa/contexto
- Testes passando

---

## Testes recomendados

### Unitários de Strategy

- dinheiro válido/inválido
- produto válido/inválido (remedios/comida/produtos)

### Unitários de Service

- tipo suportado chama strategy correta
- tipo inválido retorna falha
- doação válida chama `doacaoDAL.save`
- erro de validação não chama persistência

---

## Riscos e cuidados

- Normalizar `tipo` (`trim().toLowerCase()`) antes de resolver estratégia
- Evitar duplicação de validações comuns nas strategies
- Definir padrão único de erro (mensagem/exception)

---

## Possível evolução futura

- Aplicar mesmo padrão para cálculo de impacto da doação (ex.: score, prioridade)
- Encadear Strategy + Factory para criação automática das estratégias
- Migrar para injeção via Spring (`@Component`) nas strategies

---

## Checklist rápido

- [ ] Criar interface `DoacaoValidationStrategy`
- [ ] Criar `DoacaoDinheiroValidationStrategy`
- [ ] Criar `DoacaoProdutoValidationStrategy`
- [ ] Refatorar `DoacaoService.registrarDoacao`
- [ ] Cobrir tipo inválido
- [ ] Criar testes unitários
- [ ] Validar regressão dos endpoints de doação