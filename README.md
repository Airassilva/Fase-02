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

O sistema é composto por **dois serviços**, cada um com o seu próprio banco de dados, que se comunicam de forma assíncrona via Kafka.

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

Serviço de leitura. Consome os eventos de empréstimo publicados pelo núcleo, mantém um modelo de leitura próprio e expõe os relatórios. Um job agendado consolida os relatórios periodicamente.

- Top 20 livros mais emprestados
- Livros emprestados no momento e previsão de devolução

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
| Mapeamento | MapStruct |
| Observabilidade | Spring Boot Actuator |
| Testes | JUnit 5, Testcontainers, ArchUnit |
| Contêineres | Docker e Docker Compose |
| Build | Maven |

---

## Estrutura do repositório

```
tech-challenge-biblioteca/
├── docker-compose.yml
├── README.md
├── biblioteca-nucleo/        serviço transacional
├── biblioteca-relatorio/     serviço de relatórios
└── docs/                     diagramas e relatório técnico
```

---

## Como executar

### Pré-requisitos

- Docker e Docker Compose
- (opcional, para rodar fora do Docker) JDK 21 e Maven 3.9+

### Subindo tudo com Docker Compose

```bash
git clone https://github.com/Airassilva/Fase-02.git
cd tech-challenge-biblioteca

docker compose up --build
```

Serviços e portas (ajuste conforme o seu `docker-compose.yml`):

| Serviço | URL |
|---|---|
| API Núcleo | http://localhost:8080 |
| API Relatório | http://localhost:8081 |
| Swagger Núcleo | http://localhost:8080/swagger-ui.html |
| Swagger Relatório | http://localhost:8081/swagger-ui.html |
| Health Núcleo | http://localhost:8080/actuator/health |
| Health Relatório | http://localhost:8081/actuator/health |

Para derrubar tudo e remover os volumes:

```bash
docker compose down -v
```

### Escalando o serviço de relatório

```bash
docker compose up --scale api-relatorio=2
```

### Executando localmente (sem Docker para as aplicações)

Suba apenas a infraestrutura:

```bash
docker compose up postgres-nucleo postgres-relatorio kafka
```

E, em cada módulo:

```bash
cd biblioteca-nucleo
./mvnw spring-boot:run
```

### Variáveis de ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL do banco do serviço | `jdbc:postgresql://postgres-nucleo:5432/biblioteca_nucleo` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `biblioteca` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `biblioteca` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Endereço do Kafka | `kafka:9092` |

---

## Documentação da API

- **Swagger UI** em cada serviço, gerado pelo SpringDoc a partir dos controllers e DTOs.
- **Especificação OpenAPI (JSON)** em `/v3/api-docs`.
- **Javadoc** das portas de entrada (casos de uso) e das classes de domínio:

```bash
./mvnw javadoc:javadoc
```

---

## Principais endpoints

### API Núcleo (`/api/v1`)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/livros` | Cadastra um livro |
| `POST` | `/livros/lote` | Cadastra livros em lote |
| `GET` | `/livros` | Busca livros com filtros e paginação (`titulo`, `autor`, `isbn`, `disponivel`, `page`, `size`) |
| `GET` | `/livros/{id}` | Consulta um livro |
| `PUT` | `/livros/{id}` | Atualiza um livro |
| `DELETE` | `/livros/{id}` | Exclui um livro |
| `POST` | `/usuarios` | Cadastra um usuário |
| `GET` | `/usuarios` | Lista usuários (paginado) |
| `GET` | `/usuarios/{id}` | Consulta um usuário |
| `PUT` | `/usuarios/{id}` | Atualiza um usuário |
| `DELETE` | `/usuarios/{id}` | Exclui um usuário |
| `GET` | `/usuarios/{id}/emprestimos` | Histórico de empréstimos do usuário |
| `POST` | `/emprestimos` | Registra um empréstimo |
| `PATCH` | `/emprestimos/{id}/devolucao` | Registra a devolução |
| `POST` | `/reservas` | Reserva um livro |

### API Relatório (`/api/v1`)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/relatorios/livros-mais-emprestados` | Top 20 livros mais emprestados |
| `GET` | `/relatorios/emprestimos-ativos` | Livros emprestados no momento e previsão de devolução |

> A lista completa e os contratos de request/response estão no Swagger UI.

### Exemplo

```bash
curl -X POST http://localhost:8080/api/v1/emprestimos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "livroId": 10}'
```

---

