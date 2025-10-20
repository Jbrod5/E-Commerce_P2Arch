package com.jbrod.ecommerce_api.repositorios.productos;

import com.jbrod.ecommerce_api.modelos.productos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de Categorías.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
