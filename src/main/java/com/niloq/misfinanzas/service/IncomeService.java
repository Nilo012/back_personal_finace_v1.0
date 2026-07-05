package com.niloq.misfinanzas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        // 4. Guarda en base de datos y recupera la entidad con sus campos generados
        // (id, createdAt)
        newExpense = incomeRepository.save(newExpense);
        // 5. Retorna la versión DTO para la respuesta de la API, manteniendo la capa de
        // datos aislada
        return toDTO(newExpense);

    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    // delete income by id for current user
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("No esta autorizado para eliminar este ingreso");
        }
        incomeRepository.delete(entity);
    }

    // Lista de los 5 ingresos más recientes (ExpenseDTO).
    public List<IncomeDTO> getLatest5IncomesForCurrenUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // Calcula la suma total de los montos de todos los ingresos del usuario actual
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
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
