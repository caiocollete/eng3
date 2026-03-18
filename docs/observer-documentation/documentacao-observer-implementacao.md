# Observer (Doação registrada) — Tudo que precisa ser implementado

Este documento lista **tudo** que deve ser criado ou alterado para implementar o padrão Observer no fluxo de doação registrada.

---

## 1. Pacote a criar

```
src/main/java/sosanimais/com/example/app/observer/doacao/
```

Todas as classes e a interface abaixo ficam nesse pacote (exceto as alterações em `DoacaoService` e no ponto de registro).

---

## 2. Arquivos novos (o que criar)

### 2.1. `DoacaoRegistradaEvent.java`

**Caminho:** `.../observer/doacao/DoacaoRegistradaEvent.java`

**Responsabilidade:** Objeto imutável que representa o evento “doação foi registrada”. Carrega a doação salva para os observers usarem.

**Implementar:**

| Item | Descrição |
|------|-----------|
| Atributo | `private final Doacao doacao` |
| Construtor | `public DoacaoRegistradaEvent(Doacao doacao)` — recebe e armazena a doação (pode fazer cópia ou referência, conforme política do projeto). |
| Getter | `public Doacao getDoacao()` |

**Import:** `sosanimais.com.example.app.model.entity.Doacao`

---

### 2.2. `DoacaoObserver.java` (interface)

**Caminho:** `.../observer/doacao/DoacaoObserver.java`

**Responsabilidade:** Contrato para quem quer ser notificado quando uma doação é registrada.

**Implementar:**

| Item | Descrição |
|------|-----------|
| Método | `void onDoacaoRegistrada(DoacaoRegistradaEvent event)` |

**Import:** referência a `DoacaoRegistradaEvent` (mesmo pacote).

---

### 2.3. `DoacaoEventPublisher.java`

**Caminho:** `.../observer/doacao/DoacaoEventPublisher.java`

**Responsabilidade:** Subject: mantém a lista de observers, permite registrar/remover e, ao publicar um evento, notifica todos.

**Implementar:**

| Item | Descrição |
|------|-----------|
| Atributo | `private final List<DoacaoObserver> observers` (ex.: `new ArrayList<>()`). Preferir estrutura thread-safe se houver acesso concorrente (ex.: `CopyOnWriteArrayList`). |
| Método | `public void registrar(DoacaoObserver observer)` — adiciona à lista (evitar duplicatas se fizer sentido). |
| Método | `public void remover(DoacaoObserver observer)` — remove da lista (opcional para primeira versão). |
| Método | `public void publicar(DoacaoRegistradaEvent event)` — percorre `observers` e chama `observer.onDoacaoRegistrada(event)` em cada um. Tratar exceção dentro do loop para um observer falho não impedir os demais (ex.: try/catch por observer e log). |

**Imports:** `DoacaoRegistradaEvent`, `DoacaoObserver`, `java.util.List`, `java.util.ArrayList` (ou `CopyOnWriteArrayList`).

**Observação:** Se o projeto usar Spring, essa classe pode ser um `@Component` para ser injetada no `DoacaoService` e no ponto de registro dos observers.

---

### 2.4. `LogDoacaoObserver.java`

**Caminho:** `.../observer/doacao/LogDoacaoObserver.java`

**Responsabilidade:** Observer que registra em log (ou `System.out`/`System.err`) que uma doação foi feita.

**Implementar:**

| Item | Descrição |
|------|-----------|
| Classe | Implementa `DoacaoObserver`. |
| Método | `public void onDoacaoRegistrada(DoacaoRegistradaEvent event)` — obter `event.getDoacao()` e logar, por exemplo: id (se existir), nome do doador, tipo, data. Usar `System.out.println(...)` ou um logger, conforme o padrão do projeto. |

**Imports:** `DoacaoRegistradaEvent`, `DoacaoObserver`.

---

### 2.5. `ContadorDoacaoObserver.java`

**Caminho:** `.../observer/doacao/ContadorDoacaoObserver.java`

**Responsabilidade:** Observer que mantém um contador em memória de doações registradas (para relatório/dashboard).

**Implementar:**

| Item | Descrição |
|------|-----------|
| Atributo | `private int totalRegistradas` (ou `AtomicInteger` se houver concorrência). Inicializado em 0. |
| Método | `public void onDoacaoRegistrada(DoacaoRegistradaEvent event)` — incrementar `totalRegistradas` (ex.: `totalRegistradas++` ou `totalRegistradas.incrementAndGet()`). |
| Método (opcional) | `public int getTotalRegistradas()` — retorna o contador (útil para testes ou endpoint de estatística). |

**Imports:** `DoacaoRegistradaEvent`, `DoacaoObserver`.

---

## 3. Alterações em classes existentes

### 3.1. `DoacaoService.java`

**Caminho:** `src/main/java/sosanimais/com/example/app/controller/service/DoacaoService.java`

**Alterações:**

