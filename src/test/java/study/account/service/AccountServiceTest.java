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
import study.account.type.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_USER);
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
                .isEqualTo(ErrorCode.EXCEED_ACCOUNT_COUNT);
    }


}