# Solução: Rocket League Champions

## Como subir a aplicação

Pré-requisitos: Java 21 e PostgreSQL rodando localmente.

Antes de rodar, é necessário ciar um banco chamado `sandbox_db` no Postgres (ou ajustar usuário/senha/nome do banco no `application.properties`, que hoje está configurado com usuário `postgres`, senha `123`, na porta padrão 5432). O `ddl-auto=update` cria as tabelas sozinho na primeira subida.

Pra rodar:

```
./mvnw spring-boot:run
```

Ou direto pela IDE, rodando a classe `DuxusdesafioApplication`.

A aplicação sobe em `http://localhost:8080`, o front (telas de cadastro e consultas) já é servido nessa mesma URL, não precisa de servidor separado.

A API também está documentada via Swagger, em `http://localhost:8080/swagger-ui.html`.

## Regra de negócio para montagem de times

Uma equipe pode ter no mínimo 1 e no máximo 4 integrantes. Tentando cadastrar mais que isso, a API responde 400 com a mensagem "A equipe pode ter no máximo 4 integrantes", essa validação existe tanto no back (`ApiService.cadastrarTime`) quanto no front (o select de integrantes trava depois do 4º, e mostra a mesma mensagem).

As funções aceitas pra um integrante são só três: `ATACANTE`, `GOLEIRO` e `MEIA`. Qualquer outro valor é rejeitado com 400. A função sempre é normalizada e gravada em maiúsculo no banco, independente de como chega na requisição (mandar "meia" ou "Meia" funciona igual, só não fica salvo dessa forma).

Nesta solução, um integrante pode fazer parte de quantos times diferentes quiser, inclusive do mesmo clube em datas diferentes, não existe essa restrição. Também não tratamos a possibilidade de duplicar o mesmo integrante dentro de um único time (assumimos que o cadastro não permite escolher o mesmo integrante duas vezes na mesma equipe, já que o front usa os IDs pra montar a seleção).

## Endpoints disponíveis

Nos dois grupos abaixo, `dataInicial` e `dataFinal` são sempre opcionais (formato `AAAA-MM-DD`), quando omitidos, a consulta considera todo o histórico. Quando não existe resultado pro período, os endpoints que retornam um valor único (integrante, função, clube, time) respondem 404; os que retornam mapa de contagem respondem 200 com um mapa vazio.

### Integrante

| Método | Endpoint | Parâmetros | Retorno |
|--|--|--|--|
| POST | `/integrante/cadastro` | corpo JSON `{ nome, funcao }` | Integrante cadastrado |
| GET | `/integrante/consultaIntegrantes` | - | Lista de todos os integrantes cadastrados |
| GET | `/integrante/consultaIntegranteMaisUsado` | `dataInicial`, `dataFinal` | Integrante presente na maior quantidade de times no período |
| GET | `/integrante/consultaIntegrantesDoTimeMaisRecorrente` | `dataInicial`, `dataFinal` | Lista com os nomes dos integrantes do time mais recorrente no período |
| GET | `/integrante/consultaFuncaoMaisRecorrente` | `dataInicial`, `dataFinal` | Função mais recorrente no período |
| GET | `/integrante/consultaContagemFuncao` | `dataInicial`, `dataFinal` | Mapa contando cada integrante uma única vez, mesmo que apareça em vários times |

### Time

| Método | Endpoint | Parâmetros | Retorno |
|--|--|--|--|
| POST | `/time/cadastro` | corpo JSON `{ nomeDoClube, data, integrantesIds }` | Time cadastrado, com a composição |
| GET | `/time/consultaTimeDaData` | `data` | Time daquela data, com sua composição. Se houver mais de um time na mesma data, retorna o de maior ID |
| GET | `/time/consultaClubeMaisRecorrente` | `dataInicial`, `dataFinal` | Nome do clube com mais times cadastrados no período |
| GET | `/time/contagemClubesNoPeriodo` | `dataInicial`, `dataFinal` | Mapa contando a quantidade de aparições de cada clube no período |
