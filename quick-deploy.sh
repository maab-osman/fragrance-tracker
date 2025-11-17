#!/bin/bash
# FAST DEPLOYMENT SCRIPT FOR GITHUB + SOFTALA
# Run these commands one by one and follow the prompts

echo "🚀 FRAGRANCE TRACKER - DEPLOYMENT CHECKLIST"
echo "============================================"
echo ""

# STEP 1: FRONTEND GITHUB
echo "📝 STEP 1: PUSH FRONTEND TO GITHUB"
echo "---"
echo "Before running this:"
echo "  1. Go to https://github.com/new"
echo "  2. Create repo: fragrance-tracker-frontend"
echo "  3. Do NOT initialize with README"
echo "  4. Copy the HTTPS URL from GitHub"
echo ""
read -p "Ready? Press Enter when your GitHub repo is created..."

cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/frontend

# Check if git is initialized
if [ ! -d .git ]; then
    git init
    git config user.email "your-email@example.com"
    git config user.name "Your Name"
fi

# Remove any old remotes and add new one
git remote remove origin 2>/dev/null || true

read -p "Paste the HTTPS URL from GitHub (e.g., https://github.com/USER/fragrance-tracker-frontend.git): " FRONTEND_REPO

git remote add origin "$FRONTEND_REPO"
git add .
git commit -m "React frontend: Fragrance Tracker with Bootstrap UI and API integration" || echo "Already committed"
git branch -M main
git push -u origin main

echo "✅ Frontend pushed to GitHub!"
echo ""

# STEP 2: DEPLOY FRONTEND TO GITHUB PAGES
echo "📱 STEP 2: DEPLOY TO GITHUB PAGES"
echo "---"
echo "Running: npm run deploy"
npm run deploy

GITHUB_USERNAME=$(echo "$FRONTEND_REPO" | sed 's/.*github.com\/\([^\/]*\).*/\1/')
echo "✅ Frontend live at: https://$GITHUB_USERNAME.github.io/fragrance-tracker-frontend"
echo ""

# STEP 3: BACKEND GITHUB
echo "🔧 STEP 3: PUSH BACKEND TO GITHUB"
echo "---"
echo "Before running this:"
echo "  1. Go to https://github.com/new"
echo "  2. Create repo: fragrance-tracker"
echo "  3. Do NOT initialize with README"
echo "  4. Copy the HTTPS URL from GitHub"
echo ""
read -p "Ready? Press Enter when your GitHub repo is created..."

cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker

# Check if git is initialized
if [ ! -d .git ]; then
    git init
    git config user.email "your-email@example.com"
    git config user.name "Your Name"
fi

# Remove any old remotes and add new one
git remote remove origin 2>/dev/null || true

read -p "Paste the HTTPS URL from GitHub (e.g., https://github.com/USER/fragrance-tracker.git): " BACKEND_REPO

git remote add origin "$BACKEND_REPO"
git add .
git commit -m "Grade 5 enhancements: REST API, Testcontainers, Docker, CI/CD, React frontend" || echo "Already committed"
git branch -M main
git push -u origin main

echo "✅ Backend pushed to GitHub!"
echo ""

# STEP 4: SOFTALA INFO
echo "🎯 STEP 4: SOFTALA BACKEND DEPLOYMENT"
echo "---"
echo "Your backend JAR is ready at:"
echo "  /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/target/fragrance-tracker-0.0.1-SNAPSHOT.jar"
echo ""
echo "Ask your teacher for:"
echo "  1. Softala server URL/domain (e.g., https://softala.example.com)"
echo "  2. How to deploy the JAR (SSH, FTP, or platform instructions)"
echo "  3. Database credentials (if needed)"
echo ""

read -p "Enter your Softala backend URL (e.g., https://softala.example.com): " SOFTALA_URL

# Update frontend .env
echo "📝 Updating frontend .env with Softala URL..."
cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/frontend

# Replace the .env
cat > .env << EOF
# Frontend Environment - Production (Softala backend)
REACT_APP_API_URL=$SOFTALA_URL

# Connected to: $SOFTALA_URL
EOF

echo "✅ .env updated!"
echo ""

# STEP 5: REBUILD & REDEPLOY FRONTEND
echo "🔄 STEP 5: REBUILD & REDEPLOY FRONTEND"
echo "---"
echo "Rebuilding with new Softala URL..."
npm run build
npm run deploy

echo "✅ Frontend redeployed with Softala URL!"
echo ""

# FINAL SUMMARY
echo "🎉 DEPLOYMENT COMPLETE!"
echo "============================================"
echo ""
echo "Your project is now live:"
echo "  📱 Frontend: https://$GITHUB_USERNAME.github.io/fragrance-tracker-frontend"
echo "  💾 Backend Code: $BACKEND_REPO"
echo "  🔧 Backend API: $SOFTALA_URL"
echo ""
echo "Next: Deploy the JAR to Softala using your teacher's instructions"
echo ""
echo "JAR location: /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/target/fragrance-tracker-0.0.1-SNAPSHOT.jar"
echo ""
