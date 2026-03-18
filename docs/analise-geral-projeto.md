# Análise geral do projeto

## Visão funcional

O projeto resolve, funcionalmente, a **gestão operacional de uma ONG/abrigo de animais**.

Pelo código, pelas entidades e pelo script do banco, o sistema cobre principalmente:

- cadastro e manutenção de **animais**
- controle de **acolhimento** e **adoção**
- controle de **baias** e movimentação de animais
- cadastro de **pessoas**, **doadores**, **adotantes** e **funcionários**
- registro de **doações**
- controle de **insumos**, **produtos**, **serviços** e **armazenamento**
- registro de **transferências**
- telas de apoio para operação administrativa no navegador

Em termos práticos, é um sistema para apoiar o dia a dia da instituição: organizar animais, pessoas, recursos e movimentações internas.

---

## Arquitetura identificada

### Tipo de aplicação

A aplicação é um **monólito web em Java com Spring Boot**.

Ela possui:

- backend com APIs REST
- acesso a banco PostgreSQL
- interface web feita com HTML, CSS e JavaScript puros

### Estrutura em camadas

A organização principal segue uma arquitetura em camadas:

- `controller`: recebe requisições HTTP
- `controller/service`: concentra parte da regra de negócio
- `model/DAL`: acesso a dados
- `model/entity`: entidades de domínio
- `model/db`: infraestrutura de conexão com banco
- `view`: páginas e scripts do front-end

Isso mostra uma arquitetura inspirada em **MVC + camadas de serviço e persistência**.

### Leitura arquitetural real

Embora a estrutura indique separação por camadas, o projeto não é totalmente uniforme.

Na prática, ele é uma arquitetura **híbrida**, porque:

- alguns controllers usam `Service`
- outros controllers instanciam entidades ou DALs diretamente
- algumas entidades possuem métodos de persistência dentro delas
- parte do acesso ao banco segue uma ideia de repositório, mas sem Spring Data/JPA

Ou seja: a intenção principal é de arquitetura em camadas, mas a implementação mistura isso com características de **Active Record** em alguns pontos.

---

## Padrões identificados

### 1. MVC

O projeto segue a lógica de separação entre:

- entrada HTTP nos controllers
- regras no service
- dados em entidades e DAL
- interface nas páginas da pasta `view`

Não é um MVC clássico puro, mas a ideia está presente.

### 2. Service Layer

Existe uma camada de serviço para centralizar regras de negócio, como em:

- `AnimalService`
- `DoacaoService`
- `PessoaService`

Esse padrão ajuda a não colocar toda a lógica direto nos controllers.

### 3. Repository / DAL

O projeto usa uma camada própria de acesso a dados chamada `DAL`.

Ela funciona como um repositório manual:

- busca
- salva
- atualiza
- remove

Isso aparece em classes como:

- `AnimalDAL`
- `PessoaDAL`
- `DoacaoDAL`

### 4. Singleton

O padrão **Singleton** está claramente presente em `SingletonDB`, que mantém uma única referência para a conexão principal.

Ele é usado para compartilhar a infraestrutura de acesso ao banco entre os DALs.

No projeto, esse é um dos padrões mais evidentes e efetivamente implementados.

### 5. Facade

O padrão **Facade** não foi identificado de forma clara no projeto.

Ou seja, não existe hoje uma classe central que simplifique o acesso a vários subsistemas ao mesmo tempo, como seria esperado em uma fachada.

O que existe são:

- controllers expondo endpoints
- services executando regras de negócio
- DALs acessando o banco

Mas isso não caracteriza uma `Facade` propriamente dita.

### 6. Strategy

O projeto já possui uma pasta de `Strategy` para validação de doações, mas os arquivos ainda estão vazios.

Então a leitura correta é:

- o padrão foi **planejado**
- a implementação ainda **não está concluída**

### 7. Active Record parcial

Algumas entidades possuem métodos que acessam o repositório diretamente.

Isso aproxima essas classes de um estilo **Active Record**, por exemplo quando a própria entidade oferece operações de cadastro, busca, atualização ou exclusão.

Por isso o sistema hoje não está em um único padrão puro, mas sim em uma composição de estilos.

---

## Estilo de código

## Linguagem e convenções

O código é escrito em **Java 17**, com nomes majoritariamente em **português**, o que deixa o domínio do projeto mais próximo da regra de negócio.

Exemplos:

