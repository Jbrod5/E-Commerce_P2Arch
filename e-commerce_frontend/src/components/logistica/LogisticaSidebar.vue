<template>
  <nav
    class="d-flex flex-column p-3 bg-dark text-white shadow-lg vh-100 position-fixed"
    style="width: 250px"
  >
    <router-link
      to="/logistica"
      class="navbar-brand text-center text-light fs-4 fw-bolder mb-4 border-bottom border-light-subtle pb-3"
    >
      E-COMMERCE GT
    </router-link>

    <div class="mt-auto">
      <div
        class="d-flex flex-column align-items-start mb-4 border-bottom border-secondary pb-3 text-decoration-none"
      >
        <span class="text-secondary fw-normal small mb-1">
          <i class="bi bi-person-truck me-1"></i> Rol: Logística
        </span>
        <span
          class="fw-bold text-truncate text-success fs-6"
          :title="authStore.user?.nombre || 'Usuario'"
        >
          {{ authStore.user?.nombre || "Cargando..." }}
        </span>
      </div>

      <ul class="nav nav-pills flex-column mb-3">
        
        <li class="nav-item">
          <router-link
            :to="{ name: 'logistica-gestion-pedidos' }"
            class="nav-link text-white text-start mb-2"
            active-class="active-logistica"
          >
            <i class="bi bi-box-seam me-2"></i> Gestión de Pedidos
          </router-link>
        </li>
      </ul>

      <button @click="logout" class="btn btn-outline-danger w-100 py-2 rounded-pill">
        <i class="bi bi-power me-2"></i> Cerrar Sesión
      </button>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

// Asumo que tienes los mismos imports y store
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

/* Color activo para Logística: un verde asociado a éxito/entrega */
.nav-link.active-logistica {
  background-color: #28a745 !important; 
  color: white !important;
}

.nav-link:hover {
  background-color: #343a40;
}
</style>