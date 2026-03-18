# Padrão de Projeto: Observer

> **Classificação GoF:** Comportamental (Behavioral) — Escopo de Objeto

---

## 1. Descrição do Problema

O padrão **Observer** define uma dependência **um-para-muitos** entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

Permite que objetos interessados sejam avisados sobre mudanças de estado ou outros eventos ocorrendo em outro objeto, sem criar acoplamento forte entre eles.

---

## 2. Aplicabilidade

Use o padrão Observer quando:

- Uma mudança em um objeto requer mudanças em outros, e você não sabe quantos objetos precisam mudar.
- Um objeto deve ser capaz de notificar outros sem fazer suposições sobre quem são esses objetos.
- É necessário evitar acoplamento forte entre os objetos envolvidos.

---

## 3. Estrutura

```
<<interface>>                         <<interface>>
   Subject          1    Possui  1..*    Observer
─────────────────────────────────────────────────
+ registerObserver() : void         + update() : void
+ removeObserver()   : void
+ notifyObserver()   : void
       △                                    △
       │                                    │
<<concrete>>                          <<concrete>>
SubjectConcrete                      ObserverConcrete
──────────────────                   ─────────────────
+ registerObserver() : void          + update() : void
+ removeObserver()   : void
+ notifyObserver()   : void
+ getState()         : void
+ setState()         : void
```

### Participantes

| Componente | Papel |
|---|---|
| `Subject` *(interface)* | Define o contrato para registro, remoção e notificação de observadores. Mantém uma lista dos observadores registrados. |
| `SubjectConcrete` *(classe)* | Implementa a interface `Subject`. Define e apresenta seu estado. O principal método a ser implementado é o `notify`, usado para atualizar os observadores. |
| `Observer` *(interface)* | Possui apenas o método `update()`, chamado quando o estado do sujeito se altera. O comportamento deste método é implementado pelos observadores concretos. |
| `ObserverConcrete` *(classe)* | Implementa a interface `Observer` e registra sua relação com um ou mais sujeitos concretos para receber atualizações dos mesmos. |

---

## 4. Colaborações

- Os **sujeitos concretos** notificam seus observadores sempre que ocorre uma mudança em seu estado, evitando inconsistências.
- Os **observadores** podem consultar as informações e realizar mudanças em seu comportamento e/ou estado em resposta.

### Fluxo de execução

```
SubjectConcrete          ObserverConcrete A      ObserverConcrete B
      │                          │                       │
      │── setState() ──────────► │                       │
      │                          │                       │
      │── notifyObserver() ────► │                       │
      │                          │── update() ──────►    │
      │── notifyObserver() ────────────────────────────► │
      │                          │                  update()
```

---

## 5. Métodos-Chave

| Método | Descrição |
|---|---|
| `register()` | Permite que um observador se registre para receber atualizações de estado de um sujeito. |
| `remove()` | Permite que um observador se desregistre, interrompendo o recebimento de notificações. |
| `notify()` | Chamado pelo sujeito para notificar todos os observadores registrados em sua lista. |
| `update()` | Implementado pelo observador; executado quando é avisado pelo sujeito para realizar as mudanças necessárias. |

---

## 6. Consequências

### Vantagens

- **Baixo acoplamento:** Observer e Subject podem ser reutilizados independentemente.
- **Suporte a comunicações do tipo broadcast:** um único evento notifica todos os observadores registrados de forma automática.
- **Aberto para extensão:** novos observadores podem ser adicionados sem modificar o sujeito.

### Desvantagens

- **Atualizações inesperadas:** como os observadores não sabem uns dos outros, uma simples mudança no sujeito pode desencadear uma cascata de atualizações custosas.
- **Ordem de notificação não garantida:** a ordem em que os observadores são notificados pode variar.

---

## 7. Exemplo Conceitual (Java)

```java
// Interface Subject
public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserver();
}

// Interface Observer
public interface Observer {
    void update();
}

// Sujeito Concreto
public class SubjectConcrete implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String state;

    public void setState(String state) {
        this.state = state;
        notifyObserver();
    }

    public String getState() {
        return state;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObserver() {
        for (Observer o : observers) {
            o.update();
        }
    }
}

// Observador Concreto
public class ObserverConcrete implements Observer {
    private SubjectConcrete subject;

    public ObserverConcrete(SubjectConcrete subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    @Override
    public void update() {
        System.out.println("Estado atualizado: " + subject.getState());
    }
}
```

---

## 8. Padrões Relacionados

| Padrão | Relação |
|---|---|
| **Mediator** | Pode ser usado em conjunto para reduzir dependências entre objetos que se observam mutuamente. |
| **Singleton** | O sujeito frequentemente é um Singleton para garantir um ponto único de notificação. |
| **Command** | Observers podem encapsular a lógica de atualização como comandos. |

---

## 9. Contexto no Catálogo GoF

O Observer é um padrão **Comportamental de Objeto**, conforme classificação do GoF (*Gang of Four*):

| Escopo \ Propósito | Criacional | Estrutural | **Comportamental** |
|---|---|---|---|
| **Classe** | Factory Method | Adapter (class) | Interpreter, Template Method |
| **Objeto** | Abstract Factory, Builder, Prototype, Singleton | Adapter, Bridge, Composite, Decorator, Façade, Flyweight, Proxy | Chain of Responsibility, Command, Iterator, Mediator, Memento, **Observer**, State, Strategy, Visitor |

--- 