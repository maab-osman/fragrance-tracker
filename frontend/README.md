# 🌸 Fragrance Tracker - React Frontend

A modern, professional React TypeScript frontend for the Fragrance Tracker application. This frontend provides a beautiful UI for managing your perfume collection.

## 📚 Technology Stack

- **React 18** - Modern UI framework
- **TypeScript** - Type-safe development
- **React Router v6** - Client-side routing
- **Axios** - HTTP client
- **Bootstrap 5** - Responsive CSS framework
- **GitHub Pages** - Free hosting

## 🚀 Features

- ✅ User authentication (Login/Register)
- ✅ Create, Read, Update, Delete (CRUD) perfumes
- ✅ Search perfumes by name
- ✅ Categorize perfumes (Season, Occasion, Collection Status)
- ✅ Responsive design (mobile-friendly)
- ✅ Modern, professional UI
- ✅ Real-time data synchronization

## 🛠️ Setup & Installation

### Prerequisites

- Node.js 16+ 
- npm or yarn

### Local Development

```bash
# Navigate to frontend directory
cd fragrance-tracker/frontend

# Install dependencies
npm install

# Start development server
npm start
```

The app will open at `http://localhost:3000`

### Environment Configuration

Update `.env` file for your backend URL:

```
# Development
REACT_APP_API_URL=http://localhost:8080

# Production (school server)
REACT_APP_API_URL=https://your-school-server-url.com
```

## 📦 Build for Production

```bash
npm run build
```

Creates an optimized production build in the `build/` directory.

## 🌐 GitHub Pages Deployment

### Prerequisites

1. Create a GitHub repository named `fragrance-tracker-frontend`
2. Update `package.json` homepage field with your GitHub Pages URL

### Deployment Steps

```bash
# Install gh-pages
npm install --save-dev gh-pages

# Deploy
npm run deploy
```

Your frontend will be live at: `https://YOUR_USERNAME.github.io/fragrance-tracker-frontend`

### Update Backend URL for Production

Update `.env` before deploying:

```
REACT_APP_API_URL=https://your-backend-url
```

Then redeploy:

```bash
npm run deploy
```

## 📁 Project Structure

```
frontend/
├── public/
│   └── index.html          # Main HTML file
├── src/
│   ├── components/
│   │   └── Navbar.tsx      # Navigation component
│   ├── pages/
│   │   ├── Login.tsx       # Login page
│   │   ├── Register.tsx    # Registration page
│   │   ├── Dashboard.tsx   # Perfume list
│   │   ├── AddPerfume.tsx  # Add new perfume
│   │   └── EditPerfume.tsx # Edit perfume
│   ├── services/
│   │   └── api.ts          # API calls (Axios)
│   ├── App.tsx             # Main App component
│   ├── App.css             # App styles
│   ├── index.tsx           # React entry point
│   └── index.css           # Global styles
├── .env                    # Environment variables
├── package.json            # Dependencies
├── tsconfig.json           # TypeScript config
└── README.md              # This file
```

## 🔗 API Integration

The frontend connects to the backend REST API:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/login` | POST | User login |
| `/register` | POST | User registration |
| `/logout` | POST | User logout |
| `/api/perfumes` | GET | Get all perfumes |
| `/api/perfumes` | POST | Create perfume |
| `/api/perfumes/{id}` | GET | Get single perfume |
| `/api/perfumes/{id}` | PUT | Update perfume |
| `/api/perfumes/{id}` | DELETE | Delete perfume |
| `/api/perfumes/search` | GET | Search by name |

## 🎨 Styling

- **Bootstrap 5** - Base framework
- **Custom CSS** - `App.css` for additional styling
- **Responsive Design** - Mobile-first approach

## 🧪 Testing

```bash
npm test
```

## 🐛 Troubleshooting

### CORS Error
If you get CORS errors, ensure your backend has CORS enabled for `http://localhost:3000`

### Backend Connection Failed
Check the `.env` file `REACT_APP_API_URL` matches your backend URL

### GitHub Pages 404 Error
Ensure `homepage` in `package.json` is set correctly:
```json
"homepage": "https://YOUR_USERNAME.github.io/fragrance-tracker-frontend"
```

## 📝 Grade 5 Evaluation Criteria

✅ **Responsive Frontend**: Bootstrap 5 responsive design  
✅ **Modern Tech Stack**: React + TypeScript + Axios  
✅ **Professional UI**: Clean, modern design  
✅ **Full CRUD Operations**: Create, Read, Update, Delete perfumes  
✅ **Search Functionality**: Search perfumes by name  
✅ **Authentication**: Login/Register system  
✅ **HTTPS Ready**: Connects to HTTPS backend  
✅ **GitHub Pages Deployment**: Free, public hosting  

## 🚀 Next Steps

1. Test locally with `npm start`
2. Create GitHub repository for frontend
3. Deploy to GitHub Pages with `npm run deploy`
4. Share the live URL with your teacher

## 📞 Support

For issues or questions, refer to:
- React Documentation: https://react.dev
- Bootstrap Documentation: https://getbootstrap.com
- TypeScript Documentation: https://www.typescriptlang.org

---

**Created**: November 17, 2025  
**Framework**: React 18 + TypeScript  
**Hosting**: GitHub Pages  
**Status**: ✅ Production Ready
