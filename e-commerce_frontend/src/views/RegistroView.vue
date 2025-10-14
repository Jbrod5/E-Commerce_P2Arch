<template>
  <!-- Contenedor principal centrado, coherente con la vista de Login -->
  <div class="container d-flex justify-content-center align-items-center mt-5 mb-5">
    <div class="card shadow-lg p-4 p-md-5 w-100" style="max-width: 450px;">
      
      <!-- Título de la tarjeta -->
      <h2 class="card-title text-center mb-4 text-success">Registrar Nuevo Usuario</h2>
      
      <form @submit.prevent="handleRegister" class="needs-validation" novalidate>
        
        <!-- Campo Nombre Completo -->
        <div class="mb-3">
          <label for="nombre" class="form-label fw-bold">Nombre Completo</label>
          <input 
            type="text" 
            id="nombre" 
            v-model="nombre" 
            class="form-control"
            required 
            placeholder="Nombre"
          />
        </div>

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

        <!-- Botón de Registro -->
        <button 
          type="submit" 
          :disabled="isLoading"
          class="btn btn-success w-100 py-2 rounded-pill shadow-sm"
        >
          <!-- Usa un spinner de Bootstrap si está cargando -->
          <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ isLoading ? 'Registrando...' : 'Registrar Cuenta' }}
        </button>

        <!-- Mensajes de Estado (Alertas de Bootstrap) -->
        <p v-if="successMessage" class="alert alert-success text-center mt-3 p-2" role="alert">
          {{ successMessage }}
        </p>
        <p v-if="errorMessage" class="alert alert-danger text-center mt-3 p-2" role="alert">
          {{ errorMessage }}
        </p>

        <!-- Enlace de Login -->
        <router-link to="/" class="d-block text-center mt-3 text-muted">
          ¿Ya tienes cuenta? Inicia sesión.
        </router-link>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// Importamos la tienda de autenticación para usar el servicio de registro
import { useAuthStore } from '@/stores/auth'; 

const nombre = ref('');
const correo = ref('');
const contrasena = ref('');
const errorMessage = ref(null);
const successMessage = ref(null);
const isLoading = ref(false);

const router = useRouter();
const authStore = useAuthStore(); // Usaremos la tienda para manejar la lógica de registro

const handleRegister = async () => {
    isLoading.value = true;
    errorMessage.value = null;
    successMessage.value = null;

    try {
        // Usamos una acción de registro en la tienda Pinia (que crearemos después)
        // La tienda se encargará de llamar a la API y manejar la respuesta
        await authStore.register({ 
            nombre: nombre.value,
            correo: correo.value,
            contrasena: contrasena.value,
        });
        
        successMessage.value = '¡Registro exitoso! Redirigiendo a Login...';
        
        // Limpiamos los campos del formulario
        nombre.value = '';
        correo.value = '';
        contrasena.value = '';

        setTimeout(() => {
            router.push('/');
        }, 2000);

    } catch (error) {
        // Manejo de errores de registro, asumiendo que el backend devuelve un mensaje de error claro
        let message = 'Error al registrar. Verifique sus datos.';
        if (error.response && error.response.data) {
            // Intentamos obtener el mensaje de error del cuerpo de la respuesta del backend
            message = error.response.data.message || error.response.data.error || message;
        }
        errorMessage.value = message;
        console.error('Error durante el registro:', error);

    } finally {
        isLoading.value = false;
    }
};
</script>

<!-- Se eliminaron los estilos scope, ya que estamos usando clases de Bootstrap -->
<style scoped>
/* No se necesitan estilos personalizados gracias a Bootstrap */
</style>