# cv-indexing

## build

- dev
```shell script
./mvnw package 
```

- prod
```shell script
./mvnw clean package -Pprod
```

## run
- dev
```shell script
java -jar target/cv-indexing-0.0.1-SNAPSHOT.jar
````

- prod
```shell script
java -jar -Dspring.profiles.active=prod target/cv-indexing-0.0.1-SNAPSHOT.jar
```