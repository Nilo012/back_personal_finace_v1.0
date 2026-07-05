package com.niloq.misfinanzas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.niloq.misfinanzas.dto.ExpenseDTO;
import com.niloq.misfinanzas.entity.CategoryEntity;
import com.niloq.misfinanzas.entity.ExpenseEntity;
import com.niloq.misfinanzas.entity.ProfileEntity;
import com.niloq.misfinanzas.repository.CategoryRepository;
import com.niloq.misfinanzas.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    /**
     * Registra un nuevo gasto asociado al perfil actual y una categoría existente.
     * 
     * @param dto Datos del gasto recibidos desde el cliente.
     * @return El objeto ExpenseDTO con la información del gasto creado y sus
     *         fechas.
     */
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        // 1. Obtiene el perfil autenticado para asociarlo al nuevo gasto
        ProfileEntity profile = profileService.getCurrentProfile();
        // 2. Busca y valida que la categoría exista antes de intentar crear el gasto
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        // 3. Convierte el DTO y las entidades relacionadas en una Entidad persistible
        ExpenseEntity newExpense = toEntity(dto, profile, category);
        // 4. Guarda en base de datos y recupera la entidad con sus campos generados
        // (id, createdAt)
        newExpense = expenseRepository.save(newExpense);
        // 5. Retorna la versión DTO para la respuesta de la API, manteniendo la capa de
        // datos aislada
        return toDTO(newExpense);

    }

    /**
     * Obtiene todos los gastos del usuario actual correspondientes al mes en curso.
     * 
     * @return Lista de ExpenseDTO con los gastos filtrados por el rango del mes
     *         actual.
     */
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        // 1. Identifica al usuario autenticado para filtrar sus datos privados
        ProfileEntity profile = profileService.getCurrentProfile();

        // 2. Define el rango de fechas (desde el día 1 hasta el último día del mes)
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        // 3. Consulta la base de datos para obtener los registros en dicho rango
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(
                profile.getId(), startDate, endDate);

        // 4. Convierte cada entidad encontrada a su formato DTO para enviarlo al
        // cliente
        return list.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Elimina un gasto específico tras validar que pertenece al usuario
     * autenticado.
     * 
     * @param expenseId ID del gasto que se desea eliminar.
     * @throws RuntimeException si el gasto no existe o si el usuario no es el
     *                          dueño.
     */
    public void deleteExpense(Long expenseId) {
        // 1. Obtiene el perfil del usuario actual
        ProfileEntity profile = profileService.getCurrentProfile();

        // 2. Busca el gasto en la base de datos o lanza error si no existe
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + expenseId));

        // 3. Valida la autoría: impide que un usuario borre gastos de otro perfil
        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("No tiene autorización para eliminar este gasto");
        }

        // 4. Ejecuta la eliminación tras haber validado la existencia y permisos
        expenseRepository.delete(entity);
    }


    /**
     * Obtiene los últimos 5 gastos registrados por el usuario actual,
     * ordenados del más reciente al más antiguo.
     * 
     * @return Lista de los 5 gastos más recientes (ExpenseDTO).
     */
    public List<ExpenseDTO> getLatest5ExpensesForCurrenUser() {
        // 1. Obtiene el perfil autenticado para garantizar privacidad de los datos
        ProfileEntity profile = profileService.getCurrentProfile();

        // 2. Consulta al repositorio los 5 registros más recientes del usuario
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());

        // 3. Convierte las entidades recuperadas a DTOs para la respuesta de la API
        return list.stream()
                .map(this::toDTO)
                .toList();
    }



    /**
     * Calcula la suma total de los montos de todos los gastos del usuario actual.
     * 
     * @return El monto total acumulado, o BigDecimal.ZERO si el usuario no tiene
     *         gastos.
     */
    public BigDecimal getTotalExpenseForCurrentUser() {
        // 1. Obtiene el perfil autenticado para filtrar los gastos
        ProfileEntity profile = profileService.getCurrentProfile();

        // 2. Ejecuta la consulta SQL personalizada que suma los montos
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());

        // 3. Retorna el total calculado, o 0 si el resultado es nulo (sin gastos)
        return total != null ? total : BigDecimal.ZERO;
    }



    
    // metodos auxiliares
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }

    //
    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
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
