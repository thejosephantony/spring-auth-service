# 🛡️ Spring Auth Service

API REST para autenticação, autorização e gerenciamento de usuários, desenvolvida com Java e Spring Boot.

O projeto implementa autenticação baseada em JWT, controle de acesso por perfil, CRUD de usuários, persistência em PostgreSQL, validação de dados, tratamento centralizado de exceções e documentação interativa com Swagger/OpenAPI.

Além da API, o projeto possui um frontend desenvolvido em HTML, CSS e JavaScript para consumo dos recursos protegidos.

---

## 🎯 Objetivo

O objetivo do projeto é demonstrar a implementação de uma API REST com autenticação e autorização utilizando Spring Security e JWT.

A aplicação possui:

- autenticação de usuários;
- geração e validação de tokens JWT;
- controle de acesso por perfil (`USER` e `ADMIN`);
- CRUD de usuários;
- consulta do usuário autenticado;
- atualização do próprio perfil;
- operações administrativas sobre usuários;
- armazenamento seguro de senhas utilizando BCrypt;
- validação dos dados recebidos pela API;
- tratamento centralizado de exceções;
- persistência em PostgreSQL;
- documentação da API utilizando Swagger/OpenAPI;
- frontend para consumo da API.

---

# 🛠️ Tecnologias utilizadas

## Backend

- Java 21
- Spring Boot 3.2.2
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- Auth0 Java JWT
- Swagger/OpenAPI
- Maven

## Frontend

- HTML5
- CSS3
- JavaScript

---

# 🏗️ Arquitetura

A aplicação segue uma arquitetura em camadas, separando as responsabilidades entre controllers, services e repositories.

```text
                    ┌──────────────────┐
                    │     Frontend     │
                    │    HTML/CSS/JS   │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    Controller    │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │     Service      │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    Repository    │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    PostgreSQL    │
                    └──────────────────┘
```

O fluxo de autenticação utiliza JWT:

```text
Login
  │
  ▼
AuthController
  │
  ▼
AuthenticationManager
  │
  ▼
UserRepository
  │
  ▼
Validação da senha
  │
  ▼
TokenService
  │
  ▼
JWT
```

Nas requisições protegidas:

```text
HTTP Request
     │
     ▼
Authorization: Bearer <JWT>
     │
     ▼
SecurityFilter
     │
     ▼
TokenService
     │
     ▼
Validação do JWT
     │
     ▼
Spring Security
     │
     ▼
Controller
```

---

# 📁 Estrutura do projeto

Principais pacotes:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── joseph/
    │           └── springauthservice/
    │               ├── config/
    │               ├── controller/
    │               ├── dto/
    │               ├── entity/
    │               ├── exception/
    │               ├── repository/
    │               ├── security/
    │               └── service/
    │
    └── resources/
        └── application.properties
