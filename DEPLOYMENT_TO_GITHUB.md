# Deployment Guide: GitHub + Softala

## ⏱️ Timeline: 12 hours to submission

---

## Step 1: Push Frontend to GitHub (5 minutes)

### 1a. Create GitHub Repository for Frontend
1. Go to https://github.com/new
2. Create a new repo called: **`fragrance-tracker-frontend`**
3. Do NOT initialize with README (you'll push existing code)
4. Click "Create repository"

### 1b. Initialize Git and Push Frontend
```bash
cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/frontend

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: React frontend with Bootstrap UI"

# Add remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/fragrance-tracker-frontend.git

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

✅ Your frontend code is now on GitHub!

---

## Step 2: Deploy Frontend to GitHub Pages (3 minutes)

```bash
cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/frontend

# Deploy to GitHub Pages (uses gh-pages package)
npm run deploy
```

**Expected output:**
```
Published branch: gh-pages
```

✅ Your frontend is now live at:
```
https://YOUR_USERNAME.github.io/fragrance-tracker-frontend
```

---

## Step 3: Push Backend to GitHub (5 minutes)

### 3a. Create GitHub Repository for Backend
1. Go to https://github.com/new
2. Create a new repo called: **`fragrance-tracker`**
3. Do NOT initialize with README
4. Click "Create repository"

### 3b. Initialize Git and Push Backend
```bash
cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Grade 5: Spring Boot backend with REST API, Testcontainers, Docker, CI/CD"

# Add remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/fragrance-tracker.git

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

✅ Your backend code is now on GitHub!

---

## Step 4: Deploy Backend JAR to Softala (ask your teacher for exact steps)

Your backend JAR is ready at:
```
/Users/maabeltayeb/backendprogramming2025/fragrance-tracker/target/fragrance-tracker-0.0.1-SNAPSHOT.jar
```

**To deploy to Softala:**
1. Ask your teacher/instructor for:
   - Softala server URL/domain
   - SSH/FTP credentials or deployment method
   - Port number (usually 8080 or 443)
   - Database credentials (if not using H2)

2. Once you have Softala URL, update the frontend `.env`:
   ```
   cd /Users/maabeltayeb/backendprogramming2025/fragrance-tracker/frontend
   
   # Edit .env and update:
   REACT_APP_API_URL=https://your-actual-softala-url.com
   
   # Rebuild and redeploy
   npm run build
   npm run deploy
   ```

---

## Step 5: What Your Teacher Will See

### Live Demo:
- **Frontend URL**: https://YOUR_USERNAME.github.io/fragrance-tracker-frontend
- **Backend Code**: https://github.com/YOUR_USERNAME/fragrance-tracker
- **Deployed Backend**: https://softala-server.com (once deployed)

### In the repos, teacher will see:
✅ Grade 5 features:
- REST API (`/api/perfumes` CRUD endpoints)
- Testcontainers integration tests
- Docker Dockerfile + docker-compose.yml
- GitHub Actions CI/CD workflow
- Professional documentation (README.md, etc.)
- React TypeScript frontend with Bootstrap

✅ Code quality:
- Clean MVC architecture (backend)
- Organized component structure (frontend)
- Proper error handling
- Security config (Spring Security)

---

## Quick Checklist Before Submitting

- [ ] Frontend built successfully (`npm run build` completed)
- [ ] Frontend deployed to GitHub Pages
- [ ] Backend pushed to GitHub
- [ ] Softala URL known and frontend `.env` updated
- [ ] Backend JAR deployment documented or started
- [ ] Both GitHub repos have README.md with instructions
- [ ] Demo URL works (https://YOUR_USERNAME.github.io/fragrance-tracker-frontend)

---

## Troubleshooting

### Frontend won't deploy to GitHub Pages
- Ensure you pushed to `origin main` first
- Check that `gh-pages` is installed: `npm list gh-pages`
- Run `npm run deploy` from the frontend directory

### GitHub Pages showing 404
- Wait 1-2 minutes after deployment
- Check GitHub repo Settings → Pages → check it's set to deploy from `gh-pages` branch

### Frontend loads but can't reach backend
- Check browser DevTools → Network tab for API errors
- Ensure `.env` has the correct Softala URL
- Verify CORS is enabled on backend (already added via `@CrossOrigin`)

---

## After Deployment: Show Teacher

1. **Live URL**: Open https://YOUR_USERNAME.github.io/fragrance-tracker-frontend in browser
2. **GitHub repos**: Show code structure and README
3. **Try registration**: Use the live frontend to register a test user
4. **Swagger docs**: Show backend API at `/swagger-ui.html` (if backend is running)
5. **Docker setup**: Point out the Dockerfile and docker-compose for containerization

---

**Good luck! You've got this! 🚀**
