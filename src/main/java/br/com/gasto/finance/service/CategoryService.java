package br.com.gasto.finance.service;

import br.com.gasto.finance.dto.CategoryRequest;
import br.com.gasto.finance.dto.CategoryResponse;
import br.com.gasto.finance.model.Category;
import br.com.gasto.finance.model.User;
import br.com.gasto.finance.repository.CategoryRepository;
import br.com.gasto.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> listar (Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public CategoryResponse criar (Long userId, CategoryRequest req) {
        User user = userRepository.getReferenceById(userId);

        Category category = new Category();
        category.setNome(req.nome());
        category.setCor(req.cor() != null && !req.cor().isBlank() ? req.cor() : "#4E9F3D");
        category.setIcone(req.icone() != null && !req.icone().isBlank() ? req.icone() : "\uD83D\uDCB8");
        category.setUser(user);

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    public void excluir(Long userId, Long categoryId) {
        Category category = categoryRepository.findByUserIdAndId(userId, categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        categoryRepository.delete(category);
    }
}
