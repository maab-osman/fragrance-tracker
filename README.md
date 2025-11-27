
# Fragrance Tracker – Spring Boot Web Application

A simple Spring Boot app to manage personal fragrance collections.

Live site: https://fragrance-tracker-production.up.railway.app

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

### REST API (quick reference)
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

## Local development (H2) — optional

Use the `dev` profile to run with an in-memory H2 database.

Example:
```bash
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 console: http://localhost:8080/h2-console

## License

MIT License - See LICENSE file for details

## Contact

- Author: Maab Osman
- GitHub: [fragrance-tracker](https://github.com/maab-osman/fragrance-tracker)

---

**Last Updated**: November 2025  
**Java Version**: 17+  
**Spring Boot Version**: 3.5.7
