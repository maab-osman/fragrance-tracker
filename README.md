# Fragrance Tracker - Spring Boot Application

A feature-rich Spring Boot web application for tracking and managing fragrance/perfume collections with authentication, RESTful API, and cloud-ready deployment.

## Features

✅ **User Authentication & Security**
- Form-based login/register
- OAuth2 social login (Google) - ready to configure
- Spring Security with role-based access control
- SSL/HTTPS support

✅ **Core Functionality**
- CRUD operations for perfumes
- Fragrance note tracking
- Collection status management (OWNED, WISHLIST, FINISHED)
- Review system for perfumes
- User-specific data isolation

✅ **REST API**
- Full RESTful API for perfume management
- Located at `/api/perfumes`
- JSON request/response bodies
- Proper HTTP status codes (200, 201, 204, 404)
- Swagger/OpenAPI documentation at `/swagger-ui.html`

✅ **Database**
- JPA/Hibernate ORM
- Support for H2 (development), MySQL/MariaDB (production)
- Easy database switching via Spring profiles
- Automatic schema creation

✅ **Advanced Features**
- Name-based perfume search
- Pagination support
- Input validation with Jakarta Validation
- Comprehensive error handling

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 17+
- **Build Tool**: Maven
- **Database**: MySQL 8.0 / MariaDB (production), H2 (development)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security 6, OAuth2 Client
- **API Docs**: Springdoc OpenAPI (Swagger UI)
- **Testing**: JUnit 5, Testcontainers, Spring Security Test
- **Web**: Spring MVC, Thymeleaf, Bootstrap 5

## Project Structure

```
fragrance-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/maab/fragrance_tracker/
│   │   │       ├── controller/           # MVC & REST controllers
│   │   │       │   ├── HomeController.java
│   │   │       │   ├── PerfumeController.java    # MVC endpoints
│   │   │       │   └── PerfumeRestController.java # REST API
│   │   │       ├── service/              # Business logic
│   │   │       ├── model/                # JPA entities
│   │   │       ├── repository/           # Data access
│   │   │       └── config/               # Security & app config
│   │   └── resources/
│   │       ├── application.properties     # Production config (MariaDB)
│   │       ├── application-dev.properties # Development config (H2)
│   │       ├── templates/                # Thymeleaf views
│   │       │   ├── login.html
│   │       │   ├── register.html
│   │       │   ├── dashboard.html
│   │       │   └── perfumes/
│   │       │       ├── list.html
│   │       │       ├── add.html
│   │       │       └── edit.html
│   │       └── static/                   # CSS, JS, images
│   └── test/
│       └── java/
│           └── com/maab/fragrance_tracker/ # Integration & unit tests
├── pom.xml
├── mvnw
└── README.md
```

## Quick Start

### Prerequisites

- **Java 17+** (tested on Java 23)
- **Maven 3.8+** (included: `./mvnw`)
- **MySQL 8.0** or **MariaDB 10.5+** (optional for production)
- **Docker** (optional for running database in container)

### Local Development (H2 In-Memory Database)

1. **Clone and navigate to project**:
   ```bash
   cd fragrance-tracker
   ```

2. **Run with development profile** (uses H2 in-memory):
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```
   Or set the profile in your IDE.

3. **Open browser**:
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

4. **Test Credentials**:
   - Username: testuser
   - Password: password123
   (Or register a new account)

### Production Setup (MariaDB/MySQL)

1. **Start MariaDB** (using Docker):
   ```bash
   docker run -d \
     --name fragrance-mariadb \
     -e MYSQL_DATABASE=fragrancedb \
     -e MYSQL_USER=maab \
     -e MYSQL_PASSWORD=password \
     -e MYSQL_ROOT_PASSWORD=root \
     -p 3306:3306 \
     mariadb:10.5
   ```

2. **Build and run** (uses `application.properties` with MariaDB):
   ```bash
   ./mvnw clean package
   java -jar target/fragrance-tracker-0.0.1-SNAPSHOT.jar
   ```
   
   Or run without production profile (will connect to MariaDB on localhost:3306):
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access application**:
   - HTTP: http://localhost:8080
   - HTTPS: https://localhost:8443 (with SSL enabled)

## REST API Documentation

### Base URL
- Development: `http://localhost:8080/api/perfumes`
- Production: `https://localhost:8443/api/perfumes`

### Endpoints

All endpoints require authentication (login or OAuth2).

#### Get all perfumes
```http
GET /api/perfumes
Content-Type: application/json

Response: 200 OK
[
  {
    "id": 1,
    "name": "Eau de Cologne",
    "brand": "Guerlain",
    "season": "SPRING",
    "occasion": "DAY",
    "fragranceNotes": ["Bergamot", "Lemon"],
    "description": "Fresh citrus fragrance",
    "collectionStatus": "OWNED"
  }
]
```

#### Get perfume by ID
```http
GET /api/perfumes/{id}
Response: 200 OK or 404 Not Found
```

#### Create perfume
```http
POST /api/perfumes
Content-Type: application/json

{
  "name": "Joséphine",
  "brand": "Balmain",
  "season": "FALL",
  "occasion": "FORMAL",
  "fragranceNotes": ["Rose", "Vanilla"],
  "description": "Elegant oriental fragrance",
  "collectionStatus": "OWNED"
}

Response: 201 Created
```

