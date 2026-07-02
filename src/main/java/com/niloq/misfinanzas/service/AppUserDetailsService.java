package com.niloq.misfinanzas.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// Implementación de UserDetailsService para cargar los detalles del usuario
// desde la base de datos
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    // Implementación del método loadUserByUsername para cargar los detalles del
    // usuario por correo electrónico
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar el perfil del usuario en la base de datos por correo electrónico
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        // Crear un objeto UserDetails a partir del perfil encontrado y devolverlo
        return User.builder()
                // Establecer el nombre de usuario como el correo electrónico del perfil
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList()) // Puedes asignar roles según tu lógica
                .build();
    }

}
