# cv-indexing

### team
- Gabriel OLIVEIRA MARTINS
- Ian RODRIGUES DULEBA

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