package com.niloq.misfinanzas.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.niloq.misfinanzas.dto.ExpenseDTO;
import com.niloq.misfinanzas.dto.IncomeDTO;
import com.niloq.misfinanzas.dto.RecentTransactionDTO;
import com.niloq.misfinanzas.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

/**
 * Consolida toda la información necesaria para el panel principal (Dashboard).
 * Combina saldos, totales y una lista unificada de transacciones recientes.
 * 
 * @return Mapa con los datos financieros estructurados para el frontend.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        // 1. Obtiene las listas de ingresos y gastos recientes
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrenUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrenUser();
        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                latestIncomes.stream()
                        .map(income -> RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("ingresos")
                                .build()),
                latestExpenses.stream()
                        .map(expense -> RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("gastos")
                                .build()))
                // 3. Ordena las transacciones de más recientes a más antiguas
                .sorted((a, b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());

                    }
                    return cmp;

                }).collect(Collectors.toList());
        // 4. Calcula indicadores financieros clave
        returnValue.put("totalBalance",
                incomeService.getTotalIncomeForCurrentUser()
                        .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expense", latestExpenses);
        returnValue.put("recent5Income", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);
        return returnValue;

    }

}
