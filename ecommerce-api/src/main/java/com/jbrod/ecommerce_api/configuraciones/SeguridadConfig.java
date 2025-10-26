package com.jbrod.ecommerce_api.configuraciones;

import com.jbrod.ecommerce_api.servicios.JwtFiltroAutenticacion;
import com.jbrod.ecommerce_api.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity(prePostEnabled = true) //Habilitar PreAutorized en controladores :3
public class SeguridadConfig {

    // Usamos @Lazy permitiendo que SeguridadConfiguracion se cree
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
    public DaoAuthenticationProvider authenticationProvider(UsuarioService usuarioService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ----------------------------------------------------------------------
    // --- CONFIGURACIÓN CORS ---
    // ----------------------------------------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 1. Agrega los orígenes permitidos
        //configuration.setAllowedOrigins(Arrays.asList(
        //        "http://localhost:5173",                     // Para desarrollo local
        //        "https://e-commercegt.netlify.app",          // dominio de netlify :3
        //        "https://*.ngrok-free.app",                  // Ngrok si termina en .app xd
        //        "https://*.ngrok-free.dev"                   // Ngrok si termina en .dev xd
        //));
        //Mejor permitir todo :'ccccc
        //configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Para producción y desarrollo
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://e-commercegt.netlify.app",
                "https://semiobliviously-voluptuous-charlee.ngrok-free.dev" // dominio actual xddd
        ));

        // Pattern para cualquier subdominio de ngrok
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://*.ngrok-free.dev",
                "https://*.ngrok-free.app"
        ));



        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
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
                        // 1. Acceso público
                        //para autenticación (LOGIN, REGISTRO)
                        .requestMatchers("/api/auth/**").permitAll()
                        //.requestMatchers("/auth/**").permitAll()                        //recursos como imagenes
                        .requestMatchers("/imagenes/**").permitAll()




                        // 2. Rutas que requieren un rol específico
                        //  Usar hasAuthority() para la cadena literal 'moderador'
                        .requestMatchers("/api/moderador/**").hasAnyAuthority("moderador", "MODERADOR")

                        // Usar hasAnyAuthority() para las cadenas literales 'comun' y 'administrador'
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyAuthority("comun", "administrador", "ADMINISTRADOR", "COMUN")



                        // Carrito debe ser accesible por el usuario comun :3
                        .requestMatchers(HttpMethod.POST,  "/api/carrito/**").hasAnyAuthority("comun", "COMUN")
                        // Pago debe ser accesible por usuario comun
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyAuthority("comun", "COMUN")
                        .requestMatchers(HttpMethod.GET,  "/api/pedidos/**").hasAnyAuthority("comun", "COMUN")
                        // Tarjetas debe ser accesible por usuario comun
                        .requestMatchers(HttpMethod.POST, "api/tarjetas/**").hasAnyAuthority("comun", "COMUN")
                        .requestMatchers(HttpMethod.GET, "api/tarjetas/**").hasAnyAuthority("comun", "COMUN")
                        // Productos y resenas deben ser accesibles por comun
                        .requestMatchers(HttpMethod.GET, "/api/productos/{id}").hasAnyAuthority("comun", "COMUN")
                        .requestMatchers(HttpMethod.POST, "/api/productos/{id}").hasAnyAuthority("comun", "COMUN")
                        //El vendedor debe tener acceso a sus propios ingresos
                        .requestMatchers(HttpMethod.GET, "/api/ingresos/vendedor/**")
                        .hasAnyAuthority("comun", "COMUN", "administrador", "ADMINISTRADOR")


                        //Accesibles por todos pero logueados
                        // Rutas de Notificaciones get y post
                        .requestMatchers(HttpMethod.GET, "/api/notificaciones/**").hasAnyAuthority("comun", "administrador", "ADMINISTRADOR", "COMUN", "modarador", "MODERADOR", "logistica", "LOGISTICA")
                        .requestMatchers(HttpMethod.POST, "/api/notificaciones/**").hasAnyAuthority("comun", "administrador", "ADMINISTRADOR", "COMUN", "modarador", "MODERADOR", "logistica", "LOGISTICA")


                        // **********************************************************************************

                        // 3. El resto de rutas requieren autenticación (el catch-all final)
                        .anyRequest().authenticated()
                )

                // 4. Configura la política de sesiones como sin estado (STATELESS)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Agrega nuestro proveedor de autenticación.
                .authenticationProvider(daoAuthenticationProvider)

                // 6. AGREGAR EL FILTRO JWT (inyectado por campo)
                .addFilterBefore(jwtAuthFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
