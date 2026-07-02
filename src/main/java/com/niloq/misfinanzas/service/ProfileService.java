package com.niloq.misfinanzas.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.niloq.misfinanzas.dto.AuthDTO;
import com.niloq.misfinanzas.dto.ProfileDTO;
import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.ProfileRepository;
import com.niloq.misfinanzas.util.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    // dependency injection for password encoder
    private final PasswordEncoder passwordEncoder;
    public final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;



    // Método para registrar un nuevo perfil de usuario
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        // newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));

        newProfile = profileRepository.save(newProfile);
        // enviar correo de activacion
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activa tu cuenta de finanzas personales";
        String body = "Haz clic en el siguiente enlace para activar tu cuenta " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);

    }

    // Método para convertir un ProfileDTO a ProfileEntity
    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                // 2 Codifique la contraseña antes de guardarla en la base de datos.
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImagenUrl(profileDTO.getProfileImagenUrl())
                .createdTt(profileDTO.getCreatedTt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

    }

    // Método para convertir un ProfileEntity a ProfileDTO
    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImagenUrl(profileEntity.getProfileImagenUrl())
                .createdTt(profileEntity.getCreatedTt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();

    }

    // Método para activar el perfil del usuario mediante el token de activación
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    // Método para verificar si el perfil del usuario está activo mediante el correo
    // electrónico
    public boolean isAccountActive(String email) {
        // Buscar el perfil del usuario en la base de datos por correo electrónico y
        // devolver el estado de activación
        return profileRepository.findByEmail(email)
                // Si se encuentra el perfil, devolver el valor de isActive; si no, devolver
                // false
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    // Método para obtener el perfil del usuario actualmente autenticado
    public ProfileEntity getCurrentProfile() {
        // Obtener la autenticación actual del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Obtener el correo electrónico del usuario autenticado
        String email = authentication.getName();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Perfil no encontrado para el email: " + authentication.getName()));
    }

    // Método para obtener el perfil público de un usuario por correo electrónico
    public ProfileDTO getPublicProfile(String email) {
        // Si el correo electrónico es nulo, obtener el perfil del usuario actualmente
        // autenticado; de lo contrario, buscar el perfil por correo electrónico
        ProfileEntity currentUser = null;

        if (email == null) {
            // Obtener el perfil del usuario actualmente autenticado
            currentUser = getCurrentProfile();

        } else {
            //
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Perfil no encontrado para el email: " + email));
        }
        // Construir y devolver un ProfileDTO con los datos del perfil encontrado
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImagenUrl(currentUser.getProfileImagenUrl())
                .createdTt(currentUser.getCreatedTt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }




    
    // Método para autenticar al usuario y generar un token de autenticación
    public Map<String, Object> authenticateAppGenerateToken(AuthDTO authDTO) {
        try {
            //
            authenticationManager.authenticate(
                    // Crear un objeto UsernamePasswordAuthenticationToken con el correo electrónico y la contraseña proporcionados
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
                    
                    
                    String token = jwtUtil.generateToken(authDTO.getEmail());
                    
                    
                    // Si la autenticación es exitosa, se puede generar un token JWT aquí (no implementado en este ejemplo)
                    return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail()),
                    "message", "Autenticación exitosa");

        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

}