package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.JWT.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void enviarCorreoVerificacion(UsuarioJPA usuario) throws MessagingException {
        String destinatario = usuario.getCorreo();
        String asunto = "¡Verifica tu cuenta de Entrenador Pokémon!";

        String contenidoHtml = construirPlantillaCorreo(usuario);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true);

        mailSender.send(mensaje);
        System.out.println("Correo enviado a: " + destinatario);
    }

    private String construirPlantillaCorreo(UsuarioJPA usuario) {
        String nombreCompleto = usuario.getNombre();

        String tokenVerificacion = jwtUtils.generateVerificationToken(
                usuario.getCorreo(),
                usuario.getIdUsuario()
        );

        String enlaceVerificacion = baseUrl + "/pokedex/verificar?token=" + tokenVerificacion;

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #1a1a2e;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 50px auto;
                        background-color: rgba(30, 30, 30, 0.95);
                        border-radius: 15px;
                        overflow: hidden;
                        box-shadow: 0 4px 15px rgba(0,0,0,0.5);
                        border: 3px solid #ff4136;
                    }
                    .header {
                        background: rgba(0, 0, 0, 0.3);
                        padding: 40px 20px;
                        text-align: center;
                        position: relative;
                    }
                    .header img {
                        width: 80px;
                        height: auto;
                        animation: bounce 2s infinite;
                        margin-bottom: 15px;
                    }
                    @keyframes bounce {
                        0%%, 100%% { transform: translateY(0); }
                        50%% { transform: translateY(-10px); }
                    }
                    .header h1 {
                        color: white;
                        margin: 20px 0 10px 0;
                        font-size: 28px;
                        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
                    }
                    .header p {
                        color: rgba(255, 255, 255, 0.9);
                        font-size: 14px;
                        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
                    }
                    .content {
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .content h2 {
                        color: #ff4136;
                        margin-bottom: 20px;
                        font-size: 24px;
                        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
                    }
                    .content p {
                        color: rgba(255, 255, 255, 0.9);
                        line-height: 1.6;
                        margin-bottom: 30px;
                        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
                    }
                    .btn {
                        display: inline-block;
                        padding: 15px 40px;
                        background-color: #ff4136;
                        color: white;
                        text-decoration: none;
                        border-radius: 8px;
                        font-weight: bold;
                        font-size: 16px;
                        transition: all 0.3s ease;
                        border: 2px solid black;
                        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
                    }
                    .btn:hover {
                        background-color: #dc1f0f;
                        transform: scale(1.05);
                    }
                    .footer {
                        background-color: rgba(0, 0, 0, 0.3);
                        padding: 20px;
                        text-align: center;
                        color: rgba(255, 255, 255, 0.7);
                        font-size: 12px;
                        border-top: 2px solid rgba(255, 65, 54, 0.3);
                    }
                    .footer p {
                        margin: 5px 0;
                        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
                    }
                    .warning {
                        margin-top: 30px;
                        padding: 15px;
                        background-color: rgba(255, 255, 255, 0.1);
                        border-left: 4px solid #ff4136;
                        color: rgba(255, 255, 255, 0.9);
                        text-align: left;
                        border-radius: 5px;
                    }
                    .warning strong {
                        color: #ff4136;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Pok%%C3%%A9_Ball_icon.svg/1026px-Pok%%C3%%A9_Ball_icon.svg.png" 
                             alt="Pokeball">
                        <h1>¡Bienvenido Entrenador!</h1>
                        <p>Por favor verifica tu correo electrónico</p>
                    </div>
                    
                    <div class="content">
                        <h2>¡Hola, %s!</h2>
                        <p>
                            ¡Gracias por unirte a nuestra comunidad de entrenadores Pokémon! <br><br>
                            Para completar tu registro y comenzar tu aventura, necesitas verificar tu correo electrónico.
                        </p>
                        
                        <a href="%s" class="btn">
                            Verificar mi cuenta
                        </a>
                        
                        <div class="warning">
                            <strong>Importante:</strong> Este enlace expirará en 48 horas. 
                            Si no solicitaste este registro, puedes ignorar este correo.
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p style="font-weight: bold;">PokeRegistro</p>
                        <p>Este correo fue enviado automáticamente</p>
                        <p>© 2026 PokeRegistro.</p>
                    </div>
                </div>
            </body>
            </html>
            """, nombreCompleto, enlaceVerificacion);
    }

    public void enviarCorreoCodigo(UsuarioJPA usuario, String codigo) throws MessagingException {
        String destinatario = usuario.getCorreo();
        String asunto = "Código de recuperación de contraseña";

        String contenidoHtml = "<h3>Hola " + usuario.getNombre() + "</h3>"
                + "<p>Tu código de verificación es: <b>" + codigo + "</b></p>"
                + "<p>Si no solicitaste este cambio, ignora este correo.</p>";

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true);

        mailSender.send(mensaje);
        System.out.println("Correo enviado a: " + destinatario + " con código: " + codigo);
    }

}
