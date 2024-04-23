package study.account.domain;

import jakarta.persistence.*;
import lombok.*;
import study.account.exception.AccountException;
import study.account.type.AccountStatus;

import java.time.LocalDateTime;

import static study.account.type.ErrorCode.*;

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


    public void setUser(User user) {
        this.user = user;
        user.getAccounts().add(this);
    }

    public void closeAccount() {
        this.accountStatus = AccountStatus.CLOSED;
        this.unRegisteredAt = LocalDateTime.now();
    }

    public void plusBalance(Long amount) {
        this.balance += amount;
    }

    public void minusBalance(Long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new AccountException(EXCEED_ACCOUNT_BALANCE);
        }
    }

}
