package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.modelos.Rol;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jbrod.ecommerce_api.repositorios.RolRepositorio;

/**
 * Implementa la interfaz UserDetailsService de Spring Security.
 * Es la fuente de la verdad para cargar usuarios desde la base de datos
 * durante el proceso de autenticación.
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    // Se declara la dependencia sin @Autowired en el campo.
    private PasswordEncoder passwordEncoder;

    // CRUCIAL: Inyección por Setter. Spring la ejecuta después de crear este bean, rompiendo el ciclo.
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
        com.jbrod.ecommerce_api.modelos.Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        // -------------------------------------------------------------------------
        // DEBUGGING: Imprimir los valores para verificar si el mapeo es correcto.
        // -------------------------------------------------------------------------
        System.out.println("DEBUG HASH DB: " + usuario.getContrasena());
        System.out.println("DEBUG ROL: " + usuario.getRol().getNombre());
        System.out.println("DEBUG ACTIVO: " + usuario.getActivo());
        // -------------------------------------------------------------------------

        // 2. Mapea la información del usuario a un UserDetails de Spring Security.
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena()) // Contraseña encriptada de la DB
                .roles(usuario.getRol().getNombre().toUpperCase()) // Rol
                .disabled(!usuario.getActivo())
                .build();
    }


    /**
     * Registra un nuevo usuario, asignándole el rol por defecto.
     * @param nuevoUsuario El objeto Usuario enviado desde el controlador (con nombre, correo y contraseña sin codificar).
     * @return El objeto Usuario guardado en la base de datos.
     * @throws RuntimeException Si el rol por defecto o el correo ya existen.
     */
    public Usuario registrarNuevoUsuario(Usuario nuevoUsuario) {

        // 1. Verificar si el usuario ya existe (por correo)
        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByCorreo(nuevoUsuario.getCorreo());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("El correo ya está registrado: " + nuevoUsuario.getCorreo());
        }

        // 2. Codificar la contraseña
        // NOTA: 'passwordEncoder' ahora se inyecta por el setter
        String contrasenaCodificada = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(contrasenaCodificada);

        // 3. Asignar el rol por defecto (ROLE_CLIENTE)
        Rol rolCliente = rolRepositorio.findByNombre("comun")
                .orElseThrow(() -> new RuntimeException("Rol 'comun' no encontrado."));

        nuevoUsuario.setRol(rolCliente);

        // 4. Configurar estado (activo por defecto)
        nuevoUsuario.setActivo(true);

        // 5. Guardar en la base de datos
        return usuarioRepositorio.save(nuevoUsuario);
    }
}