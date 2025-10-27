<template>
  <nav
    class="d-flex flex-column p-3 bg-dark text-white shadow-lg vh-100 position-fixed"
    style="width: 250px"
  >
    <!-- Título de la Aplicación -->
    <router-link
      to="/admin"
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
          <i class="bi bi-person-workspace me-1"></i> Rol: Administrador
        </span>
        <span
          class="fw-bold text-truncate text-info fs-6"
          :title="authStore.user?.nombre || 'Usuario'"
        >
          {{ authStore.user?.nombre || "Cargando..." }}
        </span>
      </div>

      <!-- Menú de Navegación del Administrador -->
      <ul class="nav nav-pills flex-column mb-3">
        
        <!-- Dashboard / Reportes -->
        <li class="nav-item">
          <router-link
            :to="{ name: 'admin-index' }"
            class="nav-link text-white text-start mb-2"
            active-class="active"
          >
            <div><i class="bi bi-speedometer2 me-2"></i> Dashboard de Reportes</div>
          </router-link>
        </li>

        <!-- Gestión de Empleados -->
        <li class="nav-item">
          <router-link
            :to="{ name: 'admin-gestion-empleados' }"
            class="nav-link text-white text-start mb-2"
            active-class="active"
          >
            <i class="bi bi-people-fill me-2"></i> Gestión de Empleados
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
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

// Instancias de Pinia y Vue Router
const authStore = useAuthStore();
const router = useRouter();

/**
 * Llama a la acción de logout de la tienda de autenticación y redirige al login.
 */
const logout = () => {
  authStore.logout();
  router.push("/");
};
</script>

<style scoped>
/* Estilos adaptados del componente original */
.bg-dark {
  background-color: #212529 !important;
}

.nav-link.active {
  background-color: #17a2b8 !important; /* Un color distinto para Administrador (teal/cyan) */
  color: white !important;
}

.nav-link:hover {
  background-color: #343a40;
}
</style>
