package com.niloq.misfinanzas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niloq.misfinanzas.dto.ExpenseDTO;
import com.niloq.misfinanzas.dto.FilterDTO;
import com.niloq.misfinanzas.dto.IncomeDTO;
import com.niloq.misfinanzas.service.ExpenseService;
import com.niloq.misfinanzas.service.IncomeService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    /**
     * Endpoint para filtrar transacciones de forma dinámica según criterios de
     * búsqueda.
     * 
     * @param filter Objeto que contiene las fechas, palabra clave, campo de orden y
     *               tipo.
     * @return Lista de transacciones (ingresos o gastos) o un error si el tipo es
     *         inválido.
     */
    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter) {
        // 1. Asigna valores por defecto para evitar errores si el cliente no envía
        // filtros
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";

        // 2. Configura el criterio de ordenamiento (Ascendente o Descendente)
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        // 3. Ejecuta la búsqueda según el tipo de transacción solicitado
        if ("ingreso".equals(filter.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if ("gasto".equals(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else {
            // 4. Retorna error 400 si el filtro 'type' no es reconocido
            return ResponseEntity.badRequest().body("Tipo inválido. Debe ser 'ingreso' o 'gasto'.");
        }
    }

}
