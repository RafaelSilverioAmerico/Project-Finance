package br.com.gasto.finance.service;

import br.com.gasto.finance.dto.AuthResponse;
import br.com.gasto.finance.dto.LoginRequest;
import br.com.gasto.finance.dto.RegisterRequest;
import br.com.gasto.finance.model.User;
import br.com.gasto.finance.repository.UserRepository;
import br.com.gasto.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Ja existe uma conta com esse email");
        }

        User user = new User();
        user.setNome(req.nome());
        user.setEmail(req.email());
        user.setSenha(passwordEncoder.encode(req.senha()));

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, user.getId(), user.getNome(), user.getEmail());
    }

    public AuthResponse login(LoginRequest req) {

        // Buscar o usuário ou lança exceção
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos"));

        // Confere a senha ou lança exceção
        if (!passwordEncoder.matches(req.senha(), user.getSenha())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        // Gera token e devolve AuthResponse
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getNome(), user.getEmail());

    }
}
