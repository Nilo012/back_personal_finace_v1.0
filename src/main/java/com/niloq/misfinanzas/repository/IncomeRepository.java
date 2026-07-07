package com.niloq.misfinanzas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
//import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.niloq.misfinanzas.entity.IncomeEntity;


public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    // Obtiene todos los gastos de un perfil específico ordenados de más reciente a
    // más antiguo
    // SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? ORDER BY date DESC
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileid);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id= :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    //
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort);

    // Obtiene una lista de gastos de un perfil dentro de un rango de fechas
    // específico
    // SQL: SELECT * FROM tbl_incomes WHERE profile_id = ? AND date BETWEEN ? AND ?
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}
