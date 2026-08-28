package br.com.gasto.finance.service;

import br.com.gasto.finance.dto.ExpenseRequest;
import br.com.gasto.finance.dto.ExpenseResponse;
import br.com.gasto.finance.model.Category;
import br.com.gasto.finance.model.Expense;
import br.com.gasto.finance.model.User;
import br.com.gasto.finance.repository.CategoryRepository;
import br.com.gasto.finance.repository.ExpenseRepository;
import br.com.gasto.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ExpenseResponse criar(Long userId, ExpenseRequest req) {
        User user = userRepository.getReferenceById(userId);

        Category category = categoryRepository.findByUserIdAndId(userId, req.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        Expense expense = new Expense();
        expense.setDescricao(req.descricao());
        expense.setValor(req.valor());
        expense.setData(req.data());
        expense.setCategory(category);
        expense.setUser(user);

        expense = expenseRepository.save(expense);
        ExpenseResponse response = ExpenseResponse.fromEntity(expense);

        notificarAlteracao(userId, "criado", response);
        return response;
    }

    public List<ExpenseResponse> listarPorPeriodo(Long userId, LocalDate inicio, LocalDate fim) {

        return expenseRepository.findByUserIdAndDataBetween(userId, inicio, fim).stream()
                .map(ExpenseResponse::fromEntity)
                .toList();
    }

    public void excluir(Long userId, Long expenseId) {

        Expense expense = expenseRepository.findByUserIdAndId(userId, expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Valor não encontrado"));

        expenseRepository.delete(expense);
        notificarAlteracao(userId, "excluindo", ExpenseResponse.fromEntity(expense));
    }

    private void notificarAlteracao(Long userId, String tipoEvento, ExpenseResponse response) {
        Map<String, Object> payload = Map.of("evento", tipoEvento, "gasto", response);
        messagingTemplate.convertAndSend("/topic/gastos/" + userId, (Object) payload);
    }

}

