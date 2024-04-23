package study.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NO_USER("해당 id의 사용자가 없습니다"),
    NO_ACCOUNT("해당 계좌번호에 해당하는 계좌가 없습니다"),
    EXCEED_ACCOUNT_COUNT("계좌의 갯수는 10개를 초과할 수 없습니다"),
    NOT_MATCH_USER_AND_ACCOUNT("해당 유저의 계좌가 아닙니다"),
    ACCOUNT_ALREADY_CLOSED("해당 계좌는 이미 해지된 계좌입니다"),
    ACCOUNT_STILL_HAS_BALANCE("해당 계좌의 잔액이 존재합니다"),
    EXCEED_ACCOUNT_BALANCE("해당 계좌의 잔액이 부족합니다"),
    TRANSACTION_AMOUNT_GET_OUT_RANGE("거래금액이 범위를 벗어났습니다");

    private final String description;
}
