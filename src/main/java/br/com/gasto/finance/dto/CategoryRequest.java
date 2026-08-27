package br.com.gasto.finance.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(

        @NotBlank
        String nome,

        String cor,

        String icone
) {
}
