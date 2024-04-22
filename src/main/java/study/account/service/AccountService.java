package study.account.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.account.domain.Account;
import study.account.domain.User;
import study.account.dto.AccountDto;
import study.account.exception.AccountException;
import study.account.repository.AccountRepository;
import study.account.repository.UserRepository;
import study.account.type.AccountStatus;
import study.account.type.ErrorCode;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final EntityManager em;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountDto createAccount(Long userId, Long initialBalance) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.NO_USER));

        validationAccountCount(findUser);


        Account account = Account.builder()
                .user(findUser)
                .accountNumber(getNewAccountNumber())
                .accountStatus(AccountStatus.IN_USE)
                .balance(initialBalance)
                .registeredAt(LocalDateTime.now())
                .build();
        account.setUser(findUser);

        accountRepository.save(account);
        return AccountDto.fromEntity(account);
    }

    private String getNewAccountNumber() {
        return accountRepository.findFirstByOrderByIdDesc()
                .map(account ->
                        (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");
    }

    private void validationAccountCount(User findUser) {
        if (findUser.getAccounts().size() >= 10) {
            throw new AccountException(ErrorCode.EXCEED_ACCOUNT_COUNT);
        }
    }
}
