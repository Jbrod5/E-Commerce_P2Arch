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
        com.jbrod.ecommerce_api.modelos.Usuario usuario = usuarioRepository.findByCorreo(correo)
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
}