```

### Responsabilidade dos principais pacotes

| Pacote | Responsabilidade |
|---|---|
| `controller` | Exposição dos endpoints REST |
| `service` | Regras de negócio |
| `repository` | Acesso ao banco de dados |
| `entity` | Entidades persistidas |
| `dto` | Objetos utilizados nas requisições e respostas |
| `security` | Filtros e componentes relacionados à autenticação |
| `config` | Configurações do Spring Security |
| `exception` | Exceções e tratamento de erros |

---

# 🔐 Autenticação

A autenticação é realizada através do endpoint:

```http
POST /auth/login
```

### Requisição

```json
{
  "email": "admin@admin.com",
  "password": "********"
}
```

### Resposta

```json
{
  "token": "eyJ..."
}
```

O token JWT deve ser enviado nas requisições protegidas utilizando o header:

```http
Authorization: Bearer <token>
```

A API utiliza autenticação stateless, portanto não mantém sessão HTTP do usuário.

---

# 🔑 JWT

O projeto utiliza JWT para representar a autenticação do usuário.

O `TokenService` é responsável por:

- gerar tokens;
- definir o emissor do token;
- definir o usuário associado ao token;
- definir a expiração;
- validar tokens recebidos.

A validade configurada atualmente para o token é de **2 horas**.

O segredo utilizado para assinar o token deve ser configurado através de variável de ambiente.

---

# 👥 Perfis de acesso

A aplicação possui dois perfis:

```text
USER
ADMIN
```

## USER

Usuários comuns podem:

- realizar login;
- consultar recursos protegidos permitidos pela aplicação;
- consultar o próprio perfil;
- atualizar o próprio perfil.

## ADMIN

Administradores possuem permissões adicionais para operações administrativas.

Administradores podem:

- criar usuários;
- consultar usuários;
- atualizar usuários pelo ID;
- excluir usuários pelo ID;
- acessar os recursos disponíveis para usuários autenticados.

---

# 🔒 Regras de autorização

| Método | Endpoint | Público | USER | ADMIN |
|---|---|---:|---:|---:|
| `POST` | `/auth/login` | ✅ | ✅ | ✅ |
| `POST` | `/users` | ❌ | ❌ | ✅ |
| `GET` | `/users` | ❌ | ✅ | ✅ |
| `GET` | `/users/{id}` | ❌ | ✅ | ✅ |
| `GET` | `/users/me` | ❌ | ✅ | ✅ |
| `PUT` | `/users/me` | ❌ | ✅ | ✅ |
| `PUT` | `/users/{id}` | ❌ | ❌ | ✅ |
| `DELETE` | `/users/{id}` | ❌ | ❌ | ✅ |

---

# 📚 Endpoints

## 🔐 Autenticação

### Login

## 👤 Administrador inicial

Para o ambiente de desenvolvimento, configure:

```powershell
$env:ADMIN_EMAIL="admin@admin.com"
$env:ADMIN_PASSWORD="admin123"

```http
POST /auth/login
```

Exemplo:

```json
{
  "email": "admin@admin.com",
  "password": "********"
}
```

Resposta:

```json
{
  "token": "eyJ..."
}
```

---

# 👤 Usuários

## Criar usuário

```http
POST /users
```

Requer:

```text
ADMIN
```

Exemplo:

```json
{
  "name": "Joseph",
  "email": "joseph@example.com",
  "password": "123456"
}
```

A senha é armazenada utilizando BCrypt e não é retornada pela API.

Resposta:

```json
{
  "id": 1,
  "name": "Joseph",
  "email": "joseph@example.com",
  "role": "USER"
}
```

Status:

```text
201 Created
```

---

## Listar usuários

```http
GET /users
```

Requer autenticação.

Resposta:

```json
[
  {
    "id": 1,
    "name": "Joseph",
    "email": "joseph@example.com",
    "role": "USER"
  }
]
```

Status:

```text
200 OK
```

---

## Buscar usuário por ID

```http
GET /users/{id}
```

Exemplo:

```http
GET /users/1
```

Requer autenticação.

Resposta:

```json
{
  "id": 1,
  "name": "Joseph",
  "email": "joseph@example.com",
  "role": "USER"
}
```

Status:

```text
200 OK
```

---

## Consultar usuário autenticado

```http
GET /users/me
```

Requer autenticação.

O usuário é identificado através do JWT enviado na requisição.

Resposta:

```json
{
  "id": 1,
  "name": "Joseph",
  "email": "joseph@example.com",
  "role": "USER"
}
```

Status:

```text
200 OK
```

---

## Atualizar próprio perfil

```http
PUT /users/me
```

Requer autenticação.

Exemplo:

```json
{
  "name": "Joseph Atualizado",
  "email": "novo@email.com",
  "password": "654321"
}
```

A senha somente é alterada quando uma nova senha válida é informada.

Status:

```text
200 OK
```

---

## Atualizar usuário por ID

```http
PUT /users/{id}
```

Requer:

```text
ADMIN
```

Exemplo:

```http
PUT /users/2
```

```json
{
  "name": "Usuário Atualizado",
  "email": "usuario@email.com",
  "password": "654321"
}
```

