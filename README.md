
# Fragrance Tracker – Spring Boot Web Application

A full-stack Spring Boot application for managing personal fragrance collections.  
Users can register, log in, and track perfumes they own, want to buy, or have finished.  
Built with modern Spring Boot, JPA, Thymeleaf, and secure authentication.

---

## Features

### User Accounts & Security
- Register & login with Spring Security
- Password encryption using BCrypt
- User-specific data isolation (each user sees only their perfumes)

### Perfume Collection Management
- Add, edit, delete personal fragrances
- Track **season**, **occasion**, **notes**, and **collection status**
- Search fragrances by name
- List fragrances with clean UI using Thymeleaf + Bootstrap

### REST API (Basic)
- Read-only REST endpoints for perfumes (JSON)
- Located under `/api/perfumes`
- Standard HTTP responses (200 / 201 / 404)

### Database Options
Supports both:
- **H2 in-memory** (default for development)
- **MySQL / MariaDB** (for production deployments)

Uses JPA + Hibernate with automatic schema generation.

---

## Tech Stack

- **Backend**: Spring Boot 3 (Java 17+)
- **Web**: Spring MVC + Thymeleaf + Bootstrap
- **Database**: H2 / MySQL / MariaDB
- **Security**: Spring Security (form login)
- **Build Tool**: Maven

---

# Project Structure

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

## Local development (H2) — safe, no remote DB changes

Use the `dev` profile to run the application with an in-memory H2 database that won't touch your MySQL or other remote databases.

Run locally with Maven (uses the H2 profile):

```bash
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
```

Notes:
- The H2 console will be available at http://localhost:8080/h2-console when the app is running with the `dev` profile.
- The `application-dev.properties` file is intended for local development and should remain uncommitted (it's already ignored by `.gitignore`).
- To explicitly set the profile via environment variable:

```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw -DskipTests spring-boot:run
```

If you want me to run the app locally now with the `dev` profile and confirm the H2 console appears, say "please run dev" and I'll start it for you.

## License

MIT License - See LICENSE file for details

## Contact

- Author: Maab Osman
- GitHub: [fragrance-tracker](https://github.com/maab-osman/fragrance-tracker)

---

**Last Updated**: November 2025  
**Java Version**: 17+  
**Spring Boot Version**: 3.5.7
