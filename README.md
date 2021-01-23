# jwt-sample

## Usage via docker-compose

Build war app:

```bash
./gradlew clean bootWar
```

Create local network if needed:

```bash
docker network create purasu-local
```

```bash
docker-compose up --build --force-recreate

# jwt sample login page
open http://localhost:8080/jwt-sample/login
```