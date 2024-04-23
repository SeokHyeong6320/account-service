package study.account.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AccountInfo {

    @NotBlank
    @Size(min = 10, max = 10)
    private String accountNumber;

    @NotBlank
    private Long balance;

}
