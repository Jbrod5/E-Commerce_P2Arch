import { defineStore } from 'pinia';
import axios from 'axios';
import Cookies from 'js-cookie';

// Definimos la URL base de tu backend Spring Boot
const API_URL = 'http://localhost:8080/api/auth/login';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: Cookies.get('jwtToken') || null, // Cargar token desde cookie
        user: null, // Almacenará { correo, rol }
        isAuthenticated: !!Cookies.get('jwtToken'),
    }),
    
    actions: {
        async login(correo, contrasena) {
            try {
                // Petición POST a tu endpoint de Spring Boot
                const response = await axios.post(API_URL, {
                    correo: correo,
                    contrasena: contrasena,
                });
                
                const { token, rol } = response.data;

                // 1. Guardar el token y la información del usuario en el estado
                this.token = token;
                this.user = { correo, rol };
                this.isAuthenticated = true;

                // 2. Guardar el token en una cookie (es mejor que localStorage para JWT)
                Cookies.set('jwtToken', token, { expires: 7 }); // Expira en 7 días
                
                return true; // Éxito

            } catch (error) {
                this.token = null;
                this.user = null;
                this.isAuthenticated = false;
                Cookies.remove('jwtToken');
                
                // Lanzar el error para que el componente Login lo maneje
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