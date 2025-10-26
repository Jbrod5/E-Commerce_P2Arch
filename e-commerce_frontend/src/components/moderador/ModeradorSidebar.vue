<template>
  <nav
    class="d-flex flex-column p-3 bg-dark text-white shadow-lg vh-100 position-fixed"
    style="width: 250px"
  >
    <!-- Título de la Aplicación -->
    <router-link
      to="/moderador"
      class="navbar-brand text-center text-light fs-4 fw-bolder mb-4 border-bottom border-light-subtle pb-3"
    >
      E-COMMERCE GT
    </router-link>

    <!-- Información del Rol y Usuario -->
    <div class="mt-auto">
      <div
        class="d-flex flex-column align-items-start mb-4 border-bottom border-secondary pb-3 text-decoration-none"
      >
        <span class="text-secondary fw-normal small mb-1">
          <i class="bi bi-shield-fill me-1"></i> Rol: Moderador
        </span>
        <span
          class="fw-bold text-truncate text-info fs-6"
          :title="authStore.user?.nombre || 'Usuario'"
        >
          {{ authStore.user?.nombre || "Cargando..." }}
        </span>
      </div>

      <!-- Menú de Navegación del Moderador -->
      <ul class="nav nav-pills flex-column mb-3">
        <li class="nav-item">
          <router-link
            :to="{ name: 'moderador-index' }"
            class="nav-link text-white text-start mb-2 d-flex justify-content-between align-items-center"
            active-class="active"
          >
            <div><i class="bi bi-file-earmark-check me-2"></i> Solicitudes de Venta</div>
            <!-- Podríamos poner un badge con el conteo de pendientes aquí -->
            <span
              v-if="moderadorStore.cantidadPendientes > 0"
              class="badge bg-warning text-dark rounded-pill"
            >
              {{ moderadorStore.cantidadPendientes }}
            </span>
          </router-link>
        </li>

        <li>
          <router-link
            :to="{ name: 'gestionSanciones' }"
            class="nav-link text-white text-start mb-2"
            active-class="active"
          >
            <i class="bi bi-person-slash me-2"></i> Gestión de Sanciones
          </router-link>
        </li>
      </ul>

      <!-- Botón de Cerrar Sesión -->
      <button @click="logout" class="btn btn-outline-danger w-100 py-2 rounded-pill">
        <i class="bi bi-power me-2"></i> Cerrar Sesión
      </button>
    </div>
  </nav>
</template>

<script setup>
import { onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";
// Asumimos que existirá una store para el moderador que cargará el conteo de pendientes
import { useModeradorStore } from "@/stores/moderador"; 

// Instancias de Pinia y Vue Router
const authStore = useAuthStore();
const moderadorStore = useModeradorStore(); // TODO: Necesitas crear esta store en src/stores/moderador.js
const router = useRouter();

/**
 * Llama a la acción de logout de la tienda de autenticación y redirige al login.
 */
const logout = () => {
  authStore.logout();
  router.push("/");
};

onMounted(() => {
  // Cargar el conteo de solicitudes pendientes al montar el sidebar
  moderadorStore.cargarConteoPendientes(); 
});
</script>

<style scoped>
.bg-dark {
  background-color: #212529 !important;
}

.nav-link.active {
  background-color: #0d6efd !important; /* Azul Bootstrap por defecto */
  color: white !important;
}

.nav-link:hover {
  background-color: #343a40;
}
</style>
