package com.jbrod.ecommerce_api.utilidades;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase utilitaria para generar el hash BCrypt de contraseñas.
 * Se utiliza para actualizar manualmente las contraseñas en la base de datos (PostgreSQL).
 */
public class EncriptadorContrasenas {

    public static void main(String[] args) {

        // 1. Instancia el codificador que utiliza Spring Security
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 2. Define la contraseña que deseas encriptar (ej. la de tu usuario ADMIN)
        String contrasenaPlana = "user123";

        // 3. Genera el hash (el resultado es diferente cada vez)
        String hashGenerado = encoder.encode(contrasenaPlana);

        // 4. Muestra la informacion para copiar y pegar
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Contraseña Plana: " + contrasenaPlana);
        System.out.println("Hash BCrypt (para la DB): " + hashGenerado);
        System.out.println("----------------------------------------------------------------------");

        // Opcional: Probar si el hash es correcto (debería ser true)
        boolean esValida = encoder.matches(contrasenaPlana, hashGenerado);
        System.out.println("Prueba de validación (debe ser TRUE): " + esValida);
    }
}