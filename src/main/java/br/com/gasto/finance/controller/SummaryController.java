package br.com.gasto.finance.controller;

import br.com.gasto.finance.dto.SummaryResponse;
import br.com.gasto.finance.security.AuthenticatedUser;
import br.com.gasto.finance.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resumo")
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/diario")
    public ResponseEntity<SummaryResponse> diario(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) LocalDate dia) {
        LocalDate alvo = dia != null ? dia : LocalDate.now();
        return ResponseEntity.ok(summaryService.diario(user.id(), alvo));
    }

    @GetMapping("/semanal")
    public ResponseEntity<SummaryResponse> semanal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) LocalDate semanal) {
        LocalDate alvo = semanal != null ? semanal : LocalDate.now();
        return ResponseEntity.ok(summaryService.semanal(user.id(), alvo));
    }

    @GetMapping("/mensal")
    public ResponseEntity<SummaryResponse> mensal(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        Integer anoAlvo = ano != null ? ano : LocalDate.now().getYear();
        Integer mesAlvo = mes != null ? mes : LocalDate.now().getMonthValue();
        return ResponseEntity.ok(summaryService.mensal(user.id(), anoAlvo, mesAlvo));
    }
}
