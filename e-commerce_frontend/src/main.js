import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth';


import ngrokImage from './directives/ngrokImages';

const app = createApp(App)


app.use(createPinia())
const authStore = useAuthStore();
authStore.initialize();



app.use(router)


app.directive('ngrok-img', ngrokImage);

app.mount('#app')
