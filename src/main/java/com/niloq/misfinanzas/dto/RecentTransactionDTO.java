package com.niloq.misfinanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO optimizado para representar transacciones recientes (gastos o ingresos)
 * en las vistas de historial o paneles de resumen del usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentTransactionDTO {

    private Long id;
    private Long profileId;
    private String icon;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Define la naturaleza del movimiento (ej: "EXPENSE" o "INCOME")
    private String type;

}
