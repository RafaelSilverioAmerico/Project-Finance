package br.com.gasto.finance.repository;

import br.com.gasto.finance.model.Category;
import br.com.gasto.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);
    Optional<Category> findByUserIdAndId(Long userId, Long id);


}