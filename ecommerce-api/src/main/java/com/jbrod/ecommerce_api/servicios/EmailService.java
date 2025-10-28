package com.jbrod.ecommerce_api.servicios;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    // Dependencia para obtener la configuración (correo y contraseña)
    private final ConfiguracionGlobalService configuracionGlobalService;

    // Constructor que Spring usa para inyectar la dependencia (ConfiguracionGlobalService)
    public EmailService(ConfiguracionGlobalService configuracionGlobalService) {
        this.configuracionGlobalService = configuracionGlobalService;
    }

    /**
     * Metodo público para enviar un correo electrónico.
     * @param destinatario La dirección de correo a la que se envía.
     * @param asunto El tema del correo.
     * @param cuerpo El contenido del mensaje.
     */
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        // 1. Obtener remitente desde el servicio de configuracion :3
        String remitente = configuracionGlobalService.getCorreoRemitente();
        String password = configuracionGlobalService.getContrasenaAplicacion();

        // 2. Validación de credenciales, si no existen, lanza una excepcion
        if (remitente == null || remitente.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalStateException("Configuración de correo no establecida.");
        }

        // 3. Configuración de propiedades para el servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");             // Habilitar autenticación
        props.put("mail.smtp.starttls.enable", "true");  // Habilitar TLS (seguridad)
        props.put("mail.smtp.host", "smtp.zoho.com");    // Host del servidor
        props.put("mail.smtp.port", "587");              // Puerto estándar para STARTTLS

        // 4. Crear la sesión de correo con las propiedades y el autenticador
        Session session = Session.getInstance(props, new Authenticator() {
            // El autenticador proporciona el nombre de usuario y la contraseña de la app
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        // 5. Manejar cualquer posible error
        try {
            // Crear un nuevo mensaje (MimeMessage) usando la sesión
            Message mensaje = new MimeMessage(session);

            // Establecer la dirección del remitente
            mensaje.setFrom(new InternetAddress(remitente));

            // Establecer los destinatarios (puede ser uno o varios)
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

            // Establecer el asunto del correo
            mensaje.setSubject(asunto);

            // Establecer el cuerpo del mensaje (texto plano por defecto)
            mensaje.setText(cuerpo);

            // 6. Enviar el mensaje
            Transport.send(mensaje);

            // Mensaje de wxito en la consola :D
            System.out.println("Correo enviado exitosamente a " + destinatario);
        } catch (MessagingException e) {
            // 7. Si hay un error al enviar, se lanza una RuntimeException con el detalle
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
}