package study.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
}
