package br.com.ccgl.sunharvestbackend.service;

import br.com.ccgl.sunharvestbackend.entity.User;
import br.com.ccgl.sunharvestbackend.exception.DuplicateEmailException;
import br.com.ccgl.sunharvestbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    public User register(String displayName, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email já cadastrado: " + email);
        }
        User user = User.builder()
                .displayName(displayName)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }
}
