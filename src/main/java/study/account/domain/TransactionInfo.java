package study.account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TransactionInfo {

    @NotBlank
    @Size(min = 10, max = 10)
    private String accountNumber;

    @NotBlank
    private TransactionType transactionType;

    @NotBlank
    private TransactionResultType resultType;

    @NotBlank
    private String transactionId;

    @NotBlank
    private Long amount;

    @NotBlank
    private LocalDateTime transactedAt;


}