| Onde | O que fazer |
|------|-------------|
| Atributos | Adicionar dependência do publisher: `private final DoacaoEventPublisher doacaoEventPublisher`. |
| Construtor | Receber `DoacaoEventPublisher` (parâmetro) e atribuir a `doacaoEventPublisher`. Se usar Spring: `@Autowired public DoacaoService(DoacaoDAL doacaoDAL, DoacaoEventPublisher doacaoEventPublisher)`. Se não usar injeção: instanciar `new DoacaoEventPublisher()` e, em algum ponto de configuração, registrar os observers (ver seção 4). |
| Método `registrarDoacao` | Após `Doacao doacaoSalva = doacaoDAL.save(doacao);` (ou equivalente), **se** `doacaoSalva != null` (e preferencialmente `doacaoSalva.getId() != null`): chamar `doacaoEventPublisher.publicar(new DoacaoRegistradaEvent(doacaoSalva));`. Não adicionar lógica de log, email ou contador dentro do service — apenas publicar o evento. |

**Imports a adicionar:**  
`DoacaoRegistradaEvent`, `DoacaoEventPublisher` (pacote `...observer.doacao`).

---

## 4. Registro dos observers (onde configurar)

É necessário **registrar** os observers no publisher em algum momento antes do primeiro uso (ex.: na subida da aplicação).

**Opção A – Sem Spring (manual):**

- Onde o `DoacaoService` é criado (ou onde o `DoacaoEventPublisher` é criado), após instanciar o publisher:
  - Criar instâncias de `LogDoacaoObserver` e `ContadorDoacaoObserver`.
  - Chamar `doacaoEventPublisher.registrar(logObserver)` e `doacaoEventPublisher.registrar(contadorObserver)`.

**Opção B – Com Spring:**

- Criar uma classe de configuração (ex.: `ObserverConfig` em `config` ou `observer`) anotada com `@Configuration`.
- Método `@Bean` que retorna `DoacaoEventPublisher`:
  - Instanciar o publisher.
  - Instanciar e registrar `LogDoacaoObserver` e `ContadorDoacaoObserver`.
  - Retornar o publisher.
- Garantir que `DoacaoService` receba esse bean no construtor (e que `DoacaoDAL` também seja fornecido, se for bean).

**Arquivo sugerido (se usar Spring):**  
`src/main/java/sosanimais/com/example/app/config/ObserverConfig.java` (ou nome equivalente).

**Conteúdo mínimo do método de configuração:**

- `DoacaoEventPublisher publisher = new DoacaoEventPublisher();`
- `publisher.registrar(new LogDoacaoObserver());`
- `publisher.registrar(new ContadorDoacaoObserver());`
- Retornar `publisher` (ou registrar no container conforme o padrão do projeto).

---

## 5. Resumo do que implementar (checklist)

| # | Item | Tipo |
|---|------|------|
| 1 | Pacote `...observer.doacao` | Criar pasta |
| 2 | `DoacaoRegistradaEvent` (atributo, construtor, getter) | Classe nova |
| 3 | `DoacaoObserver` (método `onDoacaoRegistrada`) | Interface nova |
| 4 | `DoacaoEventPublisher` (lista, `registrar`, `remover`, `publicar`) | Classe nova |
| 5 | `LogDoacaoObserver` (`onDoacaoRegistrada` com log) | Classe nova |
| 6 | `ContadorDoacaoObserver` (contador + `onDoacaoRegistrada`) | Classe nova |
| 7 | `DoacaoService`: campo publisher, construtor, chamar `publicar` após salvar | Alterar |
| 8 | Ponto de registro: registrar `LogDoacaoObserver` e `ContadorDoacaoObserver` no publisher (config ou onde o service/publisher é criado) | Configuração |

---

## 6. Ordem sugerida de implementação

1. Criar o pacote `observer.doacao`.
2. Criar `DoacaoRegistradaEvent`.
3. Criar a interface `DoacaoObserver`.
4. Criar `DoacaoEventPublisher`.
5. Criar `LogDoacaoObserver` e `ContadorDoacaoObserver`.
6. Alterar `DoacaoService` (campo, construtor, chamada a `publicar`).
7. Implementar o registro dos observers (config ou código de startup).
8. Testar: registrar uma doação via API e verificar no console/log a mensagem do `LogDoacaoObserver` e, se expuser o contador, que `ContadorDoacaoObserver` incrementou.

---

## 7. Comportamento esperado após implementar

- Ao chamar `POST /apis/doacoes` com uma doação válida e o backend salvar com sucesso:
  - O `DoacaoService` chama `doacaoEventPublisher.publicar(new DoacaoRegistradaEvent(doacaoSalva))`.
  - O publisher chama `onDoacaoRegistrada` em cada observer.
  - `LogDoacaoObserver` escreve no log (ou console) os dados da doação.
  - `ContadorDoacaoObserver` incrementa o total de doações registradas.
- Nenhuma lógica de log ou contador fica dentro do `DoacaoService`; ele apenas publica o evento.

---

## 8. Possíveis extensões (não obrigatórias para esta entrega)

- **Remover observer:** implementar e usar `DoacaoEventPublisher.remover(observer)` se precisar desinscrever em runtime.
- **Exceções:** em `publicar`, decidir se falha em um observer propaga ou só é logada (recomendado: só logar e continuar nos demais).
- **Thread-safety:** se houver acesso concorrente à lista de observers, usar `CopyOnWriteArrayList` ou sincronização no publisher.
- **Novo observer:** ex. `EmailDoadorObserver` que envia email ao doador; basta implementar `DoacaoObserver` e registrar no publisher, sem alterar `DoacaoService`.
