# Strategy: conceito funcional

## O que ele resolve

O padrão **Strategy** é usado quando existe mais de uma forma de executar a mesma tarefa.

Em vez de colocar todas as regras dentro de uma única classe com vários `if` e `else`, cada regra fica separada em uma estratégia própria.

Assim:

- o sistema identifica o tipo da situação
- escolhe a estratégia correta
- executa a regra correspondente

---

## Como pensar no Strategy

Imagine a seguinte situação:

> "O sistema precisa validar uma doação."

Mas a validação muda conforme o tipo:

- doação em dinheiro
- doação de remédios
- doação de comida
- doação de produtos

Sem Strategy, o `DoacaoService` teria que decidir tudo com vários condicionais.

Com Strategy, o service não precisa conhecer os detalhes de cada regra. Ele apenas escolhe quem vai validar.

---

## Funcionamento na prática

O fluxo funcional é este:

1. O sistema recebe uma doação.
2. Primeiro valida os campos comuns.
3. Depois verifica o tipo da doação.
4. Com base nesse tipo, escolhe a estratégia correta.
5. A estratégia executa a validação específica.
6. Se estiver tudo certo, a doação é salva.

Ou seja, existe uma separação clara:

- o serviço coordena o fluxo
- a estratégia aplica a regra específica

---

## Papéis principais

### Contexto

É quem usa a estratégia.

No projeto, esse papel fica no `DoacaoService`, porque ele recebe a doação, escolhe a estratégia correta e continua o fluxo.

### Strategy

É o contrato comum que define como uma validação deve funcionar.

Todas as estratégias seguem a mesma ideia: receber a doação e validar conforme sua regra.

### Estratégias concretas

São as implementações reais.

Exemplos:

- estratégia para validar doação em dinheiro
- estratégia para validar doação de produto

Cada uma cuida apenas do seu caso.

---

## Exemplo com doação

Pense assim:

- o `DoacaoService` recebe uma doação
- ele vê se o tipo é `dinheiro`
- se for, usa a estratégia de dinheiro
- se for `remedios`, `comida` ou `produtos`, usa a estratégia de produto

Então:

- a estratégia de dinheiro valida o valor
- a estratégia de produto valida o nome do produto

O service não precisa escrever todas essas regras dentro dele.

---

## Vantagem principal

A maior vantagem é deixar a regra de negócio mais organizada.

Isso facilita manutenção, testes e crescimento do sistema, porque novas regras podem ser adicionadas sem reescrever o fluxo principal.

Exemplo:

Se amanhã existir um novo tipo de doação, basta criar uma nova estratégia e registrá-la no fluxo.

---

## Resumo final

O Strategy funciona como uma troca de comportamento conforme a necessidade:

- existe uma tarefa
- existem várias formas de executá-la
- o sistema escolhe a forma certa para aquele caso

No projeto, isso faz sentido na **validação de doações por tipo**, porque cada tipo possui uma regra diferente, mas o fluxo principal continua sendo o mesmo.
