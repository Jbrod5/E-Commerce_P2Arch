<template>
  <div class="login-container">
    <h2>Iniciar Sesión en E-commerce</h2>
    
    <form @submit.prevent="handleLogin" class="login-form">
      
      <div class="input-group">
        <label for="correo">Correo Electrónico</label>
        <input 
          type="email" 
          id="correo" 
          v-model="correo" 
          required 
          placeholder="ejemplo@correo.com"
        />
      </div>

      <div class="input-group">
        <label for="contrasena">Contraseña</label>
        <input 
          type="password" 
          id="contrasena" 
          v-model="contrasena" 
          required 
          placeholder="********"
        />
      </div>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Cargando...' : 'Acceder' }}
      </button>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <router-link to="/register" class="link-register">
        ¿Aún no tienes cuenta? Regístrate aquí.
      </router-link>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

// CORRECCIÓN: Definir las variables del formulario
const correo = ref(''); 
const contrasena = ref(''); 
const errorMessage = ref(null);
const isLoading = ref(false);

const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
    isLoading.value = true;
    errorMessage.value = null;

    try {
        await authStore.login(correo.value, contrasena.value);
        router.push('/'); 
        
    } catch (err) {
        errorMessage.value = 'Fallo en el inicio de sesión. Verifique sus credenciales.';
        console.error('Error durante la autenticación:', err);

    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
.login-container {
    /* ... (Estilos existentes) ... */
    max-width: 400px;
    margin: 50px auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.login-form > * { margin-bottom: 15px; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
button { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
button:disabled { background-color: #a0c9ff; cursor: not-allowed; }
.error-message { color: red; text-align: center; margin-top: 10px; }

/* NUEVO ESTILO */
.link-register {
    display: block;
    text-align: center;
    margin-top: 10px;
    font-size: 0.9em;
    color: #007bff;
    text-decoration: none;
}
</style>