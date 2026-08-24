package br.com.gasto.finance.dto;

public record AuthResponse(

        String token,
        Long userId,
        String nome,
        String email
) {}
