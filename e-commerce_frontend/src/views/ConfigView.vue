<template>
  <div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="w-100" style="max-width: 900px;">
      <h5 class="text-dark mb-4 text-center">
        <i class="bi bi-gear me-2"></i>
        Configuración
      </h5>

      <div class="row justify-content-center">
        <!-- Configuración Backend -->
        <div class="col-lg-5 mb-4">
          <div class="card h-100">
            <div class="card-header bg-light text-center">
              <h6 class="card-title mb-0">
                <i class="bi bi-server me-2"></i>
                URL del Backend
              </h6>
            </div>
            <div class="card-body d-flex flex-column">
              <div class="mb-3">
                <label class="form-label">URL Base del API</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="nuevaURL"
                  placeholder="https://tu-servidor.ngrok-free.dev/api"
                />
              </div>

              <div class="d-flex gap-2 mb-3">
                <button
                  @click="guardarURL"
                  class="btn btn-primary flex-fill"
                  :disabled="!nuevaURL || nuevaURL === urlActual"
                >
                  <i class="bi bi-check-lg me-2"></i>
                  Guardar
                </button>
                <button 
                  @click="restaurarPorDefecto" 
                  class="btn btn-outline-secondary"
                >
                  <i class="bi bi-arrow-clockwise"></i>
                </button>
              </div>

              <div v-if="mensaje" :class="['alert mb-3 text-center', mensajeClase]">
                <small class="fw-bold">{{ mensaje }}</small>
              </div>

              <div class="border rounded p-2 bg-light mt-auto">
                <small class="text-muted d-block">URL actual en uso:</small>
                <code class="text-dark">{{ urlActual }}</code>
              </div>
            </div>
          </div>
        </div>

        <!-- Configuración Correo -->
        <div class="col-lg-5 mb-4">
          <div class="card h-100">
            <div class="card-header bg-light text-center">
              <h6 class="card-title mb-0">
                <i class="bi bi-envelope me-2"></i>
                Configuración de Correo
              </h6>
            </div>
            <div class="card-body d-flex flex-column">
              <div class="mb-3">
                <label class="form-label">Correo Remitente</label>
                <input
                  type="email"
                  class="form-control mb-2"
                  v-model="correo"
                  placeholder="tucorreo@zohomail.com"
                />
                
                <label class="form-label">Contraseña de Aplicación</label>
                <input
                  type="password"
                  class="form-control"
                  v-model="contrasena"
                  placeholder="Ingresa tu contraseña de aplicación"
                />
              </div>

              <button 
                @click="guardarCorreo" 
                class="btn btn-primary w-100 mt-auto"
                :disabled="!correo || !contrasena"
              >
                <i class="bi bi-envelope-check me-2"></i>
                Guardar Configuración
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { actualizarBaseURL, obtenerBaseURL } from "@/plugins/axios.js";
import api from "@/plugins/axios.js";

const nuevaURL = ref("");
const urlActual = ref("");
const mensaje = ref("");
const mensajeClase = ref("");
const correo = ref('');
const contrasena = ref('');

const cargarURLActual = () => {
  urlActual.value = obtenerBaseURL();
  nuevaURL.value = urlActual.value.replace("/api", "");
};

const guardarURL = async () => {
  if (!nuevaURL.value) {
    mostrarMensaje("Por favor ingresa una URL válida", "alert-danger");
    return;
  }

  let urlBase = nuevaURL.value.trim();

  if (urlBase.endsWith("/api")) {
    urlBase = urlBase.substring(0, urlBase.length - 4);
  }

  if (urlBase.endsWith("/")) {
    urlBase = urlBase.substring(0, urlBase.length - 1);
  }

  try {
    actualizarBaseURL(urlBase + "/api");
    urlActual.value = urlBase + "/api";
    await api.post("/config/backend-url", { url: urlBase });
    mostrarMensaje("Configuración guardada correctamente", "alert-success");
  } catch (error) {
    console.error("Error:", error);
    mostrarMensaje("Error al sincronizar con el servidor", "alert-warning");
  }
};

const restaurarPorDefecto = () => {
  const urlPorDefecto = "http://localhost:8080/api";
  actualizarBaseURL(urlPorDefecto);
  nuevaURL.value = urlPorDefecto;
  urlActual.value = urlPorDefecto;
  mostrarMensaje("🔄 Configuración restaurada por defecto", "alert-success");
};

const mostrarMensaje = (texto, clase) => {
  mensaje.value = texto;
  mensajeClase.value = clase;
  setTimeout(() => {
    mensaje.value = "";
  }, 5000);
};

const guardarCorreo = async () => {
  if (!correo.value || !contrasena.value) {
    mostrarMensaje("Completa ambos campos", "alert-danger");
    return;
  }

  try {
    await api.post('/config/correo', { 
      correo: correo.value, 
      contrasena: contrasena.value 
    });
    mostrarMensaje('Configuración de correo guardada', 'alert-success');
  } catch (error) {
    console.error(error);
    mostrarMensaje('Error al guardar la configuración', 'alert-danger');
  }
};

onMounted(() => {
  cargarURLActual();
});
</script>