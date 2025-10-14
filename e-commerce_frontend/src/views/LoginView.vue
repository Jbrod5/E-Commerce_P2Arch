<template>
  <!-- Contenedor principal centrado con margen superior y sombra -->
  <div class="container d-flex justify-content-center align-items-center mt-5 mb-5">
    <div class="card shadow-lg p-4 p-md-5 w-100" style="max-width: 450px;">
      
      <!-- Título de la tarjeta -->
      <h2 class="card-title text-center mb-4 text-primary">Iniciar Sesión</h2>
      
      <form @submit.prevent="handleLogin" class="needs-validation" novalidate>
        
        <!-- Campo Correo Electrónico -->
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

        <!-- Campo Contraseña -->
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

        <!-- Botón de Acceder -->
        <button 
          type="submit" 
          :disabled="isLoading"
          class="btn btn-primary w-100 py-2 rounded-pill shadow-sm"
        >
          <!-- Usa un spinner de Bootstrap si está cargando -->
          <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ isLoading ? 'Cargando...' : 'Acceder' }}
        </button>

        <!-- Mensaje de Error (alerta de Bootstrap) -->
        <p v-if="errorMessage" class="alert alert-danger text-center mt-3 p-2" role="alert">
          {{ errorMessage }}
        </p>

        <!-- Enlace de Registro -->
        <router-link to="/register" class="d-block text-center mt-3 text-muted">
          ¿No tienes cuenta? Regístrate aquí.
        </router-link>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
// Asumo que tienes una store Pinia llamada auth, si no existe, créala.
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
        // Llama a la acción de login en tu Pinia store
        await authStore.login(correo.value, contrasena.value);
        // Redirige al inicio si es exitoso
        router.push('/'); 
        
    } catch (err) {
        // Mensaje de error genérico en caso de fallo de credenciales o de red
        errorMessage.value = 'Fallo en el inicio de sesión. Verifique sus credenciales.';
        console.error('Error durante la autenticación:', err);

    } finally {
        isLoading.value = false;
    }
};
</script>

<!-- Se eliminaron los estilos scope, ya que estamos usando clases de Bootstrap -->
<style scoped>
/* No se necesitan estilos personalizados gracias a Bootstrap */
</style>