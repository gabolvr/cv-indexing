# cv-indexing

### team
- Gabriel OLIVEIRA MARTINS
- Ian RODRIGUES DULEBA

### pre-requisites
- Run Elasticsearch on port 9200 or having Docker and Docker Compose installed and execute
```shell
docker-compose up -d
```
- Copy `logstash.conf` into `bin/` folder of Logstash and execute
```shell script
./logstash -f logstash.conf
```

### build

- dev
```shell script
./mvnw package 
```

- prod
```shell script
./mvnw clean package -Pprod
```

### run
- dev
```shell script
java -jar target/cv-indexing-1.0.jar
````

- prod (if build was in dev)
```shell script
java -jar -Dspring.profiles.active=prod target/cv-indexing-1.0.jar
```

### api

#### - Add CV

PUT http://localhost:8080/api/cvs

Body : CV file as binary or CV file(s) as value for key "cv" (using Postman)

#### - Search in CV

GET http://localhost:8080/api/cvs?search={KEYWORD}

```shell script
curl -X GET "http://localhost:8080/api/cvs?search=java"
```

#### - Get CV by ID

GET http://localhost:8080/api/cvs/id/{ID}

```shell script
curl -X GET "http://localhost:8080/api/cvs/id/{ID}"
```
