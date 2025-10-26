import axios from 'axios';
import Cookies from 'js-cookie';

// 1. Crear una instancia base de Axios
const api = axios.create({
    // La URL base para la mayoría los tus endpoints 
    //baseURL: 'http://localhost:8080/api', 
    baseURL: ' https://semiobliviously-voluptuous-charlee.ngrok-free.dev',


    headers: {
        // 'Content-Type': 'application/json', 
    },
});

// 2. INTERCEPTOR DE PETICIONES (Añade el JWT a TODAS las peticiones)
api.interceptors.request.use(config => {
    const token = Cookies.get('jwtToken');

    // Si el token existe, lo añade al encabezado Authorization
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, error => {
    return Promise.reject(error);
});

// 3. INTERCEPTOR DE RESPUESTAS (Manejo de 401 Unauthorized/Expirado)
api.interceptors.response.use(response => {
    return response;
}, error => {
    // Si el backend responde 401, aquí podrías forzar el logout o redirigir
    if (error.response && error.response.status === 401) {
        console.error("Token jwt expirado o no autorizado.");
        // Ejemplo de redirección manual si el 401 no es el login
        // window.location.href = '/login'; 

        //Eliminar las cookies de la sesion
        Cookies.remove('jwtToken');
        Cookies.remove('user');

        // Redirige a la página de login (la URL raíz '/')
        window.location.href = '/'; 
        
        return Promise.reject("Token expirado: Redirigiendo a login.");
    }
    return Promise.reject(error);
});


export default api;
