import api from '@/plugins/axios.js';

export default {
  mounted(el, binding) {
    const imageUrl = binding.value;
    
    if (!imageUrl) return;
    
    // Si la URL contiene ngrok o es del backend, cargarla con axios
    const needsProxy = imageUrl.includes('ngrok') || 
                       imageUrl.includes('localhost:8080') ||
                       imageUrl.startsWith('/imagenes');
    
    if (needsProxy) {
      // Cargar la imagen con axios (que incluye el header ngrok-skip-browser-warning)
      api.get(imageUrl, {
        responseType: 'blob',
        baseURL: imageUrl.startsWith('http') ? '' : api.defaults.baseURL.replace('/api', '')
      })
      .then(response => {
        const blob = response.data;
        const objectURL = URL.createObjectURL(blob);
        el.src = objectURL;
        
        // Limpiar cuando el elemento se destruya
        el._revokeObjectURL = () => URL.revokeObjectURL(objectURL);
      })
      .catch(error => {
        console.error('Error cargando imagen:', error);
        // Fallback: intentar cargar directamente
        el.src = imageUrl;
      });
    } else {
      // URL externa, usarla directamente
      el.src = imageUrl;
    }
  },
  
  beforeUnmount(el) {
    // Limpiar el blob URL si existe
    if (el._revokeObjectURL) {
      el._revokeObjectURL();
    }
  },
  
  updated(el, binding) {
    // Si cambia la URL, recargar
    if (binding.value !== binding.oldValue) {
      if (el._revokeObjectURL) {
        el._revokeObjectURL();
      }
      // Re-montar
      this.mounted(el, binding);
    }
  }
};