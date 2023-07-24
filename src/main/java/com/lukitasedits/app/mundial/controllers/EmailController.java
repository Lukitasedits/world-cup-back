package com.lukitasedits.app.mundial.controllers;

import com.lukitasedits.app.mundial.entities.Usuario;
import com.lukitasedits.app.mundial.repositories.ChangePasswordDTO;
import com.lukitasedits.app.mundial.repositories.EmailValuesDTO;
import com.lukitasedits.app.mundial.repositories.VerificationEmailDTO;
import com.lukitasedits.app.mundial.services.EmailService;
import com.lukitasedits.app.mundial.services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/email-password")
@CrossOrigin(origins = {"http://localhost:4200"})
public class EmailController {

    @Autowired
    UsuariosService usuariosService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String mailFrom;


    @PostMapping("/send-email-register")
    public ResponseEntity<?> sendEmailRegister(@RequestBody EmailValuesDTO dto) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuariosService.getByUsernameAndEmail(dto.getUsername(), dto.getMailTo());
        if (!usuarioOpt.isPresent()) {
            response.put("mensaje", "No existe ningún usuario con esas credenciales");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        Usuario usuario = usuarioOpt.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(usuario.getEmail());
        dto.setSubject("Hola " + dto.getUsername() +  ". Haz click en el siguiente botón para verificar tu email y habilitar tu cuenta.");
        dto.setUsername(usuario.getUsername());
        UUID uuid = UUID.randomUUID();
        String tokenEmail = uuid.toString();
        dto.setTokenEmail(tokenEmail);
        usuario.setTokenEmail(tokenEmail);
        usuariosService.save(usuario);
        emailService.sendEmail(dto);

        response.put("mensaje", "Te hemos enviado un correo");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping("/email-verification")
    public ResponseEntity<?> emailVerification(@RequestBody VerificationEmailDTO dto){
        System.out.println("emailVerification()");
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuariosService.getByTokenEmail(dto.getTokenEmail());
        if(!usuarioOpt.isPresent()){
            response.put("mensaje", "No existe ningún usuario con esas credenciales");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.enable();
        usuario.setTokenEmail(null);
        usuariosService.save(usuario);
        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }

    @PostMapping("/send-email-recover")
    public ResponseEntity<?> sendEmailRecover(@RequestBody EmailValuesDTO dto) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = usuariosService.getByUsernameAndEmail(dto.getUsername(), dto.getMailTo());
        if (!usuarioOpt.isPresent()) {
            response.put("mensaje", "No existe ningún usuario con esas credenciales");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        Usuario usuario = usuarioOpt.get();
        dto.setMailFrom(mailFrom);
        dto.setMailTo(usuario.getEmail());
        dto.setSubject("Hola " + dto.getUsername() + ". Haz click en el siguiente botón para cambiar tu contraseña.");
        dto.setUsername(usuario.getUsername());
        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();
        dto.setTokenPassword(tokenPassword);
        usuario.setTokenPassword(tokenPassword);
        usuariosService.save(usuario);
        emailService.sendEmail(dto);

        response.put("mensaje", "Te hemos enviado un correo");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "el campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            response.put("mensaje", "Las contraseña no coinciden");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Usuario> usuarioOpt = usuariosService.getByTokenPassword(dto.getTokenPassword());

        if (!usuarioOpt.isPresent()) {
            response.put("mensaje", "No existe ningún usuario con esas credenciales");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        Usuario usuario = usuarioOpt.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(newPassword);
        usuario.setTokenPassword(null);
        usuariosService.save(usuario);
        response.put("mensaje", "Contraseña actualizada");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

}
