package com.jbrod.ecommerce_api.repositorios; // Usamos tu paquete principal + 'repositorios'

import com.jbrod.ecommerce_api.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Necesario para manejar la posibilidad de que no se encuentre el usuario

/**
 * Repositorio de Spring Data JPA para la entidad Usuario.
 * Proporciona métodos CRUD y de consulta por correo electrónico.
 */
@Repository
// JpaRepository<[Clase Entidad], [Tipo de Dato de la Llave Primaria (Integer)]>
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su correo electrónico.
     * Este método es vital para la autenticación (login).
     *
     * @param correo El correo electrónico del usuario.
     * @return Un Optional que contiene el Usuario si existe, o vacío si no.
     */
    Optional<Usuario> findByCorreo(String correo);
}