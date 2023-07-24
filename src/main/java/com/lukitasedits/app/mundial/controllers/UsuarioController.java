package com.lukitasedits.app.mundial.controllers;

import com.lukitasedits.app.mundial.entities.FiguritasUsuario;
import com.lukitasedits.app.mundial.entities.Usuario;
import com.lukitasedits.app.mundial.services.UsuariosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:4200"})
public class UsuarioController {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Operation(summary = "Guarda el usuario desde el objeto.", description = "Esta es la descripción")
    @PostMapping("/")
    public ResponseEntity<?> guardarUsuario(
            @Valid @Schema(
                    description = "Datos del usuario a crear",
                    example = "{\"nombre\": \"John Doe\", \"edad\": 30}"
                    ) @RequestBody() Usuario usuario, BindingResult result) throws Exception{
        Usuario nuevoUsuario = null;
        Set<FiguritasUsuario> figuritas = new HashSet<>();
        Map<String, Object> response = new HashMap<>();

        usuario.setPassword(this.bCryptPasswordEncoder.encode(usuario.getPassword()));

        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "el campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(usuariosService.existeUsuario(usuario.getUsername())){
            response.put("mensaje", "El usuario " + usuario.getUsername() + " ya existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        if (usuariosService.emailRegistrado(usuario.getEmail())){
            response.put("mensaje", "El email " + usuario.getEmail() + " ya está registrado.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            nuevoUsuario = usuariosService.guardarUsuario(usuario, figuritas);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar registrar usuario.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<Usuario>(nuevoUsuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Agrega la figurita {figuritaId} al usuario {username} ")
    @PostMapping("/{username}/{figuritaId}")
    public  ResponseEntity<?> agregarFigurita(  @Parameter(description = "nombre del usuario a obtener")
                                                    @PathVariable("username") String username, @PathVariable("figuritaId") Long figuritaId){
        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario '" + username + "' no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        if(figuritaId < 0 || figuritaId >= 646L){
            response.put("mensaje", "La figurita " + figuritaId + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        FiguritasUsuario nuevaFigurita = null;
        try {
            Usuario usuario = usuariosService.obtenerUsuario(username);
            nuevaFigurita = usuariosService.agregarFigurita(usuario, figuritaId);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar agregar figurita.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<FiguritasUsuario>(nuevaFigurita, HttpStatus.OK);
    }

    @Operation(summary = "Retorna una lista con los usuarios existentes.")
    @ApiResponse(
            responseCode = "200",
            description = "Usuario creado correctamente",
            content = @Content(mediaType="application/json",
            schema = @Schema(implementation = Usuario.class))
    )
    @ApiResponse(
            responseCode = "404",
            description="No existen usuarios",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "500",
            description="Error al intentar obtener usuarios.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @ Schema(implementation = Map.class)
            )
    )
    @GetMapping("/")
    public ResponseEntity<?> obtenerUsuarios(){
        Map<String, Object> response = new HashMap<>();

        List<Usuario> usuarios = new ArrayList<>();
        try{
            usuarios = usuariosService.obtenerUsuarios();

            if(usuarios.isEmpty()){
                response.put("mensaje", "No existen usuarios.");
                return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar obtener usuarios.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
    }

    @Operation(summary = "Otorga la información del usuario {username}")
    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable("username") String username){
        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        Usuario nuevoUsuario = null;
        try{
            nuevoUsuario = usuariosService.obtenerUsuario(username);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar obtener usuario.");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return  new ResponseEntity<Usuario>(nuevoUsuario, HttpStatus.OK);

    }

    @Operation(summary = "Retorna el numero de veces que tiene el usuario {username} la figurita {figuritaId}.")
    @GetMapping("/{username}/{figuritaId}")
    public ResponseEntity<?> contarFiguritaDeUsuario(@PathVariable("username") String username, @PathVariable("figuritaId") Long figuritaId){

        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        if(figuritaId < 0 || figuritaId >= 646L){
            response.put("mensaje", "La figurita " + figuritaId + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        int cantidad = 0;
        try{
            Usuario usuario = usuariosService.obtenerUsuario(username);
            cantidad = usuariosService.cantFigurita(usuario, figuritaId);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar obtener la cantidad de figurita " + figuritaId + " de " + username + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Number>(cantidad, HttpStatus.OK);

    }

    @Operation(summary = "Retorna una lista con las figuritas del usuario {username}.")
    @GetMapping("/figuritas/{username}")
    public ResponseEntity<?> getFiguritasDelUsuario(@PathVariable("username") String username){

        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        Map<Long, Integer> figuritasUsuario = null;
        try{
            Usuario usuario = usuariosService.obtenerUsuario(username);
            figuritasUsuario = usuariosService.getFiguritasDelUsuario(usuario);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar obtener las figuritas de " + username + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Map<Long, Integer>>(figuritasUsuario, HttpStatus.OK);
    }

    @Operation(summary = "Actualiza la información del usuario dada en forma de objeto.")
    @PutMapping("/update/")
    public ResponseEntity<?> actualizarUsuario(@Valid @RequestBody() Usuario usuario, BindingResult result){
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "el campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(!usuariosService.existeUsuario(usuario.getUsername())){
            response.put("mensaje", "El usuario " + usuario.getUsername() + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        Usuario nuevoUsuario = null;

        try{
            nuevoUsuario = usuariosService.update(usuario, usuario.getId());
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar cambiar la contraseña del usuario " + usuario.getUsername() + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Usuario>(nuevoUsuario, HttpStatus.OK);
    }

    @Operation(summary = "Cambia la contraseña del usuario {username} por {newPassword}.")
    @PutMapping("/change-password/{username}/{newPassword}")
    public ResponseEntity<?> changePassword(@PathVariable("username") String username, @PathVariable("newPassword") String newPassword){

        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            Usuario usuario = usuariosService.obtenerUsuario(username);
            usuariosService.changePassword(usuario, newPassword);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar cambiar la contraseña del usuario " + username + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Elimina el usuario de la base de datos identificado por {usuairoId}.")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<?> eliminarUsuarioById(@PathVariable("usuarioId") Long id){

        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(id)){
            response.put("mensaje", "El usuario con id " + id + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            usuariosService.eliminarUsuario(id);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar eliminiar el usuario con id " + id + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Elimina el usuario de la base de datos identificado por {username}.")
    @DeleteMapping()
    public ResponseEntity<?> eliminarUsuarioByUsername(@RequestParam(value = "username") String username){

        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            usuariosService.eliminarUsuario(username);
        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar eliminiar el usuario " + username + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Resta una figurita {figuritaId} al usuario {username}.")
    @DeleteMapping("/{username}/{figuritaId}")
    public ResponseEntity<?> restarFigurita(@PathVariable("username") String username, @PathVariable("figuritaId") Long figuritaId){


        Map<String, Object> response = new HashMap<>();

        if(!usuariosService.existeUsuario(username)){
            response.put("mensaje", "El usuario " + username + " no existe.");
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            Usuario usuario = usuariosService.obtenerUsuario(username);
            int cantFigurita = usuariosService.cantFigurita(usuario, figuritaId);
            if(cantFigurita == 0){
                response.put("mensaje", "El usuario " + username + " no tiene la figurita " + figuritaId + ". ");
                return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                usuariosService.restarFigurita(usuario, figuritaId);
            }

        } catch (DataAccessException e){
            response.put("mensaje", "Error al intentar eliminiar la figurita " + figuritaId + " de " + username + ".");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
