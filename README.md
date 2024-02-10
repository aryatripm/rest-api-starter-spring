# Spring Boot REST API Starter Kit
This is a starter kit for building RESTful APIs with Spring Boot. It is a simple project that can be used as a starting point for building RESTful APIs. 

## Features
- **Authentication**: JWT based authentication.
- **Authorization**: Role based access control.
- **User Management**: Create, update, delete and get users.
- **Swagger**: API documentation.
- **Unit Testing**: JUnit based unit tests.

## Spring Dependencies
- **Spring Boot**: Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".
- **Spring Data JPA**: Makes it easy to implement JPA based repositories.
- **Spring Security**: Highly customizable authentication and access-control mechanism for most applications.
- **Spring Boot Actuator**: Provides production-ready features to help you monitor and manage your application.
- **Spring Boot DevTools**: Provides fast application restarts, LiveReload, and configurations for enhanced development experience.
- **Spring Boot Test**: Provides a simple way to write tests for your Spring application.

## Requirements
- Java 21
- Maven 3.6.3
- PostgreSQL 11

## Getting Started
### Clone the repository
```bash
git clone https://github.com/aryatripm/rest-api-starter-spring.git
```

### Change directory
```bash
cd rest-api-starter-spring
```

### Build the project
```bash
mvn clean install
```

### Run the project
```bash
mvn spring-boot:run
```

### Access the API documentation
Open your browser and access the following URL:
```
http://localhost:8080/swagger-ui.html
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.