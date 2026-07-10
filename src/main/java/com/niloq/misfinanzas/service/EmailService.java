package com.niloq.misfinanzas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Async   
    public void sendEmail(String to, String subjet, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subjet);
            message.setText(body);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("FALLO AL ENVIAR CORREO: " + e.getMessage());
        }

    }

    // @Value("${spring.mail.username}")
    // private String username;

    // @Value("${spring.mail.password}")
    // private String password;

    // @PostConstruct
    // public void test() {
    //     System.out.println("USERNAME = " + username);
    //     System.out.println("PASSWORD = " + password);
    // }

}
