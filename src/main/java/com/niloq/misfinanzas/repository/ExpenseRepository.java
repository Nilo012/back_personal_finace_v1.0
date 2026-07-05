package com.niloq.misfinanzas.repository;


import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.niloq.misfinanzas.entity.ExpenseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    
    // Obtiene todos los gastos de un perfil específico ordenados de más reciente a más antiguo
    // SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? ORDER BY date DESC
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);
    
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileid);

    // 
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id= :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
    //
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );

    // Obtiene una lista de gastos de un perfil dentro de un rango de fechas específico
    // SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? AND date BETWEEN ? AND ?
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
