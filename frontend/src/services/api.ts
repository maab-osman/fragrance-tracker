import axios from 'axios';

// Configure based on environment
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Allow cookies for session
});

export const authAPI = {
  login: (username: string, password: string) =>
    api.post('/login', { username, password }),
  register: (username: string, password: string, email: string) =>
    api.post('/register', { username, password, email }),
  logout: () => api.post('/logout'),
};

export const perfumeAPI = {
  getAll: () => api.get('/api/perfumes'),
  getById: (id: number) => api.get(`/api/perfumes/${id}`),
  create: (perfume: any) => api.post('/api/perfumes', perfume),
  update: (id: number, perfume: any) => api.put(`/api/perfumes/${id}`, perfume),
  delete: (id: number) => api.delete(`/api/perfumes/${id}`),
  search: (name: string) => api.get('/api/perfumes/search', { params: { name } }),
};

export default api;
