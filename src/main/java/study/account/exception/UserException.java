package study.account.exception;

import lombok.Getter;
import study.account.type.ErrorCode;

@Getter
public class UserException extends ServiceException{

    private ErrorCode errorCode;
    private String errorMessage;

    public UserException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
