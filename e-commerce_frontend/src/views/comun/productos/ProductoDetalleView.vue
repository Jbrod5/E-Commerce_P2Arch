<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import { obtenerDetalleProducto, enviarResena } from "@/api/producto";
import { useAuthStore } from "@/stores/auth";
import { useCarritoStore } from "@/stores/carrito";

const carritoStore = useCarritoStore();
const route = useRoute();
const authStore = useAuthStore();

const productoId = route.params.id;

// Datos del producto
const producto = ref(null);
const isLoading = ref(true);
const error = ref(null);

// Datos del formulario de reseña
const resenaForm = ref({
  calificacion: 5, // Valor por defecto
  comentario: "",
});
const isSendingResena = ref(false);
const resenaError = ref(null);
const resenaSuccess = ref(null);

// Computed para saber si el usuario ya ha reseñado
const yaReseno = computed(() => {
  if (!producto.value || !authStore.isAuthenticated) return false;
  const nombreUsuarioActual = authStore.user?.nombre;
  return producto.value.resenas.some((r) => r.nombreUsuario === nombreUsuarioActual);
});

// --- Lógica de Carga de Datos ---
const loadProductoDetalle = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    producto.value = await obtenerDetalleProducto(productoId);

    // Si el usuario ya reseñó, precargar sus datos en el formulario
    if (yaReseno.value) {
      const miResena = producto.value.resenas.find(
        (r) => r.nombreUsuario === authStore.user?.nombre
      );
      if (miResena) {
        resenaForm.value.calificacion = miResena.calificacion;
        resenaForm.value.comentario = miResena.comentario;
      }
    }
  } catch (err) {
    console.error("Error al cargar detalle del producto:", err);
    const status = err.response?.status;
    error.value =
      status === 404
        ? `Producto #${productoId} no encontrado o no está disponible.`
        : "Error al cargar el detalle del producto.";
  } finally {
    isLoading.value = false;
  }
};

// --- Lógica de Reseña ---
const submitResena = async () => {
  if (resenaForm.value.calificacion < 1 || resenaForm.value.calificacion > 5) {
    resenaError.value = "La calificación debe estar entre 1 y 5.";
    return;
  }

  isSendingResena.value = true;
  resenaError.value = null;
  resenaSuccess.value = null;
  try {
    await enviarResena(productoId, resenaForm.value);
    resenaSuccess.value = "¡Reseña enviada/actualizada exitosamente!";

    // Recargar el detalle
    await loadProductoDetalle();
  } catch (err) {
    console.error("Error al enviar reseña:", err);
    resenaError.value =
      "Error al enviar la reseña. " + (err.response?.data || err.message);
  } finally {
    isSendingResena.value = false;
  }
};

