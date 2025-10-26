<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-warning fw-bold">
      <i class="bi bi-person-slash me-2"></i> Gestión de Sanciones
    </h1>
    <p class="text-secondary">
      Aquí podrás supervisar el comportamiento de los vendedores y suspender cuentas que infrinjan las normas.
    </p>

    <!-- Pestañas de Navegación -->
    <ul class="nav nav-tabs mb-4" id="sancionesTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="suspender-tab" data-bs-toggle="tab" data-bs-target="#suspender" type="button" role="tab">
                Suspender Usuario
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="historial-tab" data-bs-toggle="tab" data-bs-target="#historial" type="button" role="tab">
                Historial de Sanciones
            </button>
        </li>
    </ul>

    <!-- Contenido de las Pestañas -->
    <div class="tab-content" id="sancionesTabsContent">
        <!-- Pestaña 1: Suspender Usuario -->
        <div class="tab-pane fade show active" id="suspender" role="tabpanel">
            <div class="card shadow-sm p-4">
                <h5 class="card-title">Aplicar Nueva Suspensión</h5>
                <p class="card-text text-danger small">
                    Recuerda que suspender una cuenta la desactiva (`activo=false`) y debe tener una fecha de fin.
                </p>
                <!-- Formulario de Suspensión (a implementar) -->
                <div class="alert alert-warning mt-3">
                    **PENDIENTE DE IMPLEMENTACIÓN:** Aquí irá el formulario para buscar al vendedor, ingresar el motivo, y establecer la fecha de fin de la suspensión.
                </div>
            </div>
        </div>

        <!-- Pestaña 2: Historial -->
        <div class="tab-pane fade" id="historial" role="tabpanel">
            <div class="card shadow-sm p-4">
                <h5 class="card-title">Registro de Suspensiones</h5>
                <p class="card-text text-secondary small">
                    Tabla con el historial de la tabla `suspension`.
                </p>
                <!-- Tabla del Historial (a implementar) -->
                <div class="alert alert-info mt-3">
                    **PENDIENTE DE IMPLEMENTACIÓN:** Aquí irá la tabla con el historial de suspensiones, con datos como: Vendedor, Moderador, Motivo, Fecha Inicio, Fecha Fin, y Estado (Activa/Inactiva).
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
</script>
