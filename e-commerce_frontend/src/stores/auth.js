// src/stores/auth.js

import { defineStore } from 'pinia';
// import axios from 'axios'; // <-- ELIMINAR ESTA LÍNEA
import Cookies from 'js-cookie';
import api from '@/plugins/axios.js'; // <-- USAR INSTANCIA CONFIGURADA

// Definimos la URL base para el endpoint de login, relativo a la baseURL de 'api'
const LOGIN_ENDPOINT = '/auth/login';
const REGISTER_ENDPOINT = '/auth/register'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: Cookies.get('jwtToken') || null, 
        user: null, 
        isAuthenticated: !!Cookies.get('jwtToken'),
    }),
    
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
                // Petición POST usando la instancia 'api'
                const response = await api.post(LOGIN_ENDPOINT, {
                    correo: correo,
                    contrasena: contrasena,
                });
                
                const { token, rol } = response.data;

                // 1. Guardar el token y la información del usuario en el estado
                this.token = token;
                this.user = { correo, rol };
                this.isAuthenticated = true;

                // 2. Guardar el token en una cookie (es mejor que localStorage para JWT)
                Cookies.set('jwtToken', token, { expires: 7 }); 
                
                return true; 

            } catch (error) {
                this.token = null;
                this.user = null;
                this.isAuthenticated = false;
                Cookies.remove('jwtToken');
                
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