// --- Utilidades de Formato ---
const formatCurrency = (amount) => {
  const num = parseFloat(amount);
  if (isNaN(num)) return "Q0.00";
  return new Intl.NumberFormat("es-GT", { style: "currency", currency: "GTQ" }).format(
    num
  );
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString("es-GT", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
};

// Función para mostrar las estrellas (para promedio y reseñas individuales)
const getStars = (rating) => {
  const roundedRating = Math.round(rating);
  return Array.from({ length: 5 }, (_, index) => {
    return index < roundedRating;
  });
};

// Funcion para agregar al carrito :3
const manejarAgregarProducto = async () => {
  const cantidad = 1;
  try {
    await carritoStore.agregarOActualizarProducto(productoId, cantidad);
    alert("¡Producto agregado al carrito!");
  } catch (error) {
    console.error(error);
    alert("Error al agregar el producto.");
  }
};

onMounted(() => {
  if (productoId) {
    loadProductoDetalle();
  }
});
</script>

<template>
  <div class="container py-5">

    <div v-if="isLoading" class="text-center p-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Cargando producto...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else-if="producto" class="card shadow-lg p-md-5 p-3">
      <div class="row g-4">
        <div class="col-md-5">
          <img
            v-ngrok-img="producto.imagenUrl"
            class="img-fluid rounded-3 product-image"
            :alt="producto.nombre"
          />
        </div>

        <div class="col-md-7">
          <span class="badge bg-secondary mb-2">{{ producto.nombreCategoria }}</span>
          <h1 class="mb-2 display-6">{{ producto.nombre }}</h1>

          <div class="d-flex align-items-center mb-3">
            <div class="fs-5 me-3 text-warning">
              <i
                v-for="(isFilled, index) in getStars(producto.promedioCalificaciones)"
                :key="'star-avg-' + index"
                :class="isFilled ? 'bi bi-star-fill' : 'bi bi-star'"
              >
              </i>
            </div>
            <span class="text-muted"
              >({{ parseFloat(producto.promedioCalificaciones).toFixed(2) }} de 5,
              {{ producto.cantidadResenas }} reseñas)</span
            >
          </div>

          <h2 class="text-primary fw-bold mb-4">{{ formatCurrency(producto.precio) }}</h2>

          <p class="mb-1"><strong>Vendido por:</strong> {{ producto.nombreVendedor }}</p>
          <p class="mb-1" :class="producto.stock > 0 ? 'text-success' : 'text-danger'">
            <strong>Disponibilidad:</strong>
            {{ producto.stock > 0 ? `En Stock (${producto.stock})` : "Agotado" }}
          </p>
          <p class="mb-4">
            <strong>Condición:</strong>
            <span class="badge" :class="producto.esNuevo ? 'bg-info' : 'bg-warning'">{{
              producto.esNuevo ? "Nuevo" : "Usado"
            }}</span>
          </p>

          <button
            class="btn btn-success btn-lg w-100 mb-5"
            :disabled="producto.stock === 0 || carritoStore.cargando"
            @click="manejarAgregarProducto"
          >
            <i class="bi bi-cart-plus me-2"></i>
            {{ producto.stock > 0 ? "Agregar al Carrito" : "Sin Stock" }}
          </button>
        </div>
      </div>

      <hr class="my-5" />

      <div class="row">
        <div class="col-12">
          <ul class="nav nav-tabs" id="productTabs" role="tablist">
            <li class="nav-item" role="presentation">
              <button
                class="nav-link active"
                id="desc-tab"
                data-bs-toggle="tab"
                data-bs-target="#desc-pane"
                type="button"
                role="tab"
                aria-controls="desc-pane"
                aria-selected="true"
              >
                Descripción
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link"
                id="resenas-tab"
                data-bs-toggle="tab"
                data-bs-target="#resenas-pane"
                type="button"
                role="tab"
                aria-controls="resenas-pane"
                aria-selected="false"
              >
                Reseñas ({{ producto.cantidadResenas }})
              </button>
            </li>
          </ul>

          <div class="tab-content pt-3" id="productTabsContent">
            <div
              class="tab-pane fade show active"
              id="desc-pane"
              role="tabpanel"
              aria-labelledby="desc-tab"
              tabindex="0"
            >
              <h4 class="mb-3">Detalles del Producto</h4>
              <p class="text-secondary">{{ producto.descripcion }}</p>
            </div>

            <div
              class="tab-pane fade"
              id="resenas-pane"
              role="tabpanel"
              aria-labelledby="resenas-tab"
              tabindex="0"
            >
              <div v-if="authStore.isAuthenticated" class="card p-4 mb-4 bg-light">
                <h5 class="mb-3">
                  {{ yaReseno ? "Actualizar tu Reseña" : "Dejar una Reseña" }}
                </h5>

                <form @submit.prevent="submitResena">
                  <div class="mb-3">
                    <label class="form-label d-block">Tu Calificación:</label>
                    <div class="star-rating">
                      <i
                        v-for="n in 5"
                        :key="n"
                        @click="resenaForm.calificacion = n"
                        :class="
                          n <= resenaForm.calificacion
                            ? 'bi bi-star-fill text-warning'
                            : 'bi bi-star text-muted'
                        "
                        class="fs-3 cursor-pointer me-1"
                      >
                      </i>
                      <span class="ms-2 text-muted"
                        >{{ resenaForm.calificacion }} de 5</span
                      >
                    </div>
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Comentario (Opcional)</label>
                    <textarea
                      class="form-control"
                      v-model="resenaForm.comentario"
                      rows="3"
                    ></textarea>
                  </div>

                  <div v-if="resenaError" class="alert alert-danger">
                    {{ resenaError }}
                  </div>
                  <div v-if="resenaSuccess" class="alert alert-success">
                    {{ resenaSuccess }}
                  </div>

                  <button
                    type="submit"
                    class="btn btn-primary"
                    :disabled="isSendingResena || resenaForm.calificacion === 0"
                  >
                    {{ isSendingResena ? "Enviando..." : "Enviar Reseña" }}
                  </button>
                </form>
              </div>

              <div v-if="producto.resenas && producto.resenas.length">
                <div
                  v-for="resena in producto.resenas"
                  :key="resena.nombreUsuario"
                  class="border-bottom py-3"
                >
                  <p class="mb-1">
                    <strong class="me-2">{{ resena.nombreUsuario }}</strong>
                    <span class="text-warning">
                      <i
                        v-for="(isFilled, index) in getStars(resena.calificacion)"
                        :key="'star-res-' + index"
                        :class="isFilled ? 'bi bi-star-fill' : 'bi bi-star'"
                      >
                      </i>
                    </span>
                    <small class="text-muted ms-3">{{ formatDate(resena.fecha) }}</small>
                  </p>
                  <p class="mb-0 text-secondary">
                    {{ resena.comentario || "Sin comentario." }}
                  </p>
                </div>
              </div>
              <p v-else class="text-muted mt-3">
                Sé el primero en reseñar este producto.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-image {
  max-height: 500px;
  object-fit: contain;
  width: 100%;
  background-color: #f8f9fa;
}
.cursor-pointer {
  cursor: pointer;
  transition: color 0.2s;
}
/* Asegura que los íconos de estrella se vean como se espera */
.star-rating i.bi-star-fill,
.star-rating i.bi-star {
  font-size: 1.5rem; /* Ajuste para el tamaño de las estrellas */
}
</style>
