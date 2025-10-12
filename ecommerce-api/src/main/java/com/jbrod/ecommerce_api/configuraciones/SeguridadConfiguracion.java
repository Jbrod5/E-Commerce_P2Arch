package com.jbrod.ecommerce_api.configuraciones;

import com.jbrod.ecommerce_api.servicios.JwtFiltroAutenticacion;
import com.jbrod.ecommerce_api.servicios.UsuarioServicio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration; // <-- Importación para CORS
import org.springframework.web.cors.CorsConfigurationSource; // <-- Importación para CORS
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <-- Importación para CORS

import java.util.Arrays; // <-- Importación para Listas

/**
 * Configuración principal de Spring Security para la API REST.
 * Incluye JWT (stateless) y CORS.
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {

    private final UsuarioServicio usuarioServicio;
    private final JwtFiltroAutenticacion jwtAuthFiltro;

    // Inyectamos ambos servicios en el constructor
    public SeguridadConfiguracion(UsuarioServicio usuarioServicio, JwtFiltroAutenticacion jwtAuthFiltro) {
        this.usuarioServicio = usuarioServicio;
        this.jwtAuthFiltro = jwtAuthFiltro;
    }

    /**
     * Define el codificador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ----------------------------------------------------------------------
    // --- NUEVA CONFIGURACIÓN: CORS ---
    // ----------------------------------------------------------------------
    /**
     * Define la configuración de CORS para permitir peticiones desde el frontend de Vue.js.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // El origen de tu aplicación Vue.js en desarrollo
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // Métodos permitidos (GET, POST, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Encabezados permitidos (CRUCIAL para el token Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Permite el uso de credenciales (necesario si usas cookies o sesiones)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    // ----------------------------------------------------------------------

    /**
     * Define cómo se obtienen los datos del usuario y cómo se verifica la contraseña.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioServicio);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * El AuthenticationManager es necesario para procesar la petición de Login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuración del filtro de seguridad HTTP. Define las reglas de acceso.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. HABILITAR CORS usando la configuración que definimos arriba
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Deshabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Define las reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Permite el acceso libre al endpoint de autenticacion (login/registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Todas las demas peticiones requieren autenticación
                        .anyRequest().authenticated()
                )

                // 4. Configura la política de sesiones como sin estado (STATELESS)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Agrega nuestro proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // 6. AGREGAR EL FILTRO JWT
                .addFilterBefore(jwtAuthFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}