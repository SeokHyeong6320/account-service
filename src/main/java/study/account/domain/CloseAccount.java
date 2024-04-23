package study.account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import study.account.dto.AccountDto;

import java.time.LocalDateTime;

public class CloseAccount {

    @Getter
    @AllArgsConstructor
    public static class Request {

        @NotBlank
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;
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

        @DateTimeFormat
        private LocalDateTime unregisteredAt;

        public static Response fromDto(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unregisteredAt(accountDto.getUnregisteredAt())
                    .build();
        }

    }
}
