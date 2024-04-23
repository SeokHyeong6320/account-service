package study.account.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.account.domain.Transaction;
import study.account.domain.User;
import study.account.repository.TransactionRepository;
import study.account.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class InitValue {

    private final UserRepository userRepository;

    @PostConstruct
    @Transactional
    public void initUsers() {
        User userA = User.builder().name("userA").build();
        User userB = User.builder().name("userB").build();
        User userC = User.builder().name("userC").build();

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
    }
}
