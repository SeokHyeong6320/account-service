package study.account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.UUID;
import study.account.dto.TransactionDto;
import study.account.type.TransactionResultType;

import java.time.LocalDateTime;

public class UseBalance {

    @Getter
    public static class Request {

        @NotBlank
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotBlank
        private Long amount;

    }

    public static class Response {

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotBlank
        private TransactionResultType resultType;
        @NotBlank
        private String transactionId;
        @NotBlank
        private Long amount;
        @NotBlank
        private LocalDateTime transactedAt;

        public static Response fromDto(TransactionDto transactionDto) {

            return null;
        }

    }
}
