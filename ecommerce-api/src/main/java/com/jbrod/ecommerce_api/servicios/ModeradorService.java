package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.suspension.SuspensionDTO;
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

    private final NotificacionService notificacionService;

    @Autowired
    public ModeradorService(UsuarioRepository usuarioRepository, SuspensionRepository suspensionRepository, NotificacionService notificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.suspensionRepository = suspensionRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Sanciona a un usuario, crea un registro de suspensión y desactiva su cuenta.
     * @param dto Datos de la sanción (ID del sancionado, motivo, fecha de fin).
     * @param correoModerador Correo del moderador autenticado (para la FK).
     * @return El objeto Suspension creado.
     * @throws RuntimeException Si el usuario a sancionar no existe o es un empleado.
     */
    @Transactional // Asegura que ambas operaciones (crear suspensión y desactivar usuario) se hagan o no se haga ninguna >:c
    public Suspension sancionarUsuario(SuspensionPeticionDto dto, String correoModerador) {

        // 1. Obtener el Moderador (quien ejecuta la acción)
        Usuario moderador = usuarioRepository.findByCorreo(correoModerador)
                .orElseThrow(() -> new NoSuchElementException("Moderador autenticado no encontrado."));

        // 2. Obtener el Usuario a Sancionar
        Usuario usuarioASancionar = usuarioRepository.findById(dto.getIdUsuarioASuspender())
                .orElseThrow(() -> new NoSuchElementException("Usuario a sancionar no encontrado con ID: " + dto.getIdUsuarioASuspender()));

        // 3. Validar: Solo se puede sancionar a usuarios COMUNES (vendedores)
        // rol 'comun' tiene ID = 1 o nombre = 'comun' si no cambio la bd pipipi
        if (!"comun".equalsIgnoreCase(usuarioASancionar.getRol().getNombre())) {
            throw new RuntimeException("No se pueden sancionar usuarios que no sean de tipo 'comun' (vendedor).");
        }

        // 4. Actualizar el estado 'activo' del usuario a false (o no debería hacer eso???)
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
        // rol 'comun' tiene ID = 1 si no lo cambio despues xd
        final Integer ID_ROL_COMUN = 1;

        List<Usuario> vendedores = usuarioRepository.findByRolId(ID_ROL_COMUN);

        // Mapear la lista de entidades a la lista de DTOs
        return vendedores.stream()
                .map(UsuarioVendedorDto::new) // Usamos el constructor de mapeo del DTO
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de suspensiones para un usuario.
     */
    @Transactional(readOnly = true)
    public List<SuspensionDTO> obtenerHistorialSanciones(Long idUsuario) {

        List<Suspension> historial = suspensionRepository
                .obtenerHistorialSuspensionesPorUsuario(idUsuario);

        // Mapea la lista de entidades a la lista de DTOs
        return historial.stream()
                .map(SuspensionDTO::new)
                .collect(Collectors.toList());
    }



    /**
     * Levanta la suspensión activa de un usuario, lo reactiva y marca la suspensión como inactiva.
     * Si la suspensión es por tiempo, este método se usaría para levantarla antes de la fecha de fin.
     * @param idUsuario ID del usuario al que se le levantará la sanción.
     * @param correoModerador Correo del moderador que ejecuta la acción (para registro/validación).
     * @throws NoSuchElementException Si el usuario no existe o no tiene una suspensión activa.
     */
    @Transactional
    public void levantarSuspension(Long idUsuario, String correoModerador) {
        System.out.println("Se intentara levantar la sancion del usuario: " + idUsuario);
        // 1. Obtener el Moderador (quien ejecuta la acción)
        Usuario moderador = usuarioRepository.findByCorreo(correoModerador)
                .orElseThrow(() -> new NoSuchElementException("Moderador autenticado no encontrado."));

        // 2. Obtener el Usuario a Reactivar
        Usuario usuarioAActivar = usuarioRepository.findById(idUsuario.intValue())
                .orElseThrow(() -> new NoSuchElementException("Usuario a reactivar no encontrado con ID: " + idUsuario));

        // 3. Validar si esta inactivo por suspensión
        if (usuarioAActivar.getActivo()) {
            // Se puede considerar un caso de éxito si ya está activo
            return;
        }else{
            //para evitar problemas mejor reactivar xd
            usuarioAActivar.setActivo(true);
            usuarioRepository.save(usuarioAActivar);
        }

        // 4. Buscar la suspndsn activc del usuario (debe haber solo una si alguien no toco la base de datos >:c VERDADDD???)
        Suspension suspensionActiva = suspensionRepository.findByUsuarioSancionadoIdAndActivaTrue(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("No se encontró una suspensión activa para el usuario con ID: " + idUsuario));


        // 5. Marcar la suspensión como inactiva (finalizada)
        suspensionActiva.setActiva(false);
        // Actualizar la fecha de fin a la fecha actual si se levanta antes del tiempo
        if (suspensionActiva.getFechaFin().isAfter(LocalDateTime.now())) {
            suspensionActiva.setFechaFin(LocalDateTime.now());
        }
        suspensionRepository.save(suspensionActiva);


        // 7. Notificar al usuario
        notificacionService.generarNotificacion(usuarioAActivar.getCorreo(),
                "Reactivacvion de cuenta de cuenta: E-CommerceGT",
                "Estimado " + usuarioAActivar.getNombre() + "\n\n" +
                        "Su cuenta ha sido reactivada en la plataforma E-CommerceGT porque el moderador " + moderador.getNombre() + " con correo " + moderador.getCorreo() + " lo ha considerado oportuno.\n" +
                        "Ya puede seguir disfrutando de nuestro servicio siempre y cuando se respeten nuestras condiciones de uso.\n\n"+


                        "Reafirmamos nuestro compromiso con nuestros usuarios para mantener una plataforma segura y apta para todos.");


    }
}