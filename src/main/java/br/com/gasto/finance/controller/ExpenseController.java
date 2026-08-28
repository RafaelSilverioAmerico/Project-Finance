package br.com.gasto.finance.controller;

import br.com.gasto.finance.dto.ExpenseRequest;
import br.com.gasto.finance.dto.ExpenseResponse;
import br.com.gasto.finance.security.AuthenticatedUser;
import br.com.gasto.finance.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/gastos")
public class ExpenseController {

   private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> listar(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return ResponseEntity.ok(expenseService.listarPorPeriodo(user.id(), inicio, fim));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> criar(@AuthenticationPrincipal AuthenticatedUser user,
                                                 @Valid @RequestBody ExpenseRequest req) {
        return ResponseEntity.ok(expenseService.criar(user.id(),req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@AuthenticationPrincipal AuthenticatedUser user,
                                        @PathVariable Long id) {
        expenseService.excluir(user.id(), id);
        return ResponseEntity.noContent().build();
    }
}
