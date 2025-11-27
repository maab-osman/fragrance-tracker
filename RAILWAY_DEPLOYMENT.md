# Railway Deployment Guide

## Fixed Issues
- ✅ Mixed Content security error (HTTP redirect on HTTPS page) - **NOW FIXED**
- ✅ Admin catalog edit form 500 error - **NOW FIXED**

## Deployment Steps

### 1. Set the Spring Profile to `railway`

On Railway dashboard, add this environment variable to your project:

```
SPRING_PROFILES_ACTIVE=railway
```

This activates the `application-railway.properties` configuration which:
- Trusts `X-Forwarded-Proto` header from the Railway reverse proxy
- Ensures redirects use HTTPS instead of HTTP
- Fixes form submissions to redirect properly

### 2. Database Configuration

The `application-railway.properties` includes the `prod` profile automatically, which expects:

```
DATABASE_URL=postgresql://user:password@host:port/database
```

Railway automatically sets this environment variable when you add a PostgreSQL plugin.

### 3. Verify Deployment

After deploying with these settings:

1. Go to your Railway app URL (e.g., `https://fragrance-tracker-production.up.railway.app`)
2. Login as admin
3. Try editing a perfume in the catalog
4. Form should submit successfully (no 500 error)
5. Should redirect to the catalog view with success message

### 4. Troubleshooting

**Still getting Mixed Content error?**
- Verify `SPRING_PROFILES_ACTIVE=railway` is set
- Check Railway logs for the environment variable
- Restart the deployment after setting the variable

**Form still returns 500 error?**
- Check Railway logs with: `railway logs`
- Look for stack traces starting with `[ERROR] editCatalogPerfume()`
- Report the full error message

### 5. Alternative: Manual Configuration

If using an environment-based deployment, add these properties directly:

```
SERVER_TOMCAT_REMOTEIP_REMOTE_IP_HEADER=X-Forwarded-For
SERVER_TOMCAT_REMOTEIP_PROTOCOL_HEADER=X-Forwarded-Proto
SERVER_TOMCAT_REMOTEIP_PROTOCOL_HEADER_HTTPS_VALUE=https
```

Note: Property names use underscores and uppercase for environment variables.
Spring Boot automatically converts them to dotted properties.

## Files Modified

- `src/main/resources/application-railway.properties` - Railway-specific Spring profile with proxy header configuration
- `src/main/resources/application.properties` - Development profile (localhost)
- `src/main/java/com/maab/fragrance_tracker/controller/AdminController.java` - Fixed edit form submission
- `src/main/resources/admin-discover.html` - Fixed form submission method
- `src/main/resources/static/js/admin-discover.js` - Fixed form encoding from FormData to URLSearchParams

## What Each Fix Does

| File | Issue Fixed |
|------|------------|
| `application-railway.properties` | HTTP redirects on HTTPS causing Mixed Content error |
| `AdminController.java` | @ElementCollection handling causing NullPointerException |
| `admin-discover.js` | Form data not being parsed correctly (FormData vs URLSearchParams) |
| `admin-discover.html` | Missing CSRF token meta tags |

## Testing Locally

To test the Railway configuration locally:

```bash
export SPRING_PROFILES_ACTIVE=railway
mvn spring-boot:run -DskipTests
```

Then go to `http://localhost:8080` and test the admin form.
