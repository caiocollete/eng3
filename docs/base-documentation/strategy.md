# Padrao Strategy

## Visao geral

O padrao **Strategy** define uma familia de algoritmos, encapsula cada variacao em uma classe propria e permite trocar o comportamento em tempo de execucao sem alterar a classe principal.

Em vez de concentrar muitas regras em `if` e `else`, a regra especifica e delegada para uma estrategia concreta que segue um contrato comum.

---

## Quando usar

O Strategy e indicado quando:

- existe mais de uma forma de executar a mesma operacao
- a classe principal esta ficando cheia de condicionais por tipo
- novos comportamentos precisam ser adicionados com frequencia
- queremos reduzir acoplamento e facilitar testes

---

## Estrutura do padrao

### 1. Contexto

E a classe principal que usa uma estrategia.

Responsabilidades:

- manter uma referencia para a interface da estrategia
- delegar a execucao da regra para a estrategia atual
- permitir a troca da estrategia sem mudar sua propria estrutura

### 2. Strategy

E a interface ou contrato comum entre todas as estrategias.

Responsabilidades:

- definir a assinatura do metodo que sera executado
- garantir que todas as estrategias possam ser usadas da mesma forma pelo contexto

### 3. Concrete Strategies

Sao as implementacoes concretas da estrategia.

Responsabilidades:

- implementar um comportamento especifico
- manter a mesma assinatura definida pela interface
- permitir a expansao da solucao com novas regras sem alterar o contexto

---

## Relacao entre os participantes

```text
Contexto
  -> possui uma referencia para Strategy
  -> delega a execucao para a estrategia atual

Strategy
  -> define o contrato comum

ConcreteStrategyA / ConcreteStrategyB / ConcreteStrategyN
  -> implementam comportamentos diferentes
```

---

## Exemplo sem Strategy

Sem o padrao, a classe principal costuma concentrar regras diferentes em varios blocos condicionais:

```java
public double valorConta(Veiculo veiculo, long periodo) {
    if (veiculo instanceof Passeio) {
        if (periodo < 12 * HORA) {
            return 2.0 * Math.ceil(periodo / HORA);
        } else if (periodo < 15 * DIA) {
            return 26.0 * Math.ceil(periodo / DIA);
        } else {
            return 300.0 * Math.ceil(periodo / MES);
        }
    } else if (veiculo instanceof Carga) {
        // outras regras
    } else if (veiculo instanceof Moto) {
        // outras regras
    }

    throw new IllegalArgumentException("Tipo de veiculo invalido");
}
```

### Problemas dessa abordagem

- alto acoplamento
- baixa coesao
- dificuldade para incluir novos tipos
- maior risco de regressao ao alterar regras existentes
- testes mais complexos

---

## Exemplo com Strategy

Com o padrao, a classe principal delega o calculo para uma estrategia:

```java
public interface CalculoValor {
    double calcular(long periodo, Veiculo veiculo);
}
```

```java
public class ContaEstacionamento {
    private CalculoValor calculo;
    private Veiculo veiculo;
    private long inicio;
    private long fim;

    public double valorConta() {
        return calculo.calcular(fim - inicio, veiculo);
    }

    public void setCalculo(CalculoValor calculo) {
        this.calculo = calculo;
    }
}
```

```java
public class CalculoDiaria implements CalculoValor {
    private final double valorDiaria;

    public CalculoDiaria(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    @Override
    public double calcular(long periodo, Veiculo veiculo) {
        return valorDiaria * Math.ceil((double) periodo / DIA);
    }
}
```

### Ganhos com essa abordagem

- o contexto nao precisa conhecer os detalhes de cada regra
- novas estrategias podem ser adicionadas sem reescrever a classe principal
- cada comportamento fica isolado em sua propria classe
- o codigo fica mais facil de testar e manter

---

## Aplicacao no projeto

No projeto, o Strategy foi pensado para o fluxo de validacao de doacoes.

### Problema identificado

O metodo `registrarDoacao` em `DoacaoService` concentra validacoes por tipo, como:

- `dinheiro`
- `remedios`
- `comida`
- `produtos`

Isso faz com que a regra de negocio fique presa ao service, dificultando extensao e manutencao.

### Estrutura planejada

Pacote relacionado:

`src/main/java/sosanimais/com/example/app/controller/service/strategy/Doacao`

Classes esperadas:

- `DoacaoValidationStrategy`
- `DoacaoDinheiroValidationStrategy`
- `DoacaoProdutoValidationStrategy`
- `DoacaoValidationContext`

### Fluxo esperado

1. `DoacaoService` valida os campos comuns da doacao.
2. O tipo da doacao e usado para selecionar a estrategia correta.
3. A estrategia executa a validacao especifica.
4. Se a validacao passar, a doacao e persistida.

### Exemplo conceitual

```java
public interface DoacaoValidationStrategy {
    void validate(Doacao doacao);
}
```

```java
public class DoacaoDinheiroValidationStrategy implements DoacaoValidationStrategy {
    @Override
    public void validate(Doacao doacao) {
        if (doacao.getValor() == null || doacao.getValor() <= 0) {
            throw new IllegalArgumentException("Valor invalido para doacao em dinheiro.");
        }
    }
}
```

```java
public class DoacaoProdutoValidationStrategy implements DoacaoValidationStrategy {
    @Override
    public void validate(Doacao doacao) {
        if (doacao.getProduto() == null || doacao.getProduto().getNome() == null ||
            doacao.getProduto().getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto obrigatorio.");
        }
    }
}
```

---

## Estado atual no repositorio

Hoje, o repositorio ja possui os arquivos de Strategy para doacao, mas eles ainda estao como estrutura inicial. A validacao principal continua centralizada em `DoacaoService`.

Isso significa que:

- o padrao ja foi identificado como direcao correta
- a documentacao complementar em `docs/strategy-documentation/` descreve a refatoracao desejada
- ainda existe espaco para concluir a implementacao concreta das estrategias

---

## Vantagens do Strategy neste projeto

- reduz a quantidade de condicionais no service
- facilita incluir novos tipos de doacao
- melhora a organizacao da regra de negocio
- permite testes unitarios por tipo de validacao
- deixa o codigo mais alinhado com principios de extensibilidade

---

## Conclusao

O padrao Strategy e uma boa escolha quando diferentes comportamentos precisam ser aplicados para a mesma operacao. No contexto deste projeto, ele se encaixa bem na validacao de doacoes por tipo, tornando o codigo mais modular, legivel e facil de evoluir.
