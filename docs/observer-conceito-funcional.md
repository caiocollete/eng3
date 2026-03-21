# Observer: conceito funcional

## O que ele resolve

O padrão **Observer** é usado quando uma ação principal acontece e outras ações precisam reagir a ela automaticamente, sem que a classe principal conheça todos esses comportamentos.

Em outras palavras:

- um objeto **dispara um evento**
- vários objetos **escutam esse evento**
- cada um reage do seu próprio jeito

Isso evita colocar muitas responsabilidades no mesmo lugar.

---

## Como pensar no Observer

Imagine a seguinte situação:

> "Uma doação foi registrada."

Depois disso, o sistema pode querer:

- salvar um log
- atualizar um contador
- enviar um email no futuro
- gerar alguma estatística

Sem Observer, o serviço de doação teria que fazer tudo isso diretamente.

Com Observer, o serviço só faz sua responsabilidade principal:

1. validar a doação
2. salvar a doação
3. avisar que a doação foi registrada

Depois desse aviso, os observadores executam o restante.

---

## Funcionamento na prática

O fluxo funcional é este:

1. O sistema registra uma doação.
2. Após salvar com sucesso, um evento é disparado.
3. Todos os objetos interessados nesse evento são notificados.
4. Cada objeto executa sua ação sem depender dos outros.

Ou seja, existe uma separação clara:

- quem **gera** o evento
- quem **reage** ao evento

---

## Papéis principais

### Subject

É quem avisa que algo aconteceu.

No contexto do projeto, pode ser o `DoacaoService` ou um publicador de eventos ligado a ele.

### Observer

É quem fica "observando" e reage quando recebe a notificação.

Exemplos:

- observer de log
- observer de contador
- observer de email

### Evento

É o objeto que representa o que aconteceu.

Exemplo:

- "doação registrada"

Esse evento leva os dados necessários para os observers trabalharem.

---

## Exemplo com doação

Pense assim:

- o `DoacaoService` salva a doação
- depois ele informa: "uma doação foi registrada"
- o `LogDoacaoObserver` recebe esse aviso e escreve no log
- o `ContadorDoacaoObserver` recebe o mesmo aviso e incrementa o contador

Nenhum observer precisa ficar chamando o outro.
E o serviço principal não precisa saber como cada reação funciona.

---

## Vantagem principal

A maior vantagem é o **desacoplamento**.

Isso significa que o sistema fica mais fácil de manter e crescer, porque novas reações podem ser adicionadas sem alterar a lógica principal.

Exemplo:

Se amanhã você quiser enviar email ao doador, basta criar um novo observer e registrá-lo. O fluxo principal da doação continua o mesmo.

---

## Resumo final

O Observer funciona como um sistema de aviso:

- algo importante acontece
- esse acontecimento é anunciado
- quem estiver interessado reage

No projeto, a ideia faz mais sentido no evento **"doação registrada"**, porque esse momento pode gerar várias ações secundárias sem deixar o `DoacaoService` sobrecarregado.
