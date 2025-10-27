<template>
  <!-- Modal de Bootstrap -->
  <div class="modal fade show d-block" tabindex="-1" role="dialog" @click.self="cerrar">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        
        <!-- Encabezado del Modal -->
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">
            <i class="bi bi-pencil-square me-2"></i> Editar Empleado (ID: {{ form.idUsuario }})
          </h5>
          <button type="button" class="btn-close btn-close-white" @click="cerrar"></button>
        </div>

        <!-- Cuerpo del Modal (Formulario) -->
        <div class="modal-body">
          <form @submit.prevent="guardarCambios">
            
            <!-- Campo Nombre -->
            <div class="mb-3">
              <label for="nombre" class="form-label">Nombre Completo</label>
              <input type="text" id="nombre" class="form-control" v-model="form.nombre" required />
            </div>

            <!-- Campo Correo -->
            <div class="mb-3">
              <label for="correo" class="form-label">Correo Electrónico</label>
              <input type="email" id="correo" class="form-control" v-model="form.correo" required />
              <div v-if="errores.correo" class="text-danger small mt-1">{{ errores.correo }}</div>
            </div>

            <!-- Campo Contraseña (Opcional) -->
            <div class="mb-3">
              <label for="contrasena" class="form-label">Nueva Contraseña (Dejar vacío para no cambiar)</label>
              <input type="password" id="contrasena" class="form-control" v-model="form.contrasena" />
            </div>

            <!-- Estado Activo -->
            <div class="mb-4 form-check form-switch">
              <input 
                class="form-check-input" 
                type="checkbox" 
                role="switch" 
                id="estadoActivo" 
                v-model="form.activo"
              >
              <label class="form-check-label" for="estadoActivo">
                Estado: <span :class="form.activo ? 'text-success fw-bold' : 'text-danger fw-bold'">
                    {{ form.activo ? 'ACTIVO' : 'INACTIVO (Suspendido)' }}
                </span>
              </label>
            </div>
            
            <div v-if="errores.general" class="alert alert-danger p-2 small">{{ errores.general }}</div>
            
            <!-- Botones de Acción -->
            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="cerrar">Cancelar</button>
              <button type="submit" class="btn btn-success" :disabled="isSaving">
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                {{ isSaving ? 'Guardando...' : 'Guardar Cambios' }}
              </button>
            </div>
            
          </form>
        </div>
      </div>
    </div>
  </div>
  <!-- Overlay (fondo semi-transparente) -->
  <div class="modal-backdrop fade show"></div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';
import adminApi from '@/api/admin'; // Asegúrate que la ruta sea correcta

const props = defineProps({
  empleado: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['cerrar', 'empleado-actualizado']);

// Estado local del formulario
const form = ref({});
const isSaving = ref(false);
const errores = ref({ general: null, correo: null });

// Inicializa el formulario cuando la prop 'empleado' cambie
watch(() => props.empleado, (newEmpleado) => {
  if (newEmpleado) {
    // Copia los datos esenciales del empleado a la forma
    form.value = {
      idUsuario: newEmpleado.id,
      nombre: newEmpleado.nombre,
      correo: newEmpleado.correo,
      activo: newEmpleado.activo,
      contrasena: '' // La contraseña siempre se inicializa vacía
    };
    // Limpia errores
    errores.value = { general: null, correo: null };
  }
}, { immediate: true });


/**
 * Cierra el modal y emite el evento 'cerrar'.
 */
const cerrar = () => {
  emit('cerrar');
};

/**
 * Llama a la API para guardar los cambios del empleado.
 */
const guardarCambios = async () => {
  isSaving.value = true;
  errores.value = { general: null, correo: null };
  
  // 1. Construir el payload (solo campos que se envían)
  const payload = {
    nombre: form.value.nombre,
    correo: form.value.correo,
    activo: form.value.activo
  };

  // 2. Agregar contraseña si no está vacía
  if (form.value.contrasena.trim()) {
    payload.contrasena = form.value.contrasena;
  }
  
  try {
    // 3. Llamar al endpoint PUT /api/admin/empleados/{id}
    await adminApi.actualizarEmpleado(form.value.idUsuario, payload);
    
    // 4. Éxito: Emitir evento para recargar la lista y cerrar
    // En una app real, mostrarías una notificación de éxito aquí
    emit('empleado-actualizado'); 

  } catch (error) {
    console.error('Error al actualizar empleado:', error.response?.data || error.message);
    const apiError = error.response?.data?.error || 'Error desconocido al actualizar.';
    
    if (apiError.includes('correo ya está registrado')) {
      errores.value.correo = apiError;
    } else {
      errores.value.general = apiError;
    }
  } finally {
    isSaving.value = false;
  }
};
</script>

<style scoped>
/* Estilos necesarios para que el modal de Vue se parezca al de Bootstrap */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1050;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  outline: 0;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1040;
  width: 100vw;
  height: 100vh;
  background-color: #000;
  opacity: 0.5;
}
</style>
