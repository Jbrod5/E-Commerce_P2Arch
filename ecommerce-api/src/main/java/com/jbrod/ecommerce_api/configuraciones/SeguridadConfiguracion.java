package com.jbrod.ecommerce_api.configuraciones;

import com.jbrod.ecommerce_api.servicios.JwtFiltroAutenticacion;
import com.jbrod.ecommerce_api.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy; // Importación necesaria
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración principal de Spring Security para la API REST.
 * Incluye JWT (stateless) y CORS.
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {

    // CAMBIO CRUCIAL: Usamos @Lazy aquí. Esto permite que SeguridadConfiguracion se cree
    // (y defina PasswordEncoder) antes de que el filtro JWT esté completamente resuelto.
    @Autowired
    @Lazy
    private JwtFiltroAutenticacion jwtAuthFiltro;

    // ----------------------------------------------------------------------
    // --- BEANS DE SEGURIDAD ---
    // ----------------------------------------------------------------------

    /**
     * Define el codificador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define cómo se obtienen los datos del usuario y cómo se verifica la contraseña.
     * Se inyecta UsuarioServicio por parámetro.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UsuarioServicio usuarioServicio) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioServicio);
        // Llama directamente al bean passwordEncoder()
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ----------------------------------------------------------------------
    // --- CONFIGURACIÓN CORS ---
    // ----------------------------------------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        // CORRECCIÓN: Devolver 'source' que implementa CorsConfigurationSource
        return source;
    }

    /**
     * El AuthenticationManager es necesario para procesar la petición de Login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ----------------------------------------------------------------------
    // --- SECURITY FILTER CHAIN ---
    // ----------------------------------------------------------------------

    /**
     * Configuración del filtro de seguridad HTTP. Define las reglas de acceso.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        http
                // 1. HABILITAR CORS usando la configuración que definimos arriba
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Deshabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Define las reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Configura la política de sesiones como sin estado (STATELESS)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Agrega nuestro proveedor de autenticación.
                // Spring inyecta automáticamente el bean DaoAuthenticationProvider que definimos.
                .authenticationProvider(daoAuthenticationProvider)

                // 6. AGREGAR EL FILTRO JWT (inyectado por campo)
                .addFilterBefore(jwtAuthFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}