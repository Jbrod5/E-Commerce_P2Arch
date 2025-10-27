<template>
  <div class="container-fluid py-5 px-4 bg-light min-vh-100">
    <h1 class="mb-5 text-primary border-bottom pb-2 font-weight-bold">
      Registrar Nuevo Producto
    </h1>
    <div class="card shadow-lg p-5 rounded-3">
      <!-- Ya NO usamos enctype="multipart/form-data" -->
      <form v-on:submit.prevent="submitProduct" novalidate>
        <div class="row">
          <!-- Columna 1: Datos del Producto -->
          <div class="col-md-6 border-end pe-4">
            <h5 class="mb-4 text-secondary">Información General</h5>

            <!-- Nombre -->
            <div class="mb-3">
              <label for="nombre" class="form-label fw-bold">Nombre del Producto</label>
              <input
                type="text"
                id="nombre"
                v-model="form.nombre"
                class="form-control"
                required
              />
            </div>

            <!-- Descripción -->
            <div class="mb-3">
              <label for="descripcion" class="form-label fw-bold">Descripción</label>
              <textarea
                id="descripcion"
                v-model="form.descripcion"
                class="form-control"
                rows="3"
                required
              ></textarea>
            </div>

            <div class="row">
              <!-- Precio -->
              <div class="col-md-6 mb-3">
                <label for="precio" class="form-label fw-bold">Precio (Q)</label>
                <input
                  type="number"
                  id="precio"
                  v-model.number="form.precio"
                  class="form-control"
                  required
                  min="0.01"
                  step="0.01"
                />
              </div>

              <!-- Stock -->
              <div class="col-md-6 mb-3">
                <label for="stock" class="form-label fw-bold">Stock / Cantidad</label>
                <input
                  type="number"
                  id="stock"
                  v-model.number="form.stock"
                  class="form-control"
                  required
                  min="1"
                />
              </div>
            </div>

            <!-- Categoría (Placeholder) -->
            <div class="mb-3">
              <label for="idCategoria" class="form-label fw-bold">Categoría</label>
              <select
                id="idCategoria"
                v-model.number="form.idCategoria"
                class="form-select"
                required
              >
                <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                  {{ cat.nombre }}
                </option>
              </select>
            </div>

            <!-- Es Nuevo Checkbox -->
            <div class="form-check mb-3">
              <input
                class="form-check-input"
                type="checkbox"
                v-model="form.esNuevo"
                id="esNuevo"
              />
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
              <label for="imagenFile" class="form-label fw-bold"
                >Seleccionar Imagen</label
              >
              <!-- Usamos @change para activar el handler de conversión a Base64 -->
              <input
                class="form-control"
                type="file"
                id="imagenFile"
                @change="handleFileUpload"
                required
                accept="image/*"
              />
              <div class="form-text">Máx. 5MB.</div>
            </div>

            <!-- Previsualización de la Imagen -->
            <!-- Usamos form.imagenBase64 (que ahora es el URL de datos) -->
            <div
              v-if="form.imagenBase64"
              class="mt-4 p-3 border rounded bg-white shadow-sm"
            >
              <p class="fw-bold text-muted mb-2">Previsualización:</p>
              <img
                :src="form.imagenBase64"
                alt="Previsualización del producto"
                class="img-fluid rounded shadow-sm"
                style="max-height: 250px; width: 100%; object-fit: cover"
              />
            </div>

            <!-- Mensaje de error específico para la imagen si el Base64 falla -->
            <div v-if="imageError" class="alert alert-warning mt-2">{{ imageError }}</div>
          </div>
        </div>

        <!-- Botón de Envío -->
        <hr class="mt-5" />
        <button
          type="submit"
          :disabled="isLoading"
          class="btn btn-primary btn-lg w-100 mt-4 rounded-pill shadow"
        >
          <span
            v-if="isLoading"
            class="spinner-border spinner-border-sm me-2"
            role="status"
            aria-hidden="true"
          ></span>
          {{ isLoading ? "Guardando..." : "Guardar Producto" }}
        </button>

        <!-- Mensajes de Estado Globales -->
        <div v-if="errorMessage" class="alert alert-danger mt-4">{{ errorMessage }}</div>
        <div v-if="successMessage" class="alert alert-success mt-4">
          {{ successMessage }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import axios from "@/plugins/axios"; // Asume que este archivo configura la base URL y el interceptor
import { onMounted } from "vue";

const isLoading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const imageError = ref(""); // Para errores específicos de imagen

// Lista real de categorías desde el backend
const categorias = ref([]);

// Cargar las categorías al montar la vista
onMounted(async () => {
  try {
    const response = await axios.get("/utilidades/categorias"); // Llama a /api/categorias automáticamente por baseURL
    categorias.value = response.data;
  } catch (error) {
    console.error("Error al cargar categorías:", error);
  }
});

// Estado del formulario
const form = reactive({
  nombre: "",
  descripcion: "",
  precio: 0.01,
  stock: 1,
  idCategoria: 1,
  esNuevo: true,
  imagenBase64: null, // NUEVO CAMPO para almacenar la cadena Base64
});

/**
 * Maneja la selección del archivo y lo convierte a Base64.
 */
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  form.imagenBase64 = null; // Resetear Base64
  imageError.value = "";

  if (file) {
    // Validación de tamaño (Max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      imageError.value = "El archivo es demasiado grande. Máximo 5MB.";
      event.target.value = null; // Limpiar el input file
      return;
    }

    const reader = new FileReader();

    // Función que se ejecuta cuando el archivo ha sido leído
    reader.onload = (e) => {
      // e.target.result es la cadena Base64 con el prefijo (data:image/png;base64,...)
      form.imagenBase64 = e.target.result;
    };

    // Función que se ejecuta si hay un error de lectura
    reader.onerror = () => {
      imageError.value = "No se pudo leer el archivo.";
    };

    // Iniciar la lectura del archivo como Base64
    reader.readAsDataURL(file);
  } else {
    imageError.value = "Debe seleccionar una imagen para el producto.";
  }
};

