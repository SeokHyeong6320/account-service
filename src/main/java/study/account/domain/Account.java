package study.account.domain;

import jakarta.persistence.*;
import lombok.*;
import study.account.type.AccountStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Account extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    public void createAccount(User user) {
        this.user = user;
        user.getAccounts().add(this);
    }

    public void plusBalance(long amount) {
        this.balance += amount;
    }

    public void minusBalance(long amount) {
        this.balance -= amount;
    }

}
