package com.niloq.misfinanzas.service;

import java.util.List;


import org.springframework.stereotype.Service;


import com.niloq.misfinanzas.dto.CategoryDTO;
import com.niloq.misfinanzas.entity.CategoryEntity;
import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;  

    // Método para guardar una nueva categoría
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        // Obtener el perfil actual del usuario
        ProfileEntity profile = profileService.getCurrentProfile();
        // Verificar si ya existe una categoría con el mismo nombre para este perfil
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {

            throw new RuntimeException("Ya existe una categoría con el mismo nombre para este perfil");
        }
        // Convertir el CategoryDTO a CategoryEntity y guardarlo en la base de datos
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory= categoryRepository.save(newCategory);
        return toDTO(newCategory);

    }


    // 2.Método para obtener todas las categorías del perfil actual
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    // 3.Método para obtener todas las categorías de un tipo específico para el perfil actual
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        // Obtener el perfil actual del usuario
        ProfileEntity profile = profileService.getCurrentProfile();
        // Obtener todas las categorías del tipo especificado para el perfil actual
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        // Convertir la lista de CategoryEntity a una lista de CategoryDTO y devolverla
        return entities.stream().map(this::toDTO).toList();
    }


    // 4.Método para actualizar una categoría existente
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada para este perfil"));

        // Actualizar los campos de la categoría existente
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setType(categoryDTO.getType());
        // Guardar los cambios en la base de datos
        existingCategory= categoryRepository.save(existingCategory);
        return toDTO(existingCategory);

        // Guardar los cambios en la base de datos
        // CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        // return toDTO(updatedCategory);
    }
















    
    // Método para convertir un CategoryDTO a CategoryEntity
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }
    // Método para convertir un CategoryEntity a CategoryDTO
    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() !=null ? entity.getProfile().getId():null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }
    
    
}
