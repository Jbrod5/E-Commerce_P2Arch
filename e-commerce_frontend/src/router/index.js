import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; 

// Importaciones directas de Vistas de Autenticación
import LoginView from '../views/LoginView.vue';
import RegistroView from '../views/RegistroView.vue';





// Usuario comun/vendedor
import VendedorLayout from '@/layouts/VendedorLayout.vue';
import CrearProductoView from '@/views/comun/CrearProductoView.vue'; 


import IndexView from '@/views/comun/IndexView.vue'; 


import CarritoView from '@/views/comun/pedido/CarritoView.vue'; 
import PagoView from '@/views/comun/pedido/PagoView.vue';

// Función de ayuda para obtener el nombre del dashboard según el rol
const getDashboardName = (rol) => {
    switch (rol) {
        case 'administrador': return 'admin-index';
        case 'moderador': return 'moderador-index';
        case 'logistica': return 'logistica-index';
        // Redirige al índice real del contenido (Marketplace)
        case 'comun': return 'vendedor-index'; 
        default: return 'login';
    }
}


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    
    
    // RUTA RAIZ: Apunta a Login
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
      meta: { requiresAuth: true, roles: ['ROLE_ADMINISTRADOR'] }
    },
    // Moderador
    {
      path: '/moderador',
      name: 'moderador-index',
      component: () => import('../views/moderador/IndexView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_MODERADOR'] }
    },
    // Logística
    {
      path: '/logistica',
      name: 'logistica-index',
      component: () => import('../views/logistica/IndexView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_LOGISTICA'] }
    },
    
    // RUTA DE VENDEDOR/COMÚN - USA EL LAYOUT CONTENEDOR
    {
      path: '/vendedor',
      name: 'vendedor-dashboard', // Nombre del dashboard de redirección (del layout)
      component: VendedorLayout, // Usa la plantilla de la barra lateral
      meta: { requiresAuth: true, roles: ['ROLE_COMUN'] },
      children: [
        
        // RUTA POR DEFECTO (MARKETPLACE) - /vendedor
        {
             path: '', 
             name: 'vendedor-index',
             component: IndexView 
        },
        
        {
          // La ruta completa será /vendedor/crear-producto
          path: 'crear-producto', 
          name: 'crearProducto',
          component: CrearProductoView,
        },
        {
          // La ruta completa será /vendedor/inventario
          path: 'inventario',
          name: 'inventarioVendedor',
          component: () => import('@/views/comun/InventarioView.vue'), 
        },
        // carrito
        {
          path: 'carrito', 
          name: 'carrito', 
          component: CarritoView,
        },
        {
          path: 'pago', 
          name: 'pago',
          component: PagoView,
        },
      ]
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
    const userRole = authStore.rol; // Ejemplo: 'comun'

    // A. Redirección de NO AUTENTICADOS (Rutas Protegidas)
    if (to.meta.requiresAuth && !isAuthenticated) {
        return next({ name: 'login' });
    }

    // B. Redirección de AUTENTICADOS (De Login/Register a su Dashboard)
    if (to.meta.requiresGuest && isAuthenticated) {
        const dashboardName = getDashboardName(userRole);
        return next({ name: dashboardName });
    }
    
    // C. Verificación de Rol (Acceso a Dashboards específicos)
    if (to.meta.roles && isAuthenticated) {
        const requiredRoles = to.meta.roles; // Ejemplo: ['ROLE_COMUN']
        
        // CORRECCIÓN: Creamos la cadena de autoridad completa para compararla con el meta.roles
        const userAuthority = `ROLE_${userRole.toUpperCase()}`; // Ejemplo: 'ROLE_COMUN'

        if (requiredRoles.includes(userAuthority)) {
            // Rol correcto, continuar
            return next();
        } else {
            // Rol incorrecto para la ruta a la que intenta acceder.
            // Redirige a SU PROPIO dashboard
            const dashboardName = getDashboardName(userRole);
            return next({ name: dashboardName }); 
        }
    }
    
    // D. Continuar normalmente
    return next();
});


export default router;
