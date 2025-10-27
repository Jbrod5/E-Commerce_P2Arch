package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.modelos.Rol;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jbrod.ecommerce_api.repositorios.RolRepository;
// Importación necesaria para manejar la autoridad como objeto
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List; // Importación para List.of() o Collections.singletonList()
import java.util.stream.Collectors;

/**
 * Implementa la interfaz UserDetailsService de Spring Security.
 * Es la fuente de la verdad para cargar usuarios desde la base de datos
 * durante el proceso de autenticación.
 */
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Este método es llamado por Spring Security durante el login para buscar
     * un usuario por su nombre de usuario (que en nuestro caso es el correo).
     */
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        // 1. Busca el usuario en la base de datos por el correo.
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // -------------------------------------------------------------------------
        // Imprimir los valores para verificar si el mapeo de usuario es correcto :c
        System.out.println("HASH DB: "  + usuario.getContrasena());
        System.out.println("ROL: "      + usuario.getRol().getNombre());
        System.out.println("ACTIVO: "   + usuario.getActivo());
        System.out.println();
        // -------------------------------------------------------------------------

        // --- CORRECCIÓN CRUCIAL: Mapeamos a autoridad exacta y en minúsculas ---
        // 1. Obtenemos el nombre del rol y lo convertimos a minúsculas
        String rolNombre = usuario.getRol().getNombre().toLowerCase();

        // 2. Mapea la información del usuario a un UserDetails de Spring Security.
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena()) // Contraseña encriptada de la DB
                // Usamos .authorities() para evitar el prefijo 'ROLE_'
                // y aseguramos que la autoridad sea exactamente "comun" o "administrador".
                .authorities(List.of(new SimpleGrantedAuthority(rolNombre)))
                .disabled(!usuario.getActivo())
                .build();
    }


    public Usuario obtenerPorCorreo(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
        return usuario;
    }


    // ... el resto de la clase UsuarioService se mantiene igual ...
    /**
     * Método de soporte para otros servicios que necesitan la entidad Usuario (JPA).
     * @param correo Correo del usuario.
     * @return Optional que contiene la entidad Usuario.
     */
    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }





    /**
     * Registra un nuevo usuario, asignándole el rol por defecto.
     * @param nuevoUsuario El objeto Usuario enviado desde el controlador (con nombre, correo y contraseña sin codificar).
     * @return El objeto Usuario guardado en la base de datos.
     * @throws RuntimeException Si el rol por defecto o el correo ya existen.
     */
    public Usuario registrarNuevoUsuario(Usuario nuevoUsuario) {

        // 1. Verificar si el usuario ya existe (por correo)
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(nuevoUsuario.getCorreo());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("El correo ya está registrado: " + nuevoUsuario.getCorreo());
        }

        // 2. Codificar la contraseña
        // NOTA: 'passwordEncoder' ahora se inyecta por el setter
        String contrasenaCodificada = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(contrasenaCodificada);

        // 3. Asignar el rol por defecto (ROLE_CLIENTE)
        Rol rolCliente = rolRepository.findByNombre("comun")
                .orElseThrow(() -> new RuntimeException("Rol 'comun' no encontrado."));

        nuevoUsuario.setRol(rolCliente);

        // 4. Configurar estado (activo por defecto)
        nuevoUsuario.setActivo(true);

        // 5. Guardar en la base de datos
        return usuarioRepository.save(nuevoUsuario);
    }



    // EMPLEADOS
    /**
     * Registra un nuevo empleado (Moderador, Logística, Administrador)
     * @param nuevoEmpleado El objeto Usuario con datos de registro (nombre, correo, contrasena sin codificar).
     * @param rolNombre El nombre del rol a asignar ("moderador", "logistica", "administrador").
     * @return El objeto Usuario guardado.
     * @throws RuntimeException Si el correo ya existe o el rol es inválido.
     */
    public Usuario registrarEmpleado(Usuario nuevoEmpleado, String rolNombre) {

        // 1. Validar el rol
        if (!rolNombre.equalsIgnoreCase("moderador") && !rolNombre.equalsIgnoreCase("logistica") && !rolNombre.equalsIgnoreCase("administrador")) {
            throw new RuntimeException("Rol de empleado inválido: " + rolNombre);
        }

        // 2. Verificar si el correo ya existe
        if (usuarioRepository.findByCorreo(nuevoEmpleado.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado: " + nuevoEmpleado.getCorreo());
        }

        // 3. Buscar el Rol por nombre (asegúrate que 'rolRepository' tenga findByNombre(String))
        Rol rolEmpleado = rolRepository.findByNombre(rolNombre.toLowerCase())
                .orElseThrow(() -> new RuntimeException("Rol '" + rolNombre + "' no encontrado en el sistema."));

        // 4. Asignar rol y codificar contraseña
        nuevoEmpleado.setRol(rolEmpleado);
        String contrasenaCodificada = passwordEncoder.encode(nuevoEmpleado.getContrasena());
        nuevoEmpleado.setContrasena(contrasenaCodificada);
        nuevoEmpleado.setActivo(true); // Los empleados inician activos

        // 5. Guardar y retornar
        return usuarioRepository.save(nuevoEmpleado);
    }

    /**
     * Actualiza la información (nombre, correo, contrasena, activo) de un usuario por su ID.
     * @param id El ID del usuario a actualizar.
     * @param datosActualizados El objeto Usuario con los campos a actualizar.
     * @return El objeto Usuario actualizado.
     * @throws RuntimeException Si el usuario no existe o el nuevo correo ya está en uso.
     */
    public Usuario actualizarUsuario(Integer id, Usuario datosActualizados) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 1. Actualizar Nombre
        if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().isEmpty()) {
            usuario.setNombre(datosActualizados.getNombre());
        }

        // 2. Actualizar Correo
        if (datosActualizados.getCorreo() != null && !datosActualizados.getCorreo().isEmpty() && !usuario.getCorreo().equals(datosActualizados.getCorreo())) {
            if (usuarioRepository.findByCorreo(datosActualizados.getCorreo()).isPresent()) {
                throw new RuntimeException("El nuevo correo ya está registrado.");
            }
            usuario.setCorreo(datosActualizados.getCorreo());
        }

        // 3. Actualizar Contraseña (solo si se provee una nueva)
        if (datosActualizados.getContrasena() != null && !datosActualizados.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(datosActualizados.getContrasena()));
        }

        // 4. Actualizar Estado Activo (puede ser usado para suspender/rehabilitar)
        // Asumimos que el DTO puede traer 'activo' como booleano si se desea cambiar
        if (datosActualizados.getActivo() != null) {
            usuario.setActivo(datosActualizados.getActivo());
        }

        // 5. El rol no se actualiza a través de este método, se mantiene.

        return usuarioRepository.save(usuario);
    }


    /**
     * Obtiene la lista de todos los empleados (Moderador, Logística, Administrador).
     * @return Lista de objetos Usuario (sin contraseña).
     */
    public List<Usuario> obtenerEmpleados() {

        List<Integer> idsRolesEmpleados = List.of(2, 3, 4);


        List<Usuario> empleados = usuarioRepository.findByRolIdIn(idsRolesEmpleados);
        return empleados.stream()
                .peek(u -> u.setContrasena(null))
                .collect(Collectors.toList());
    }
}
