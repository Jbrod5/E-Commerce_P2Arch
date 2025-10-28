package com.jbrod.ecommerce_api.repositorios; // Usamos tu paquete principal + 'repositorios'

import com.jbrod.ecommerce_api.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Usuario.
 * Proporciona métodos CRUD y de consulta por correo electrónico.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su correo electrónico.
     * Util para implementacion del login.
     *
     * @param correo El correo electrónico del usuario.
     * @return Un Optional que contiene el Usuario si existe, o vacío si no.
     */
    Optional<Usuario> findByCorreo(String correo);


    /**
     * Busca un usuario por su id.
     *
     * @param id El correo electrónico del usuario.
     * @return Un Optional que contiene el Usuario si existe, o vacío si no.
     */
    Optional<Usuario> findById(Integer id);


    /**
     * Busca todos los usuarios que tienen un Rol específico (usando el ID del Rol).
     * @param rolId ID del rol (ej: 1 para 'comun').
     * @return Lista de usuarios con ese rol.
     */
    List<Usuario> findByRolId(Integer rolId);


    // Dentro de UsuarioRepository.java
    List<Usuario> findByRolIdIn(List<Integer> rolIds);

}