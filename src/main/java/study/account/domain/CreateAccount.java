package study.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.account.dto.AccountDto;

import java.time.LocalDateTime;


public class CreateAccount {

    @Getter
    @AllArgsConstructor
    public static class Request {

        private Long userId;
        private Long initialBalance;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response fromDto(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt())
                    .build();
        }

    }
}
