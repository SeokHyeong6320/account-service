package study.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.account.aop.AccountLock;
import study.account.domain.Account;
import study.account.domain.Transaction;
import study.account.domain.User;
import study.account.dto.TransactionDto;
import study.account.exception.AccountException;
import study.account.exception.TransactionException;
import study.account.exception.UserException;
import study.account.repository.AccountRepository;
import study.account.repository.TransactionRepository;
import study.account.repository.UserRepository;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static study.account.type.AccountStatus.CLOSED;
import static study.account.type.ErrorCode.*;
import static study.account.type.TransactionResultType.F;
import static study.account.type.TransactionResultType.S;
import static study.account.type.TransactionType.CANCEL;
import static study.account.type.TransactionType.USE;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @AccountLock
    public TransactionDto createNewTransaction
            (Long userId, String accountNumber, Long amount) {

        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateCreateTransaction(findUser, findAccount, amount);

        findAccount.minusBalance(amount);

        Transaction transaction = saveTransaction(amount, findAccount, USE, S);


        return TransactionDto.fromEntity(transaction);
    }

    public void saveFailNewTransaction(String accountNumber, Long amount) {
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        saveTransaction(amount, findAccount, USE, F);
    }

    @AccountLock
    public TransactionDto cancelTransaction
            (String transactionId, String accountNumber, Long amount) {

        Transaction findTransaction = transactionRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionException(TRANSACTION_NOT_FOUND));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));


        validateCancelTransaction(findTransaction, accountNumber, amount);

        findTransaction.cancel();

        return TransactionDto
                .fromEntity(saveTransaction(amount, findAccount, CANCEL, S));
    }

    @Transactional(readOnly = true)
    public TransactionDto findTransaction(String transactionId) {

        return TransactionDto.fromEntity(
                transactionRepository
                        .findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new TransactionException(TRANSACTION_NOT_FOUND))
        );

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

    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void validateCreateTransaction(User findUser, Account findAccount, Long amount) {
        if (!Objects.equals(findUser.getId(), findAccount.getUser().getId())) {
            throw new AccountException(ACCOUNT_AND_USER_NOT_MATCH);
        }

        if (findAccount.getAccountStatus() == CLOSED) {
            throw new AccountException(ACCOUNT_ALREADY_CLOSED);
        }

        if (amount <= 0 || amount > 1000000000) {
            throw new TransactionException(TRANSACTION_AMOUNT_GET_OUT_RANGE);
        }
    }

    public void saveFailCancelTransaction
            (String accountNumber, Long amount) {
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        saveTransaction(amount, findAccount, CANCEL, F);
    }

    private void validateCancelTransaction
            (Transaction findTransaction, String accountNumber, Long amount) {

        if (!Objects.equals(findTransaction.getAmount(), amount)) {
            throw new TransactionException(TRANSACTION_AMOUNT_NOT_MATCH);
        }

        if (!Objects.equals
                (findTransaction.getAccount().getAccountNumber(), accountNumber)) {
            throw new TransactionException(TRANSACTION_AND_ACCOUNT_NOT_MATCH) {
            };
        }

    }

}






