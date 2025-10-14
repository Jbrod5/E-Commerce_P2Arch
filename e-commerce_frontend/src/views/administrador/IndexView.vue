<template>
  <div class="container mt-5">
    <div class="alert alert-danger" role="alert">
      <h2>Administrador</h2>
      <p class="lead">Bienvenido, {{ authStore.user.correo }}. Eres un administrador >:3.</p>
      <button @click="authStore.logout(); $router.push('/')" class="btn btn-warning mt-3">Cerrar Sesión</button>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
</script>