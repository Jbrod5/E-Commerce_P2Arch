<template>
  <div class="container-fluid py-5 px-4 bg-light min-vh-100">
    <h1 class="mb-5 text-primary border-bottom pb-2 font-weight-bold">
      Registrar Nuevo Producto
    </h1>
    <div class="card shadow-lg p-5 rounded-3">
      
      <!-- Se ha eliminado el comentario anterior para evitar errores de compilación en el template -->
      <form v-on:submit.prevent="submitProduct" enctype="multipart/form-data" novalidate>

        <div class="row">
          <!-- Columna 1: Datos del Producto -->
          <div class="col-md-6 border-end pe-4">
            <h5 class="mb-4 text-secondary">Información General</h5>

            <!-- Nombre -->
            <div class="mb-3">
              <label for="nombre" class="form-label fw-bold">Nombre del Producto</label>
              <input type="text" id="nombre" v-model="form.nombre" class="form-control" required>
            </div>

            <!-- Descripción -->
            <div class="mb-3">
              <label for="descripcion" class="form-label fw-bold">Descripción</label>
              <textarea id="descripcion" v-model="form.descripcion" class="form-control" rows="3" required></textarea>
            </div>

            <div class="row">
              <!-- Precio -->
              <div class="col-md-6 mb-3">
                <label for="precio" class="form-label fw-bold">Precio (Q)</label>
                <input type="number" id="precio" v-model.number="form.precio" class="form-control" required min="0.01" step="0.01">
              </div>

              <!-- Stock -->
              <div class="col-md-6 mb-3">
                <label for="stock" class="form-label fw-bold">Stock / Cantidad</label>
                <input type="number" id="stock" v-model.number="form.stock" class="form-control" required min="1">
              </div>
            </div>

            <!-- Categoría (Placeholder) -->
            <div class="mb-3">
              <label for="idCategoria" class="form-label fw-bold">Categoría</label>
              <select id="idCategoria" v-model.number="form.idCategoria" class="form-select" required>
                <!-- Usando mock data en el script para hacerlo dinámico -->
                <option v-for="cat in categoriasMock" :key="cat.id" :value="cat.id">
                  {{ cat.nombre }} (ID: {{ cat.id }})
                </option>
              </select>
            </div>

            <!-- Es Nuevo Checkbox -->
            <div class="form-check mb-3">
              <input class="form-check-input" type="checkbox" v-model="form.esNuevo" id="esNuevo">
              <label class="form-check-label" for="esNuevo">
                Marcar como Producto Nuevo
              </label>
            </div>

          </div>

          <!-- Columna 2: Subida de Imagen -->
          <div class="col-md-6 ps-4">
            <h5 class="mb-4 text-secondary">Imagen del Producto</h5>
            
            <!-- Campo de Archivo -->
            <div class="mb-3">
              <label for="imagenFile" class="form-label fw-bold">Seleccionar Imagen</label>
              <input class="form-control" type="file" id="imagenFile" @change="handleFileUpload" required>
              <div class="form-text">Formatos permitidos: JPG, PNG. Máx. 5MB.</div>
            </div>

            <!-- Previsualización de la Imagen -->
            <div v-if="imageUrl" class="mt-4 p-3 border rounded bg-white shadow-sm">
              <p class="fw-bold text-muted mb-2">Previsualización:</p>
              <img 
                :src="imageUrl" 
                alt="Previsualización del producto" 
                class="img-fluid rounded shadow-sm" 
                style="max-height: 250px; width: 100%; object-fit: cover;"
              >
            </div>
          </div>
        </div>

        <!-- Botón de Envío -->
        <hr class="mt-5">
        <button 
          type="submit" 
          :disabled="isLoading"
          class="btn btn-primary btn-lg w-100 mt-4 rounded-pill shadow"
        >
          <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ isLoading ? 'Guardando...' : 'Guardar Producto' }}
        </button>

        <!-- Mensajes de Estado -->
        <div v-if="errorMessage" class="alert alert-danger mt-4">{{ errorMessage }}</div>
        <div v-if="successMessage" class="alert alert-success mt-4">{{ successMessage }}</div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onBeforeUnmount } from 'vue';
