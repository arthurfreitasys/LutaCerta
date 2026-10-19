# LutaCerta

API RESTful para gestão de lutadores, eventos e lutas do universo MMA/UFC, construída com Spring Boot como projeto de estudo backend com a visão de evoluir para uma camada de curadoria que ajude fãs de MMA a descobrir lutadores e confrontos que valem a pena acompanhar.

## Sobre o projeto

O objetivo do projeto não é ser apenas um CRUD: a base de dados sobre lutadores, eventos e lutas foi desenhada para, no futuro, sustentar uma camada de análise que destaque características como estilo de luta, frequência de nocaute/finalização e equilíbrio entre competidores, sem apresentar isso como previsão de resultado, apenas como indicador de potencial de entretenimento.

## Stack

- **Java 21**
- **Spring Boot 4.1.1**
- **PostgreSQL** (hospedado no [Supabase](https://supabase.com))
- **Spring Data JPA** — persistência e acesso a dados
- **Lombok** - redução de boilerplate (getters/setters/construtores)
- **MapStruct** - mapeamento entre entidades e DTOs
- **Mockito** - testes unitários da camada de serviço

## Arquitetura

O projeto segue arquitetura em camadas, com responsabilidade única por classe:

```
Controller → Service → Repository → Banco de Dados
```

- **Controller** - expõe os endpoints REST, recebe requisições HTTP e delega ao Service
- **Service** - concentra as regras de negócio
- **Repository** - interfaces `JpaRepository`, sem SQL manual
- **DTO** - contratos de entrada (`RequestDTO`) e saída (`ResponseDTO`), sem expor as entidades JPA diretamente
- **Mapper** - conversão Entidade ↔ DTO via MapStruct, gerada em tempo de compilação

### Modelo de dados

```
Lutador
├── id (UUID)
├── nome, apelido
├── categoriaPeso (enum)
├── vitorias, derrotas, empates
└── paisOrigem

Evento
├── id (UUID)
├── nomeEvento
├── data
└── localEvento

Luta
├── id (UUID)
├── evento
├── lutador1, lutador2 
├── categoriaPeso (enum)
└── resultadoLuta (enum)
```

Uma `Luta` referencia um `Evento` e dois `Lutadores` distintos. Nas requisições, essas relações são feitas por identificador (`UUID`) o Service é responsável por buscar e validar os registros relacionados antes de persistir uma luta, incluindo a regra de que os dois lutadores de uma luta não podem ser o mesmo.

## Executando o projeto

### Pré-requisitos

- JDK 21+
- Maven 3.9+ (ou use o `./mvnw` incluso no projeto)
- Uma instância PostgreSQL (local ou via [Supabase](https://supabase.com))

### Configuração

O arquivo `application.properties` não é versionado (contém credenciais). Copie o template de exemplo e preencha com seus dados:

```bash
cp src/main/resources/application_example.properties src/main/resources/application.properties
```

```properties
spring.datasource.url=jdbc:postgresql://<host>:<porta>/<database>
spring.datasource.username=<usuario>
spring.datasource.password=<senha>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Nota:** se estiver usando Supabase, prefira o modo **Session Pooler** (porta 6543 ou 5432 via pooler) em redes sem suporte a IPv6 - a conexão direta exige IPv6 e pode falhar em alguns provedores. No modo pooler, o `username` segue o formato `postgres.<referencia-do-projeto>`.

### Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Endpoints

| Recurso | Verbo | Rota | Descrição   |
|---|---|---|-------------|
| Lutador | `GET` | `/api/lutadores` | Lista paginada |
| Lutador | `GET` | `/api/lutadores/{id}` | Busca por ID |
| Lutador | `POST` | `/api/lutadores` | Cadastra um lutador |
| Lutador | `PUT` | `/api/lutadores/{id}` | Atualiza um lutador |
| Lutador | `DELETE` | `/api/lutadores/{id}` | Remove um lutador | 
| Evento | `GET` / `POST` / `PUT` / `DELETE` | `/api/eventos[/{id}]` | CRUD padrão |
| Luta | `GET` / `POST` / `PUT` / `DELETE` | `/api/lutas[/{id}]` | CRUD padrão |

## Exemplos de uso

### Cadastrar um lutador

`POST /api/lutadores`

```json
{
  "nome": "Carlos Prates",
  "apelido": "The Nightmare",
  "categoriaPeso": "MEIO_MEDIO",
  "vitorias": 22,
  "derrotas": 6,
  "empates": 0,
  "paisOrigem": "Brasil"
}
```

Os campos e valores devem respeitar as validações e os enums definidos pela API.

### Consultar lutadores

`GET /api/lutadores?page=0&size=10&sort=nome,asc`

Retorna uma página de lutadores, com opções para controlar a quantidade de registros e a ordenação.

### Cadastrar uma luta

`POST /api/lutas`

A requisição utiliza os identificadores de um evento e de dois lutadores previamente cadastrados. O Service consulta os registros relacionados e aplica as regras de negócio antes de salvar a luta.

## Tratamento de erros

Erros são capturados globalmente (`@RestControllerAdvice`) e devolvidos em um formato padronizado:

```json
{
  "timestamp": "2026-10-19T17:00:00",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Lutador não encontrado pelo ID: ...",
  "path": "/api/lutadores/..."
}
```

| Cenário | Status |
|---|---|
| Recurso não encontrado (`ResourceNotFoundException`) | `404` |
| Rota/recurso inexistente (`NoResourceFoundException`) | `404` |
| Dados inválidos (`@Valid` / `MethodArgumentNotValidException`) | `400` |
| Regra de negócio violada (`RegraDeNegocioException`) | `400` |
| Erro inesperado | `500` |

## Testes automatizados

A camada de `Service` é coberta por testes unitários com Mockito:
```bash
./mvnw test
```

Cobertura atual:
- `LutaServiceTest` - criação, busca, remoção e a regra de lutadores iguais
- `LutadorServiceTest` - criação, busca, atualização e a regra de remoção com histórico
- `EventoServiceTest` - criação, busca, atualização e remoção

## Status do desenvolvimento

- [x] Modelagem de entidades e relacionamentos
- [x] Repositories (Spring Data JPA)
- [x] DTOs de entrada e saída
- [x] Mappers (MapStruct)
- [x] Tratamento global de exceções
- [x] Camada de Service (Lutador, Evento, Luta)
- [x] Camada de Controller
- [x] Regras de negócio (lutadores iguais, remoção com histórico)
- [x] Testes unitários da camada de Service
- [ ] Documentação OpenAPI customizada
- [ ] Testes de integração (Controller)
- [ ] Camada de curadoria/recomendação

## Roadmap

A evolução planejada do projeto, além do CRUD:

- **Camada de análise** sobre o histórico de lutas, identificando estilo predominante, frequência de finalização/nocaute e equilíbrio entre competidores
- **Categorização de confrontos** para o público leigo (ex.: *"Não perca"*, *"Vale acompanhar"*, *"Para quem gosta de técnica"*, *"Potencial desconhecido"*), com critérios objetivos e documentados
- Distinção clara entre estatísticas da carreira profissional do atleta e resultados específicos no UFC, caso essa granularidade seja incorporada

## Autor

Desenvolvido por **Arthur** ([Arthur Dev](https://github.com/arthurfreitasys)) como projeto de estudo em Java/Spring Boot, estudante de Análise e Desenvolvimento de Sistemas na FATEC.