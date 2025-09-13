# HomeTail

HomeTail is a Java-based web application for managing and finding homes for animals. It's built with Spring Boot 3.5.3 and provides a RESTful API for managing animal profiles, user accounts, and adoption requests.

## Prerequisites

- Java 21 (as specified in pom.xml)
- Maven 3.8.0 or higher
- MySQL 8.0 or higher
- Docker (optional, for containerized deployment)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/hometail.git
   cd hometail
   ```

2. **Database Setup**
   - Create a MySQL database named `hometail`
   - Update the database configuration in `src/main/resources/application.properties` with your database credentials
   - The application uses JPA for ORM, and tables will be created automatically on startup

3. **Build the application**
   ```bash
   ./mvnw clean install
   ```

## Configuration

The main configuration file is located at `src/main/resources/application.properties`. Key configurations include:

- Database connection settings
- JWT token settings for authentication
- File upload settings (for animal images)
- Server port (default: 9090)

To configure for different environments, you can use Spring profiles by creating:
- `application-dev.properties` for development
- `application-prod.properties` for production

## Running the Application

### Development Mode

```bash
./mvnw spring-boot:run
```

The application will be available at: http://localhost:9090

### Production Mode

```bash
./mvnw clean package -Pprod
java -jar target/hometail-0.0.1-SNAPSHOT.jar
```

### Using Docker

1. Build the Docker image:
   ```bash
   docker-compose build
   ```

2. Start the containers:
   ```bash
   docker-compose up -d
   ```

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: http://localhost:9090/swagger-ui.html
- API Docs (JSON): http://localhost:9090/v3/api-docs

## Project Structure

```
src/main/java/com/hometail/
├── config/         # Configuration classes (JWT, Security, etc.)
├── controller/     # REST controllers (Animal, User, Auth, etc.)
├── dto/            # Data Transfer Objects
├── exception/      # Custom exception handling
├── mapper/         # Object mappers (MapStruct)
├── model/          # JPA entities
├── repository/     # Spring Data JPA repositories
├── security/       # Security configuration and JWT
├── service/        # Business logic implementation
│   └── impl/       # Service implementations
└── spec/           # Specifications for JPA criteria queries

src/main/resources/
├── static/         # Static files
├── templates/      # Thymeleaf templates (if any)
└── application.properties  # Application configuration
```

## Key Features

- JWT-based authentication
- RESTful API endpoints
- File upload handling for animal images
- Role-based access control
- OpenAPI/Swagger documentation
- Exception handling with custom error responses
- Data validation using Jakarta Bean Validation
```

## Development

### Code Style
- Follow Google Java Style Guide
- Use meaningful commit messages
- Write unit tests for new features

### API Documentation

The API is documented using SpringDoc OpenAPI. After starting the application, access:
- Swagger UI: http://localhost:9090/swagger-ui.html
- OpenAPI JSON: http://localhost:9090/v3/api-docs

### Database Migrations
- The application uses JPA's auto-ddl for schema management
- For production, consider using Flyway or Liquibase

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Message Convention
- `feat:` for new features
- `fix:` for bug fixes
- `docs:` for documentation changes
- `refactor:` for code refactoring
- `test:` for test related changes
- `chore:` for build and dependency updates

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
