package study.account.exception;

import lombok.AllArgsConstructor;
import study.account.type.ErrorCode;

public class AccountException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
