package study.account.exception;

import lombok.Getter;
import study.account.type.ErrorCode;

@Getter
public class AccountException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
