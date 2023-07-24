package com.lukitasedits.app.mundial.services.impl;

import com.lukitasedits.app.mundial.entities.Usuario;
import com.lukitasedits.app.mundial.repositories.UsuarioRepository;
import com.lukitasedits.app.mundial.services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuariosService usuariosService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuariosService.obtenerUsuario(username);
        return usuario;
    }
}
