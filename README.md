#  SuperNova VET

Aplicação web desenvolvida com **Spring Boot** para gerenciamento e monitoramento de pets, permitindo o cadastro de animais e tutores, acompanhamento do nível de risco dos pets e controle de acesso através de diferentes perfis de usuário.

O projeto utiliza **Oracle Database**, **Spring Data JPA**, **Flyway**, **Spring Security**, **Bean Validation** e **Swagger/OpenAPI**.

---

##  Integrantes

- Kaique Mascarenhas dos Santos
- Felipe Augusto Lopes Ferreira

---

##  Objetivo do projeto

O **SuperNova VET** tem como objetivo auxiliar no gerenciamento e monitoramento de informações relacionadas aos pets.

A aplicação permite:

- Gerenciar pets;
- Gerenciar tutores;
- Consultar pets por espécie;
- Consultar pets por nível de risco;
- Identificar pets em situação crítica;
- Gerar um resumo dos animais por nível de risco;
- Validar os dados enviados para a API;
- Controlar o acesso através de diferentes perfis de usuário;
- Armazenar os dados em banco Oracle;
- Controlar alterações no banco de dados utilizando Flyway.

---

#  Tecnologias utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- Flyway
- Oracle Database
- Maven
- Swagger / OpenAPI
- Git
- GitHub
- IntelliJ IDEA

---

#  Estrutura do projeto

A aplicação está organizada em diferentes camadas.

```text
src/main/java/Novamonitor
│
├── config
│   ├── DataInitializer.java
│   └── OpenApiConfig.java
│
├── controller
│   ├── PetController.java
│   └── TutorController.java
│
├── dto
│
├── entity
│   ├── Pet.java
│   └── Tutor.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│
├── repository
│   ├── PetRepository.java
│   └── TutorRepository.java
│
├── security
│   ├── CustomUserDetailsService.java
│   └── SecurityConfig.java
│
└── service
    └── Petservice.java
```

As migrations do banco ficam em:

```text
src/main/resources/db/migration
```

---

#  Banco de dados

O projeto utiliza **Oracle Database**.

As principais entidades utilizadas atualmente pela aplicação Java são:

### Tutor

Responsável por armazenar informações dos tutores e também os dados utilizados na autenticação.

Principais campos:

```text
id
nome
email
telefone
senha
perfil
```

### Pet

Responsável por armazenar os animais cadastrados no sistema.

Principais campos:

```text
id
nome
idade
especie
nivelRisco
tutor
```

Existe um relacionamento entre Pet e Tutor.

```text
Tutor
  │
  └──────< Pet
```

Um tutor pode estar relacionado a diferentes pets.

---

#  Flyway

O projeto utiliza **Flyway** para controle de versão do banco de dados.

As migrations ficam localizadas em:

```text
src/main/resources/db/migration
```

Exemplos:

```text
V1__baseline.sql
V2__criar_tabela_usuario.sql
V3__adicionar_seguranca_tutor.sql
```

A migration V3 adiciona os campos necessários para autenticação e autorização dos usuários.

```sql
ALTER TABLE ch_tutor
    ADD (
        ds_senha VARCHAR2(255),
        ds_perfil VARCHAR2(30)
    );
```

O Flyway executa automaticamente as migrations pendentes durante a inicialização da aplicação.

---

#  Spring Security

A aplicação utiliza **Spring Security** para autenticação e autorização.

Foram definidos dois tipos de usuário:

```text
ADMIN
VETERINARIO
```

## Permissões

| Operação | ADMIN | VETERINARIO |
|---|:---:|:---:|
| Visualizar pets | ✅ | ✅ |
| Cadastrar pets | ✅ | ✅ |
| Atualizar pets | ✅ | ✅ |
| Excluir pets | ✅ | ❌ |
| Visualizar tutores | ✅ | ❌ |
| Gerenciar tutores | ✅ | ❌ |
| Consultar pets críticos | ✅ | ✅ |
| Consultar resumo de risco | ✅ | ✅ |

A autenticação da API utiliza **HTTP Basic Authentication**.

As senhas são armazenadas utilizando:

```text
BCrypt
```

O hash da senha não é retornado nas respostas JSON da API.

---

#  Comportamento da segurança

Quando um usuário tenta acessar uma rota protegida sem autenticação, a API retorna:

```text
401 Unauthorized
```

Quando um usuário está autenticado, mas não possui permissão:

```text
403 Forbidden
```

Quando possui a permissão necessária:

```text
200 OK
```

---

#  Validação de dados

A aplicação utiliza **Jakarta Bean Validation** para validar os dados recebidos.

Entre as validações implementadas estão:

### Pet

- Nome obrigatório;
- Idade obrigatória;
- Idade não pode ser negativa;
- Espécie obrigatória;
- Nível de risco obrigatório;
- Nível de risco deve ser `BAIXO`, `MEDIO` ou `ALTO`;
- Tutor obrigatório.

### Tutor

- Nome obrigatório;
- E-mail obrigatório;
- Formato de e-mail válido;
- Telefone obrigatório;
- Telefone contendo 10 ou 11 números.

Quando existem dados inválidos, a aplicação retorna:

```text
400 Bad Request
```

Exemplo:

```json
{
  "especie": "A espécie é obrigatória",
  "idade": "A idade não pode ser negativa",
  "nivelRisco": "O nível de risco deve ser BAIXO, MEDIO ou ALTO",
  "nome": "O nome do pet é obrigatório",
  "tutor": "O tutor é obrigatório"
}
```

