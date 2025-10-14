<template>
  <div class="container d-flex justify-content-center align-items-center mt-5 mb-5">
    <div class="card shadow-lg p-4 p-md-5 w-100" style="max-width: 450px;">
      
      <h2 class="card-title text-center mb-4 text-primary">Iniciar Sesión</h2>
      
      <form @submit.prevent="handleLogin" class="needs-validation" novalidate>
        
        <div class="mb-3">
          <label for="correo" class="form-label fw-bold">Correo Electrónico</label>
          <input 
            type="email" 
            id="correo" 
            v-model="correo" 
            class="form-control"
            required 
            placeholder="ejemplo@correo.com"
          />
        </div>

        <div class="mb-4">
          <label for="contrasena" class="form-label fw-bold">Contraseña</label>
          <input 
            type="password" 
            id="contrasena" 
            v-model="contrasena" 
            class="form-control" 
            required 
            placeholder="********"
          />
        </div>

        <button 
          type="submit" 
          :disabled="isLoading"
          class="btn btn-primary w-100 py-2 rounded-pill shadow-sm"
        >
          <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ isLoading ? 'Cargando...' : 'Acceder' }}
        </button>

        <p v-if="errorMessage" class="alert alert-danger text-center mt-3 p-2" role="alert">
          {{ errorMessage }}
        </p>

        <router-link to="/register" class="d-block text-center mt-3 text-muted">
          ¿No tienes cuenta? Regístrate aquí.
        </router-link>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth'; 
import { useRouter } from 'vue-router';

// --- Estado del formulario ---
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
        // Llama a la acción de login de Pinia
        await authStore.login(correo.value, contrasena.value);
        
        // El Router Guard interceptará esta ruta y redirigirá al dashboard de rol correcto.
        router.push('/admin'); //cujalquier ruta para que funcione xd
        
    } catch (err) {
        const backendMessage = err.response?.data?.mensaje || err.response?.data?.error;
        
        // Asignamos el mensaje específico o uno genérico si falla
        errorMessage.value = backendMessage || 'Fallo en el inicio de sesión. Verifique sus credenciales.';
        
        console.error('Error durante la autenticación:', err);

    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>

</style>