## Eventos (Kafka)

Tópico: `biblioteca.emprestimos` (chave da mensagem: `emprestimoId`)

```json
{
  "eventId": "7f1c2d3e-8a9b-4c5d-9e0f-1a2b3c4d5e6f",
  "tipo": "EMPRESTIMO_REALIZADO",
  "ocorridoEm": "2026-10-17T14:30:00Z",
  "emprestimoId": 123,
  "livroId": 45,
  "livroTitulo": "Clean Architecture",
  "livroIsbn": "9780134494166",
  "usuarioId": 9,
  "dataPrevistaDevolucao": "2026-10-31",
  "dataDevolucao": null
}
```

Tipos de evento: `EMPRESTIMO_REALIZADO` e `LIVRO_DEVOLVIDO`.

---

## Decisões de design

- **Dois serviços** Usuários, livros e empréstimos compartilham regras transacionais (estoque e validação de usuário), então ficam juntos no núcleo. Relatórios têm perfil de leitura e podem evoluir de forma independente.
- **Banco por serviço.** Cada serviço é dono dos seus dados. O relatório nunca consulta o banco do núcleo.
- **Transactional Outbox.** O empréstimo e o evento são gravados na mesma transação do núcleo, e um relay publica no Kafka. Isso evita inconsistência entre banco e fila.
- **Consumidor idempotente.** O serviço de relatório guarda o `eventId` processado e ignora duplicados, já que o Kafka entrega "pelo menos uma vez".
- **Retry e Dead Letter Topic** para eventos que falham no processamento.
- **Modelo de leitura no relatório.** O consumidor mantém tabelas de empréstimos ativos e de contagem por livro, e o job agendado gera os snapshots consolidados.
- **Padrões utilizados:** Ports and Adapters, Repository, Strategy (política de prazo de devolução), Specification (filtros dinâmicos) e DTO/Mapper.
- **Consistência eventual.** Os relatórios refletem o estado do núcleo com um pequeno atraso, e essa é uma decisão consciente.

---

## Data e hora

- Datas e horas armazenadas e trafegadas em **UTC**, no formato **ISO-8601** (`2026-10-17T14:30:00Z`).
- Datas sem hora (como a previsão de devolução) usam `LocalDate` no formato `yyyy-MM-dd`.
- O prazo de devolução é calculado com a API `java.time`, a partir de um `Clock` injetado, o que torna as regras testáveis.
- Fuso de referência para exibição: `America/Recife`.

---

## Otimização de acesso a dados

- **Paginação** em todas as listagens e buscas.
- **Índices** em `isbn`, `titulo` e `autor`, e nas chaves estrangeiras de empréstimo.
- **Operações em lote** com `saveAll` e `hibernate.jdbc.batch_size`, além do endpoint de cadastro de livros em lote.
- **Prevenção de N+1** com `@EntityGraph` e fetch join onde necessário.
- **Agregações no banco.** O top 20 usa `GROUP BY` e `LIMIT` na consulta, e não processamento em memória.
- **Streams** para o processamento de coleções no domínio e na camada de aplicação.
- **Controle de concorrência** no estoque com lock otimista (`@Version`) ou pessimista.

---

## Docker e escalabilidade

- **Dockerfile multi-stage** em cada serviço: build com Maven e imagem final enxuta com JRE 21.
- **Docker Compose** orquestra os dois serviços, os dois bancos PostgreSQL e o Kafka, com `healthcheck` e `depends_on`.
- **Serviços stateless:** podem ser replicados atrás de um balanceador de carga.
- **Consumer group do Kafka:** várias réplicas do serviço de relatório dividem as partições automaticamente.
- **Job agendado em múltiplas réplicas:** o uso de ShedLock evita execução duplicada.
- **Threads virtuais** (Java 21) habilitadas para melhorar a vazão em operações de I/O.

### Evolução futura

- Réplica de leitura ou view materializada para os relatórios
- Cache (Redis) para o catálogo, que é lido com muito mais frequência do que escrito
- Balanceador (Nginx) na frente das réplicas
- Orquestração com Kubernetes

---

## Testes

```bash
# em cada módulo
./mvnw test
```

- **Unitários** para regras de domínio (empréstimo, devolução, prazo, estoque).
- **Integração** com Testcontainers (PostgreSQL e Kafka reais).

---

## Autoria

Desenvolvido por **Aira** como parte da pós-graduação em Arquitetura e Desenvolvimento Java na FIAP.