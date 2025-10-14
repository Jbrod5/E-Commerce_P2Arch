// src/stores/auth.js

import { defineStore } from 'pinia';
// import axios from 'axios'; // <-- ELIMINAR ESTA LÍNEA
import Cookies from 'js-cookie';
import api from '@/plugins/axios.js'; // <-- USAR INSTANCIA CONFIGURADA

// Definimos la URL base para el endpoint de login, relativo a la baseURL de 'api'
const LOGIN_ENDPOINT = '/auth/login'; 

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: Cookies.get('jwtToken') || null, 
        user: null, 
        isAuthenticated: !!Cookies.get('jwtToken'),
    }),
    
    actions: {
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
        }
    },
});