/**
 * Envia la data del formulario como JSON.
 * Ya no necesitamos FormData.
 */
const submitProduct = async () => {
  errorMessage.value = "";
  successMessage.value = "";
  imageError.value = "";

  if (!form.imagenBase64) {
    errorMessage.value = "Debe seleccionar y cargar una imagen Base64 antes de enviar.";
    return;
  }

  isLoading.value = true;

  // 1. Crear el DTO final.
  // Opcional: Si el backend SÓLO quiere la data pura (sin el prefijo 'data:image/...'),
  // se lo quitamos aquí antes de enviar:
  const payload = { ...form }; // Copiamos el formulario

  // Si el Base64 empieza con el prefijo y el backend NO lo quiere
  if (payload.imagenBase64.startsWith("data:")) {
    payload.imagenBase64 = payload.imagenBase64.split(",")[1];
  }

  try {
    // 2. Hacer la petición POST a la API.
    // El Content-Type por defecto de Axios será 'application/json', lo cual es correcto.
    const response = await axios.post("/productos", payload);

    successMessage.value = `Producto "${response.data.nombre}" creado exitosamente (ID: ${response.data.id}).`;

    // Opcional: limpiar el formulario después del éxito
    Object.assign(form, {
      nombre: "",
      descripcion: "",
      precio: 0.01,
      stock: 1,
      idCategoria: categorias.value.length > 0 ? categorias.value[0].id : null,
      esNuevo: true,
      imagenBase64: null, // Limpiar la Base64 y la previsualización
    });
  } catch (error) {
    if (error.response) {
      if (error.response.status === 403 || error.response.status === 401) {
        // El 403 (Forbidden) original que te salió.
        errorMessage.value =
          "Acceso Denegado (401/403): Su sesión ha expirado o no tiene permisos. Verifique su login y las Cookies.";
      } else if (error.response.status === 400) {
        // Error de validación, quizás el Base64 era muy grande o datos inválidos.
        errorMessage.value = `Error de validación (400): ${
          error.response.data?.message || "Verifique los datos ingresados."
        }`;
      } else {
        errorMessage.value = `Error al crear el producto. Código: ${
          error.response.status
        } - ${error.response.data?.message || "Error en el servidor"}`;
      }
    } else {
      errorMessage.value =
        "Error de red. Asegúrese de que el servidor Spring Boot esté funcionando.";
    }
    console.error("Error al enviar producto:", error);
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
