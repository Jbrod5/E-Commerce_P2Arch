// src/stores/auth.js

import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import api from '@/plugins/axios.js'; 

// Definimos la URL base para el endpoint de login, relativo a la baseURL de 'api'
const LOGIN_ENDPOINT = '/auth/login';
const REGISTER_ENDPOINT = '/auth/register'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        // El token persiste en la cookie
        token: Cookies.get('jwtToken') || null, 
        // El objeto user con el rol persiste en la cookie, si existe
        user: Cookies.get('user') ? JSON.parse(Cookies.get('user')) : null, 
        isAuthenticated: !!Cookies.get('jwtToken'),
    }),
    

    getters: {
        // Métodos para verificar el rol del usuario de forma sencilla :3333
        isAdmin: (state) => state.user?.rol === 'ROLE_ADMINISTRADOR',
        isModerador: (state) => state.user?.rol === 'ROLE_MODERADOR',
        isLogistica: (state) => state.user?.rol === 'ROLE_LOGISTICA',
        isComun: (state) => state.user?.rol === 'ROLE_COMUN',

        // Getter para obtener el rol actual
        rol: (state) => state.user?.rol || null,
    },

    actions: {
        initialize() {
            if (this.token) {
                // Configurar el encabezado de autorización para Axios
                api.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
                this.isAuthenticated = true;
            } else {
                this.isAuthenticated = false;
            }
        },

        async login(correo, contrasena) {
            try {
                const response = await api.post(LOGIN_ENDPOINT, {
                    correo: correo,
                    contrasena: contrasena,
                });
                
                // Extraemos el token Y el rol del backend
                const { token, rol } = response.data;
                const userData = { correo, rol }; // Objeto a guardar

                // 1. Guardar en el estado
                this.token = token;
                this.user = userData;
                this.isAuthenticated = true;

                // 2. Guardar en cookies
                Cookies.set('jwtToken', token, { expires: 7, secure: false, sameSite: 'Lax' }); 
                Cookies.set('user', JSON.stringify(userData), { expires: 7, secure: false, sameSite: 'Lax' });
                
                // 3. Configurar Axios
                api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                
                return true; 

            } catch (error) {
                // Limpiar si falla
                this.logout();
                throw error; 
            }
        },

        logout() {
            this.token = null;
            this.user = null;
            this.isAuthenticated = false;
            Cookies.remove('jwtToken');
        },

        async register(userData) {
            try {
                // Llama al endpoint POST /api/auth/register de Spring Boot
                await api.post(REGISTER_ENDPOINT, userData);
                
                // Si el registro es exitoso, la vista se encargará de redirigir o mostrar éxito.
                return true; 

            } catch (error) {
                // Si el backend lanza un error (ej: correo ya registrado), lo propagamos.
                throw error; 
            }
        }



    },
});