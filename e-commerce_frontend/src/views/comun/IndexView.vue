<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-secondary border-bottom pb-2">Explorar Productos Disponibles</h1>
    
    <div v-if="isLoading" class="alert alert-info text-center">
        <span class="spinner-border spinner-border-sm me-2"></span> Cargando Marketplace...
    </div>
    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    
    <div v-if="mensajeCarrito" :class="`alert alert-${tipoMensaje} alert-dismissible fade show`" role="alert">
        {{ mensajeCarrito }}
        <button type="button" class="btn-close" @click="mensajeCarrito = null"></button>
    </div>
    
    <div v-if="productosPaginados.length > 0 && !isLoading" class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      
      <div class="col" v-for="producto in productosPaginados" :key="producto.id">
        
        <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden d-flex flex-column product-container">
            
            <RouterLink 
                :to="{ name: 'detalleProducto', params: { id: producto.id } }" 
                class="text-decoration-none text-dark d-flex flex-column h-100"
            >
              
              <img 
                v-ngrok-img="producto.imagenUrl || 'https://placehold.co/400x200/4c4c4c/ffffff?text=Sin+Imagen'" 
                
                class="card-img-top object-fit-cover" 
                alt="Imagen del producto" 
                style="height: 200px;"
                onerror="this.onerror=null; this.src='https://placehold.co/400x200/4c4c4c/ffffff?text=Producto';"
              />
              
              <div class="card-body d-flex flex-column flex-grow-1">
                <h5 class="card-title text-truncate">{{ producto.nombre }} 
                    <span v-if="producto.esNuevo" class="badge bg-info text-white ms-2">Nuevo</span>
                </h5>
                
                <p class="card-text text-muted small flex-grow-1 mb-3 description-text">{{ producto.descripcion.substring(0, 70) + '...' }}</p>
                
                <div class="d-flex justify-content-between align-items-center mb-0 mt-auto">
                    <h4 class="text-primary fw-bold mb-0">Q{{ producto.precio.toFixed(2) }}</h4>
                    <small :class="producto.stock > 0 ? 'text-success' : 'text-danger'">{{ producto.stock > 0 ? 'En Stock' : 'Agotado' }}</small>
                </div>
              </div>
            </RouterLink>
            
            <div class="card-footer bg-white border-top-0 p-3 pt-0 button-footer">
                <button 
                    class="btn btn-primary w-100 rounded-pill shadow-sm" 
                    :disabled="producto.stock <= 0 || carritoStore.cargando" 
                    @click.stop="manejarAgregarProducto(producto.id)"
                >
                    <span v-if="carritoStore.cargando" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    <i v-else class="bi bi-cart-plus me-2"></i> 
                    {{ producto.stock <= 0 ? 'Agotado' : 'Añadir al Carrito' }}
                </button>
            </div>
        </div>
      </div>
      
    </div>

    <div v-else-if="!isLoading && allProductos.length === 0" class="alert alert-warning text-center mt-5">
        <i class="bi bi-info-circle me-2"></i> No se encontraron productos en el Marketplace.
    </div>

    <div v-if="totalPages > 1 && !isLoading" class="d-flex justify-content-center mt-5">
        <nav aria-label="Paginación de productos">
            <ul class="pagination">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="cambiarPagina(currentPage - 1)" :disabled="currentPage === 1">
                        Anterior
                    </button>
                </li>
                
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                    <button class="page-link" @click="cambiarPagina(page)">
                        {{ page }}
                    </button>
                </li>

                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="cambiarPagina(currentPage + 1)" :disabled="currentPage === totalPages">
                        Siguiente
                    </button>
                </li>
            </ul>
        </nav>
    </div>

  </div>
</template>

<style scoped>
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css");

/* 1. Estilos del RouterLink y el contenedor general */
.product-container {
    /* El contenedor principal de la tarjeta ahora tiene la sombra y el hover */
    transition: transform 0.2s, box-shadow 0.2s;
    /* Aseguramos que la columna Flexbox funcione */
    display: flex;
    flex-direction: column;
}

.product-container:hover {
    transform: translateY(-5px);
    box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.25) !important;
}

