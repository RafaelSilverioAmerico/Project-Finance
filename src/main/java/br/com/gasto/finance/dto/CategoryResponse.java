package br.com.gasto.finance.dto;

import br.com.gasto.finance.model.Category;

public record CategoryResponse(Long id, String nome, String cor, String icone) {
    public static CategoryResponse fromEntity(Category c) {
        return new CategoryResponse(c.getId(), c.getNome(), c.getCor(), c.getIcone());
    }
}
