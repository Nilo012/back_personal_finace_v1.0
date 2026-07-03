package com.niloq.misfinanzas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niloq.misfinanzas.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // Método para obtener todas las categorías de un perfil específico
    // select * from tlb_categories where profile_id = ?
    List<CategoryEntity> findByProfileId(Long profileId);

    // Método para obtener una categoría específica de un perfil específico
    // select * from tlb_categories where id = ? and profile_id = ?
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    // Método para obtener todas las categorías de un tipo específico (income o expense) de un perfil específico
    // select * from tlb_categories where type = ? and profile_id = ?
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    // Método para verificar si existe una categoría con un nombre específico para un perfil específico 
    // select * from tlb_categories where name = ? and profile_id = ?
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
