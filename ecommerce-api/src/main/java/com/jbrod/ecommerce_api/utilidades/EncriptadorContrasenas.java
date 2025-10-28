package com.jbrod.ecommerce_api.utilidades;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Clase utilitaria para generar el hash BCrypt de contraseñas.
 * Se utiliza para actualizar manualmente las contraseñas en la base de datos (PostgreSQL).
 */
public class EncriptadorContrasenas {

    public static void main(String[] args) {

        // 1. Instancia el codificador que utiliza Spring Security
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 2. Contraseña a encriptar
        String contrasenaPlana = "admin123";

        // 3. Genera el hash (el resultado es diferente cada vez aaaaaaaaaaaa)
        String hashGenerado = encoder.encode(contrasenaPlana);

        // 4. Muestra la informacion para copiar y pegar
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Contraseña Plana: " + contrasenaPlana);
        System.out.println("Hash BCrypt (para la DB): " + hashGenerado);
        System.out.println("----------------------------------------------------------------------");

        // Probar si el hash es correcto (debería ser true)
        boolean esValida = encoder.matches(contrasenaPlana, hashGenerado);
        System.out.println("Prueba de validación (debe ser TRUE): " + esValida);


        // CLAVE SECRETA QUE YA NO ES SECRETA XD
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Clave secreta >:3");
        String CLAVE_SECRETA = "TRES_TRISTES_tigres_tragaban_trigo_en_un_trigalllllll:333333";
        String SECRET_KEY = Base64.getEncoder().encodeToString(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8));
        System.out.println(SECRET_KEY);
    }
}