# CodeLab - API
[Interactive API](https://gql.bruty.net/graphiql)

[Hosted API](https://gql.bruty.net/graphql)
## Navigation
[Website](https://github.com/mbruty/mike-CodeLab-Web)

API

[CodeEngine](https://github.com/mbruty/mike-CodeLab-CodeEngine)

[Docker Scheduler](https://github.com/mbruty/mike-CodeLab-Scheduler)
## Project Vision
Many software developers rely on online coding platforms, such as LeetCode, to enhance their knowledge. These platforms have been successful with students who want to expand their learning beyond what is taught. These platforms could also be helpful in educational institutions to teach software development. However, the current platform’s lack the ability for teachers to create tasks and courses, and they do not provide enough insight into the effects of optimisations on the users' code.

The CodeLab platform aims to bring online coding platforms to the education sector. CodeLab offers the same features as the majority of online coding platforms. CodeLab also offers a way for teachers to create tasks, and group the into modules. For students, CodeLab offers more detailed utilisation statistics than other platform. CodeLab also does not limit usage to only algorithm-style questions and facilitates a wider bredth of teaching.
## Development Setup
Ensure that you have Java version 17 or newer.
 1. Clone the repository
 2. Run `./gradlew build --refresh-dependencies`
 3. Run `java ./build/libs/COMP3000-Graphql-<version number>.jar`
## Setup
Ensure that you have yout GitHub personal-access-token linked to docker.

### Docker
 1. Run `docker pull ghcr.io/mbruty/mike-codelab-graphql/api-image:latest`.
 2. Run `docker run --rm ghcr.io/mbruty/mike-codelab-graphql/api-image:latest`

### Docker Compose
Run `docker pull ghcr.io/mbruty/mike-codelab-graphql/api-image:latest`.
```yaml
api:
  image: ghcr.io/mbruty/mike-codelab-graphql/api-image:latest
  restart: always
  ports:
    - 8080:8080
```

## Technologies uesd
|Name|Version|
|--|--|
|Java|17|
|Kotlin|1.5|
|exposed-core|0.38.2|
|exposed-dao|0.38.2|
|exposed-jdbc|0.38.2|
|exposed-java-time|0.38.2|
|graphql-dgs-platform-dependencies|5.2.2|
|graphql-dgs-spring-boot-starter|5.2.2|
|jackson-module-kotlin|2.15.0|
|jbcrypt|0.4|
|jedis|3.3.0|
|jjwt-api|0.11.5|
|jjwt-impl|0.11.5|
|jjwt-jackson|0.11.5|
|junit-jupiter|5.8.1|
|kotlin-reflect|1.8.20|
| kotlinx-serialization-json | 1.4.0 |
|postgresql|42.5.0|
|testcontainers:postgresql|1.17.6|
|spring-boot-starter-amqp|3.0.6|
|spring-boot-starter-web|3.0.6|
|spring-data-redis|2.3.3|