import axios from '@/plugins/axios'; // Asume que este archivo configura la base URL y el interceptor

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const imageUrl = ref(null); 
const file = ref(null); 
const currentObjectUrl = ref(null); 

// Data mock para las categorías
const categoriasMock = [
  { id: 1, nombre: 'Electrónica' },
  { id: 2, nombre: 'Ropa' },
];

// Estado del formulario
const form = reactive({
  nombre: '',
  descripcion: '',
  precio: 0.01,
  stock: 1,
  idCategoria: 1, 
  esNuevo: true,
});

/**
 * Revoca el URL de objeto temporal para liberar memoria.
 */
const revokeTemporaryUrl = () => {
  if (currentObjectUrl.value) {
    URL.revokeObjectURL(currentObjectUrl.value);
    currentObjectUrl.value = null;
    imageUrl.value = null;
  }
};

/**
 * Hook de ciclo de vida: Asegura que el URL se revoque si el usuario abandona el componente.
 */
onBeforeUnmount(() => {
    revokeTemporaryUrl();
});

/**
 * Maneja la selección del archivo, revoca el URL anterior y crea uno nuevo para previsualizar.
 */
const handleFileUpload = (event) => {
  const selectedFile = event.target.files[0];
  
  revokeTemporaryUrl(); 
  
  if (selectedFile) {
    file.value = selectedFile;
    const newUrl = URL.createObjectURL(selectedFile);
    currentObjectUrl.value = newUrl; 
    imageUrl.value = newUrl; 
    errorMessage.value = ''; 
  } else {
    file.value = null;
  }
};

/**
 * Envia la data del formulario como multipart/form-data.
 * Confiamos en el interceptor de axios para añadir el JWT desde las Cookies.
 */
const submitProduct = async () => {
  errorMessage.value = '';
  successMessage.value = '';
  
  if (!file.value) {
    errorMessage.value = 'Debe seleccionar una imagen para el producto.';
    return;
  }
  
  isLoading.value = true;

  // 1. Crear el objeto FormData
  const formData = new FormData();
  
  // 2. Añadir la parte JSON del DTO bajo la clave 'data'
  // Es importante usar JSON.stringify para que Spring pueda mapear esto a un DTO en el backend
  formData.append('data', JSON.stringify(form)); 
  
  // 3. Añadir la parte del archivo bajo la clave 'imagenFile'
  formData.append('imagenFile', file.value);

  try {
    // 4. Hacer la petición POST a la API.
    // **CAMBIO CLAVE**: Pasamos la configuración de headers para asegurar que el Content-Type
    // sea manejado correctamente por el navegador como 'multipart/form-data',
    // evitando el error 'application/octet-stream' en el backend.
    const response = await axios.post('/productos', formData, {
        headers: {
            'Content-Type': undefined // Deja que el navegador establezca multipart/form-data automáticamente
        }
    });

    successMessage.value = `Producto "${response.data.nombre}" creado exitosamente (ID: ${response.data.id}).`;
    
    // Opcional: limpiar el formulario después del éxito
    Object.assign(form, {
        nombre: '',
        descripcion: '',
        precio: 0.01,
        stock: 1,
        idCategoria: categoriasMock[0].id,
        esNuevo: true,
    });
    
    revokeTemporaryUrl(); // Limpiar la previsualización

  } catch (error) {
    if (error.response) {
      if (error.response.status === 403 || error.response.status === 401) {
        // 401/403 es el error más probable si la configuración de Content-Type se arregló
        errorMessage.value = 'Acceso Denegado (401/403): Su sesión ha expirado o no tiene permisos. Verifique su login y las Cookies.';
      } else if (error.response.status === 400) {
         errorMessage.value = 'Error de validación (400): Verifique los datos ingresados y que la imagen sea válida.';
      } else {
         errorMessage.value = `Error al crear el producto. Código: ${error.response.status} - ${error.response.data?.message || 'Error en el servidor'}`;
      }
    } else {
      errorMessage.value = 'Error de red. Asegúrese de que el servidor Spring Boot esté funcionando.';
    }
    console.error('Error al enviar producto:', error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* No se necesitan estilos adicionales si se usa Bootstrap */
.min-vh-100 {
    min-height: 100vh;
}
</style>
