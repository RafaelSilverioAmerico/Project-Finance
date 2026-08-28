package br.com.gasto.finance.service;

import br.com.gasto.finance.dto.SummaryResponse;
import br.com.gasto.finance.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final ExpenseRepository expenseRepository;

    public SummaryResponse diario(Long userId, LocalDate dia) {
        return montarResumo(userId, "diario", dia, dia);
    }

    public SummaryResponse semanal(Long userId, LocalDate qualquerDiadaSemana) {
        LocalDate inicio = qualquerDiadaSemana.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fim = qualquerDiadaSemana.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return montarResumo(userId, "semanal", inicio, fim);
    }

    public SummaryResponse mensal(Long userId, int ano, int mes) {
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.with(TemporalAdjusters.lastDayOfMonth());
        return montarResumo(userId, "mensal", inicio, fim);
    }

    private SummaryResponse montarResumo(Long userId, String periodo, LocalDate inicio, LocalDate fim) {
        var total = expenseRepository.somaPorPeriodo(userId, inicio, fim);

        List<SummaryResponse.CategoriaTotal> porCategoria = expenseRepository
                .somaPorCategoria(userId, inicio, fim).stream()
                .map(p -> new SummaryResponse.CategoriaTotal(p.getCategoriaId(), p.getNome(), p.getCor(), p.getIcone(), p.getTotal()))
                .toList();

        List<SummaryResponse.DiaTotal> porDia = expenseRepository
                .somaPorDia(userId, inicio, fim).stream()
                .map(p -> new SummaryResponse.DiaTotal(p.getData(), p.getTotal()))
                .toList();

        return new SummaryResponse(periodo, inicio, fim, total, porCategoria, porDia);
    }
}
