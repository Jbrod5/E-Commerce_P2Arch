import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'



// Importaciones de los componentes
import LoginView from '../views/LoginView.vue';
import RegistroView from '../views/RegistroView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    //{
    //  path: '/login', // <--- Nueva Ruta de Login
    //  name: 'login',
    //  component: LoginView 
    //},
    {
      path: '/register', // <-- 2. DEFINIR LA RUTA
      name: 'register',
      component: RegistroView // <-- 3. ASIGNAR EL COMPONENTE
    }
  ],
})

export default router
