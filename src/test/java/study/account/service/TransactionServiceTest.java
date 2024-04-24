package study.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.account.domain.Account;
import study.account.domain.Transaction;
import study.account.domain.User;
import study.account.dto.TransactionDto;
import study.account.exception.AccountException;
import study.account.repository.AccountRepository;
import study.account.repository.TransactionRepository;
import study.account.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static study.account.type.AccountStatus.CLOSED;
import static study.account.type.AccountStatus.IN_USE;
import static study.account.type.ErrorCode.*;
import static study.account.type.TransactionResultType.S;
import static study.account.type.TransactionType.CANCEL;
import static study.account.type.TransactionType.USE;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("거래 생성 성공")
    void successCreateNewTransaction() throws Exception {
        // given
        User user = User.builder().id(1L).build();
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .id(2L)
                .user(user)
                .accountNumber("1000000011")
                .balance(10000L)
                .accountStatus(IN_USE)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        Transaction transaction = Transaction.builder()
                .id(3L)
                .transactionId("transactionId")
                .transactionType(USE)
                .transactionResultType(S)
                .amount(1000L)
                .account(account)
                .transactedAt(LocalDateTime.now())
                .balanceSnapshot(9000L)
                .build();
        given(transactionRepository.save(any()))
                .willReturn(transaction);

        // when
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        TransactionDto transactionDto =
                transactionService.createNewTransaction
                        (1L, "1000000001", 1000L);

        // then
        verify(transactionRepository, times(1))
                .save(captor.capture());

        assertThat(captor.getValue().getAmount()).isEqualTo(1000L);
        assertThat(captor.getValue().getBalanceSnapshot())
                .isEqualTo(9000L);
        assertThat(captor.getValue().getTransactionType()).isEqualTo(USE);
        assertThat(captor.getValue().getTransactionResultType()).isEqualTo(S);
        assertThat(captor.getValue().getAccount()).isEqualTo(account);

        assertThat(transactionDto.getAccountNumber())
                .isEqualTo("1000000011");
        assertThat(transactionDto.getTransactionType()).isEqualTo(USE);
        assertThat(transactionDto.getResultType()).isEqualTo(S);
        assertThat(transactionDto.getAmount()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("거래 생성 실패 - 해당 유저 없음")
    void failCreateNewTransaction_userNotFound() throws Exception {
        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1000L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 생성 실패 - 해당 계좌 없음")
    void failCreateNewTransaction_userNotAccount() throws Exception {
        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(User.builder().id(1L).build()));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1000L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 생성 실패 - 해당 유저의 계좌가 아님")
    void failCreateNewTransaction_userAndAccountNotMatch() throws Exception {
        // given
        User userA = User.builder().id(1L).build();
        User userB = User.builder().id(2L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(userA));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().user(userB).build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1000L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(USER_AND_ACCOUNT_NOT_MATCH);
    }

    @Test
    @DisplayName("거래 생성 실패 - 이미 해지된 계좌")
    void failCreateNewTransaction_accountAlreadyClosed() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .user(user)
                        .accountStatus(CLOSED)
                        .build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1000L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ACCOUNT_ALREADY_CLOSED);
    }

    @Test
    @DisplayName("거래 생성 실패 - 계좌 잔액 부족")
    void failCreateNewTransaction_amountExceedAccountBalance() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .user(user)
                .balance(1000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));



        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1001L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(TRANSACTION_AMOUNT_EXCEED_BALANCE);
    }

    @Test
    @DisplayName("거래 생성 실패 - 거래 금액 범위 벗어남(마이너스)")
    void failCreateNewTransaction_amountGetOutRange1() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .user(user)
                .balance(1000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));



        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", -1L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(TRANSACTION_AMOUNT_GET_OUT_RANGE);
    }

    @Test
    @DisplayName("거래 생성 실패 - 거래 금액 범위 벗어남(너무 큼)")
    void failCreateNewTransaction_amountGetOutRange2() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .user(user)
                .balance(1000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));



        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.createNewTransaction
                        (1L, "1000000000", 1000000001L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(TRANSACTION_AMOUNT_GET_OUT_RANGE);
    }

    @Test
    @DisplayName("거래 취소 성공")
    void successCancelTransaction() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        Account account = Account.builder()
                .id(2L)
                .user(user)
                .accountNumber("1000000011")
                .balance(9500L)
                .accountStatus(IN_USE)
                .build();

        Transaction transaction = Transaction.builder()
                .id(3L)
                .transactionId("transactionId")
                .transactionResultType(S)
                .amount(1000L)
                .account(account)
                .transactedAt(LocalDateTime.now())
                .balanceSnapshot(9500L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        TransactionDto transactionDto =
                transactionService.cancelTransaction
                        ("transactionId",
                                "1000000011", 1000L);

        // then
        verify(transactionRepository, times(1))
                .save(captor.capture());

        assertThat(captor.getValue().getAmount()).isEqualTo(1000L);
        assertThat(captor.getValue().getBalanceSnapshot())
                .isEqualTo(10500L);
        assertThat(captor.getValue().getTransactionType()).isEqualTo(CANCEL);
        assertThat(captor.getValue().getTransactionResultType()).isEqualTo(S);
        assertThat(captor.getValue().getAccount()).isEqualTo(account);

        assertThat(transactionDto.getAccountNumber())
                .isEqualTo("1000000011");
        assertThat(transactionDto.getTransactionType()).isEqualTo(CANCEL);
        assertThat(transactionDto.getResultType()).isEqualTo(S);
        assertThat(transactionDto.getAmount()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("거래 취소 실패 - 해당 거래 없음")
    void failCancelTransaction_userNotAccount() throws Exception {
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelTransaction("transactionId",
                                "100000000", 1000L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 취소 실패 - 해당 계좌 없음")
    void failCancelTransaction_accountNotAccount() throws Exception {
        // given
        Transaction transaction = Transaction.builder().id(2L).build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelTransaction("transactionId",
                                "100000000", 1000L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 취소 실패 - 거래 금액 불일치")
    void failCancelTransaction_amountNotMatch() throws Exception {
        // given
        Account account = Account.builder()
                .id(2L)
                .accountNumber("1000000011")
                .balance(9500L)
                .accountStatus(IN_USE)
                .build();

        Transaction transaction = Transaction.builder()
                .id(3L)
                .transactionId("transactionId")
                .amount(1000L)
                .account(account)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelTransaction("transactionId",
                                "100000000", 1500L));


        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(TRANSACTION_AMOUNT_NOT_MATCH);
    }

    @Test
    @DisplayName("거래 취소 실패 - 계좌 불일치")
    void failCancelTransaction_transactionAndAccountNotMatch() throws Exception {
        // given
        Account account = Account.builder()
                .id(2L)
                .accountNumber("1000000011")
                .build();

        Transaction transaction = Transaction.builder()
                .id(3L)
                .amount(1000L)
                .account(account)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService
                        .cancelTransaction("transactionId",
                                "100000000", 1000L));


        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(TRANSACTION_AND_ACCOUNT_NOT_MATCH);
    }










}