package study.account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.account.dto.AccountDto;

import java.time.LocalDateTime;


public class CreateAccount {

    @Getter
    @AllArgsConstructor
    public static class Request {

        @NotBlank
        private Long userId;

        @NotBlank
        private Long initialBalance;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {

        @NotBlank
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
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
