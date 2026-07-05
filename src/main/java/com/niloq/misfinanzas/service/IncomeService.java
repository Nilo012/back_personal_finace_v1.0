package com.niloq.misfinanzas.service;

import org.springframework.stereotype.Service;


import com.niloq.misfinanzas.dto.IncomeDTO;
import com.niloq.misfinanzas.entity.CategoryEntity;

import com.niloq.misfinanzas.entity.IncomeEntity;
import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.CategoryRepository;
import com.niloq.misfinanzas.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;


    /**
     * Registra un nuevo gasto asociado al perfil actual y una categoría existente.
     * 
     * @param dto Datos del gasto recibidos desde el cliente.
     * @return El objeto ExpenseDTO con la información del gasto creado y sus
     *         fechas.
     */
    public IncomeDTO addIncome(IncomeDTO dto) {
        // 1. Obtiene el perfil autenticado para asociarlo al nuevo gasto
        ProfileEntity profile = profileService.getCurrentProfile();
        // 2. Busca y valida que la categoría exista antes de intentar crear el gasto
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        // 3. Convierte el DTO y las entidades relacionadas en una Entidad persistible
        IncomeEntity newExpense = toEntity(dto, profile, category);
        // 4. Guarda en base de datos y recupera la entidad con sus campos generados (id, createdAt)
        newExpense = incomeRepository.save(newExpense);
        // 5. Retorna la versión DTO para la respuesta de la API, manteniendo la capa de datos aislada
        return toDTO(newExpense);

    }




    // metodos auxiliares
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    private IncomeDTO toDTO(IncomeEntity entity) {
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
