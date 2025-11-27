# Database Configuration Guide for Fragrance Tracker

## 📋 Overview

The application now supports three deployment profiles with different databases:

| Profile | Database | Use Case | URL |
|---------|----------|----------|-----|
| **dev** (default) | H2 in-memory | Local development | `jdbc:h2:mem:fragrancedb` |
| **prod** | PostgreSQL | Railway deployment | Environment variable `DATABASE_URL` |
| **softala** | MySQL | Softala deployment | `jdbc:mysql://localhost:3306/maab` |

---

##  Deployment Instructions

### **Option 1: Railway (PostgreSQL) - RECOMMENDED**

#### Setup on Railway:

1. **Create a PostgreSQL database plugin:**
   - Go to your Railway project
   - Click "Create" → "Database" → "PostgreSQL"
   - Railway automatically creates a `DATABASE_URL` environment variable

2. **Set environment variables in Railway:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   PORT=8080
   ```

3. **Optional: Set OAuth2 credentials:**
   ```
   GOOGLE_CLIENT_ID=your-google-client-id
   GOOGLE_CLIENT_SECRET=your-google-client-secret
   GITHUB_CLIENT_ID=your-github-client-id
   GITHUB_CLIENT_SECRET=your-github-client-secret
   ```

4. **Deploy:**
   - Push to GitHub: `git push origin main`
   - Railway auto-detects `Dockerfile` and deploys

#### What happens automatically:
- Spring Boot detects `DATABASE_URL` environment variable
- Automatically configures PostgreSQL connection
- Creates tables and indexes on first run
- DevDataLoader seeds admin user and sample perfumes

**Advantages:**
-  No manual database setup needed
- PostgreSQL is more robust than H2
- Free tier includes database
- Easy to scale

---

### **Option 2: Softala (MySQL)**

#### Setup on Softala:

1. **Ensure MySQL is running:**
   - Softala has MySQL at `localhost:3306`
   - Database: `maab`
   - Username: `maab`
   - Password: `password`

2. **Set environment variable:**
   ```bash
   export SPRING_PROFILES_ACTIVE=softala
   ```

3. **Deploy JAR:**
   ```bash
   java -Dspring.profiles.active=softala -jar fragrance-tracker-0.0.1-SNAPSHOT.jar
   ```

#### What happens:
- Connects to existing MySQL database
- Creates/updates tables automatically (`spring.jpa.hibernate.ddl-auto=update`)
- DevDataLoader creates admin user if it doesn't exist

---

### **Option 3: Local Development (H2)**

#### Default behavior (no setup needed):

```bash
mvn spring-boot:run
# or
java -jar target/fragrance-tracker-0.0.1-SNAPSHOT.jar
```

- Uses H2 in-memory database
- Access H2 console at `http://localhost:8080/h2-console`
- Data is reset on each restart (good for testing)

---

## 🔧 Configuration Files

### **application.properties** (Default - DEV)
```properties
spring.profiles.active=dev
spring.datasource.url=jdbc:h2:mem:fragrancedb
```

### **application-prod.properties** (Railway)
```properties
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/fragrance}
spring.datasource.driver-class-name=org.postgresql.Driver
server.port=${PORT:8080}
server.ssl.enabled=false  # Railway handles SSL at edge
```

### **application-softala.properties** (Softala)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/maab?useSSL=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=maab
spring.datasource.password=password
server.port=7074
server.ssl.enabled=true  # Softala has SSL certificate
```

---

## 📊 Database Tables Created

The DevDataLoader and JPA automatically create these tables:

```sql
-- User table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    admin BOOLEAN DEFAULT false,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP
);

-- Perfume table
CREATE TABLE perfume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    description TEXT,
    season VARCHAR(50),
    occasion VARCHAR(50),
    fragrance_notes JSON,
    collection_status VARCHAR(50),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Review table
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP,
    perfume_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (perfume_id) REFERENCES perfume(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Admin Account Created by DevDataLoader

When the app starts, it automatically creates:

```
Username: admin
Email: admin@example.com
Password: admin123
Role: ADMIN
```

This user can access `/admin/catalog` to manage the fragrance catalog.

---

## 🌍 Environment Variables Supported

| Variable | Profile | Required | Example |
|----------|---------|----------|---------|
| `SPRING_PROFILES_ACTIVE` | All | No (defaults to `dev`) | `prod` or `softala` |
| `DATABASE_URL` | prod | Auto-detected by Railway | `postgresql://user:pass@host:5432/db` |
| `PORT` | prod | No (defaults to 8080) | `8080` |
| `DB_USER` | prod | Optional | `postgres` |
| `DB_PASSWORD` | prod | Optional | `password` |
| `GOOGLE_CLIENT_ID` | All | No | `your-google-id.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | All | No | `your-google-secret` |
| `GITHUB_CLIENT_ID` | All | No | `your-github-id` |
| `GITHUB_CLIENT_SECRET` | All | No | `your-github-secret` |

---

## Verification Checklist

After deployment:

1. **Can you log in?**
   - Go to `/login`
   - Try username: `admin`, password: `admin123`
   - Should redirect to `/dashboard`

2. **Is the database persisting data?**
   - Add a perfume to your collection
   - Restart the app
   - Your perfume should still be there (on prod/softala, not on dev)

3. **Can you see catalog perfumes?**
   - Go to `/discover`
   - Should see "Citrus Sunrise", "Nocturne Oud", "Vanilla Dream", etc.
   - These are created by DevDataLoader

4. **Are reviews working?**
   - Click "Details" on a perfume
   - Add a review
   - Should display immediately

---

## Troubleshooting

### **"Connection refused" Error**
**Cause:** Database isn't running or wrong profile active
**Fix:** 
```bash
# Railway: Check database plugin exists
# Softala: Verify MySQL is running
# Local: Ignore (H2 is in-memory)
```

### **"Table doesn't exist" Error**
**Cause:** Hibernate isn't creating tables
**Fix:** Check `spring.jpa.hibernate.ddl-auto=update` in correct profile

### **"No such column: FRAGRANCE_NOTES" Error**
**Cause:** Old database schema
**Fix:** Drop all tables and restart (DevDataLoader will recreate)

### **Admin account not working**
**Cause:** DevDataLoader didn't run or password wrong
**Fix:** 
```bash
# Use password: admin123 (not "admin")
# Or create new user via /register
```

---

## 📝 Summary

| Deployment | Database | Setup | Notes |
|------------|----------|-------|-------|
| **Railway** | PostgreSQL | Automatic | Just set `SPRING_PROFILES_ACTIVE=prod` |
| **Softala** | MySQL | Already exists | Set `SPRING_PROFILES_ACTIVE=softala` |
| **Local** | H2 | None | Default `dev` profile |

**Recommended:** Use Railway with PostgreSQL for the best experience! 
