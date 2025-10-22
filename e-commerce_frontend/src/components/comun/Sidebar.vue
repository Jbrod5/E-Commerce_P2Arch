<template>
  <nav class="d-flex flex-column p-3 bg-dark text-white shadow-lg" style="width: 250px; min-height: 100vh;">
    
    <router-link to="/vendedor" class="navbar-brand text-center text-light fs-4 fw-bolder mb-4 border-bottom border-light-subtle pb-3">
      E-COMMERCE
    </router-link>

    <div class="mb-4">
      <input type="search" class="form-control form-control-sm bg-secondary border-0 text-white" placeholder="Buscar productos..." aria-label="Search">
    </div>

    <ul class="nav nav-pills flex-column mb-auto">
      
      <li class="nav-item">
        <router-link :to="{ name: 'carrito' }" class="nav-link text-white text-start mb-2 d-flex justify-content-between align-items-center" active-class="active">
          <div><i class="bi bi-cart me-2"></i> Carrito</div>
          
          <span v-if="carritoStore.cantidadProductos > 0" class="badge bg-danger rounded-pill">
            {{ carritoStore.cantidadProductos }}
          </span>
        </router-link>
      </li>
      
      <div class="flex-grow-1"></div>
    </ul>

    <hr class="text-white-50">

    <div class="mt-auto">
        
        <div class="d-flex flex-column align-items-start mb-3 border-bottom border-secondary pb-2">
            <span class="text-secondary fw-normal small mb-1">
                <i class="bi bi-person-fill me-1"></i> Vendedor:
            </span>
            <span class="fw-bold text-truncate text-info fs-6" :title="authStore.user?.nombre || 'Usuario'">
                {{ authStore.user?.nombre || 'Cargando...' }} 
            </span>
        </div>
        
        <ul class="nav nav-pills flex-column mb-3">
            <li class="nav-item">
                <router-link to="/vendedor/inventario" class="nav-link text-white text-start mb-2" active-class="active">
                    <i class="bi bi-box-seam me-2"></i> Mis Productos
                </router-link>
            </li>
            
            <li>
                <router-link to="/vendedor/crear-producto" class="nav-link text-white text-start mb-2" active-class="active">
                    <i class="bi bi-plus-circle me-2"></i> Crear Producto
                </router-link>
            </li>
        </ul>

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
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useCarritoStore } from '@/stores/carrito';
import { useRouter } from 'vue-router';

// Instancias de Pinia y Vue Router
const authStore = useAuthStore();
const carritoStore = useCarritoStore(); 
const router = useRouter();

/**
 * Llama a la acción de logout de la tienda de autenticación y redirige al login.
 */
const logout = () => {
    authStore.logout();
    router.push('/');
};

onMounted(() => {
    // Asegurarse de que la información del carrito esté precargada
    carritoStore.cargarCarrito();
});
</script>

<style scoped>
/* Estilos basados en Bootstrap */
nav {
    position: sticky;
    top: 0;
}

.bg-dark {
    background-color: #212529 !important;
}

.nav-link.active {
    background-color: var(--bs-primary) !important;
    color: white !important;
}

.nav-link:hover {
    background-color: #343a40; 
}
</style>