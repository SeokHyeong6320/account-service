package study.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.account.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
}
