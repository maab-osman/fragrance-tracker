#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BACKUP="/tmp/fragrance-tracker-backup-$(date +%Y%m%dT%H%M%S).tar.gz"

echo "Creating repository backup at $BACKUP"
cd "$REPO_ROOT"
tar -czf "$BACKUP" .

echo "Removing sensitive files and unneeded docs (local working tree)"
FILES_TO_REMOVE=(
  "DEPLOYMENT.md"
  "DEPLOYMENT_TO_GITHUB.md"
  "FRONTEND_SETUP.md"
  "HELP.md"
  "NEXT_STEPS.md"
  "OAUTH2_SETUP.md"
  "REACT_FRONTEND_COMPLETE.md"
  "README.md"
  "SUBMISSION_GUIDE.md"
  "src/main/resources/application.properties"
  "src/main/resources/application-prod.properties"
  "src/main/resources/keystore.p12"
)

for f in "${FILES_TO_REMOVE[@]}"; do
  if [ -e "$f" ]; then
    echo "Removing $f"
    git rm -f "$f" || rm -f "$f" || true
  else
    echo "Not found: $f"
  fi
done

# Add templates for properties if not present
TEMPLATE_DIR="src/main/resources"
mkdir -p "$TEMPLATE_DIR"

cat > "$TEMPLATE_DIR/application-prod.properties.template" <<'EOF'
spring.application.name=fragrance-tracker

# Production database configuration (DO NOT commit real credentials)
# Replace values with environment variables or a secure secrets store before use.
spring.datasource.url=jdbc:mysql://<DB_HOST>:3306/<DB_NAME>?useSSL=true&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

server.ssl.enabled=true
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=springboot

spring.web.resources.static-locations=file:${user.home}/le-webroot/,classpath:/static/
EOF

cat > "$TEMPLATE_DIR/application.properties.template" <<'EOF'
spring.application.name=fragrance-tracker

# Placeholder configuration. Use environment variables or profile-specific properties for dev/prod.

server.ssl.enabled=false
server.port=8080
EOF

# Ensure .gitignore contains entries to prevent re-adding
GITIGNORE_FILE=".gitignore"
for entry in "src/main/resources/keystore.p12" "src/main/resources/application-prod.properties" "src/main/resources/application.properties"; do
  if ! grep -qxF "$entry" "$GITIGNORE_FILE" 2>/dev/null; then
    echo "$entry" >> "$GITIGNORE_FILE"
    echo "Added $entry to .gitignore"
  fi
done

# Commit changes locally
if git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
  git add .gitignore src/main/resources/application-prod.properties.template src/main/resources/application.properties.template || true
  if git diff --staged --quiet; then
    echo "No changes staged to commit."
  else
    git commit -m "chore: remove docs and untrack sensitive files; add property templates"
    echo "Committed cleanup changes locally."
  fi
else
  echo "Not a git repository; files removed but no commit performed."
fi

echo "Done. Backup at $BACKUP"
