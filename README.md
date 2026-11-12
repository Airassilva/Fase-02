# 📚 Biblioteca Online

Sistema de biblioteca online desenvolvido como **Tech Challenge** da pós-graduação em **Arquitetura e Desenvolvimento Java (FIAP)**.

O sistema permite cadastrar livros, gerir usuários, registrar empréstimos e devoluções, consultar o catálogo e gerar relatórios de uso da biblioteca.

---

## Sumário

- [Visão geral da arquitetura](#visão-geral-da-arquitetura)
- [Tecnologias](#tecnologias)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Como executar](#como-executar)
- [Documentação da API](#documentação-da-api)
- [Principais endpoints](#principais-endpoints)
- [Eventos (Kafka)](#eventos-kafka)
- [Decisões de design](#decisões-de-design)
- [Data e hora](#data-e-hora)
- [Otimização de acesso a dados](#otimização-de-acesso-a-dados)
- [Docker e escalabilidade](#docker-e-escalabilidade)
- [Testes](#testes)
- [Autoria](#autoria)

---

## Visão geral da arquitetura

O workspace contém dois serviços independentes, cada um com seu próprio PostgreSQL. O `biblioteca-nucleo` é transacional e publica eventos em Kafka por meio de uma transactional outbox. O `biblioteca-relatorio` consome esses eventos e mantém um modelo de leitura para consultas e snapshots de relatórios.

Fluxo de dados:

```
┌──────────────────────────┐    evento     ┌───────┐   consome   ┌──────────────────────────┐
│     biblioteca-nucleo    │ ────────────► │ Kafka │ ──────────► │    biblioteca-relatorio  │
│  livros · usuários ·     │   (outbox)    └───────┘             │  consumer · schedule ·   │
│  empréstimos             │                                     │  endpoints de relatório  │
└────────────┬─────────────┘                                     └────────────┬─────────────┘
             │                                                                │
     PostgreSQL (núcleo)                                           PostgreSQL (relatório)
```

### `biblioteca-nucleo`

Núcleo transacional. Contém o CRUD de livros e usuários, o registro de empréstimos, devoluções e reservas, e a consulta do catálogo com filtros e paginação. Toda regra de negócio crítica (estoque, prazos, limites) é executada aqui dentro de uma única transação.

Organizado em **arquitetura hexagonal (ports and adapters)**, com módulos por área de negócio:

```
dev.aira.biblioteca.nucleo
├── livro/        domain · application (ports in/out, services) · adapter (web, persistence)
├── usuario/      domain · application · adapter
├── emprestimo/   domain · application · adapter
└── shared/       config · exception · outbox
```

Regra de dependência: `adapter → application → domain`. O domínio não depende de Spring nem de JPA, e os módulos conversam entre si apenas por portas.

### `biblioteca-relatorio`

Serviço somente de leitura do ponto de vista do negócio. O consumidor Kafka aplica eventos de empréstimo e devolução ao seu próprio modelo de leitura; a API consulta esse banco sem acessar o banco do núcleo nem chamá-lo por HTTP. O processamento deduplica eventos pelo `eventId` e tolera reentregas e eventos fora de ordem. Falhas persistentes são encaminhadas para `biblioteca.emprestimos.DLT`.

Organizado em arquitetura hexagonal, com domínio Java puro e adapters separados para REST, Kafka, agendamento e persistência.

---

## Tecnologias

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Persistência | Spring Data JPA, Hibernate, PostgreSQL |
| Migrações | Flyway |
| Mensageria | Apache Kafka (Spring Kafka) |
| Documentação | SpringDoc OpenAPI (Swagger UI) e Javadoc |
| Mapeamento | MapStruct no núcleo; mapeamento manual nos relatórios |
| Observabilidade | Spring Boot Actuator |
| Testes | JUnit 5, Mockito, Testcontainers |
| Contêineres | Docker e Docker Compose |
| Build | Maven |

---

## Estrutura do repositório

```
Fase-02/
├── README.md
└── tech-challenge-biblioteca/
    ├── docker-compose.yml
    ├── nginx/                 gateway para o núcleo
    ├── biblioteca-nucleo/     serviço transacional
    └── biblioteca-relatorio/  serviço de consultas e snapshots
```

---

## Como executar

### Pré-requisitos

- Docker e Docker Compose
- Para rodar fora do Docker: JDK 21; o Maven Wrapper do projeto está incluído

### Subindo os serviços

Para iniciar os dois serviços, bancos, Kafka e gateway:

```bash
cd tech-challenge-biblioteca
docker compose up --build api-nucleo api-relatorio api-gateway
```

O Compose inicia o broker e cria os tópicos `biblioteca.emprestimos` e `biblioteca.emprestimos.DLT`. A API do núcleo fica atrás do gateway; a API de relatórios é publicada diretamente. Para subir somente o núcleo e sua infraestrutura:

```powershell
cd tech-challenge-biblioteca
docker compose up --build api-gateway
```

Para executar o serviço de relatórios fora do Docker, inicie primeiro o banco de relatórios, Kafka e núcleo:

```powershell
cd tech-challenge-biblioteca
docker compose up -d postgres-relatorio kafka-init api-nucleo
cd biblioteca-relatorio
.\mvnw.cmd spring-boot:run
```

Serviços e portas:

| Serviço | URL |
|---|---|
| API Núcleo (gateway) | http://localhost:8081 |
| API Relatórios | http://localhost:8082 |
| Kafka externo | `localhost:29092` |
| Swagger Núcleo | http://localhost:8081/swagger-ui/index.html |
| Swagger Relatórios | http://localhost:8082/swagger-ui.html |
| Health Núcleo | http://localhost:8081/actuator/health |
| Health Relatórios | http://localhost:8082/actuator/health |
| Kafka UI | http://localhost:8090 |
| Mailpit (caixa de e-mails de teste) | http://localhost:8025 |

Para escalar horizontalmente o núcleo, use `docker compose up --build --scale api-nucleo=3 api-gateway`. As instâncias do núcleo não publicam portas individuais; o Nginx encaminha o tráfego para elas.

O Mailpit recebe os avisos de atraso sem conectar a um SMTP externo. Abra http://localhost:8025 para consultar os e-mails. Para testar o job sem esperar pelo horário diário, defina `APP_EMPRESTIMOS_ATRASO_CRON="0 * * * * *"` antes de iniciar o Compose; assim ele roda a cada minuto.

Para parar a infraestrutura (preservando os dados nos volumes):

```bash
docker compose down
```

Use `docker compose down -v` somente se também quiser apagar os dados persistidos.

### Variáveis de ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL do banco; use o nome do serviço no Compose ou `localhost` ao executar a aplicação localmente | `jdbc:postgresql://localhost:5432/biblioteca_nucleo` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `biblioteca` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `biblioteca` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Endereço do Kafka; para a aplicação local, use a porta externa publicada pelo Compose | `localhost:29092` |
| `APP_KAFKA_TOPICS_EMPRESTIMOS` | Tópico de eventos de empréstimos (produtor do núcleo e consumidor de relatórios) | `biblioteca.emprestimos` |
| `APP_EMPRESTIMOS_ATRASO_CRON` | Cron do job de notificação (UTC; padrão diário às 09:00) | `0 0 9 * * *` |
| `APP_RELATORIO_CRON` | Cron dos snapshots no serviço de relatórios (padrão a cada 5 minutos) | `0 */5 * * * *` |
| `SPRING_MAIL_HOST` / `SPRING_MAIL_PORT` | SMTP para envio de notificações | `mailpit` / `1025` no Compose |
| `HIBERNATE_JDBC_BATCH_SIZE` | Quantidade máxima de instruções compatíveis agrupadas pelo Hibernate | `50` |

O serviço de relatórios usa por padrão `jdbc:postgresql://localhost:5433/biblioteca_relatorio`; no Compose, aponta para `postgres-relatorio`. As credenciais do PostgreSQL são compartilhadas por padrão entre os dois bancos e podem ser alteradas com `POSTGRES_USER` e `POSTGRES_PASSWORD`.

---

## Documentação da API

- **Swagger UI** em cada serviço, gerado pelo SpringDoc a partir dos controllers e DTOs.
- **Especificação OpenAPI (JSON)** em `/v3/api-docs`.

```bash
./mvnw javadoc:javadoc
```

---

## Principais endpoints

### API Núcleo

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/biblioteca/livros` | Cadastra um livro |
| `GET` | `/api/biblioteca/livros` | Busca livros com filtros `nome`, `autor` e `isbn`, além de paginação |
| `GET` | `/api/biblioteca/livros/{id}` | Consulta um livro |
| `PUT` | `/api/biblioteca/livros/{id}` | Atualiza quantidade total e descrição |
| `DELETE` | `/api/biblioteca/livros/{id}` | Exclui um livro |
| `POST` | `/api/biblioteca/usuarios` | Cadastra um usuário |
| `GET` | `/api/biblioteca/usuarios` | Lista usuários com filtros `nome`, `email`, `tipo` e `ativo`, além de paginação |
| `GET` | `/api/biblioteca/usuarios/{id}` | Consulta um usuário |
| `PUT` | `/api/biblioteca/usuarios/{id}` | Atualiza nome e e-mail |
| `DELETE` | `/api/biblioteca/usuarios/{id}` | Desativa um usuário |
| `POST` | `/api/biblioteca/emprestimo` | Registra empréstimo se houver exemplar; caso contrário, cria reserva |
| `POST` | `/api/biblioteca/reserva` | Solicita empréstimo se houver exemplar; caso contrário, cria reserva |
| `PUT` | `/api/biblioteca/emprestimo/{emprestimoId}/devolver` | Registra devolução do empréstimo |

> A lista completa e os contratos de request/response estão no Swagger UI.

### API Relatórios

Base: `http://localhost:8082/api/v1/relatorios`.

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/livros-mais-emprestados` | Top 20 geral, ordenado pelo total de empréstimos e título |
| `GET` | `/emprestimos-ativos?page=0&size=20` | Empréstimos em aberto, com previsão de devolução e paginação |
| `GET` | `/semanal` | Relatório da semana vigente |
| `GET` | `/semanal?referencia=2026-11-12` | Semana que contém a data de referência |

A paginação de empréstimos aceita `page` a partir de zero e `size` entre 1 e 100. O relatório semanal considera semanas de segunda a domingo no fuso `America/Recife`, sempre inclui os sete dias e apresenta zero nos dias sem empréstimos. Inclui totais de empréstimos, devoluções, empréstimos em aberto e atrasados, além do ranking semanal. Respostas de erro de validação e conversão de parâmetros usam `ProblemDetail` (HTTP 400). `usuarioId` não é exposto nas respostas.

Como o relatório é atualizado assincronamente a partir do Kafka, os totais refletem os eventos já consumidos (consistência eventual).

### Exemplo: solicitar empréstimo

```bash
curl -X POST http://localhost:8081/api/biblioteca/emprestimo \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": "7f1c2d3e-8a9b-4c5d-9e0f-1a2b3c4d5e6f", "livroId": "1a2b3c4d-5e6f-4789-8a9b-0c1d2e3f4a5b"}'
```

---

## Eventos (Kafka)

O núcleo grava eventos na tabela `outbox_event` na mesma transação que altera o empréstimo e o estoque. O relay busca os eventos pendentes e os publica no tópico `biblioteca.emprestimos`, usando o ID do empréstimo como chave. O tópico pode ser configurado por `APP_KAFKA_TOPICS_EMPRESTIMOS`.

```json
{
  "eventId": "7f1c2d3e-8a9b-4c5d-9e0f-1a2b3c4d5e6f",
  "tipo": "EMPRESTIMO_REALIZADO",
  "ocorridoEm": "2026-10-17T14:30:00Z",
  "emprestimoId": "123e4567-e89b-12d3-a456-426614174000",
  "livroId": "1a2b3c4d-5e6f-4789-8a9b-0c1d2e3f4a5b",
  "livroTitulo": "Clean Architecture",
  "livroAutor": "Robert C. Martin",
  "livroIsbn": "9780134494166",
  "usuarioId": "7f1c2d3e-8a9b-4c5d-9e0f-1a2b3c4d5e6f",
  "dataEmprestimo": "2026-10-17",
  "dataPrevistaDevolucao": "2026-10-31",
  "dataDevolucao": null
}
```

Tipos emitidos: `EMPRESTIMO_REALIZADO` e `LIVRO_DEVOLVIDO`. Ambos incluem os dados completos do empréstimo; a criação de reserva ainda não gera evento. O consumidor do serviço de relatórios usa o `eventId` para ignorar duplicatas e conserva os dados de devolução mesmo se o evento de empréstimo chegar depois. Mensagens malformadas ou inválidas não são retentadas; falhas retentáveis recebem até três novas tentativas com intervalo de um segundo antes de serem encaminhadas ao tópico `.DLT`.

---

## Decisões de design

- **Separação em serviços.** Usuários, livros e empréstimos compartilham regras transacionais no núcleo; o relatório é um serviço independente, baseado em eventos e com modelo de leitura próprio.
- **Banco por serviço.** O núcleo e os relatórios usam bancos PostgreSQL separados. O serviço de relatórios obtém seus dados exclusivamente dos eventos Kafka.
- **Transactional Outbox.** O empréstimo/devolução e seu evento são gravados na mesma transação do núcleo; se o Kafka estiver indisponível, o evento permanece pendente para nova tentativa.
- **Entrega pelo menos uma vez.** Se o Kafka confirmar a publicação e o processo falhar antes de marcar o evento como publicado no banco, o relay pode publicar o mesmo evento novamente. Consumidores devem deduplicar usando `eventId`.
- **Retenção da outbox.** O relay tenta publicar lotes de até 20 eventos a cada 5 segundos por padrão e remove eventos publicados há mais de 7 dias diariamente. Os intervalos podem ser ajustados por `app.outbox.intervalo-ms` e `app.outbox.limpeza-intervalo-ms`.
- **Falhas persistentes no Kafka.** O relay registra a falha e tenta novamente nas execuções seguintes; o núcleo não configura uma Dead Letter Topic para a publicação da outbox.
- **Padrões utilizados:** Ports and Adapters, Repository, Strategy (política de prazo de devolução), Specification (filtros dinâmicos) e DTO/Mapper.
- **Consistência eventual.** Os endpoints de relatório refletem os eventos do núcleo após o consumo assíncrono.
- **Snapshots protegidos.** O agendador grava snapshots da semana vigente e da anterior, com upsert por tipo e início do período. ShedLock usa a tabela PostgreSQL `shedlock`, o horário do banco e o lock `gerarSnapshotsRelatorio` para evitar execução simultânea entre réplicas; o lock expira após no máximo 10 minutos.

---

## Data e hora

- Instantes (como `ocorridoEm` dos eventos) são serializados em **UTC**, no formato ISO-8601 com `Z` (por exemplo, `2026-10-17T14:30:00Z`).
- Datas de empréstimo, previsão e devolução são datas civis (`LocalDate`), serializadas em ISO-8601 como `yyyy-MM-dd`, sem horário nem offset.
- O dia civil é determinado pelo fuso `America/Recife`; o cálculo usa a API `java.time` e um `Clock` injetado para manter as regras testáveis.
- O período semanal vai de segunda a domingo nesse fuso. Para a semana vigente, dias futuros aparecem com total zero; empréstimos atrasados são os que estão em aberto e têm previsão anterior à data atual local.
- Os contêineres usam `TZ=UTC` para processos e logs; isso não altera o fuso de negócio explícito do `Clock`.

---

## Otimização de acesso a dados

- **Paginação** nas listagens de livros e usuários.
- **Tamanho de página limitado** a 100 registros (padrão 20) para controlar volume de dados e memória.
- **Índices** em ISBN, título, autor e chaves usadas em empréstimos e reservas. A migração V7 adiciona índices GIN com `pg_trgm` nos campos usados em buscas parciais, permitindo acelerar os filtros `LIKE '%termo%'`.
- **Filtros textuais** fazem escape dos curingas SQL (`%` e `_`) para que buscas parciais preservem o significado literal dos termos informados.
- **Lotes JDBC do Hibernate** habilitados (padrão 50) e operações de escrita ordenadas para agrupar instruções compatíveis em uma transação. O relay também lê eventos pendentes em lotes de até 20.
- **Controle de concorrência** no estoque e na fila de reservas com bloqueios pessimistas; a verificação de limite de empréstimos também bloqueia o usuário durante a operação.

---

## Docker e escalabilidade

- **Dockerfile multi-stage** para o núcleo: build com Maven e imagem final enxuta com JRE 21.
- **Docker Compose** permite escalar horizontalmente o núcleo com `--scale api-nucleo=3`, atrás de um gateway Nginx. PostgreSQL e Kafka locais continuam sendo instâncias únicas; o cenário demonstra escalabilidade da API, não alta disponibilidade da infraestrutura.
- **Snapshots escaláveis:** réplicas de `api-relatorio` podem compartilhar o banco PostgreSQL; ShedLock coordena a execução do snapshot para que apenas uma réplica execute a tarefa por vez.
- **Relay concorrente:** `FOR UPDATE SKIP LOCKED` permite que instâncias do núcleo busquem registros pendentes sem bloquear as linhas já reservadas por outra instância.
- **Threads virtuais** (Java 21) habilitadas para melhorar a vazão em operações de I/O.
- **Avisos de atraso:** o núcleo verifica diariamente às 09:00 UTC os empréstimos vencidos e envia e-mail pelo Mailpit. Cada empréstimo é notificado no máximo uma vez a cada 7 dias; a última notificação só é gravada após o envio bem-sucedido. O ShedLock usa PostgreSQL para impedir que réplicas enviem o mesmo lote simultaneamente.

### Produção

O Compose é destinado ao desenvolvimento e à demonstração de escala horizontal da API. Para produção, use PostgreSQL gerenciado com backup/alta disponibilidade e Kafka gerenciado ou cluster com replicação adequada; configure segredos fora do Compose, limites de CPU/memória, monitoramento e autoscaling no orquestrador. O fator de replicação 1 do Kafka local não protege contra indisponibilidade do broker. Se houver múltiplas réplicas de relatórios, todas devem compartilhar o banco do serviço, incluindo a tabela ShedLock.

### Evolução futura

- Réplica de leitura ou view materializada para os relatórios
- Cache (Redis) para o catálogo, que é lido com muito mais frequência do que escrito
- Orquestração com Kubernetes

---

## Testes

```bash
# na pasta tech-challenge-biblioteca/biblioteca-nucleo ou biblioteca-relatorio (Linux/macOS)
./mvnw test
```

No Windows, execute `.\mvnw.cmd test` no PowerShell, na pasta do módulo desejado. A suíte de relatórios inclui testes unitários de domínio e serviços, contrato do evento e testes de integração com PostgreSQL e Kafka via Testcontainers; os testes de integração são ignorados quando Docker não está disponível.

---

## Autoria

Desenvolvido por **Aira** como parte da pós-graduação em Arquitetura e Desenvolvimento Java na FIAP.