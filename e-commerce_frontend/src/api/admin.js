import api from '@/plugins/axios.js';

const ADMIN_BASE_URL = '/admin/empleados';

export default {
    /**
     * Obtiene la lista de todos los empleados.
     * GET /api/admin/empleados
     */
    async obtenerEmpleados() {
        const response = await api.get(ADMIN_BASE_URL);
        return response.data;
    },

    /**
     * Registra un nuevo empleado.
     * POST /api/admin/empleados
     * @param {Object} empleado - { nombre, correo, contrasena, rol: { nombre: 'moderador' } }
     */
    async registrarEmpleado(empleado) {
        const response = await api.post(ADMIN_BASE_URL, empleado);
        return response.data;
    },

    /**
     * Actualiza la información de un usuario por ID.
     * PUT /api/admin/empleados/{id}
     * @param {number} id - ID del usuario.
     * @param {Object} datos - { nombre, correo, contrasena?, activo? }
     */
    async actualizarEmpleado(id, datos) {
        const response = await api.put(`${ADMIN_BASE_URL}/${id}`, datos);
        return response.data;
    },
};