##  Online Banking System with JWT Authentication

## User Registration, User Login and Authorization process.

## Spring Boot Server Architecture with Spring Security

## Dependency

```
MySQL:
```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```
## Configure Spring Datasource, JPA, App properties
Open `src/main/resources/application.properties`
- For PostgreSQL:
```
spring.datasource.url= jdbc:postgresql://localhost:5432/testdb
spring.datasource.username= postgres
spring.datasource.password= (Your Db Password)

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto= update

# App Properties
excelrbank.app.jwtSecret= excelrbankSecretKey
excelrbank.app.jwtExpirationMs= 86400000(1 day in milliseconds)
```
- For MySQL
```
spring.datasource.url=jdbc:mysql://localhost:3306/online_bankingfinal?useSSL=false
spring.datasource.username=root
spring.datasource.password=(Your DB Password)

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

# App Properties
excelrbank.app.jwtSecret= ======================excelrbank=Spring===========================
excelrbank.app.jwtExpirationMs=86400000
```
## Run Spring Boot application
```
mvn spring-boot:run
```

## Run following SQL insert statements
```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```


