import axios from 'axios';

// Instance Axios préconfigurée
const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Backend
    headers: {
        'Content-Type': 'application/json',
    }
})

// Avant chaque requête, on vérifie le token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('jwt_token');

        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;