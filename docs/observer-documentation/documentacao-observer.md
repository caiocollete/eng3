# Onde implementar Observer no projeto

## Melhor opção: **Eventos de Doação (Doação registrada)**

Quando uma **doação é registrada**, vários comportamentos podem ser acionados sem o `DoacaoService` conhecer cada um:

- Registrar em log / auditoria  
- Atualizar contador ou relatório de doações  
- (Futuro) Enviar email de agradecimento ao doador  
- (Futuro) Notificar setor de estoque se for doação de produto  

Hoje o service só valida e persiste; qualquer nova reação exigiria alterar o mesmo método. Com Observer, o service apenas **dispara um evento** e **observers** reagem, mantendo o service simples e fácil de estender.

---

## Por que Doação e não outro fluxo?

| Opção | Prós | Contras |
|-------|------|--------|
| **Doação registrada** | Um único ponto de disparo (`registrarDoacao`); domínio claro; vários observers possíveis (log, relatório, email); não interfere em fluxo crítico existente. | Nenhum relevante. |
| Animal transferido / cadastrado | Desacopla atualização de baia do `AnimalService`. | Fluxo já complexo; mais risco de regressão; vários pontos de disparo (salvar, atualizar, transferir). |
| Conexão DB | Possível notificar reconexão. | Menos natural para Observer; poucos “interessados” no evento. |

**Conclusão:** Observer em **evento de doação registrada** é a opção mais segura, didática e útil para o projeto.

---

## Estrutura sugerida

### Pacote

`src/main/java/sosanimais/com/example/app/observer/doacao`

### Papéis

1. **Subject (quem notifica)**  
   - Pode ser o próprio `DoacaoService` (ele mantém lista de observers e chama `notificar()` após salvar).  
   - Ou uma classe dedicada, ex.: `DoacaoEventPublisher`, injetada no service, que mantém os observers e dispara o evento.

2. **Evento**  
   - Objeto que carrega os dados do evento, ex.: `DoacaoRegistradaEvent(Doacao doacao)`.

3. **Observer (interface)**  
   - Ex.: `DoacaoObserver` com método `void onDoacaoRegistrada(DoacaoRegistradaEvent event)`.

4. **Observers concretos (exemplos)**  
   - `LogDoacaoObserver` – registra em log/console que uma doação foi feita (nome doador, tipo, data).  
   - `ContadorDoacaoObserver` – mantém um contador em memória (ou chama serviço de relatório) quando uma doação é registrada.  
   - (Futuro) `EmailDoadorObserver` – envia email de agradecimento.

### Fluxo

1. Controller chama `DoacaoService.registrarDoacao(doacao)`.  
2. Service valida e persiste (como hoje).  
3. Se salvou com sucesso, service dispara o evento “doação registrada” (ex.: chama `publisher.publicar(new DoacaoRegistradaEvent(doacaoSalva))`).  
4. O subject/publisher percorre os observers e chama `onDoacaoRegistrada(event)` em cada um.  
5. Cada observer executa sua lógica (log, contador, email, etc.) sem o service precisar conhecê-la.

---

## O que cada classe faz (resumo)

| Classe / Interface | Papel |
|--------------------|--------|
| `DoacaoRegistradaEvent` | Objeto do evento: guarda a `Doacao` registrada (e dados úteis para os observers). |
| `DoacaoObserver` | Contrato: método para ser notificado quando uma doação é registrada. |
| `DoacaoEventPublisher` (ou o próprio service como subject) | Mantém lista de `DoacaoObserver`; ao publicar evento, notifica todos. |
| `LogDoacaoObserver` | Implementa `DoacaoObserver`; em `onDoacaoRegistrada` faz log da doação. |
| `ContadorDoacaoObserver` | Implementa `DoacaoObserver`; em `onDoacaoRegistrada` atualiza contador/estatística. |
| `DoacaoService` | Continua validando e salvando; após salvar, chama o publisher para publicar o evento (não chama observers diretamente, se usar publisher). |

---

## Integração mínima no `DoacaoService`

- No construtor (ou por injeção), obter o `DoacaoEventPublisher`.  
- No final de `registrarDoacao`, após `doacaoDAL.save(doacao)` retornar com sucesso e a doação com ID preenchido:
  - Chamar `publisher.publicar(new DoacaoRegistradaEvent(doacaoSalva))`.
- Nenhuma lógica de log, email ou contador dentro do service; só disparar o evento.

---

## Alternativa: Observer em eventos de Animal

Se no futuro quiser usar Observer também no fluxo de **animal** (ex.: “animal transferido de baia”, “animal adotado”):

- **Subject/Publisher:** um `AnimalEventPublisher` (ou o `AnimalService` como subject).  
- **Eventos:** ex. `AnimalTransferidoEvent`, `AnimalAdotadoEvent`.  
- **Observers:** um para “atualizar ocupação da baia” (lógica hoje dentro do `AnimalService`), outro para log, etc.

Isso desacoplaria a atualização de baias do `AnimalService`, mas é um refactor maior. Recomendação: implementar primeiro Observer em **doação**; depois, se quiser, replicar o padrão em animal.

---

## Checklist de implementação (Doação)

- [ ] Criar `DoacaoRegistradaEvent`
- [ ] Criar interface `DoacaoObserver`
- [ ] Criar classe `DoacaoEventPublisher` (registrar/remover observers, método `publicar(event)`)
- [ ] Criar `LogDoacaoObserver` e `ContadorDoacaoObserver`
- [ ] No `DoacaoService`, após salvar com sucesso, publicar o evento
- [ ] Registrar os observers no publisher (no startup da aplicação ou onde fizer sentido)
- [ ] Testar: registrar doação e verificar log e contador sendo atualizados
