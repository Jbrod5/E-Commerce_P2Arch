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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // <-- 2. IMPORTAR CLASE DE REFERENCIA

/**
 * Configuración principal de Spring Security para la API REST.
 * Deshabilita la seguridad basada en sesiones y la reemplaza por JWT (stateless).
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {

    private final UsuarioServicio usuarioServicio;
    private final JwtFiltroAutenticacion jwtAuthFiltro; // <-- 3. DECLARAR EL FILTRO

    // 4. Inyectamos ambos servicios en el constructor
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
                // 1. Deshabilita CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define las reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Permite el acceso libre al endpoint de autenticacion (login/registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Todas las demas peticiones requieren autenticación
                        .anyRequest().authenticated()
                )

                // 3. Configura la política de sesiones como sin estado (STATELESS)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Agrega nuestro proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // 5. AGREGAR EL FILTRO JWT
                // Agregamos nuestro filtro antes del filtro de autenticación estándar de Spring.
                // Esto asegura que el token se valide antes de que Spring intente buscar usuario/contraseña.
                .addFilterBefore(jwtAuthFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}