package br.com.gasto.finance.service;

import br.com.gasto.finance.dto.AuthResponse;
import br.com.gasto.finance.dto.RegisterRequest;
import br.com.gasto.finance.model.User;
import br.com.gasto.finance.repository.UserRepository;
import br.com.gasto.finance.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
