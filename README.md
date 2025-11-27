
# Fragrance Tracker – Spring Boot Web Application

Manage, explore, and review fragrances with user accounts, admin catalog control, and a discover engine.

## Purpose
Fragrance Tracker is a learning + utility project that solves a real personal need: remembering how each perfume performs, when to wear it, and how others feel about it. Instead of a static spreadsheet, this application provides:

* Personal organization – track seasonality, occasions, descriptive notes, and whether a scent is already in your collection or only in the global catalog.
* Guided discovery – recommended, random, and trending modes help you explore beyond your own list.
* Community-style feedback – lightweight review & rating system (1–5 stars + comments) with aggregation for quick comparison.
* Role separation – normal users curate their private collection; admins maintain a shared catalog and moderate reviews.
* Experimentation space – integrates advanced Spring Boot features (security customization, OAuth2 client readiness, OpenAPI, layered architecture, test suite) beyond the basic lecture material.

The design focuses on clean separation (controller/service/repository), secure update rules (ownership + role checks), and efficient JPA mappings (in-place mutation of note lists) so it can be extended later.

Live site: https://fragrance-tracker-production.up.railway.app

---

## Features

### User Accounts, Roles & Security
- Form login with Spring Security (session-based)
- BCrypt password hashing
- Role-based authorization: `USER` vs `ADMIN`
- Ownership checks: users can only modify their own perfumes
- Admin-only catalog editing & moderation (delete perfumes & reviews)
- CSRF protection on forms; API CSRF relaxed for stateless JSON endpoints
- Thymeleaf security extras for conditional UI rendering


### Perfume Collection & Catalog
- Add, edit, delete personal fragrances (your collection)
- Separate admin-maintained catalog perfumes; users can clone catalog items into their collection
- Track **season**, **occasion**, **notes**, and **collection status**
- Search fragrances by name
- In-place mutation of fragrance notes (efficient JPA `@ElementCollection` updates)
- Clean UI with Thymeleaf + Bootstrap, secured conditional actions

### Discover & Recommendation Engine
- Modes: `recommended`, `random`, `trending`
- Simple recommendation logic + safety fallbacks + average rating aggregation
- Caching-friendly design (stateless controller responses)

### Reviews System
- Users add 1–5 star rating + comment
- Aggregated average rating & total count per perfume
- Admin can moderate (delete reviews)

### Testing & Quality
- MockMvc + Spring Security test coverage (authentication, authorization, redirects, JSON responses)
- Entity serialization safeguards (avoid recursive user->perfumes graphs)
- Separation of concerns: controller/service/repository layers

### REST API (Quick Reference)
- JSON endpoints under `/api/**`. Keep it simple—most actions need you to be logged in.

Public
- GET `/api/discover?mode=recommended|random|trending&limit=8` — list perfumes to explore

Requires login
- POST `/api/collection` — add a catalog perfume to your collection; body: `{ "perfumeId": <id> }`
- GET `/api/perfumes` — your perfumes
- GET `/api/perfumes/{id}` — one perfume by id
- POST `/api/perfumes` — create a perfume you own
- PUT `/api/perfumes/{id}` — update your perfume (admins can update catalog too)
- GET `/api/perfumes/{id}/reviews` — list reviews (avg rating included)
- POST `/api/perfumes/{id}/reviews` — add a review; body: `{ "rating": 1-5, "comment": "..." }`

Admin only
- PUT `/api/admin/catalog/{id}` — update a catalog perfume
- DELETE `/api/perfumes/{id}` — delete a perfume
- DELETE `/api/reviews/{id}` — delete a review

### API Documentation (OpenAPI / Swagger UI)
Included via `springdoc-openapi-starter-webmvc-ui`.
Once enabled (e.g. in production profile):
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

Add (example) to `application.properties` to expose in prod:
```
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

### Database Options
Supports:
- **H2 in-memory** (default for development & tests)
- **MySQL / MariaDB** (runtime connector included)

Currently deployed with **PostgreSQL on Railway**.

PostgreSQL driver already included in `pom.xml`.

`application.properties` sample for MariaDB:
```
spring.datasource.url=jdbc:mysql://YOUR_HOST:3306/fragrances?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASS
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

PostgreSQL sample:
```
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/fragrances
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASS
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

Railway environment variable mapping (PostgreSQL):
```
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}}
spring.datasource.username=${PGUSER}
spring.datasource.password=${PGPASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.datasource.hikari.maximum-pool-size=5
```

Softala profile example (if used):
```
spring.datasource.url=jdbc:postgresql://SOFTALA_HOST:5432/SOFTALA_DB
spring.datasource.username=SOFTALA_USER
spring.datasource.password=SOFTALA_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

Uses JPA + Hibernate with automatic schema generation (`ddl-auto=update`), and efficient `@ElementCollection` for notes.

---

## Tech Stack

- **Backend**: Spring Boot 3 (Java 17+)
- **Web**: Spring MVC + Thymeleaf + Bootstrap
- **Database**: H2 / MySQL / MariaDB
- **Security**: Spring Security (form login) + optional OAuth2 social login
- **Build Tool**: Maven

---

## Project Structure

```
src/main/java/com/maab/fragrance_tracker/
│
├── controller/    # MVC & API controllers
├── model/         # JPA entities
├── repository/    # Spring Data JPA
├── service/       # Business logic
└── config/        # Security configuration

src/main/resources/
│
├── templates/     # Thymeleaf HTML views
├── static/        # CSS, JS, images
└── application.properties (local configs, not committed)
```

## Local Development (H2) – Optional

Use the `dev` profile to run with an in-memory H2 database.

Example:
```bash
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console: http://localhost:8080/h2-console

## Advanced Spring Boot Features (Beyond Lectures)
These illustrate independent learning & wider scope:
- Custom Security configuration (role hierarchy, granular antMatcher rules, CSRF relaxation for APIs)
- OAuth2 Client (social login ready)
- OpenAPI (springdoc) integration for live API docs
- Thymeleaf + Spring Security dialect usage
- JPA `@ElementCollection` for dynamic notes list
- Review aggregation logic & average rating computation
- Recommendation/discover modes with parameterized strategy
- Test suite with MockMvc & Spring Security test utilities



## Running With MariaDB (Example)
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/fragrances
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=secret
./mvnw spring-boot:run
```

## Testing
```bash
./mvnw test
```
Generates reports; covers security constraints, CRUD, discover, and reviews.

## License

MIT License - See LICENSE file for details

## Contact

- Author: Maab Osman
- GitHub: [fragrance-tracker](https://github.com/maab-osman/fragrance-tracker)

---

**Last Updated**: November 2025  
**Java Version**: 17+  
**Spring Boot Version**: 3.5.7
