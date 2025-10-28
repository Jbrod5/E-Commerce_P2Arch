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
    
    <!-- BARRA DE FILTROS -->
    <div v-if="!isLoading" class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <!-- Búsqueda por nombre -->
          <div class="col-md-4">
            <label for="busqueda" class="form-label small fw-bold">
              <i class="bi bi-search"></i> Buscar Producto
            </label>
            <input 
              type="text" 
              id="busqueda"
              class="form-control form-control-sm" 
              placeholder="Nombre del producto..."
              v-model="filtros.busqueda"
            />
          </div>

          <!-- Filtro por categoría -->
          <div class="col-md-3">
            <label for="categoria" class="form-label small fw-bold">
              <i class="bi bi-tag"></i> Categoría
            </label>
            <select 
              id="categoria"
              class="form-select form-select-sm" 
              v-model="filtros.categoriaId"
            >
              <option value="">Todas las categorías</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                {{ cat.nombre }}
              </option>
            </select>
          </div>

          <!-- Ordenar por precio -->
          <div class="col-md-3">
            <label for="orden" class="form-label small fw-bold">
              <i class="bi bi-sort-numeric-down"></i> Ordenar por Precio
            </label>
            <select 
              id="orden"
              class="form-select form-select-sm" 
              v-model="filtros.ordenPrecio"
            >
              <option value="">Sin ordenar</option>
              <option value="asc">Menor a Mayor</option>
              <option value="desc">Mayor a Menor</option>
            </select>
          </div>

          <!-- Solo productos nuevos -->
          <div class="col-md-2">
            <label class="form-label small fw-bold d-block">
              <i class="bi bi-stars"></i> Filtros
            </label>
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="soloNuevos"
                v-model="filtros.soloNuevos"
              />
              <label class="form-check-label small" for="soloNuevos">
                Solo productos nuevos
              </label>
            </div>
          </div>
        </div>

        <!-- Botón para limpiar filtros -->
        <div class="row mt-3">
          <div class="col-12">
            <button 
              class="btn btn-outline-secondary btn-sm"
              @click="limpiarFiltros"
            >
              <i class="bi bi-x-circle"></i> Limpiar Filtros
            </button>
            <span class="ms-3 small text-muted">
              Mostrando {{ productosFiltrados.length }} de {{ allProductos.length }} productos
            </span>
          </div>
        </div>
      </div>
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
                
                <!-- CATEGORÍA AGREGADA -->
                <div class="mb-2">
                  <span class="badge bg-secondary text-white small">
                    <i class="bi bi-tag-fill"></i> 
                    {{ producto.categoria?.nombre || 'Sin categoría' }}
                  </span>
                </div>
                
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

    <div v-else-if="!isLoading && productosFiltrados.length === 0 && allProductos.length > 0" class="alert alert-warning text-center mt-5">
        <i class="bi bi-funnel me-2"></i> No se encontraron productos con los filtros aplicados. Intenta ajustar tu búsqueda.
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
const categorias = ref([]);
const isLoading = ref(true);
const errorMessage = ref('');
const mensajeCarrito = ref(null);
const tipoMensaje = ref('');

// --- FILTROS ---
const filtros = ref({
  busqueda: '',
  categoriaId: '',
  ordenPrecio: '',
  soloNuevos: false
});

// --- PAGINACIÓN ---
const currentPage = ref(1);
const pageSize = 15;

// Computed: Productos filtrados
const productosFiltrados = computed(() => {
  let productos = [...allProductos.value];

  // Filtro de búsqueda por nombre
  if (filtros.value.busqueda.trim()) {
    const busqueda = filtros.value.busqueda.toLowerCase();
    productos = productos.filter(p => 
      p.nombre.toLowerCase().includes(busqueda) ||
      p.descripcion.toLowerCase().includes(busqueda)
    );
  }

  // Filtro por categoría
  if (filtros.value.categoriaId) {
    productos = productos.filter(p => 
      p.categoria?.id === filtros.value.categoriaId
    );
  }

  // Filtro solo nuevos
  if (filtros.value.soloNuevos) {
    productos = productos.filter(p => p.esNuevo === true);
  }

  // Ordenar por precio
  if (filtros.value.ordenPrecio === 'asc') {
    productos.sort((a, b) => a.precio - b.precio);
  } else if (filtros.value.ordenPrecio === 'desc') {
    productos.sort((a, b) => b.precio - a.precio);
  }

  return productos;
});

const totalPages = computed(() => Math.ceil(productosFiltrados.value.length / pageSize));

const productosPaginados = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return productosFiltrados.value.slice(start, start + pageSize);
});

// --- FUNCIONES ---
const limpiarFiltros = () => {
  filtros.value = {
    busqueda: '',
    categoriaId: '',
    ordenPrecio: '',
    soloNuevos: false
  };
  currentPage.value = 1;
};

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

// --- OBTENER CATEGORÍAS ---
const fetchCategorias = async () => {
  try {
    const response = await axios.get('/utilidades/categorias');
    categorias.value = response.data;
  } catch (error) {
    console.error('Error al cargar categorías:', error);
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
  fetchCategorias();
  carritoStore.cargarCarrito();
});
</script>