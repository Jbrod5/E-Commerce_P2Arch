package com.jbrod.ecommerce_api.repositorios;


import com.jbrod.ecommerce_api.modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad Rol.
 * Permite realizar operaciones CRUD sin escribir código SQL.
 */
@Repository
public interface RolRepositorio extends JpaRepository<Rol, Integer> {

    // Se crea un método de búsqueda personalizado.
    // Spring Data JPA infiere la consulta SQL basándose en el nombre del método.
    Rol findByNombre(String nombre);
}