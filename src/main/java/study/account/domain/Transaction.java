package study.account.domain;

import jakarta.persistence.*;
import lombok.*;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id @GeneratedValue
    @Column(name = "transaction_indexid")
    private Long id;

    @Column(name = "transaction_id", updatable = false)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transactionResult_type")
    private TransactionResultType transactionResultType;

    @Column(name = "transaction_amount")
    private Long amount;
    @Column(name = "balanceSnapshot")
    private Long balanceSnapshot;

    private LocalDateTime transactedAt;

    public void cancel() {
        this.account.plusBalance(this.amount);
    }
}
