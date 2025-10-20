<template>
  <nav class="d-flex flex-column p-3 bg-dark text-white vh-100 shadow-lg" style="width: 250px;">
    
    <!-- Título/Logo de la Aplicación -->
    <router-link to="/" class="navbar-brand text-center text-primary fs-4 fw-bold mb-4 border-bottom pb-3">
      E-COMMERCE
    </router-link>

    <!-- 1. CUADRO DE BÚSQUEDA (Placeholder) -->
    <div class="mb-4">
      <input type="search" class="form-control form-control-sm" placeholder="Buscar productos..." aria-label="Search">
    </div>

    <!-- 2. ENLACES PRINCIPALES (Vendedor) -->
    <ul class="nav nav-pills flex-column mb-auto">
      <li class="nav-item">
        <!-- Botón solicitado: Mis Productos (Inventario) -->
        <router-link to="/vendedor/inventario" class="nav-link text-white text-center mb-2" active-class="active">
          <i class="bi bi-box-seam me-2"></i> Mis Productos
        </router-link>
      </li>
      <li>
        <!-- Enlace para Crear Producto -->
        <router-link to="/vendedor/crear-producto" class="nav-link text-white text-center mb-2" active-class="active">
          <i class="bi bi-plus-circle me-2"></i> Crear Producto
        </router-link>
      </li>
    </ul>

    <!-- SEPARADOR -->
    <hr class="text-white-50">

    <!-- 3. SECCIÓN DE PERFIL Y CIERRE DE SESIÓN (Sticky Bottom) -->
    <div class="mt-auto">
        <!-- Información del Usuario -->
        <div class="d-flex align-items-center mb-3">
            <!-- Imagen de Carita (Placeholder con Bootstrap Icons) -->
            <i class="bi bi-person-circle fs-3 me-2 text-info"></i>
            
            <!-- Nombre del Usuario -->
            <span class="fw-bold text-truncate" :title="authStore.user?.correo || 'Usuario'">
                {{ authStore.user?.nombre || 'Vendedor' }}
            </span>
        </div>

        <!-- 4. Botón de Cerrar Sesión -->
        <button 
            @click="logout" 
            class="btn btn-outline-danger w-100 py-2 rounded-pill"
        >
            <i class="bi bi-power me-2"></i> Cerrar Sesión
        </button>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

// Instancias de Pinia y Vue Router
const authStore = useAuthStore();
const router = useRouter();

/**
 * Llama a la acción de logout de la tienda de autenticación y redirige al login.
 */
const logout = () => {
    authStore.logout();
    router.push('/');
};
</script>

<style scoped>
/* Importamos los íconos de Bootstrap si no están en index.html */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css");

.bg-dark {
    background-color: #212529 !important; /* Oscuro estándar de Bootstrap */
}

/* Estilo para el enlace activo */
.nav-link.active {
    background-color: #0d6efd !important; /* Azul primario de Bootstrap */
    color: white !important;
}

.nav-link {
    transition: background-color 0.2s;
}

.nav-link:hover {
    background-color: #343a40; /* Un poco más claro al hacer hover */
}
</style>
