package com.niloq.misfinanzas.controller;

import java.util.Map;

//import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.niloq.misfinanzas.dto.AuthDTO;
import com.niloq.misfinanzas.dto.ProfileDTO;
import com.niloq.misfinanzas.service.ProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    // Endpoint para activar el perfil de usuario mediante un token de activación
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivate = profileService.activateProfile(token);
        if (isActivate) {
            return ResponseEntity.ok("Perfil activado con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro el token de activacion o ya fue usado");
        }
    }

        // Endpoint para iniciar sesión y generar un token de autenticación
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        // Verificar si la cuenta está activada antes de autenticar
        try {
            if(!profileService.isAccountActive(authDTO.getEmail())){
                
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Cuenta no activada",
                    "message", "Por favor, activa tu cuenta antes de iniciar sesión."
                ));
            }
            Map<String, Object> response = profileService.authenticateAppGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Credenciales inválidas",
                "message", "Por favor, verifica tus credenciales e intenta de nuevo."
            ));
        }
    }

    
    // Endpoint de prueba para verificar la autenticación del usuario
    @GetMapping("/test")
    public String test() {
        // Lógica para obtener la información del perfil del usuario autenticado
        return "Información del perfil del usuario autenticado";
    }

}
