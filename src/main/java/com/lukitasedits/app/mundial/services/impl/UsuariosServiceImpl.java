package com.lukitasedits.app.mundial.services.impl;

import com.lukitasedits.app.mundial.entities.FiguritasUsuario;
import com.lukitasedits.app.mundial.entities.Usuario;
import com.lukitasedits.app.mundial.repositories.FiguritasUsuarioRepository;
import com.lukitasedits.app.mundial.repositories.UsuarioRepository;
import com.lukitasedits.app.mundial.services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UsuariosServiceImpl implements UsuariosService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FiguritasUsuarioRepository figuritasRepository;


    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario guardarUsuario(Usuario usuario, Set<FiguritasUsuario> figuritasDelUsuario) throws Exception {

        usuario.setFiguritas(figuritasDelUsuario);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Usuario usuarioNuevo, Long id) {
        Usuario usuarioTabla = usuarioRepository.findById(id).orElse(null);

        if(usuarioNuevo.getUsername()!=null){
            usuarioTabla.setUsername(usuarioNuevo.getUsername());
        }
        if(usuarioNuevo.getPassword()!=null){
            usuarioTabla.setPassword(usuarioNuevo.getPassword());
        }
        if(usuarioNuevo.getEmail()!=null){
            usuarioTabla.setEmail(usuarioNuevo.getEmail());
        }
        if(usuarioNuevo.getFiguritas()!=null){
            usuarioTabla.setFiguritas(usuarioNuevo.getFiguritas());
        }

        return  usuarioRepository.save(usuarioTabla);

    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarUsuario(String username){
        usuarioRepository.deleteByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuario(Usuario usuario) {
        return usuarioRepository.existsByUsernameAndEmail(usuario.getUsername(), usuario.getEmail());
    }

    public boolean existeUsuario(String username){
        return  usuarioRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailRegistrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }


    @Override
    @Transactional
    public FiguritasUsuario agregarFigurita(Usuario usuario, Long figuritaId) {

        if(figuritaId < 0L || figuritaId > 646L) {
            throw new RuntimeException("No existe esa figurita");
        }

        FiguritasUsuario figuritaUsuario = new FiguritasUsuario();

        if(existeUsuario(usuario)) {

            if(cantFigurita(usuario, figuritaId) == 0) {
                figuritaUsuario.setUsuario(usuario);
                figuritaUsuario.setIdFigurita(figuritaId);
                figuritaUsuario.setCantidad(1);
                figuritaUsuario = figuritasRepository.save(figuritaUsuario);
            } else {
                figuritaUsuario = figuritasRepository.getByUsuarioAndIdFigurita(usuario, figuritaId);
                int cantidadActual = figuritaUsuario.getCantidad();
                cantidadActual++;
                figuritaUsuario.setCantidad(cantidadActual);
                figuritasRepository.save(figuritaUsuario);
            }
        }

        return figuritaUsuario;
    }

    @Override
    @Transactional(readOnly = true)
    public int cantFigurita(Usuario usuario, Long figuritaId) {

        if(figuritasRepository.existsByUsuarioAndIdFigurita(usuario, figuritaId)){

            return figuritasRepository.getByUsuarioAndIdFigurita(usuario, figuritaId).getCantidad();
        }
        return 0;
    }

    @Override
    public void restarFigurita(Usuario usuario, Long figuritaId) {

        if(cantFigurita(usuario, figuritaId) >= 1){
            FiguritasUsuario figuritaUsuario = figuritasRepository.getByUsuarioAndIdFigurita(usuario, figuritaId);
            int cantidadActual = cantFigurita(usuario, figuritaId);
            cantidadActual--;

            if(cantidadActual == 0){
                figuritasRepository.deleteById(figuritaUsuario.getId());
            } else{
                figuritaUsuario.setCantidad(cantidadActual);
                figuritasRepository.save(figuritaUsuario);
            }

        }


    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuario(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    @Transactional
    public Usuario changePassword(Usuario usuario, String newPassword) {

        Usuario usuarioActualizado = usuario;

        usuarioActualizado.setPassword(newPassword);

        return usuarioRepository.save(usuarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getFiguritasDelUsuario(Usuario usuario){
        Set<FiguritasUsuario> figuritasUsuarios = figuritasRepository.findByUsuario(usuario);
        Map<Long, Integer> retorno = new HashMap<>();

        figuritasUsuarios.forEach(f -> {
            retorno.put(f.getIdFigurita(), f.getCantidad());
        });

        return retorno;

    }

    @Override
    public Optional<Usuario> getByUsernameAndEmail(String username, String email) {
        return usuarioRepository.findByUsernameAndEmail(username, email);
    }

    @Override
    public Optional<Usuario> getByTokenPassword(String tokenPassword) {
        return usuarioRepository.findByTokenPassword(tokenPassword);
    }

    @Override
    public Optional<Usuario> getByTokenEmail(String tokenEmail) {
        return usuarioRepository.findByTokenEmail(tokenEmail);
    }

}
