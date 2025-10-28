package com.jbrod.ecommerce_api.servicios;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Clave secreta fuerte
    // Se encripta en EncriptadorContraseñas xd
    //private static final String CLAVE_SECRETA = "TRES_TRISTES_tigres_tragaban_trigo_en_un_trigalllllll:333333";
    private static final String SECRET_KEY = "VFJFU19UUklTVEVTX3RpZ3Jlc190cmFnYWJhbl90cmlnb19lbl91bl90cmlnYWxsbGxsbGw6MzMzMzMz";

    // Tiempo de expiración del token (1 dia en milisegundossss - mil milisegundos * 60 segundos * 60 minutos * 24 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * 1. Genera un JWT para un usuario.
     */
    public String generarToken(UserDetails userDetails) {
        // Obtenemos las authorities (roles) del usuario
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst() // Asumimos un solo rol por ahora
                .orElse("ROLE_DEFAULT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", roles); // Añadir el rol a los claims del token

        return construirToken(claims, userDetails);
    }

    private String construirToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // El correo del usuario es el sujeto
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma con la clave secreta
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Metodos de Validacion  --------------------------------------------------------------------------------------

    /**
     * 2. Extrae el correo (username) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 3. Valida si un token es válido para un usuario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // metodos auxiliares para Claims
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}