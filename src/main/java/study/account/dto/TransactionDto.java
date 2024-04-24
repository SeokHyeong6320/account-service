package study.account.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.account.domain.Transaction;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TransactionDto {

    private String transactionId;
    private String accountNumber;

    private Long amount;

    private TransactionType transactionType;
    private TransactionResultType resultType;

    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .resultType(transaction.getTransactionResultType())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
