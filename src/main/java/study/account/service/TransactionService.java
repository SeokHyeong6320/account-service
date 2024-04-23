package study.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.account.domain.Account;
import study.account.domain.Transaction;
import study.account.domain.User;
import study.account.dto.TransactionDto;
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
import static study.account.type.TransactionResultType.*;
import static study.account.type.TransactionType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public TransactionDto createNewTransaction
            (Long userId, String accountNumber, Long amount) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(NO_USER));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(NO_ACCOUNT));

        validateCreateTransaction(findUser, findAccount, amount);

        findAccount.minusBalance(amount);

        Transaction transaction = saveTransaction(amount, findAccount, USE, S);


        return TransactionDto.fromEntity(transaction);
    }

    public void saveFailTransaction(String accountNumber, Long amount) {
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(NO_ACCOUNT));
        saveTransaction(amount, findAccount, USE, F);
    }

    private Transaction saveTransaction
            (Long amount, Account findAccount,
             TransactionType transactionType, TransactionResultType resultType) {
        Transaction transaction = Transaction.builder()
                .transactionId(getUUID())
                .account(findAccount)
                .transactionType(transactionType)
                .amount(amount)
                .balanceSnapshot(findAccount.getBalance())
                .transactionResultType(resultType)
                .transactedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
        return transaction;
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






