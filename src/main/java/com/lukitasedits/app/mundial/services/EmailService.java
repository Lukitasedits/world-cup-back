package com.lukitasedits.app.mundial.services;

import com.lukitasedits.app.mundial.repositories.EmailValuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value( "${mail.urlFront}")
    private String urlFront;

    public void sendEmail(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("leblonvideoclub@gmail.com");
        message.setTo("lukas.petersen77@gmail.com");
        message.setSubject("Prueba envío email simple");
        message.setText("Esto es el contenido del email");

        javaMailSender.send(message);
    }

    public void sendEmail(EmailValuesDTO dto){
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("userName", dto.getUsername());
            if(dto.getTokenPassword() != null) {
                model.put("title", "Cambiar Contraseña");
                model.put("url", urlFront + "change-password/" + dto.getTokenPassword());
            } else if (dto.getTokenEmail() != null){
                model.put("title", "Verificar Usuario");
                model.put("url", urlFront + "autorizar/" + dto.getTokenEmail());
            }
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template", context);
            helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);
            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
