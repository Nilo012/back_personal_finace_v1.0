package com.niloq.misfinanzas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niloq.misfinanzas.dto.ExpenseDTO;
import com.niloq.misfinanzas.service.ExpenseService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto) {
        ExpenseDTO saved = expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Endpoint para obtener la lista de gastos del mes actual para el usuario
     * autenticado.
     * 
     * @return ResponseEntity con la lista de gastos (DTOs) y estado 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses() {
        // 1. Invoca al servicio para obtener los datos filtrados y transformados
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();

        // 2. Retorna la lista empaquetada en un ResponseEntity con código 200 OK
        return ResponseEntity.ok(expenses);
    }

    /**
     * Endpoint para eliminar un gasto por su identificador único.
     * 
     * @param id ID del gasto a eliminar recibido vía path variable.
     * @return ResponseEntity con estado 204 (No Content) si la eliminación fue
     *         exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        // 1. Ejecuta la lógica de eliminación definida en el servicio
        expenseService.deleteExpense(id);
        // 2. Retorna una respuesta vacía con código 204 (No Content) indicando éxito
        return ResponseEntity.noContent().build();
    }

}
