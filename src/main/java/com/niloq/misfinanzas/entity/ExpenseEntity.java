package com.niloq.misfinanzas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Genera automáticamente los getters, setters, toString, equals y hashCode
@Data
//Crea constructores que aceptan todos los parámetros y uno vacío (necesario para JPA).
@AllArgsConstructor
@NoArgsConstructor
//Permite crear objetos de forma elegante (ej: ExpenseEntity.builder().name("Comida").build()).
@Builder
//Le dice a Spring que esta clase representa una tabla en la base de datos.
@Entity
//Especifica que la tabla se llamará exactamente tbl_expenses en lugar de usar el nombre de la clase.
@Table(name = "tbl_expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String icon;
    private LocalDate date;
    private BigDecimal amount;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @ManyToOne
    @JoinColumn(name = "category_id",nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id",nullable = false)
    private ProfileEntity profile;

    @PrePersist
    public void prePersist(){
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
    
}
