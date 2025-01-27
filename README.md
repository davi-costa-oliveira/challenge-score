# Blipay desafio

Desafio Desenvolvido por Davi Costa de Oliveira em Jan/2025

# Getting Started

## Pré-requisitos

- *JDK 11 ou mais recente*
- *Gradle 8.1+*

### Start application

```
./gradlew run
```

### Run tests

```
./gradlew test
```

### Endpoints Disponíveis

- Consultar Score: GET /score
  Parâmetros:
    - name: Nome do solicitante
    - age: Idade do solicitante
    - income: Renda do solicitante
    - city: Cidade para a consulta climática
    - cpf: CPF do solicitante


- Histórico de Consultas de Score por CPF: GET /score/history/{cpf}

## Contatos

Caso tenha alguma dúvida entrar em contato pelo email docdavicosta@gmail.com