/* El RouterLink ahora solo maneja la navegación */
.product-container .router-link-active {
    /* Opcional: Asegura que el área clicable ocupe el 100% del espacio superior */
    flex-grow: 1;
}

/* 2. Estilos para el footer del botón */
.button-footer {
    border-radius: 0 0 0.5rem 0.5rem; 
    border-top: none !important; 
    /* Usamos un borde sólido que coincida con el borde de la tarjeta (si tuviera) */
    border: 1px solid #dee2e6;
    border-top: none;
    /* Ya no es necesario height: 75px; si el padding es suficiente */
}
/* Estilos para el borde superior del RouterLink y su borde inferior para simular la tarjeta */
.product-container {
    border: 1px solid #dee2e6;
    border-radius: 0.5rem; /* El contenedor completo tiene el borde redondeado */
}
.button-footer {
    border: none; /* Quitamos el borde del footer, ya que el contenedor tiene el borde completo */
}

.description-text {
    /* Asegura que todas las descripciones tengan una altura mínima */
    min-height: 2.5rem; 
    line-height: 1.25;
}

/* El img dentro del RouterLink debe mantener su tamaño */
.card-img-top {
    height: 200px;
    object-fit: cover;
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from '@/plugins/axios';
import { useCarritoStore } from '@/stores/carrito';

// --- Estado local y Stores ---
const carritoStore = useCarritoStore();

const allProductos = ref([]); // Almacena TODOS los productos
const isLoading = ref(true);
const errorMessage = ref('');
const mensajeCarrito = ref(null);
const tipoMensaje = ref('');

// --- VARIABLES DE PAGINACIÓN LOCAL ---
const currentPage = ref(1); // Usaremos índice 1 para mostrar al usuario (página 1, 2, 3...)
const pageSize = 12;      // Productos por página
// ------------------------------------

// --- Computed Properties para Paginación ---

// Calcula el número total de páginas
const totalPages = computed(() => {
    return Math.ceil(allProductos.value.length / pageSize);
});

// Devuelve solo los productos de la página actual
const productosPaginados = computed(() => {
    const start = (currentPage.value - 1) * pageSize;
    const end = start + pageSize;
    return allProductos.value.slice(start, end);
});

// --- Lógica del Carrito (sin cambios) ---
const manejarAgregarProducto = async (productoId) => {
    const cantidad = 1;
    mensajeCarrito.value = null;

    try {
        await carritoStore.agregarOActualizarProducto(productoId, cantidad);
        mensajeCarrito.value = '¡Producto agregado al carrito!';
        tipoMensaje.value = 'success';
    } catch (error) {
        const mensajeError = typeof error === 'string' ? error : 'Error desconocido al añadir al carrito.';
        mensajeCarrito.value = `Error: ${mensajeError}`;
        tipoMensaje.value = 'danger';
    } finally {
        setTimeout(() => { mensajeCarrito.value = null; }, 4000);
    }
};

/**
 * Obtiene TODOS los productos APROBADOS (Marketplace).
 */
const fetchMarketplaceProducts = async () => {
    isLoading.value = true;
    errorMessage.value = '';
    
    try {
        // Obtenemos todos los datos (el backend devuelve una lista completa)
        const response = await axios.get('/productos'); 
        allProductos.value = response.data;
        // Reiniciamos la página a 1 después de una carga exitosa
        currentPage.value = 1; 
    } catch (error) {
        console.error('Error al cargar el Marketplace:', error);
        errorMessage.value = 'No se pudo cargar el Marketplace. El servidor puede estar inactivo.';
    } finally {
        isLoading.value = false;
    }
};

/**
 * Función para cambiar de página.
 */
const cambiarPagina = (nuevaPagina) => {
    if (nuevaPagina >= 1 && nuevaPagina <= totalPages.value) {
        currentPage.value = nuevaPagina;
        // Opcional: Desplazarse al inicio de la página para ver el cambio
        window.scrollTo({ top: 0, behavior: 'smooth' }); 
    }
};

onMounted(() => {
    fetchMarketplaceProducts();
    carritoStore.cargarCarrito(); 
});
</script>

