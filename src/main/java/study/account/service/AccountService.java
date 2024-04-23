package study.account.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Literal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.account.domain.Account;
import study.account.domain.User;
import study.account.dto.AccountDto;
import study.account.exception.AccountException;
import study.account.repository.AccountRepository;
import study.account.repository.UserRepository;
import study.account.type.AccountStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static study.account.type.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountDto createAccount(Long userId, Long initialBalance) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(NO_USER));

        validateAccountCount(findUser);

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

    private void validateAccountCount(User findUser) {
        if (findUser.getAccounts().size() >= 10) {
            throw new AccountException(EXCEED_ACCOUNT_COUNT);
        }
    }

    public AccountDto closeAccount(Long userId, String accountNumber) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(NO_USER));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(NO_ACCOUNT));

        validateCloseAccount(userId, findAccount);

        findAccount.closeAccount();

        accountRepository.save(findAccount);

        return AccountDto.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .unregisteredAt(findAccount.getUnRegisteredAt())
                .build();
    }

    private void validateCloseAccount(Long userId, Account account) {
        isMatchUserAndAccount(userId, account);

        isAccountClose(account);

        isAccountHasBalance(account);
    }

    private void isAccountHasBalance(Account account) {
        if (account.getBalance() != 0) {
            throw new AccountException(ACCOUNT_STILL_HAS_BALANCE);
        }
    }

    private void isAccountClose(Account account) {
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountException(ACCOUNT_ALREADY_CLOSE);
        }
    }

    private void isMatchUserAndAccount(Long userId, Account account) {
        if (!Objects.equals(userId, account.getUser().getId())) {
            throw new AccountException(NOT_MATCH_USER_AND_ACCOUNT);
        }
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAccountList(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(NO_USER));

        for (Account account : findUser.getAccounts()) {
            System.out.println(account.getAccountNumber() + " " + account.getBalance());
        }

        return findUser.getAccounts().stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }


}








