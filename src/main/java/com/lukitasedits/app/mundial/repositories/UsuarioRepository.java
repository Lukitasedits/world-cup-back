package com.lukitasedits.app.mundial.repositories;

import com.lukitasedits.app.mundial.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Usuario findByUsername(String username);

    public Optional<Usuario> findByUsernameAndEmail(String username, String email);

    public Optional<Usuario> findByTokenPassword(String tokenPassword);

    public Optional<Usuario> findByTokenEmail(String tokenEmail);

    public boolean existsByUsernameAndEmail(String username, String email);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public boolean existsById(Long id);

    public void deleteByUsername(String username);

}
