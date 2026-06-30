package com.niloq.misfinanzas.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.niloq.misfinanzas.dto.ProfileDTO;
import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // enviar activacion de email
         String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
         String subject = "Activa tu cuenta de finanzas personales";
         String body = "Haz clic en el siguiente enlace para activar tu cuenta " + activationLink;
         emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
        
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImagenUrl(profileDTO.getProfileImagenUrl())
                .createdTt(profileDTO.getCreatedTt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
                

    }

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

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
        .map(profile->{
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        })
        .orElse(false);
    }

}