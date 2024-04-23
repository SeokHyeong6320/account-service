package study.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import study.account.domain.Account;
import study.account.domain.User;
import study.account.dto.AccountDto;
import study.account.exception.AccountException;
import study.account.repository.AccountRepository;
import study.account.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static study.account.type.AccountStatus.CLOSED;
import static study.account.type.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("계좌 생성 성공 - 기존 계좌 존재")
    void successCreateAccount_existAccount() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .accounts(Mockito.mock(List.class))
                .build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .balance(1000L)
                        .user(user)
                        .accountNumber("1000000012")
                        .build());

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));


        // when
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService
                .createAccount(1L, 1000L);

        // then
        verify(accountRepository, times(1))
                .save(captor.capture());

        assertThat(captor.getValue().getAccountNumber())
                .isEqualTo("1000000013");
        assertThat(accountDto.getUserId()).isEqualTo(1L);
        assertThat(accountDto.getAccountNumber()).isEqualTo("1000000013");
    }

    @Test
    @DisplayName("계좌 생성 성공 - 기존 계좌 없음")
    void successCreateAccount_noAccount() throws Exception {
        // given

        User user = User.builder().id(1L).accounts(new ArrayList<>()).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .balance(1000L)
                        .user(user)
                        .accountNumber("1000000013")
                        .build());

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());


        // when
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService
                .createAccount(1L, 1000L);

        // then
        verify(accountRepository, times(1))
                .save(captor.capture());

        assertThat(captor.getValue().getAccountNumber())
                .isEqualTo("1000000000");
        assertThat(accountDto.getUserId()).isEqualTo(1L);
        assertThat(accountDto.getAccountNumber()).isEqualTo("1000000000");
    }

    @Test
    @DisplayName("계좌 생성 실패 - 유저 없음")
    void failCreateAccount_noUser() throws Exception {
        // given

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 생성 실패 - 계좌 10개 초과")
    void failCreateAccount_exceedAccount() throws Exception {
        // given
        List accounts = Mockito.mock(List.class);
        given(accounts.size()).willReturn(10);

        User user = User.builder().id(1L).accounts(accounts).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ACCOUNT_COUNT_EXCEED);
    }

    @Test
    @DisplayName("계좌 해지 성공")
    void successCloseAccount() throws Exception {
        User user = User.builder()
                .id(1L)
                .accounts(Mockito.mock(List.class))
                .build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .id(2L)
                        .user(user)
                        .accountNumber("1000000012")
                        .balance(0L)
                        .registeredAt(now())
                        .build()));


        // when
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService
                .closeAccount(1L, "1000000013");

        // then
        verify(accountRepository, times(1))
                .save(captor.capture());

        assertThat(captor.getValue().getAccountNumber())
                .isEqualTo("1000000012");
        assertThat(captor.getValue().getAccountStatus())
                .isEqualTo(CLOSED);
        assertThat(accountDto.getUnregisteredAt()).isNotNull();
    }

    @Test
    @DisplayName("계좌 해지 실패 - 유저 없음")
    void failCloseAccount_noUser() throws Exception {
        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService
                        .closeAccount(1L, "1000000000"));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 헤당 계좌 없음")
    void failCreateAccount_noAccount() throws Exception {
        // given
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService
                        .closeAccount(1L, "1000000000"));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 해당 유저의 계좌가 아님")
    void failCreateAccount_noMatchUserAndAccount() throws Exception {
        // given
        User userA = User.builder().id(1L).build();
        User userB = User.builder().id(2L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(userA));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .id(2L)
                        .user(userB)
                        .accountNumber("1000000012")
                        .balance(0L)
                        .registeredAt(now())
                        .build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService
                        .closeAccount(1L, "1000000000"));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(USER_AND_ACCOUNT_NOT_MATCH);
    }

    @Test
    @DisplayName("계좌 해지 실패 - 잔액 남음")
    void failCreateAccount_stillHasBalance() throws Exception {
        // given
        User userA = User.builder().id(1L).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(userA));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .id(2L)
                        .user(userA)
                        .accountNumber("1000000012")
                        .balance(1000L)
                        .registeredAt(now())
                        .build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService
                        .closeAccount(1L, "1000000000"));

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ACCOUNT_BALANCE_STILL_EXIST);
    }

    @Test
    @DisplayName("계좌 조회 성공")
    void successGetAccountInfo() throws Exception {
        // given
        User userA = User.builder().id(1L).build();

        List<Account> list = Arrays.asList(
                Account.builder()
                        .user(userA)
                        .accountNumber("1111111111")
                        .balance(1111L)
                        .build(),
                Account.builder()
                        .user(userA)
                        .accountNumber("2222222222")
                        .balance(2222L)
                        .build(),
                Account.builder()
                        .user(userA)
                        .accountNumber("3333333333")
                        .balance(3333L)
                        .build()
        );

        userA = userA.builder().accounts(list).build();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(userA));


        // when
        List<AccountDto> accountDtos = accountService.getAccountList(1L);


        // then
        assertThat(accountDtos.size()).isEqualTo(3);
        assertThat(accountDtos.get(0).getAccountNumber())
                .isEqualTo("1111111111");
        assertThat(accountDtos.get(0).getBalance()).isEqualTo(1111L);
        assertThat(accountDtos.get(1).getAccountNumber())
                .isEqualTo("2222222222");
        assertThat(accountDtos.get(1).getBalance()).isEqualTo(2222L);
        assertThat(accountDtos.get(2).getAccountNumber())
                .isEqualTo("3333333333");
        assertThat(accountDtos.get(2).getBalance()).isEqualTo(3333L);
    }

    @Test
    @DisplayName("계좌 조회 실패 - 해당 유저 없음")
    void failGetAccountInfo_noUser() throws Exception {
        // given
        given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountList(1L));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(USER_NOT_FOUND);

    }


}