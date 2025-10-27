<template>
  <!-- Modal de Bootstrap -->
  <div class="modal fade show d-block" tabindex="-1" role="dialog" @click.self="cerrar">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        
        <!-- Encabezado del Modal -->
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">
            <i class="bi bi-person-plus-fill me-2"></i> Registrar Nuevo Empleado
          </h5>
          <button type="button" class="btn-close btn-close-white" @click="cerrar"></button>
        </div>

        <!-- Cuerpo del Modal (Formulario) -->
        <div class="modal-body">
          <form @submit.prevent="guardarRegistro">
            
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

            <!-- Campo Contraseña -->
            <div class="mb-3">
              <label for="contrasena" class="form-label">Contraseña</label>
              <input type="password" id="contrasena" class="form-control" v-model="form.contrasena" required />
            </div>
            
            <!-- Campo Rol -->
            <div class="mb-4">
              <label for="rol" class="form-label">Rol de Empleado</label>
              <select id="rol" class="form-select" v-model="form.rolNombre" required>
                <option value="" disabled>Seleccione un Rol</option>
                <option value="administrador">Administrador</option>
                <option value="moderador">Moderador</option>
                <option value="logistica">Logística</option>
              </select>
            </div>
            
            <div v-if="errores.general" class="alert alert-danger p-2 small">{{ errores.general }}</div>
            
            <!-- Botones de Acción -->
            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" @click="cerrar">Cancelar</button>
              <button type="submit" class="btn btn-success" :disabled="isSaving">
                <span v-if="isSaving" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                {{ isSaving ? 'Registrando...' : 'Registrar Empleado' }}
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
import { ref, defineEmits } from 'vue';
import adminApi from '@/api/admin'; 

const emit = defineEmits(['cerrar', 'empleado-actualizado']);

// Estado inicial del formulario
const form = ref({
  nombre: '',
  correo: '',
  contrasena: '',
  rolNombre: '' // Aquí guardamos el nombre del rol seleccionado
});

const isSaving = ref(false);
const errores = ref({ general: null, correo: null });

/**
 * Cierra el modal y emite el evento 'cerrar'.
 */
const cerrar = () => {
  emit('cerrar');
};

/**
 * Llama a la API para registrar al nuevo empleado.
 */
const guardarRegistro = async () => {
  isSaving.value = true;
  errores.value = { general: null, correo: null };
  
  // Construir el payload según lo que espera el backend (UsuarioService.registrarEmpleado)
  const payload = {
    nombre: form.value.nombre,
    correo: form.value.correo,
    contrasena: form.value.contrasena,
    // El backend espera el rol como un objeto con la propiedad 'nombre'
    rol: { nombre: form.value.rolNombre } 
  };
  
  try {
    // Llamar al endpoint POST /api/admin/empleados
    await adminApi.registrarEmpleado(payload);
    
    // Éxito: Emitir evento para recargar la lista y cerrar
    emit('empleado-actualizado'); 

  } catch (error) {
    console.error('Error al registrar empleado:', error.response?.data || error.message);
    const apiError = error.response?.data?.error || 'Error desconocido al registrar.';
    
    if (apiError.includes('El correo ya está registrado')) {
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
