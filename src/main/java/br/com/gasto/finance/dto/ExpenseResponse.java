package br.com.gasto.finance.dto;

import br.com.gasto.finance.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id,
                              String descricao,
                              BigDecimal valor,
                              LocalDate data,
                              Long categoriaId,
                              String categoriaNome,
                              String categoriaCor,
                              String categoriaIcone) {

    public static ExpenseResponse fromEntity(Expense e) {
        return new ExpenseResponse(
                e.getId(),
                e.getDescricao(),
                e.getValor(),
                e.getData(),
                e.getCategory().getId(),
                e.getCategory().getNome(),
                e.getCategory().getCor(),
                e.getCategory().getIcone()
        );
    }

}
