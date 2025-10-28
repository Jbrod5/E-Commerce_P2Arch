package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.usuario.TarjetaCreacionDto;
import com.jbrod.ecommerce_api.dto.usuario.TarjetaResponseDto;
import com.jbrod.ecommerce_api.modelos.usuario.Tarjetas;
import com.jbrod.ecommerce_api.repositorios.usuario.TarjetasRepository;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarjetasService {

    @Autowired
    private TarjetasRepository tarjetasRepository;

    /**
     * Crea una nueva tarjeta para un usuario.
     * @param usuarioId ID del usuario.
     * @param dto Datos de la tarjeta.
     * @return DTO de respuesta de la tarjeta creada.
     */
    public TarjetaResponseDto crearTarjeta(Long usuarioId, TarjetaCreacionDto dto) {

        // 1. Extraer los últimos 4 dígitos (parte visible)
        String numeroCompleto = dto.getNumeroTarjeta();
        String parteVisible = numeroCompleto.substring(numeroCompleto.length() - 4);

        // 2. Mapear DTO a Entidad
        Tarjetas tarjeta = new Tarjetas();
        tarjeta.setUsuarioId(usuarioId);
        tarjeta.setNumeroTarjeta(numeroCompleto); // Solo se guarda en DB, no se expone
        tarjeta.setParteVisible(parteVisible);
        tarjeta.setTitular(dto.getTitular());
        tarjeta.setMesVencimiento(dto.getMesVencimiento());
        tarjeta.setAnioVencimiento(dto.getAnioVencimiento());

        // 3. Guardar y Mapear a DTO de respuesta
        Tarjetas tarjetaGuardada = tarjetasRepository.save(tarjeta);
        return new TarjetaResponseDto(tarjetaGuardada);
    }

    /**
     * Obtiene todas las tarjetas de un usuario.
     * @param usuarioId ID del usuario.
     * @return Lista de DTOs de tarjetas, omitiendo el numero completo.
     */
    public List<TarjetaResponseDto> obtenerTarjetasPorUsuario(Long usuarioId) {
        List<Tarjetas> tarjetas = tarjetasRepository.findByUsuarioId(usuarioId);

        return tarjetas.stream()
                .map(TarjetaResponseDto::new) // Mapea cada Tarjetas a TarjetaResponseDto
                .collect(Collectors.toList());
    }

    /**
     * Elimina una tarjeta por su ID, asegurando que pertenezca al usuario.
     * @param usuarioId ID del usuario.
     * @param tarjetaId ID de la tarjeta a eliminar.
     */
    public void eliminarTarjeta(Long usuarioId, Long tarjetaId) {
        Tarjetas tarjeta = tarjetasRepository.findById(tarjetaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarjeta", tarjetaId));

        // Verificación de propiedadddd
        if (!tarjeta.getUsuarioId().equals(usuarioId)) {
            // Lanza una excepción de seguridad o acceso denegado
            throw new IllegalArgumentException("Acceso denegado. La tarjeta no pertenece al usuario.");
        }

        tarjetasRepository.delete(tarjeta);
    }
}