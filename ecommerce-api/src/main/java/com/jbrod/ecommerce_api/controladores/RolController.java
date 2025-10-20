package com.jbrod.ecommerce_api.controladores;



import com.jbrod.ecommerce_api.modelos.Rol;
import com.jbrod.ecommerce_api.repositorios.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar peticiones HTTP relacionadas con la entidad Rol.
 */
@RestController
@RequestMapping("/api/roles") // Ruta base: http://localhost:8080/api/roles
public class RolController {

    // Inyección de Dependencia para usar los métodos CRUD
    @Autowired
    private RolRepository rolRepository;

    /**
     * Obtiene todos los roles.
     * GET /api/roles
     */
    @GetMapping
    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAll();
    }

    /**
     * Obtiene un rol por su ID.
     * GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Integer id) {
        // Usa Optional para manejar el caso de que el rol no exista (404 Not Found)
        return rolRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo rol.
     * POST /api/roles
     */
    @PostMapping
    public Rol crearRol(@RequestBody Rol nuevoRol) {
        return rolRepository.save(nuevoRol); // Guarda y devuelve el objeto Rol con su ID generado
    }
}