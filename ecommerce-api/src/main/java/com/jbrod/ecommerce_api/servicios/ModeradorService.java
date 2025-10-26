package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.suspension.SuspensionPeticionDto;
import com.jbrod.ecommerce_api.dto.usuario.UsuarioVendedorDto;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.moderador.Suspension;
import com.jbrod.ecommerce_api.repositorios.UsuarioRepository;
import com.jbrod.ecommerce_api.repositorios.suspension.SuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ModeradorService {

    private final UsuarioRepository usuarioRepository;
    private final SuspensionRepository suspensionRepository;

    @Autowired
    public ModeradorService(UsuarioRepository usuarioRepository, SuspensionRepository suspensionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.suspensionRepository = suspensionRepository;
    }

    /**
     * Sanciona a un usuario, crea un registro de suspensión y desactiva su cuenta.
     * @param dto Datos de la sanción (ID del sancionado, motivo, fecha de fin).
     * @param correoModerador Correo del moderador autenticado (para la FK).
     * @return El objeto Suspension creado.
     * @throws RuntimeException Si el usuario a sancionar no existe o es un empleado.
     */
    @Transactional // Asegura que ambas operaciones (crear suspensión y desactivar usuario) se hagan o ninguna
    public Suspension sancionarUsuario(SuspensionPeticionDto dto, String correoModerador) {

        // 1. Obtener el Moderador (quien ejecuta la acción)
        Usuario moderador = usuarioRepository.findByCorreo(correoModerador)
                .orElseThrow(() -> new NoSuchElementException("Moderador autenticado no encontrado."));

        // 2. Obtener el Usuario a Sancionar
        Usuario usuarioASancionar = usuarioRepository.findById(dto.getIdUsuarioASuspender())
                .orElseThrow(() -> new NoSuchElementException("Usuario a sancionar no encontrado con ID: " + dto.getIdUsuarioASuspender()));

        // 3. Validar: Solo se puede sancionar a usuarios COMUNES (vendedores)
        // Asumiendo que el rol 'comun' tiene ID = 1 o nombre = 'comun'
        if (!"comun".equalsIgnoreCase(usuarioASancionar.getRol().getNombre())) {
            throw new RuntimeException("No se pueden sancionar usuarios que no sean de tipo 'comun' (vendedor).");
        }

        // 4. Actualizar el estado 'activo' del usuario a false
        usuarioASancionar.setActivo(false);
        usuarioRepository.save(usuarioASancionar); // Persistir el cambio

        // 5. Crear el registro de Suspensión
        Suspension nuevaSuspension = new Suspension(
                usuarioASancionar,
                moderador,
                dto.getMotivo(),
                dto.getFechaFin()
        );

        // 6. Guardar el registro de Suspensión
        return suspensionRepository.save(nuevaSuspension);
    }



    /**
     * Obtiene la lista de todos los usuarios con el rol 'comun' (vendedores),
     * mapeados a un DTO seguro.
     * @return Lista de DTOs de vendedores.
     */
    public List<UsuarioVendedorDto> obtenerUsuariosVendedores() {
        // Asumimos que el rol 'comun' tiene ID = 1, basándonos en tu script SQL.
        final Integer ID_ROL_COMUN = 1;

        List<Usuario> vendedores = usuarioRepository.findByRolId(ID_ROL_COMUN);

        // Mapear la lista de entidades a la lista de DTOs
        return vendedores.stream()
                .map(UsuarioVendedorDto::new) // Usamos el constructor de mapeo del DTO
                .collect(Collectors.toList());
    }

    // NOTA: Aquí podrías agregar un método 'obtenerHistorialSanciones()' para el administrador
    // y un 'levantarSuspension()' para reactivar al usuario después de la fecha de fin.
}