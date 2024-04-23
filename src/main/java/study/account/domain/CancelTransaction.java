package study.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.account.dto.TransactionDto;
import study.account.type.TransactionResultType;

import java.time.LocalDateTime;

public class CancelTransaction {

    @Getter
    @AllArgsConstructor
    public static class Request {
        private String transactionId;
        private String accountNumber;
        private Long amount;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String accountNumber;
        private TransactionResultType resultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response fromDto(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .resultType(transactionDto.getResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }

    }
}
