package study.account.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import study.account.type.ErrorCode;

@Slf4j
@Getter
public class AccountException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
