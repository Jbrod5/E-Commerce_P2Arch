<template>
  <div class="container py-5">
    <h1 class="mb-4">Mi Perfil y Administración</h1>
    <p class="lead">Accede rápidamente a tus herramientas de gestión y datos personales.</p>

    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mt-4">
      
      <div class="col">
        <div class="card h-100 shadow-sm custom-card card-tarjetas">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-credit-card-fill me-2"></i> Gestión de Tarjetas</h5>
            <p class="card-text text-muted">Añade, edita o elimina las tarjetas de crédito/débito guardadas para tus compras.</p>
            <router-link :to="{ name: 'gestionTarjetas' }" class="btn btn-primary mt-2">
              Administrar Tarjetas
            </router-link>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card h-100 shadow-sm custom-card card-pedidos">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-bag-check-fill me-2"></i> Mis Pedidos</h5>
            <p class="card-text text-muted">Consulta el historial completo de tus compras, seguimiento y estados de envío.</p>
            <router-link :to="{ name: 'misPedidos' }" class="btn btn-primary mt-2">
              Ver historial de pedidos
            </router-link>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card h-100 shadow-sm custom-card card-notificaciones">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-bell-fill me-2"></i> Notificaciones</h5>
            <p class="card-text text-muted">Revisa las alertas de la plataforma, aprobaciones de productos y mensajes importantes.</p>
            <button class="btn btn-outline-secondary mt-2" disabled>Ver Notificaciones (TO-DO)</button>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card h-100 shadow-sm custom-card card-ventas">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-graph-up me-2"></i> Mis Ventas</h5>
            <p class="card-text text-muted">Accede al registro de tus ventas, comisiones y ganancias como vendedor.</p>
            <button class="btn btn-outline-secondary mt-2" disabled>Ver Reporte (TO-DO)</button>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card h-100 shadow-sm custom-card card-info">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-person-fill me-2"></i> Información Personal</h5>
            <p class="card-text text-muted">Actualiza tu nombre, dirección principal o cambia tu contraseña de acceso.</p>
            <button class="btn btn-outline-secondary mt-2" disabled>Editar Perfil (TO-DO)</button>
          </div>
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup>
// No necesita mucha lógica, es principalmente navegación
</script>

<style scoped>
.custom-card {
    transition: transform 0.2s, box-shadow 0.2s;
    border-left: 5px solid transparent;
}
.custom-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-tarjetas { border-left-color: #007bff; }
.card-pedidos { border-left-color: #28a745; }
.card-notificaciones { border-left-color: #ffc107; }
.card-ventas { border-left-color: #dc3545; }
.card-info { border-left-color: #6c757d; }
</style>