---

#  Funcionalidades

Além das operações tradicionais de CRUD, foram implementados fluxos específicos do sistema.

## Consulta por nível de risco

```http
GET /pets/risco/{nivelRisco}
```

Exemplo:

```text
GET /pets/risco/ALTO
```

Retorna os pets classificados com o nível de risco informado.

---

## Consulta por espécie

```http
GET /pets/especie/{especie}
```

Exemplo:

```text
GET /pets/especie/Cachorro
```

Retorna os pets da espécie informada.

---

## Monitoramento de pets críticos

```http
GET /pets/criticos
```

Retorna somente os pets classificados com:

```text
ALTO
```

Essa funcionalidade permite identificar rapidamente animais que necessitam de maior atenção.

---

## Resumo de risco

```http
GET /pets/resumo-risco
```

Retorna um resumo da quantidade de animais em cada nível de risco.

Exemplo:

```json
{
  "BAIXO": 2,
  "MEDIO": 1,
  "ALTO": 2,
  "TOTAL": 5
}
```

---

#  Principais endpoints

## Pets

### Listar pets

```http
GET /pets
```

### Cadastrar pet

```http
POST /pets
```

### Atualizar pet

```http
PUT /pets/{id}
```

### Excluir pet

```http
DELETE /pets/{id}
```

### Buscar por risco

```http
GET /pets/risco/{nivelRisco}
```

### Buscar por espécie

```http
GET /pets/especie/{especie}
```

### Pets críticos

```http
GET /pets/criticos
```

### Resumo de risco

```http
GET /pets/resumo-risco
```

---

## Tutores

### Listar tutores

```http
GET /tutores
```

### Cadastrar tutor

```http
POST /tutores
```

As rotas de tutores são protegidas e destinadas ao perfil `ADMIN`.

---

#  Swagger / OpenAPI

A documentação da API pode ser acessada através do Swagger UI.

Com a aplicação executando localmente, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação OpenAPI também pode ser acessada em:

```text
http://localhost:8080/v3/api-docs
```

O Swagger possui suporte à autenticação HTTP Basic através da opção **Authorize**.

---

#  Configuração do banco de dados

Por segurança, usuário e senha do banco não devem ser armazenados diretamente no repositório.

O `application.properties` utiliza variáveis de ambiente:

```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
```

Antes de executar o projeto, configure:

```text
DB_USERNAME
DB_PASSWORD
```

com as credenciais do seu banco Oracle.

---

#  Como executar o projeto

## 1. Pré-requisitos

É necessário possuir:

- JDK compatível com a versão definida no `pom.xml`;
- Maven;
- Git;
- Acesso ao Oracle Database;
- IntelliJ IDEA ou outra IDE Java.

---

## 2. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
```

Entre na pasta:

```bash
cd SUPERNOVAVET_JAVA_ADVANCED
```

---

## 3. Configurar o banco

Configure as variáveis de ambiente:

```text
DB_USERNAME
DB_PASSWORD
```

No IntelliJ IDEA elas podem ser configuradas em:

```text
Run
→ Edit Configurations
→ Environment variables
```

---

## 4. Instalar as dependências

No terminal:

```bash
mvn clean install
```

No Windows também é possível utilizar o Maven Wrapper do projeto, caso esteja disponível:

```cmd
mvnw.cmd clean install
```

---

## 5. Executar a aplicação

Pelo IntelliJ, execute a classe principal da aplicação Spring Boot.

Ou utilize:

```bash
mvn spring-boot:run
```

Caso utilize Maven Wrapper no Windows:

```cmd
mvnw.cmd spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

---

#  Testando a aplicação

Após iniciar o sistema, abra:

```text
http://localhost:8080/swagger-ui/index.html
```

Clique em:

```text
Authorize
```

e informe as credenciais de um usuário cadastrado no sistema.

Depois disso será possível testar os endpoints diretamente pelo Swagger.

---

#  Controle de versão

O projeto utiliza Git para controle de versão.

Fluxo básico utilizado:

```bash
git add .
git commit -m "descricao da alteracao"
git push origin main
```

---

#  Utilização de Inteligência Artificial

Ferramentas de inteligência artificial foram utilizadas como apoio durante o desenvolvimento do projeto.

A IA foi utilizada principalmente para:

- Auxiliar na análise de erros;
- Explicar conceitos do Spring Boot;
- Auxiliar na configuração do Spring Security;
- Auxiliar na configuração do Flyway;
- Sugerir melhorias nas validações;
- Auxiliar na documentação;
- Apoiar a resolução de problemas durante a integração das tecnologias.

As sugestões foram analisadas, testadas e adaptadas de acordo com as necessidades do projeto.

---

#  Considerações finais

O projeto demonstra a construção de uma aplicação Spring Boot integrada ao Oracle Database, utilizando práticas de segurança, validação de dados e versionamento de banco.

Entre os principais conceitos aplicados estão:

- API REST;
- Spring Boot;
- Spring Data JPA;
- Oracle Database;
- Flyway;
- Spring Security;
- Autenticação;
- Autorização baseada em perfis;
- BCrypt;
- Bean Validation;
- Tratamento de exceções;
- Swagger/OpenAPI;
- Git e GitHub.

---

## SuperNova VET 🐾

Sistema de gerenciamento e monitoramento veterinário desenvolvido para a 3ª Sprint.