- `Acolhimento`
- `Doacao`
- `Baias`
- `Funcionario`
- `Transferencia`

### Características visíveis do estilo

- uso forte de classes simples com getters e setters explícitos
- baixa abstração em boa parte dos fluxos
- regras escritas de forma direta e procedural
- SQL montado manualmente em várias classes
- tratamento de erro muitas vezes feito com `null`, `boolean` e mensagens simples
- comentários informais em alguns arquivos, mostrando evolução incremental do código

### Estilo de backend

O backend tem um estilo bastante prático:

- controller responde com `ResponseEntity`
- service orquestra validações e chamadas
- DAL executa SQL diretamente

É um estilo funcional para aprendizado e evolução rápida, mas com algumas inconsistências de padronização entre módulos.

### Estilo de frontend

O front-end segue um estilo **tradicional com HTML/CSS/JS puro**, sem framework como Angular, React ou Vue.

As páginas fazem chamadas `fetch` para os endpoints REST e parecem ter foco em operação interna.

Isso sugere um sistema com interface administrativa simples, orientada a telas e formulários.

---

## Banco e persistência

O projeto usa **PostgreSQL**, mas não usa JPA/Hibernate.

O acesso é feito com:

- `java.sql.Connection`
- `Statement`
- `PreparedStatement`
- `ResultSet`

Ou seja, a persistência é **JDBC manual**.

### O que isso mostra

Essa escolha dá mais controle direto sobre SQL, mas também traz mais responsabilidade para o código:

- montar consultas
- mapear resultados manualmente
- cuidar de segurança e manutenção das queries

Há dois estilos de persistência convivendo:

- DALs mais antigos usando concatenação de SQL
- DALs mais recentes, como `DoacaoDAL` e `ProdutoDAL`, usando `PreparedStatement`

Isso mostra uma evolução do código ao longo do tempo.

---

## Organização do projeto

Pelo mapeamento da pasta `src/main/java`, o projeto tem aproximadamente:

- 16 controllers
- 15 services
- 15 classes DAL
- 15 entidades
- 67 arquivos de view

Isso indica um sistema já relativamente amplo para fins acadêmicos ou de gestão interna.

---

## O que a arquitetura comunica sobre o projeto

A arquitetura mostra que o projeto foi pensado para:

- crescer por módulos
- separar responsabilidades principais
- expor funcionalidades por API
- manter uma interface web própria

Ao mesmo tempo, ela também comunica que o sistema ainda está em consolidação de padrão, porque existem diferenças entre os módulos:

- alguns seguem melhor a separação controller -> service -> DAL
- outros ainda concentram operações na própria entidade ou no controller

Então o projeto tem uma base arquitetural clara, mas ainda em processo de uniformização.

---

## Pontos fortes percebidos

- domínio funcional bem definido
- boa cobertura de processos importantes da ONG
- separação estrutural visível por camadas
- uso de Spring Boot para expor APIs
- persistência própria que deixa claro o que está sendo feito no banco
- front-end já integrado ao backend por chamadas HTTP

---

## Pontos de atenção arquitetural

- mistura de estilos arquiteturais em módulos diferentes
- dependência frequente de instanciação manual com `new`
- uso parcial do Spring como container de dependências
- entidades com responsabilidades além do domínio
- SQL manual em vários pontos, aumentando custo de manutenção
- pasta `view` dentro de `src/main/java`, o que é funcional, mas foge do padrão mais comum
- cobertura de testes ainda mínima

Esses pontos não impedem o sistema de funcionar, mas mostram onde ele pode evoluir para ficar mais consistente.

---

## Conclusão

O projeto é, funcionalmente, um **sistema de gestão para abrigo/ONG de animais**, cobrindo cadastro, acolhimento, adoção, doações, estoque, pessoas e movimentações internas.

Arquiteturalmente, ele é melhor definido como um **monólito Spring Boot com arquitetura em camadas, persistência JDBC manual e traços de Active Record em alguns módulos**.

Os padrões mais claros são:

- MVC
- Service Layer
- Repository/DAL
- Singleton
- Facade não identificada no código atual
- Strategy planejado, mas ainda não efetivado

No estilo de código, o projeto é direto, didático e orientado à regra de negócio, com foco em funcionamento prático. A principal característica técnica dele hoje é a mistura entre uma boa intenção de separação por camadas e uma implementação ainda heterogênea entre os módulos.
