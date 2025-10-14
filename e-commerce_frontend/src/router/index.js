import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; 

// Importaciones directas de Vistas de Autenticación
import LoginView from '../views/LoginView.vue';
import RegistroView from '../views/RegistroView.vue';
// import HomeView from '../views/HomeView.vue'; // No la necesitamos si / es login






// Función de ayuda para obtener el nombre del dashboard según el rol
const getDashboardName = (rol) => {
    switch (rol) {
        case 'ROLE_ADMINISTRADOR': return 'admin-index';
        case 'ROLE_MODERADOR': return 'moderador-index';
        case 'ROLE_LOGISTICA': return 'logistica-index';
        case 'ROLE_COMUN': return 'comun-index';
        default: return 'login';
    }
}


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    
    
    // RUTA RAIZ (CORREGIDA): Apunta a Login
    {
        path: '/',
        name: 'login', // El nombre 'login' para el guard
        component: LoginView,
        meta: { requiresGuest: true } // Solo para no logueados
    },
    {
        path: '/register',
        name: 'register',
        component: RegistroView,
        meta: { requiresGuest: true }
    },


    // - - - - - - - - - - - - - - - - - - - - - - - - - POR ROL - - - - - - - - - - - - - - - - - - - - - - - - - 

// Administrador
    {
      path: '/admin', 
      name: 'admin-index',
      component: () => import('../views/administrador/IndexView.vue'), 
      meta: { requiresAuth: true, roles: ['ROLE_ADMINISTRADOR'] } // Cambiado
    },
    // Moderador
    {
      path: '/moderador',
      name: 'moderador-index',
      component: () => import('../views/moderador/IndexView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_MODERADOR'] } // Cambiado
    },
    // Logística
    {
      path: '/logistica',
      name: 'logistica-index',
      component: () => import('../views/logistica/IndexView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_LOGISTICA'] } // Cambiado
    },
    // Común (Perfil)
    {
      path: '/perfil',
      name: 'comun-index',
      component: () => import('../views/comun/IndexView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_COMUN'] } // Cambiado
    },
    
    // Ruta 404
    {
        path: '/:catchAll(.*)',
        name: 'NotFound',
        component: () => import('../views/NotFoundView.vue') 
    }
  ],
});

// ----------------------------------------------------
// Global Router Guard (CORREGIDO)
// ----------------------------------------------------
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const isAuthenticated = authStore.isAuthenticated;
    const userRole = authStore.rol; 

    // A. Redirección de NO AUTENTICADOS (Rutas Protegidas)
    // Funciona correctamente: si /admin no está logueado, va a /
    if (to.meta.requiresAuth && !isAuthenticated) {
        return next({ name: 'login' });
    }

    // B. Redirección de AUTENTICADOS (Corregido: redirige a su dashboard de rol)
    if (to.meta.requiresGuest && isAuthenticated) {
        const dashboardName = getDashboardName(userRole);
        return next({ name: dashboardName });
    }
    
    // C. Verificación de Rol (Acceso a Dashboards específicos)
    if (to.meta.roles && isAuthenticated) {
        const requiredRoles = to.meta.roles;

        if (requiredRoles.includes(userRole)) {
            // Rol correcto, continuar
            return next();
        } else {
            // Redirige a SU PROPIO dashboard
            const dashboardName = getDashboardName(userRole);
            return next({ name: dashboardName }); 
        }
    }
    
    // D. Continuar normalmente
    return next();
});


export default router;