Status:

```text
200 OK
```

---

## Excluir usuário

```http
DELETE /users/{id}
```

Requer:

```text
ADMIN
```

Exemplo:

```http
DELETE /users/2
```

Resposta:

```text
204 No Content
```

Após a exclusão, uma consulta ao mesmo ID deverá retornar:

```text
404 Not Found
```

---

# ❌ Tratamento de erros

A aplicação possui tratamento centralizado de exceções para erros conhecidos.

## 400 — Bad Request

Utilizado quando os dados enviados não atendem às validações.

Exemplos:

- e-mail inválido;
- campo obrigatório vazio;
- senha com tamanho insuficiente;
- dados inválidos.

Exemplo:

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Dados inválidos"
}
```

---

## 401 — Unauthorized

Ocorre quando uma requisição protegida é realizada sem autenticação válida.

Exemplo:

```text
GET /users/me
```

sem:

```http
Authorization: Bearer <token>
```

---

## 403 — Forbidden

Ocorre quando o usuário está autenticado, mas não possui permissão para realizar determinada operação.

Exemplo:

```text
USER
  ↓
DELETE /users/2
  ↓
403 Forbidden
```

---

## 404 — Not Found

Ocorre quando o usuário solicitado não existe.

Exemplo:

```json
{
  "status": 404,
  "error": "USER_NOT_FOUND",
  "message": "Usuário não encontrado"
}
```

---

## 409 — Conflict

Ocorre quando existe conflito com dados já cadastrados.

Exemplo:

```json
{
  "status": 409,
  "error": "USER_ALREADY_EXISTS",
  "message": "E-mail já cadastrado"
}
```

---

# 🗄️ Banco de dados

O projeto utiliza PostgreSQL.

Configuração local esperada:

```text
Host: localhost
Port: 5432
Database: spring_auth
User: postgres
```

A senha do banco não deve ser armazenada diretamente no código-fonte.

A aplicação utiliza:

```text
DB_PASSWORD
```

para acessar a senha do PostgreSQL.

---

# 🔐 Variáveis de ambiente

As informações sensíveis devem ser configuradas no ambiente de execução.

Exemplo no PowerShell:

```powershell
$env:DB_PASSWORD="sua_senha_do_postgres"
$env:JWT_SECRET="sua_chave_secreta"
```

O `application.properties` deve utilizar referências às variáveis:

```properties
spring.datasource.password=${DB_PASSWORD}

api.security.token.secret=${JWT_SECRET}
```

### Importante

Não versionar:

- senhas;
- tokens;
- chaves JWT;
- arquivos `.env` contendo informações sensíveis;
- credenciais do banco.

---

# ▶️ Como executar

## Pré-requisitos

- Java 21
- PostgreSQL
- Git

Docker não é necessário para executar a aplicação localmente.

---

## 1. Clonar o projeto

```bash
git clone https://github.com/thejosephantony/spring-auth-service.git
```

Entrar no projeto:

```bash
cd spring-auth-service
```

---

## 2. Criar o banco de dados

No PostgreSQL, crie:

```text
spring_auth
```

A aplicação será responsável pelas tabelas através da configuração do JPA.

---

## 3. Configurar as variáveis de ambiente

No PowerShell:

```powershell
$env:DB_PASSWORD="sua_senha_do_postgres"
```

Configure também o segredo JWT:

```powershell
$env:JWT_SECRET="sua_chave_secreta"
```

---

## 4. Executar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Para executar a compilação e os testes existentes:

```powershell
.\mvnw.cmd clean test
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

---

## 5. Acessar a aplicação

A API estará disponível em:

```text
http://localhost:8080
```

---

# 📖 Swagger / OpenAPI

A API possui documentação interativa através do Swagger UI.

Após iniciar a aplicação:

```text
http://localhost:8080/swagger-ui/index.html
```

O Swagger permite visualizar os endpoints e executar requisições diretamente pela interface.

