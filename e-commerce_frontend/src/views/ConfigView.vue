<template>
  <div class="container py-4">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="h3 mb-0">
        <i class="bi bi-gear-fill me-2"></i>
        Configuración
      </h1>
    </div>

    <div class="card shadow-sm border-0">
      <div class="card-body p-4">
        
        <h5 class="card-title mb-3">URL del Backend</h5>
        <p class="text-muted small mb-4">
          Configura la URL del servidor backend. Esta configuración se guarda localmente en tu navegador.
        </p>

        <div class="mb-3">
          <label for="backendUrl" class="form-label fw-semibold">URL Base del API</label>
          <input 
            type="text" 
            class="form-control" 
            id="backendUrl"
            v-model="nuevaURL"
            placeholder="https://tu-tunel.ngrok-free.dev/api"
          />
          <div class="form-text">
            Ejemplo: <code>https://semiobliviously-voluptuous-charlee.ngrok-free.dev/api</code>
          </div>
        </div>

        <div class="d-flex gap-2">
          <button 
            @click="guardarURL" 
            class="btn btn-primary"
            :disabled="!nuevaURL || nuevaURL === urlActual"
          >
            <i class="bi bi-save me-2"></i>
            Guardar Configuración
          </button>
          
          <button 
            @click="restaurarPorDefecto" 
            class="btn btn-outline-secondary"
          >
            <i class="bi bi-arrow-counterclockwise me-2"></i>
            Restaurar por Defecto
          </button>
        </div>

        <div v-if="mensaje" :class="['alert mt-3 mb-0', mensajeClase]" role="alert">
          {{ mensaje }}
        </div>

        <hr class="my-4">

        <div class="alert alert-info mb-0">
          <h6 class="alert-heading">
            <i class="bi bi-info-circle me-2"></i>
            URL Actual en uso:
          </h6>
          <code class="d-block mt-2">{{ urlActual }}</code>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { actualizarBaseURL, obtenerBaseURL } from '@/plugins/axios.js';

const nuevaURL = ref('');
const urlActual = ref('');
const mensaje = ref('');
const mensajeClase = ref('');

const cargarURLActual = () => {
  urlActual.value = obtenerBaseURL();
  nuevaURL.value = urlActual.value;
};

const guardarURL = () => {
  if (!nuevaURL.value) {
    mostrarMensaje('Por favor ingresa una URL válida', 'alert-danger');
    return;
  }

  // Asegurarse de que la URL termine en /api
  let url = nuevaURL.value.trim();
  if (!url.endsWith('/api')) {
    url += '/api';
  }

  // Guardar la nueva URL
  actualizarBaseURL(url);
  urlActual.value = url;
  
  mostrarMensaje('Configuración guardada exitosamente. Recarga la página si es necesario.', 'alert-success');
};

const restaurarPorDefecto = () => {
  const urlPorDefecto = 'http://localhost:8080/api';
  actualizarBaseURL(urlPorDefecto);
  nuevaURL.value = urlPorDefecto;
  urlActual.value = urlPorDefecto;
  
  mostrarMensaje('Configuración restaurada a valores por defecto', 'alert-success');
};

const mostrarMensaje = (texto, clase) => {
  mensaje.value = texto;
  mensajeClase.value = clase;
  
  setTimeout(() => {
    mensaje.value = '';
  }, 5000);
};

onMounted(() => {
  cargarURLActual();
});
</script>

<style scoped>
code {
  background-color: #f8f9fa;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  color: #d63384;
}
</style>