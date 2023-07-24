package com.lukitasedits.app.mundial.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min =  5, max = 15)
    private String username;

    @NotEmpty
    @Size(min =  5, message = " debe tener almenos 5 caracteres")
    private  String password;

    private String tokenPassword;

    private String tokenEmail;

    @NotEmpty
    @Email
    private String email;


    private boolean enabled = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "usuario")
    @JsonIgnore
    private Set<FiguritasUsuario> figuritasUsuarios = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void disable(){
        enabled = false;
    }

    public void enable(){
        enabled = true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Authority> autoridades = new HashSet<>();
        autoridades.add(new Authority("USER"));
        return autoridades;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenPassword() {
        return tokenPassword;
    }

    public void setTokenPassword(String tokenPassword) {
        this.tokenPassword = tokenPassword;
    }

    public Set<FiguritasUsuario> getFiguritasUsuarios() {
        return figuritasUsuarios;
    }

    public void setFiguritasUsuarios(Set<FiguritasUsuario> figuritasUsuarios) {
        this.figuritasUsuarios = figuritasUsuarios;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<FiguritasUsuario> getFiguritas() {
        return figuritasUsuarios;
    }

    public void setFiguritas(Set<FiguritasUsuario> figuritasUsuarios) {
        this.figuritasUsuarios = figuritasUsuarios;
    }

    public String getTokenEmail() {
        return tokenEmail;
    }

    public void setTokenEmail(String tokenEmail) {
        this.tokenEmail = tokenEmail;
    }

    public Usuario(){

    }
}
