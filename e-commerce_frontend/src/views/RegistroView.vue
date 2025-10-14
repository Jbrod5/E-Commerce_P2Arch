<template>
  <div class="register-container">
    <h2>Registrar Nuevo Usuario</h2>
    
    <form @submit.prevent="handleRegister" class="register-form">
      
      <div class="input-group">
        <label for="nombre">Nombre Completo</label>
        <input type="text" id="nombre" v-model="nombre" required placeholder="Nombre Apellido">
      </div>

      <div class="input-group">
        <label for="correo">Correo Electrónico</label>
        <input type="email" id="correo" v-model="correo" required placeholder="ejemplo@correo.com">
      </div>

      <div class="input-group">
        <label for="contrasena">Contraseña</label>
        <input type="password" id="contrasena" v-model="contrasena" required placeholder="********">
      </div>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Registrando...' : 'Registrar Cuenta' }}
      </button>

      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <router-link to="/login" class="link-login">¿Ya tienes cuenta? Inicia sesión.</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/plugins/axios.js'; // <-- Ya estaba correcto

const nombre = ref('');
const correo = ref('');
const contrasena = ref('');
const errorMessage = ref(null);
const successMessage = ref(null);
const isLoading = ref(false);

const router = useRouter();

// URL relativa al baseURL configurado en axios.js (http://localhost:8080/api)
const REGISTER_ENDPOINT = '/auth/register'; 

const handleRegister = async () => {
    isLoading.value = true;
    errorMessage.value = null;
    successMessage.value = null;

    try {
        await api.post(REGISTER_ENDPOINT, { 
            nombre: nombre.value,
            correo: correo.value,
            contrasena: contrasena.value,
        });
        
        successMessage.value = '¡Registro exitoso! Redirigiendo a Login...';
        
        setTimeout(() => {
            router.push('/login');
        }, 2000);

    } catch (err) {
        // Mejor manejo de errores del backend
        if (err.response && err.response.data && err.response.data.error) {
            errorMessage.value = err.response.data.error;
        } else {
            errorMessage.value = 'Error al registrar. Verifique sus datos o intente más tarde.';
        }
    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
/* Añade estos estilos para que el formulario se vea bien */
.register-container {
    max-width: 400px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.register-form > * { margin-bottom: 15px; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
button { width: 100%; padding: 10px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }
.error-message { color: red; text-align: center; margin-top: 10px; }
.success-message { color: green; text-align: center; margin-top: 10px; }
.link-login { display: block; text-align: center; margin-top: 20px; font-size: 0.9em; }
</style>