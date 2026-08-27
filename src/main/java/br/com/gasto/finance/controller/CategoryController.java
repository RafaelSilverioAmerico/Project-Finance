package br.com.gasto.finance.controller;

import br.com.gasto.finance.dto.CategoryRequest;
import br.com.gasto.finance.dto.CategoryResponse;
import br.com.gasto.finance.security.AuthenticatedUser;
import br.com.gasto.finance.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listar(@AuthenticationPrincipal AuthenticatedUser user) {
        return  ResponseEntity.ok(categoryService.listar(user.id()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> criar(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.criar(user.id(), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> escluir(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
       categoryService.excluir(user.id(), id);
       return ResponseEntity.noContent().build();
    }
}
