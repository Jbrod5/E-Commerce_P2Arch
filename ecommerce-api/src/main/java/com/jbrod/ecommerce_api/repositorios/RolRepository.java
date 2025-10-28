package com.jbrod.ecommerce_api.repositorios;

import com.jbrod.ecommerce_api.modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- IMPORTACIÓN NECESARIA

/**
 * Repositorio de Spring Data JPA para la entidad Rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Se cambia el tipo de retorno para usar Optional, lo que permite el uso de orElseThrow() en el servicio.
    Optional<Rol> findByNombre(String nombre);
}