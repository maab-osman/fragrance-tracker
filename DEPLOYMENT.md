# Deployment Guide for Fragrance Tracker

This guide covers deploying the Fragrance Tracker application to production environments with MariaDB database.

## Table of Contents
1. [Local Docker Deployment](#local-docker-deployment)
2. [Cloud Deployment (Render)](#cloud-deployment-render)
3. [Cloud Deployment (Railway)](#cloud-deployment-railway)
4. [Cloud Deployment (Fly.io)](#cloud-deployment-flyio)
5. [Manual VPS Deployment](#manual-vps-deployment)
6. [Environment Configuration](#environment-configuration)

---

## Local Docker Deployment

Perfect for testing production-like environment locally.

### Prerequisites
- Docker and Docker Compose installed
- Git (for cloning repository)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/fragrance-tracker.git
   cd fragrance-tracker
   ```

2. **Start application stack**
   ```bash
   docker-compose up --build
   ```
   
   This will:
   - Build the Spring Boot Docker image
   - Start MariaDB container
   - Start the application container
   - Expose ports 8080 (HTTP) and 8443 (HTTPS)

3. **Access application**
   - Web UI: http://localhost:8080
   - HTTPS: https://localhost:8443 (self-signed certificate)
   - H2 Console (dev mode): http://localhost:8080/h2-console

4. **View logs**
   ```bash
   docker-compose logs -f fragrance-tracker
   ```

5. **Stop containers**
   ```bash
   docker-compose down
   ```

---

## Cloud Deployment (Render)

Render is free for simple deployments with postgreSQL/MySQL support.

### Prerequisites
- GitHub account and repository
- Render account (free at https://render.com)

### Steps

1. **Push code to GitHub**
   ```bash
   git remote add origin https://github.com/yourusername/fragrance-tracker.git
   git branch -M main
   git push -u origin main
   ```

2. **Create Web Service on Render**
   - Go to https://dashboard.render.com
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select branch: `main`

3. **Configure Web Service**
   - **Name**: `fragrance-tracker`
   - **Environment**: `Docker`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/fragrance-tracker-0.0.1-SNAPSHOT.jar`
   - **Instance Type**: Free or Starter (5 slots free)

4. **Add Database**
   - Go to "Databases" → "New +"
   - Create MySQL database
   - Note connection details (JDBC URL, username, password)

5. **Set Environment Variables**
   In Web Service settings → "Environment":
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=jdbc:mysql://<db-host>:3306/fragrancedb
   SPRING_DATASOURCE_USERNAME=<db-username>
   SPRING_DATASOURCE_PASSWORD=<db-password>
   GOOGLE_CLIENT_ID=<your-google-oauth2-client-id>
   GOOGLE_CLIENT_SECRET=<your-google-oauth2-secret>
   ```

6. **Deploy**
   - Render auto-deploys on push to `main` branch
   - View deployment logs in Render dashboard

7. **Access Application**
   - Your URL will be: `https://fragrance-tracker.onrender.com`

---

## Cloud Deployment (Railway)

Railway offers free tier with GitHub integration.

### Prerequisites
- GitHub account
- Railway account (free tier available)
- Railway CLI installed (optional)

### Steps

1. **Using Railway Dashboard**
   - Go to https://railway.app
   - Click "New Project"
   - Choose "Deploy from GitHub"
   - Select your `fragrance-tracker` repository
   - Authorize Railway to access GitHub

2. **Add MySQL Database**
   - In project dashboard, click "Add Services"
   - Select "MySQL"
   - Railway auto-configures connection variables

3. **Configure Application**
   - In "Variables" section, add:
   ```
   SPRING_PROFILES_ACTIVE=prod
   SPRING_DATASOURCE_URL=${{MySQL.DATABASE_URL}}
   GOOGLE_CLIENT_ID=<your-client-id>
   GOOGLE_CLIENT_SECRET=<your-secret>
   ```

4. **Deploy**
   - Railway auto-deploys on push
   - Check deployment status in dashboard

5. **Access Application**
   - Railway generates public URL automatically
   - View in "Deployments" tab

---

## Cloud Deployment (Fly.io)

Fly.io offers simple deployment with geographic distribution.

### Prerequisites
- Fly.io account (free tier available)
- Fly CLI installed: `curl https://fly.io/install.sh | sh`
- GitHub repository with code

### Steps

1. **Authenticate with Fly**
   ```bash
   flyctl auth login
   ```

2. **Initialize Fly app**
   ```bash
   cd fragrance-tracker
   flyctl launch
   ```
   
   When prompted:
   - **App Name**: `fragrance-tracker` (or custom)
   - **Region**: Select closest to you
   - **Database**: Choose "No" (we'll use managed MySQL)

3. **Create MySQL Database (Fly Postgres)**
   ```bash
   flyctl mysql create
   # Or use external managed MySQL (e.g., AWS RDS, PlanetScale)
   ```

4. **Configure `fly.toml`**
   ```toml
   app = "fragrance-tracker"
   primary_region = "sfo"

   [build]
   dockerfile = "Dockerfile"

   [env]
   SPRING_PROFILES_ACTIVE = "prod"

   [[services]]
   protocol = "tcp"
   internal_port = 8080
   processes = ["app"]

   [[services.ports]]
   port = 80
   handlers = ["http"]
   force_https = true

   [[services.ports]]
   port = 443
   handlers = ["tls", "http"]
   ```

5. **Set Environment Variables**
   ```bash
   flyctl secrets set \
     SPRING_DATASOURCE_URL="jdbc:mysql://..." \
     SPRING_DATASOURCE_USERNAME="..." \
     SPRING_DATASOURCE_PASSWORD="..." \
     GOOGLE_CLIENT_ID="..." \
     GOOGLE_CLIENT_SECRET="..."
   ```

6. **Deploy**
   ```bash
   flyctl deploy
   ```

7. **Access Application**
   ```bash
   flyctl open
   ```

---

## Manual VPS Deployment

For AWS EC2, DigitalOcean, Linode, or other VPS providers.

### Prerequisites
- VPS with Ubuntu 20.04+ or similar
- SSH access to VPS
- Domain name (optional, for DNS)

### Steps

1. **Connect to VPS**
   ```bash
   ssh root@your-vps-ip
   ```

2. **Install Java & MySQL**
   ```bash
   # Update system
   apt update && apt upgrade -y
   
   # Install Java 17
   apt install -y openjdk-17-jdk
   
   # Install MariaDB
   apt install -y mariadb-server mariadb-client
   
   # Start MariaDB
   systemctl start mariadb
   systemctl enable mariadb
   ```

3. **Configure MariaDB**
   ```bash
   # Secure installation
   mysql_secure_installation
   
   # Create database and user
   mysql -u root -p
   ```
   
   In MySQL prompt:
   ```sql
   CREATE DATABASE fragrancedb;
   CREATE USER 'maab'@'localhost' IDENTIFIED BY 'strong_password';
   GRANT ALL PRIVILEGES ON fragrancedb.* TO 'maab'@'localhost';
   FLUSH PRIVILEGES;
   EXIT;
   ```

4. **Clone and build application**
   ```bash
   cd /opt
   git clone https://github.com/yourusername/fragrance-tracker.git
   cd fragrance-tracker
   ./mvnw clean package
   ```

5. **Create systemd service**
   ```bash
   sudo nano /etc/systemd/system/fragrance-tracker.service
   ```
   
   Content:
   ```ini
   [Unit]
   Description=Fragrance Tracker Spring Boot Application
   After=network.target

   [Service]
   Type=simple
   User=root
   WorkingDirectory=/opt/fragrance-tracker
   ExecStart=/usr/bin/java -jar /opt/fragrance-tracker/target/fragrance-tracker-0.0.1-SNAPSHOT.jar \
     --spring.datasource.url=jdbc:mysql://localhost:3306/fragrancedb \
     --spring.datasource.username=maab \
     --spring.datasource.password=strong_password
   SuccessExitStatus=143
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```

6. **Enable and start service**
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable fragrance-tracker
   sudo systemctl start fragrance-tracker
   sudo systemctl status fragrance-tracker
   ```

7. **Configure reverse proxy (Nginx)**
   ```bash
   apt install -y nginx
   sudo nano /etc/nginx/sites-available/default
   ```
   
   Content:
   ```nginx
   server {
       listen 80;
       server_name yourdomain.com;

       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

   Enable and test:
   ```bash
   sudo systemctl restart nginx
   sudo nginx -t
   ```

8. **Set up HTTPS (Let's Encrypt)**
   ```bash
   apt install -y certbot python3-certbot-nginx
   sudo certbot --nginx -d yourdomain.com
   ```

9. **Check logs**
   ```bash
   sudo journalctl -u fragrance-tracker -f
   ```

---

## Environment Configuration

### Production (application.properties)
```properties
spring.application.name=fragrance-tracker

# MariaDB Configuration
spring.datasource.url=jdbc:mysql://db-host:3306/fragrancedb?useSSL=true&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=maab
spring.datasource.password=<secure-password>

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server & SSL
server.ssl.enabled=true
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<keystore-password>

# OAuth2 (optional)
spring.security.oauth2.client.registration.google.client-id=<your-client-id>
spring.security.oauth2.client.registration.google.client-secret=<your-secret>
spring.security.oauth2.client.registration.google.redirect-uri=https://yourdomain.com/login/oauth2/code/google
```

### Performance Tuning
```properties
# Connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Caching (optional)
spring.cache.type=simple

# Logging
logging.level.root=INFO
logging.level.com.maab=DEBUG
```

---

## Monitoring & Logs

### Docker
```bash
docker logs -f fragrance-tracker
```

### Systemd
```bash
journalctl -u fragrance-tracker -f
tail -f /var/log/syslog | grep fragrance-tracker
```

### Application Logs
Logs are typically written to stdout and captured by the container or systemd.

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused (MariaDB) | Ensure MariaDB is running and port 3306 is accessible |
| SSL certificate error | Update `keystore.p12` or disable SSL in dev (`server.ssl.enabled=false`) |
| Out of memory | Increase JVM heap: `java -Xmx512m -jar application.jar` |
| OAuth2 not working | Verify client ID/secret and redirect URI in Google Console |
| Database migration failed | Check logs for SQL errors; ensure user has CREATE/ALTER permissions |

---

## Backup & Maintenance

### Database Backup
```bash
mysqldump -u maab -p fragrancedb > backup-$(date +%Y%m%d).sql
```

### Database Restore
```bash
mysql -u maab -p fragrancedb < backup-20231115.sql
```

### Application Updates
```bash
cd /opt/fragrance-tracker
git pull origin main
./mvnw clean package
sudo systemctl restart fragrance-tracker
```

---

## Next Steps

1. Configure custom domain and HTTPS
2. Set up monitoring (e.g., Uptime Robot)
3. Configure backups (database and code)
4. Set up CI/CD for automatic deployments
5. Monitor application logs and errors

For questions, refer to [README.md](README.md) or [official Spring Boot documentation](https://spring.io/projects/spring-boot).
