
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

## License

MIT License - See LICENSE file for details

## Contact

- Author: Maab Osman
- GitHub: [fragrance-tracker](https://github.com/maab-osman/fragrance-tracker)

---

**Last Updated**: November 2025  
**Java Version**: 17+  
**Spring Boot Version**: 3.5.7
