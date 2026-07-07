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

    @PostMapping
    public ResponseEntity<?> filterTransactions (@RequestBody FilterDTO filter){
        LocalDate startDate = filter.getStartDate() !=null ? filter.getStartDate() : LocalDate.MIN;
         LocalDate endDate = filter.getEndDate() !=null ? filter.getEndDate() : LocalDate.now();
         String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
         String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
         Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
         Sort sort = Sort.by(direction,sortField);
         if("ingreso". equals(filter.getType())){
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);

         }else if ("gasto".equals(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
            
         }else {
            return ResponseEntity.badRequest().body("Tipo invalido. debe ser ingreso o gasto ");
         }
 
    }
    
    
}
