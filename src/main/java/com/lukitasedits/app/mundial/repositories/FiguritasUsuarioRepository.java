package com.lukitasedits.app.mundial.repositories;

import com.lukitasedits.app.mundial.entities.FiguritasUsuario;
import com.lukitasedits.app.mundial.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface FiguritasUsuarioRepository extends CrudRepository<FiguritasUsuario, Long> {

    public boolean existsByUsuarioAndIdFigurita(Usuario usuario, Long idFigurita);
    public FiguritasUsuario getByUsuarioAndIdFigurita(Usuario usuario, Long idFigurita);
    public Set<FiguritasUsuario> findByUsuario (Usuario usuario);

}
