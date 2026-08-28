package br.com.gasto.finance.repository;

import br.com.gasto.finance.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByUserIdAndId(Long userId, Long id);
    List<Expense> findByUserIdAndDataBetween(Long userId, LocalDate inicio, LocalDate fim);


    // SUM(e.valor) calcula a soma dos valores.
    // COALESCE garante que, caso a soma seja NULL (nenhum registro encontrado),
    // seja retornado 0 em vez de NULL, evitando problemas no front-end.

    @Query("select coalesce(sum(e.valor), 0) from Expense e where e.user.id = :userId and e.data between :inicio and :fim")
    BigDecimal somaPorPeriodo(@Param("userId") Long userId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    interface CategoriaTotalProjection {
        Long getCategoriaId();
        String getNome();
        String getCor();
        String getIcone();
        BigDecimal getTotal();
    }

    @Query("""
    select e.category.id as categoriaId, e.category.nome as nome, e.category.cor as cor, e.category.icone as icone,
           coalesce(sum(e.valor), 0) as total
    from Expense e
    where e.user.id = :userId and e.data between :inicio and :fim
    group by e.category.id, e.category.nome, e.category.cor, e.category.icone
    order by total desc
    """)
    List<CategoriaTotalProjection> somaPorCategoria(@Param("userId") Long userId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    interface DiaTotalProjection {
        LocalDate getData();
        BigDecimal getTotal();
    }

    @Query("""
    select e.data as data, coalesce(sum(e.valor), 0) as total
    from Expense e
    where e.user.id = :userId and e.data between :inicio and :fim
    group by e.data
    order by e.data asc
    """)
    List<DiaTotalProjection> somaPorDia(@Param("userId") Long userId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
