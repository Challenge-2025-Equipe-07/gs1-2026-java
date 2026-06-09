# SunHarvest Backend — Global Solution 2026

## Descrição da Solução

O **SunHarvest Backend** é uma API REST desenvolvida em **Java 21** com **Spring Boot 4**, voltada para o desafio acadêmico **"Global Solution: O Espaço é a Nova Fronteira"**. A aplicação realiza operações CRUD completas, persistindo dados em um banco de dados **Oracle** conteinerizado com Docker.

A solução utiliza uma arquitetura de dois containers orquestrados via `docker-compose`:

- **gs1-app** — Container da aplicação Java (Spring Boot)
- **gs1-oracle-db** — Container do banco de dados Oracle Free 23c

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

---

## Stack Tecnológica

| Camada            | Tecnologia               |
| ----------------- | ------------------------ |
| Linguagem         | Java 21                  |
| Framework         | Spring Boot 4.0.6        |
| Build Tool        | Gradle                   |
| Banco de Dados    | Oracle Database Free 23c |
| Container Runtime | Docker / Docker Compose  |
| Imagem Oracle     | `gvenzl/oracle-free:23`  |
| Imagem Java       | `eclipse-temurin:21-jre` |

---

## How To — Passo a Passo

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução
- [Git](https://git-scm.com/) instalado
- Mínimo de **4 GB de RAM** disponível para o Docker (Oracle requer ~2 GB)

### 1. Clonar o repositório

```bash
git clone <URL_DO_REPOSITORIO>
cd gs1-2026-java
```

### 2. Subir os containers em modo background

```bash
docker compose up -d --build
```

> O primeiro build pode demorar alguns minutos (download de dependências + inicialização do Oracle).

### 3. Verificar se os containers estão em execução

```bash
docker compose ps
```

Saída esperada:

```
NAME             IMAGE                         STATUS                   PORTS
gs1-app          gs1-2026-java-gs1-app         Up X minutes             0.0.0.0:8080->8080/tcp
gs1-oracle-db    gvenzl/oracle-free:23         Up X minutes (healthy)   0.0.0.0:1521->1521/tcp
```

### 4. Testar a API

A API utiliza autenticação JWT. É necessário registrar um usuário e obter um token antes de acessar os endpoints protegidos.

```bash
# 4.1 — Registrar um usuário (endpoint público)
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User", "username":"testuser", "password":"password123"}'
# Resposta: {"token": "eyJhbGci..."}

# 4.2 — Login (endpoint público)
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser", "password":"password123"}'
# Resposta: {"token": "eyJhbGci..."}

# 4.3 — Salvar o token retornado em uma variável
TOKEN="<cole o token retornado acima>"

# 4.4 — Criar um objeto (endpoint protegido)
curl -s -X POST http://localhost:8080/simple-object \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Teste Docker"}'
# Resposta: {"id": 1, "name": "Teste Docker"}

# 4.5 — Consultar o objeto criado (endpoint protegido)
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/simple-object/1
# Resposta: {"id": 1, "name": "Teste Docker"}
```

---

## Comandos de Validação

### Exibir logs dos containers

```bash
# Logs de ambos os containers
docker compose logs

# Logs apenas da aplicação
docker compose logs gs1-app

# Logs apenas do banco de dados
docker compose logs gs1-oracle-db

# Logs em tempo real (follow)
docker compose logs -f
```

### Acessar o container da aplicação via exec

```bash
# Entrar no container da aplicação
docker container exec -it gs1-app bash

# Dentro do container, executar:
pwd
# Saída esperada: /app

ls -l
# Saída esperada: lista contendo app.jar

whoami
# Saída esperada: appuser
```

### Acessar o container do banco de dados via exec

```bash
# Entrar no container do banco
docker container exec -it gs1-oracle-db bash

# Dentro do container, executar:
pwd
ls -l
whoami
```

### Acessar o terminal interativo do Oracle e validar dados

```bash
# Conectar ao SQL*Plus dentro do container (usar terminal real, não IDE)
docker container exec -it gs1-oracle-db sqlplus sunharvest/sunharvest123@//localhost:1521/FREEPDB1
```

Dentro do SQL\*Plus, executar:

```sql
SELECT table_name FROM user_tables;
SELECT * FROM TB_SIMPLE_OBJECT;
SELECT id, username, name FROM TB_USERS;
EXIT;
```

---

## Variáveis de Ambiente

### Container da Aplicação (`gs1-app`)

| Variável                        | Descrição                | Valor Padrão                                    |
| ------------------------------- | ------------------------ | ----------------------------------------------- |
| `SPRING_PROFILES_ACTIVE`        | Perfil Spring Boot ativo | `docker`                                        |
| `SPRING_DATASOURCE_URL`         | URL JDBC do banco Oracle | `jdbc:oracle:thin:@gs1-oracle-db:1521/FREEPDB1` |
| `SPRING_DATASOURCE_USERNAME`    | Usuário do banco         | `sunharvest`                                    |
| `SPRING_DATASOURCE_PASSWORD`    | Senha do banco           | `sunharvest123`                                 |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia de DDL        | `update`                                        |

### Container do Banco de Dados (`gs1-oracle-db`)

| Variável            | Descrição                     | Valor Padrão    |
| ------------------- | ----------------------------- | --------------- |
| `ORACLE_PASSWORD`   | Senha do SYS/SYSTEM           | `oracle123`     |
| `APP_USER`          | Usuário da aplicação          | `sunharvest`    |
| `APP_USER_PASSWORD` | Senha do usuário da aplicação | `sunharvest123` |

---

## Estrutura do Projeto

```
gs1-2026-java/
├── Dockerfile                          # Multi-stage build (build + runtime)
├── docker-compose.yml                  # Orquestração dos containers
├── .dockerignore                       # Arquivos ignorados no build
├── build.gradle                        # Dependências e build config
├── src/
│   └── main/
│       ├── java/                       # Código-fonte Java
│       └── resources/
│           ├── application.properties          # Config padrão (FIAP)
│           └── application-docker.properties   # Config Docker (Oracle local)
└── README.md
```

---

## Persistência de Dados

O volume nomeado `gs1-oracle-data` garante que os dados do Oracle persistam entre reinicializações dos containers:

```bash
# Verificar o volume
docker volume inspect gs1-oracle-data

# Remover dados (CUIDADO: apaga tudo!)
docker volume rm gs1-oracle-data
```

---

## Parar e Remover

```bash
# Parar containers
docker compose down

# Parar e remover volumes (apaga dados do banco)
docker compose down -v
```
