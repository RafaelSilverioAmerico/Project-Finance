package br.com.gasto.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SummaryResponse(
        String periodo,
        LocalDate inicio,
        LocalDate fim,
        BigDecimal total,
        List<CategoriaTotal> porCategoria,
        List<DiaTotal> porDia
) {
    public record CategoriaTotal(Long categoriaId, String nome, String cor, String icone,BigDecimal total) {}
    public record DiaTotal(LocalDate data, BigDecimal total) {}
}
