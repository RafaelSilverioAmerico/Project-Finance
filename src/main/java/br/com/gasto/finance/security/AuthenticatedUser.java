package br.com.gasto.finance.security;

import org.springframework.web.filter.OncePerRequestFilter;

public record AuthenticatedUser(
        Long id,
        String email
) {}