### Fluxo para testar endpoints protegidos

1. Fazer login através de `POST /auth/login`.
2. Copiar o JWT retornado.
3. Utilizar a autenticação Bearer disponível no Swagger.
4. Informar o token.
5. Executar os endpoints protegidos.

---

# 🖥️ Frontend

O projeto também possui um frontend localizado em:

```text
/frontend
```

O frontend foi desenvolvido utilizando:

- HTML;
- CSS;
- JavaScript.

Ele consome a API REST e implementa o fluxo de autenticação através do JWT.

Entre os recursos estão:

- tela de login;
- autenticação;
- consumo da API;
- acesso aos recursos protegidos;
- gerenciamento de usuários.

O frontend foi desenvolvido como complemento à API.

---

# 🧪 Testes realizados

Os principais fluxos da API foram testados manualmente através do Swagger e do frontend.

Foram verificados:

### Autenticação

- login com credenciais válidas;
- login com credenciais inválidas;
- geração de JWT;
- acesso utilizando Bearer Token;
- rejeição de requisições sem autenticação.

### CRUD

- criação de usuário;
- listagem de usuários;
- busca por ID;
- consulta do próprio usuário;
- atualização de usuário;
- atualização do próprio perfil;
- exclusão de usuário.

### Autorização

- acesso de usuário `USER`;
- acesso de usuário `ADMIN`;
- tentativa de operação administrativa por `USER`;
- bloqueio de operações sem autenticação.

### Respostas HTTP

Foram verificados os principais códigos:

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

---

# 🔒 Segurança

O projeto utiliza algumas medidas básicas de segurança:

- autenticação stateless;
- JWT para autenticação;
- BCrypt para armazenamento das senhas;
- controle de acesso baseado em roles;
- validação dos dados recebidos;
- DTOs para entrada e saída de dados;
- não exposição da senha nas respostas;
- segredo JWT configurado por variável de ambiente;
- senha do banco configurada por variável de ambiente;
- proteção dos endpoints através do Spring Security.

---

# 🧠 Decisões técnicas

## JWT

O JWT foi utilizado para implementar autenticação stateless.

Isso permite que a API valide a identidade do usuário através do token enviado em cada requisição protegida.

## BCrypt

O BCrypt é utilizado para não armazenar as senhas dos usuários em texto puro no banco de dados.

## PostgreSQL

O PostgreSQL foi utilizado como banco de dados relacional para persistência dos usuários.

## Spring Data JPA

O Spring Data JPA foi utilizado para abstrair o acesso aos dados e implementar as operações de persistência.

## DTOs

DTOs são utilizados para separar os dados recebidos pela API das entidades persistidas no banco.

Isso também evita que informações sensíveis, como a senha, sejam retornadas nas respostas.

## Arquitetura em camadas

A separação entre:

```text
Controller
Service
Repository
```

mantém as responsabilidades organizadas e facilita a manutenção da aplicação.

---

# ⚠️ Observações

Este projeto possui foco educacional e demonstração de conceitos de:

- desenvolvimento de APIs REST;
- autenticação;
- autorização;
- JWT;
- Spring Security;
- persistência de dados;
- integração frontend/backend.

Para utilização em produção, recomenda-se adicionar controles adicionais de segurança, gerenciamento de segredos, políticas de expiração e renovação de tokens, configuração adequada de CORS, HTTPS, logs e demais requisitos de infraestrutura.

---

# 📌 Status do projeto

```text
✅ API REST
✅ CRUD de usuários
✅ Autenticação JWT
✅ Spring Security
✅ Roles USER / ADMIN
✅ PostgreSQL
✅ BCrypt
✅ Validação de dados
✅ Tratamento de exceções
✅ Swagger/OpenAPI
✅ Frontend
✅ Testes manuais dos principais fluxos
```

---

# 👨‍💻 Autor

**Joseph Antony**

GitHub:

https://github.com/thejosephantony

Repositório:

https://github.com/thejosephantony/spring-auth-service
