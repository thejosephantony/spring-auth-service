# 🛡️ Spring Auth Service

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D.svg)](https://swagger.io/)
[![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-black.svg)](https://jwt.io/)

Uma API REST desenvolvida em **Java** e **Spring Boot** para autenticação e gestão completa de usuários. Este projeto foi construído como solução para o **Desafio de Seleção - Backend Engineer**, focando em segurança, boas práticas de desenvolvimento (SOLID, Clean Code) e documentação interativa.

---

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3 (Web, Data JPA, Security, Validation)
* **Banco de Dados:** PostgreSQL
* **Segurança:** Spring Security com JWT (JSON Web Token)
* **Documentação:** Springdoc OpenAPI (Swagger UI)
* **Testes:** JUnit 5 e Mockito
* **Gerenciamento de Dependências:** Maven

---

## ⚙️ Funcionalidades Principais

* **🔐 Autenticação JWT:** Login seguro que gera e valida tokens de acesso para proteger rotas sensíveis.
* **👥 Gestão de Usuários (CRUD):** Criação, listagem, busca detalhada por ID, atualização e remoção de registros.
* **🛡️ Controle de Acesso (RBAC):** Somente usuários com a *role* `ADMIN` possuem privilégios para cadastrar, atualizar ou deletar outros usuários.
* **👤 Perfil do Usuário:** Endpoint exclusivo (`/users/me`) para que o usuário autenticado consulte seus próprios dados.
* **⚙️ Seed de Dados Inicial:** Criação automática de um usuário Administrador padrão na primeira inicialização da aplicação para viabilizar o uso do sistema.

---

## 🗺️ Estrutura de Endpoints da API

Abaixo estão as rotas disponíveis na aplicação. As rotas marcadas com o cadeado (🔒) exigem o envio do token JWT no header `Authorization: Bearer <token>`.

### 🔑 Autenticação
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Autentica um usuário e retorna o token JWT | Público |

### 👥 Usuários
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/users` | Cria um novo usuário | 🔒 Apenas `ADMIN` |
| `GET` | `/users` | Lista todos os usuários cadastrados | 🔒 `ADMIN` ou `USER` |
| `GET` | `/users/{id}` | Busca os detalhes de um usuário específico | 🔒 `ADMIN` ou `USER` |
| `GET` | `/users/me` | Retorna os dados do usuário logado no momento | 🔒 `ADMIN` ou `USER` |
| `PUT` | `/users/{id}` | Atualiza os dados de um usuário | 🔒 Apenas `ADMIN` |
| `DELETE` | `/users/{id}` | Remove um usuário do sistema | 🔒 Apenas `ADMIN` |

---

## 🛑 Tratamento de Erros

A API possui um `GlobalExceptionHandler` que intercepta exceções e retorna respostas JSON padronizadas e semânticas, utilizando os códigos HTTP corretos:
* `400 Bad Request`: Dados inválidos enviados no corpo da requisição.
* `401 Unauthorized`: Tentativa de acesso sem token, com token inválido ou expirado.
* `403 Forbidden`: Usuário autenticado, mas sem permissão (`role`) para acessar o recurso.
* `404 Not Found`: Recurso ou usuário não encontrado no banco de dados.
* `409 Conflict`: Tentativa de cadastrar um e-mail que já existe no sistema.

---

## 🛠️ Como Executar o Projeto Localmente

### Pré-requisitos
* **Java 17** ou superior instalado.
* **PostgreSQL** instalado e rodando na porta padrão (`5432`).
* **Git** para clonar o repositório.

### 1. Clonar o repositório
```bash
git clone https://github.com/thejosephantony/spring-auth-service.git
cd spring-auth-service
```

### 2. Configurar o Banco de Dados
Crie um banco de dados vazio no PostgreSQL (ex: `spring_auth_db`). Em seguida, abra o arquivo `src/main/resources/application.properties` e ajuste as credenciais (URL, usuário e senha) de acordo com a sua máquina:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spring_auth_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Executar a Aplicação
O projeto inclui o Maven Wrapper, dispensando a instalação prévia do Maven. No terminal, na raiz do projeto, execute:

**No Windows:**
```bash
./mvnw.cmd spring-boot:run
```

**No Linux/Mac:**
```bash
./mvnw spring-boot:run
```
A aplicação será iniciada e estará disponível em `http://localhost:8080`.

---

## 🔐 Acesso Inicial

Para que você não fique trancado fora do sistema, a aplicação gera automaticamente um **Administrador Inicial** na primeira execução [cite: 2]. Utilize estas credenciais no endpoint `/auth/login` para testar:

* **E-mail:** `admin@admin.com`
* **Senha:** `123456`

---

## 📚 Documentação e Testes (Swagger)

A documentação completa dos contratos da API, modelos de dados e requisitos de autenticação foi gerada utilizando **Swagger/OpenAPI** [cite: 2].

Após iniciar o servidor, acesse a interface interativa em:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Como testar rotas protegidas pelo Swagger:
1. Execute o endpoint `POST /auth/login` com as credenciais do admin inicial.
2. Copie o token retornado na resposta (sem as aspas).
3. Suba ao topo da página e clique no botão verde **"Authorize"**.
4. Digite `Bearer` seguido de um espaço e cole o seu token (Ex: `Bearer eyJhbGci...`).
5. Clique em "Authorize" e teste todos os endpoints diretamente pela interface!

---

## 🧪 Testes Automatizados

O projeto contempla uma base de testes automatizados unitários e de integração (utilizando o contexto do Spring Boot) para garantir a estabilidade das regras de negócio.
Para rodar os testes localmente, execute:
```bash
./mvnw test
```

---

## 👨‍💻 Desenvolvido por
**Joseph Antony** - [GitHub](https://github.com/thejosephantony)