#### Update perfume
```http
PUT /api/perfumes/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  ...
}

Response: 200 OK or 404 Not Found
```

#### Delete perfume
```http
DELETE /api/perfumes/{id}
Response: 204 No Content or 404 Not Found
```

#### Search by name
```http
GET /api/perfumes/search?name=Cologne
Response: 200 OK
[...perfumes matching 'Cologne'...]
```

### API Documentation (Swagger UI)

Auto-generated API docs available at:
- Development: http://localhost:8080/swagger-ui.html
- Production: https://localhost:8443/swagger-ui.html

## OAuth2 Social Login Setup (Optional)

To enable Google OAuth2 login:

1. **Create Google OAuth2 credentials**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a project
   - Enable OAuth2 Consent Screen
   - Create OAuth2 Credentials (Web Application)
   - Set Authorized Redirect URI: `http://localhost:8080/login/oauth2/code/google` (or production URL)

2. **Add to `application.properties`**:
   ```properties
   spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
   spring.security.oauth2.client.registration.google.scope=profile,email
   ```

3. **Or set environment variables**:
   ```bash
   export GOOGLE_CLIENT_ID=YOUR_CLIENT_ID
   export GOOGLE_CLIENT_SECRET=YOUR_CLIENT_SECRET
   ```

4. **Access login page** and select "Sign in with Google"

## Running Tests

### Unit & Integration Tests
```bash
./mvnw test
```

### Integration Tests with Testcontainers (MySQL in Docker)
```bash
./mvnw test -Dgroups=integration
```

### Build with Tests
```bash
./mvnw clean package
```

### Skip Tests (faster build)
```bash
./mvnw clean package -DskipTests
```

## Build & Deployment

### Build JAR
```bash
./mvnw clean package
```

### Run JAR
```bash
java -jar target/fragrance-tracker-0.0.1-SNAPSHOT.jar
```

### Deploy to Cloud (Examples)

#### Render
1. Push code to GitHub
2. Create new Web Service on Render
3. Set environment variables (MariaDB connection, OAuth2 credentials)
4. Deploy

#### Railway
1. Install Railway CLI: `npm i -g @railway/cli`
2. Run: `railway init`
3. Add MariaDB plugin
4. Configure environment variables
5. Deploy: `railway up`

#### Fly.io
1. Install Fly CLI: `curl https://fly.io/install.sh | sh`
2. Run: `fly auth login`
3. Run: `fly launch`
4. Configure `fly.toml` with resources
5. Deploy: `fly deploy`

## Environment Variables (Production)

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://db-host:3306/fragrancedb
SPRING_DATASOURCE_USERNAME=maab
SPRING_DATASOURCE_PASSWORD=<secure_password>

# OAuth2 (optional)
GOOGLE_CLIENT_ID=<your_google_client_id>
GOOGLE_CLIENT_SECRET=<your_google_client_secret>

# SSL/HTTPS
SERVER_SSL_ENABLED=true
SERVER_SSL_KEY_STORE=classpath:keystore.p12
SERVER_SSL_KEY_STORE_PASSWORD=<keystore_password>

# Server
SERVER_PORT=8443
```

## Key Classes

- `PerfumeController.java` - Web UI endpoints
- `PerfumeRestController.java` - REST API endpoints
- `PerfumeService.java` - Business logic
- `PerfumeRepository.java` - Data persistence
- `SecurityConfig.java` - Security configuration
- `Perfume.java` - JPA entity model

## Notable Features Implemented Beyond Lectures

1. **OAuth2 Social Login** - Google authentication
2. **RESTful API** - Full CRUD REST endpoints
3. **OpenAPI/Swagger** - Auto-generated API documentation
4. **Testcontainers** - Production-like MySQL integration tests
5. **Multiple Database Support** - H2, MySQL/MariaDB switching
6. **Spring Profiles** - dev, prod configuration
7. **Validation & Error Handling** - Jakarta validation + custom 404 pages
8. **Advanced Queries** - Name-based search, user-scoped queries

## Troubleshooting

### Application won't start (MariaDB)
- Ensure MariaDB is running: `docker ps`
- Check credentials in `application.properties`
- Verify database exists: `CREATE DATABASE fragrancedb IF NOT EXISTS;`

### OAuth2 login fails
- Verify client ID/secret in Google Cloud Console
- Ensure redirect URI is registered correctly
- Check browser cookies/cache

### Tests fail with Testcontainers
- Ensure Docker daemon is running
- Check Docker image availability (auto-downloads ~600MB)
- See test logs: `./mvnw test -X`

### SSL certificate errors
- For development, disable SSL in `application-dev.properties`: `server.ssl.enabled=false`
- Update keystore password in properties if changed

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

MIT License - See LICENSE file for details

## Contact

- Author: Maab Eltayeb
- Email: maab@example.com
- GitHub: [fragrance-tracker](https://github.com/yourusername/fragrance-tracker)

---

**Last Updated**: November 2025  
**Java Version**: 17+  
**Spring Boot Version**: 3.5.7
