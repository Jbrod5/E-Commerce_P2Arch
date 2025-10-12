package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementa la interfaz UserDetailsService de Spring Security.
 * Es la fuente de la verdad para cargar usuarios desde la base de datos
 * durante el proceso de autenticación.
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

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
        // Si el login sigue fallando, estos valores deben ser correctos.
        System.out.println("DEBUG HASH DB: " + usuario.getContrasena());
        System.out.println("DEBUG ROL: " + usuario.getRol().getNombre());
        System.out.println("DEBUG ACTIVO: " + usuario.getActivo());
        // -------------------------------------------------------------------------

        // 2. Mapea la información del usuario a un UserDetails de Spring Security.
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena()) // Contraseña encriptada de la DB
                .roles(usuario.getRol().getNombre().toUpperCase()) // Rol (Spring añade automáticamente ROLE_)
                // CORRECCIÓN CLAVE: Usar el campo 'activo' para indicar que la cuenta está habilitada
                .disabled(!usuario.getActivo()) // Si ACTIVO es TRUE, !TRUE es FALSE (no deshabilitado)
                .build();
    }
}