# SunHarvest — Irrigação Solar Inteligente

## Descrição da Solução

O **SunHarvest Backend** é uma API REST desenvolvida em **Java 21** com **Spring Boot 4**, voltada para o desafio acadêmico **"Global Solution: O Espaço é a Nova Fronteira"**. A aplicação realiza operações CRUD completas, persistindo dados em um banco de dados **Oracle** conteinerizado com Docker.

A solução utiliza uma arquitetura de dois containers orquestrados via `docker-compose`:

- **rm564113-gs1-app** — Container da aplicação Java (Spring Boot)
- **rm564113-gs1-oracle-db** — Container do banco de dados Oracle Free 23c

---

## Arquitetura Macro

> **Insira aqui o link para o Desenho da Arquitetura Macro da solução.**
>
> O diagrama deve mapear: **fluxo de usuários, frontend, API REST, banco de dados, VM e containers**.
>
> Ferramentas recomendadas: [Draw.io](https://app.diagrams.net/) ou [Visual Paradigm](https://online.visual-paradigm.com/).

> **⚠️ ATENÇÃO: NÃO utilize padrão TOGAF nem fluxogramas simples. O uso desses formatos resultará em nota ZERO neste critério.**

```
[Link do diagrama aqui]
```

---

## Integrantes

| RM     | Nome                       | Turma  |
| ------ | -------------------------- | ------ |
| 564113 | Camilo Micheletto da Silva | 2TDSPW |
| 564982 | Carlos André Silva         | 2TDSPW |
| 562700 | Guilherme Ribeiro da Costa | 2TDSPW |
| 566376 | Laura Lopes Cruz           | 2TDSPW |


**FIAP — Global Solutions 2026 — Java Advanced**

---

## Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                    SunHarvest API (Spring Boot 4)        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ AuthController│  │FarmController│  │AlertController│  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │          │
│  ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴───────┐  │
│  │  UserService  │  │  FarmService  │  │ AlertService │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │          │
│         │         ┌───────┴────────┐         │          │
│         │         │NasaPowerService│         │          │
│         │         │PenmanMonteith  │         │          │
│         │         └───────┬────────┘         │          │
│  ┌──────┴─────────────────┴──────────────────┴───────┐  │
│  │              Oracle Database (FIAP)                │  │
│  │   TB_USER  TB_FARM  TB_ALERT  TB_ALERT_SEVERITY   │  │
│  │   TB_FARM_CROP  TB_FARM_SOIL  TB_FARM_IRRIGATION  │  │
│  │   TB_FARM_SOLAR_PANEL                             │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
         ↕
NASA POWER API (dados climáticos por satélite)
```

## Tecnologias

- **Java 21** + **Spring Boot 4.0.6** + **Gradle 9.5.1**
- **Spring Security 7** — autenticação stateless com JWT (JJWT 0.12.6)
- **Oracle Database** — `oracle.fiap.com.br:1521:orcl`
- **Spring Data JPA** — Hibernate com `ddl-auto=create-drop`
- **Spring HATEOAS** — `EntityModel<T>` e `CollectionModel<T>` em todos os responses
- **SpringDoc OpenAPI 2.8.8** — Swagger UI com suporte a Bearer JWT
- **Spring Validation** — `@Valid`, `@NotBlank`, `@Email`, `@DecimalMin`/`@DecimalMax`
- **NASA POWER API** — dados climáticos por satélite (radiação solar, temperatura, umidade, vento)

## Padrões JPA Avançados Demonstrados

| Padrão | Onde |
|--------|------|
| `@MappedSuperclass` | `BaseEntity` → campo `createdAt` herdado por `Farm` e `Alert` |
| `@Embeddable` / `@Embedded` | `GeoLocation` (latitude, longitude, altitude) embutida em `Farm` |
| `@EmbeddedId` (chave composta) | `SolarPanelId(idSolarPanel, farmId)` em `FarmSolarPanel` |
| Java Records como DTOs | `FarmRequest`, `FarmResponse`, `AlertRequest`, `AlertResponse`, `LoginRequest`, etc. |

## Executar Localmente

**Pré-requisitos:** JDK 21+, VPN FIAP ativa (para Oracle)

```bash
git clone https://github.com/willahelmgui/SunHarvest.git
cd SunHarvest
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080` ou `gs1-2026-java-production.up.railway.app`.

### Swagger UI
`http://localhost:8080/swagger-ui.html`

1. Use `POST /api/v1/auth/register` para criar uma conta
2. Copie o token JWT retornado
3. Clique em **Authorize** → cole `Bearer <token>`
4. Explore todos os endpoints autenticados

## Endpoints da API

### Autenticação (`/api/v1/auth`)
| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/register` | Registrar novo usuário |
| POST | `/login` | Autenticar e obter JWT |
| POST | `/refresh` | Renovar token JWT |

### Fazendas (`/api/v1/farms`) — requer JWT
| Método | Path | Descrição |
|--------|------|-----------|
| POST | `/` | Cadastrar nova fazenda |
| GET | `/` | Listar fazendas do usuário |
| GET | `/{id}` | Buscar fazenda por ID |
| PATCH | `/{id}` | Atualizar fazenda |
| DELETE | `/{id}` | Excluir fazenda |
| GET | `/{id}/alerts` | Listar alertas da fazenda |
| POST | `/{id}/alerts` | Criar alerta na fazenda |
| GET | `/{id}/eto` | Calcular ETo (evapotranspiração) |

### Alertas (`/api/v1/alerts`) — requer JWT
| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/{id}` | Buscar alerta por ID |
| PATCH | `/{id}/acknowledge` | Reconhecer alerta |
| DELETE | `/{id}` | Excluir alerta (só se reconhecido) |

## Cálculo ETo — FAO-56 Penman-Monteith

```
ETo = [0.408·Δ·(Rn-G) + γ·(900/(T+273))·u2·(es-ea)] / [Δ + γ·(1+0.34·u2)]
```

O endpoint `GET /api/v1/farms/{id}/eto` busca automaticamente dados climáticos via **NASA POWER API** para as coordenadas da fazenda e retorna a ETo em **mm/dia** — indicando a quantidade de água que a cultura necessita.

## Deploy Azure

### Variáveis de ambiente necessárias
```
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@<oracle-host>:<port>:<sid>
SPRING_DATASOURCE_USERNAME=<seu-rm>
SPRING_DATASOURCE_PASSWORD=<sua-senha>
JWT_SECRET=<chave-base64-segura>
```

### Build Docker
```bash
docker build -t sunharvest .
docker run -p 8080:8080 sunharvest
```

## Regras de Negócio

- **Propriedade:** usuário só acessa suas próprias fazendas
- **Deleção de Fazenda:** bloqueada se houver alertas `CRITICAL` não reconhecidos
- **Deleção de Alerta:** bloqueada se alerta estiver com `acknowledged = 'N'`
- **Reconhecer Alerta:** `PATCH /api/v1/alerts/{id}/acknowledge?acknowledge=true`
- **Severidades:** `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`, `INFO` (pré-carregadas no startup)
