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
    
    <div v-if="productosPaginados.length > 0 && !isLoading" class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-5 g-3">
      
      <div class="col" v-for="producto in productosPaginados" :key="producto.id">
        
        <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden d-flex flex-column product-container small-card">
            
            <RouterLink 
                :to="{ name: 'detalleProducto', params: { id: producto.id } }" 
                class="text-decoration-none text-dark d-flex flex-column h-100"
            >
              
              <img 
                v-ngrok-img="producto.imagenUrl || 'https://placehold.co/300x300/4c4c4c/ffffff?text=Sin+Imagen'" 
                class="card-img-top" 
                alt="Imagen del producto" 
                onerror="this.onerror=null; this.src='https://placehold.co/300x300/4c4c4c/ffffff?text=Producto';"
              />
              
              <div class="card-body d-flex flex-column flex-grow-1 p-2">
                <h6 class="card-title text-truncate mb-1">
                  {{ producto.nombre }} 
                  <span v-if="producto.esNuevo" class="badge bg-info text-white ms-1">Nuevo</span>
                </h6>
                
                <p class="card-text text-muted small flex-grow-1 mb-2 description-text">
                  {{ producto.descripcion.substring(0, 50) + '...' }}
                </p>
                
                <div class="d-flex justify-content-between align-items-center mt-auto">
                    <span class="text-primary fw-bold small">Q{{ producto.precio.toFixed(2) }}</span>
                    <small :class="producto.stock > 0 ? 'text-success' : 'text-danger'">
                      {{ producto.stock > 0 ? 'Stock' : 'Agotado' }}
                    </small>
                </div>
              </div>
            </RouterLink>
            
            <div class="card-footer bg-white border-top-0 p-2 pt-0 button-footer">
                <button 
                    class="btn btn-primary w-100 btn-sm rounded-pill shadow-sm" 
                    :disabled="producto.stock <= 0 || carritoStore.cargando" 
                    @click.stop="manejarAgregarProducto(producto.id)"
                >
                    <span v-if="carritoStore.cargando" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    <i v-else class="bi bi-cart-plus me-1"></i> 
                    {{ producto.stock <= 0 ? 'Agotado' : 'Agregar' }}
                </button>
            </div>
        </div>
      </div>
      
    </div>

    <div v-else-if="!isLoading && allProductos.length === 0" class="alert alert-warning text-center mt-5">
        <i class="bi bi-info-circle me-2"></i> No se encontraron productos en el Marketplace.
    </div>

    <div v-if="totalPages > 1 && !isLoading" class="d-flex justify-content-center mt-4">
        <nav aria-label="Paginación de productos">
            <ul class="pagination pagination-sm">
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

/* --- CONTENEDOR GENERAL --- */
.product-container {
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  height: 100%;
}

.product-container:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.4rem 0.8rem rgba(0, 0, 0, 0.2) !important;
}

/* --- IMÁGENES CUADRADAS Y PEQUEÑAS --- */
.card-img-top {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-bottom: 1px solid #dee2e6;
}

/* --- TARJETAS MÁS COMPACTAS --- */
.small-card {
  font-size: 0.85rem;
}

/* --- DESCRIPCIÓN UNIFORME --- */
.description-text {
  min-height: 2.4rem;
  line-height: 1.2;
  overflow: hidden;
}

/* --- CARD BODY --- */
.card-body {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

/* --- FOOTER DEL BOTÓN --- */
.button-footer {
  border: none;
  margin-top: auto;
}

.btn-sm {
  font-size: 0.8rem;
  padding: 0.35rem 0.5rem;
}

/* --- REDUCCIÓN DE ESPACIOS ENTRE TARJETAS --- */
.row {
  margin-top: 0.5rem;
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from '@/plugins/axios';
import { useCarritoStore } from '@/stores/carrito';

// --- Estado local y Stores ---
const carritoStore = useCarritoStore();

const allProductos = ref([]);
const isLoading = ref(true);
const errorMessage = ref('');
const mensajeCarrito = ref(null);
const tipoMensaje = ref('');

// --- PAGINACIÓN ---
const currentPage = ref(1);
const pageSize = 15; // Más productos por página (por ser más pequeñas)

const totalPages = computed(() => Math.ceil(allProductos.value.length / pageSize));
const productosPaginados = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return allProductos.value.slice(start, start + pageSize);
});

// --- AGREGAR AL CARRITO ---
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

// --- OBTENER PRODUCTOS ---
const fetchMarketplaceProducts = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    const response = await axios.get('/productos');
    allProductos.value = response.data;
    currentPage.value = 1;
  } catch (error) {
    console.error('Error al cargar el Marketplace:', error);
    errorMessage.value = 'No se pudo cargar el Marketplace. El servidor puede estar inactivo.';
  } finally {
    isLoading.value = false;
  }
};

// --- CAMBIAR PÁGINA ---
const cambiarPagina = (nuevaPagina) => {
  if (nuevaPagina >= 1 && nuevaPagina <= totalPages.value) {
    currentPage.value = nuevaPagina;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

onMounted(() => {
  fetchMarketplaceProducts();
  carritoStore.cargarCarrito();
});
</script>
