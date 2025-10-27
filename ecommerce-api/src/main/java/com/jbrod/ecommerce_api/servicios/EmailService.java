package com.jbrod.ecommerce_api.servicios;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private final ConfiguracionGlobalService configuracionGlobalService;

    public EmailService(ConfiguracionGlobalService configuracionGlobalService) {
        this.configuracionGlobalService = configuracionGlobalService;
    }

    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        String remitente = configuracionGlobalService.getCorreoRemitente();
        String password = configuracionGlobalService.getContrasenaAplicacion();

        if (remitente == null || remitente.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalStateException("Configuración de correo no establecida.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            Transport.send(mensaje);
            System.out.println("Correo enviado exitosamente a " + destinatario);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
}
