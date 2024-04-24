package study.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("해당 id의 사용자가 없습니다"),


    ACCOUNT_AND_USER_NOT_MATCH("해당 유저의 계좌가 아닙니다"),
    ACCOUNT_NOT_FOUND("해당 계좌번호에 해당하는 계좌가 없습니다"),
    ACCOUNT_COUNT_EXCEED("계좌의 갯수는 10개를 초과할 수 없습니다"),
    ACCOUNT_ALREADY_CLOSED("해당 계좌는 이미 해지된 계좌입니다"),
    ACCOUNT_BALANCE_STILL_EXIST("해당 계좌의 잔액이 존재합니다"),


    TRANSACTION_NOT_FOUND("해당 거래가 존재하지 않습니다"),
    TRANSACTION_AMOUNT_EXCEED_BALANCE("해당 계좌의 잔액이 부족합니다"),
    TRANSACTION_AMOUNT_GET_OUT_RANGE("거래금액이 범위를 벗어났습니다"),
    TRANSACTION_AMOUNT_NOT_MATCH("거래금액과 취소금액이 일치하지 않습니다"),
    TRANSACTION_AND_ACCOUNT_NOT_MATCH("해당 계좌의 거래가 아닙니다"),

    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 현재 사용중입니다");

    private final String description;
}
