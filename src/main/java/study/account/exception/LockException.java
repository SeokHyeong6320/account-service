package study.account.exception;

import lombok.Getter;
import study.account.type.ErrorCode;

@Getter
public class LockException extends ServiceException{

    private ErrorCode errorCode;
    private String errorMessage;

    public LockException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
