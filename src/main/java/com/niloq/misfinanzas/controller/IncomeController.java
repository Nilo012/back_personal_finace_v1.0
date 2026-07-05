package com.niloq.misfinanzas.controller;

import org.springframework.web.bind.annotation.RestController;


import com.niloq.misfinanzas.dto.IncomeDTO;
import com.niloq.misfinanzas.service.IncomeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    // endpoint para agregar ingresos por ad de categoria de cdaa perfil
    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO dto) {
        IncomeDTO saved = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
}
