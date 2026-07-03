package com.niloq.misfinanzas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niloq.misfinanzas.dto.CategoryDTO;
import com.niloq.misfinanzas.service.CategoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // Endpoint para guardar una nueva categoría
    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // Endpoint para obtener todas las categorías del perfil actual
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }

    // 3.Endpoint para obtener todas las categorías de un tipo específico (gastos o ingresos) del perfil actual
    @GetMapping("/{type}")
    // Método para obtener todas las categorías de un tipo específico (gastos o ingresos) del perfil actual
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {
        // Llamar al servicio para obtener las categorías del tipo especificado para el perfil actual
        List<CategoryDTO> list = categoryService.getCategoriesByTypeForCurrentUser(type);
        // Devolver la lista de categorías en la respuesta HTTP con un estado 200 OK
        return ResponseEntity.ok(list);
    }
    
    // 4.Endpoint para actualizar una categoría existente
    @PutMapping("/{categoryId}")
    // Método para actualizar una categoría existente
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO) {
        // Llamar al servicio para actualizar la categoría con el ID especificado y los datos proporcionados
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        // Devolver la categoría actualizada en la respuesta HTTP con un estado 200 OK
        return ResponseEntity.ok(updatedCategory);
    }
}
