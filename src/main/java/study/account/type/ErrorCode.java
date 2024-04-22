package study.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NO_USER("해당 id의 사용자가 없습니다"),
    EXCEED_ACCOUNT_COUNT("계좌의 갯수는 10개를 초과할 수 없습니다");

    private final String description;
}
