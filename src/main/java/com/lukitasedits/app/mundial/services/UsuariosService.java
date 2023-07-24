package com.lukitasedits.app.mundial.services;

import com.lukitasedits.app.mundial.entities.FiguritasUsuario;
import com.lukitasedits.app.mundial.entities.Usuario;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UsuariosService {

    public List<Usuario> obtenerUsuarios();

    public Usuario guardarUsuario(Usuario usuario, Set<FiguritasUsuario> figuritasDelUsuario) throws Exception;

    public Usuario save(Usuario usuario);

    public Usuario update(Usuario usuario, Long id);

    public Usuario obtenerUsuario(String username);

    public Usuario obtenerPorId(Long id);

    public void eliminarUsuario(Long id);

    public void eliminarUsuario(String username);

    public boolean existeUsuario(Usuario usuario);

    public  boolean existeUsuario(String username);

    public boolean emailRegistrado(String email);

    public FiguritasUsuario agregarFigurita(Usuario usuario, Long figuritaId);

    public int cantFigurita(Usuario usuario, Long figuritaId);

    public void restarFigurita(Usuario usuario, Long figuritaId);

    public boolean existeUsuario(Long id);

    public Usuario changePassword(Usuario usuario, String newPassword);

    public Map<Long, Integer> getFiguritasDelUsuario(Usuario usuario);

    public Optional<Usuario> getByUsernameAndEmail(String username, String email);

    public Optional<Usuario> getByTokenPassword(String tokenPassword);

    public Optional<Usuario> getByTokenEmail(String tokenEmail);
}
