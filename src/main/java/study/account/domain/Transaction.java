package study.account.domain;

import jakarta.persistence.*;
import lombok.*;
import study.account.type.TransactionResultType;
import study.account.type.TransactionType;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id @GeneratedValue
    @Column(name = "transaction_indexid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Enumerated
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "transaction_amount")
    private Long amount;
    @Column(name = "balanceSnapshot")
    private Long balanceSnapshot;

    @Enumerated
    @Column(name = "transactionResult_type")
    private TransactionResultType transactionResultType;

    public void cancel() {
        this.account.minusBalance(this.amount);
        this.transactionType = TransactionType.CANCEL;
    }
}
