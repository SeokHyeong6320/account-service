package study.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.account.domain.Account;
import study.account.domain.Transaction;
import study.account.domain.User;
import study.account.exception.AccountException;
import study.account.repository.AccountRepository;
import study.account.repository.TransactionRepository;
import study.account.repository.UserRepository;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static study.account.type.AccountStatus.*;
import static study.account.type.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public void createNewTransaction
            (Long userId, String accountNumber, Long amount) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(NO_USER));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(NO_ACCOUNT));

        validateCreateTransaction(findUser, findAccount, amount);

        findAccount.minusBalance(amount);

        Transaction transaction = Transaction.builder()
                .transactionId(getUUID())
                .account(findAccount)
                .transactionType(TransactionType.USE)
                .amount(amount)
                .balanceSnapshot(findAccount.getBalance())
                .transactionResultType(TransactionResultType.S)
                .transactedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    private void validateCreateTransaction(User findUser, Account findAccount, Long amount) {
        if (!Objects.equals(findUser.getId(), findAccount.getUser().getId())) {
            throw new AccountException(NOT_MATCH_USER_AND_ACCOUNT);
        }
        if (findAccount.getAccountStatus() == CLOSED) {
            throw new AccountException(ACCOUNT_ALREADY_CLOSED);
        }
        if (amount <= 0 || amount > 1000000000) {
            throw new AccountException(TRANSACTION_AMOUNT_GET_OUT_RANGE);
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}






