
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
    
    public void enviarCorreo(UsuarioJPA usuario) throws MessagingException{
        
        String destinatario = usuario.getCorreo();
        String asunto = "Verifica para poder Ingresar";
        
       // String contenidoHtml = construirPlantillaCorreo(usuario);
        
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
    }
    
}
