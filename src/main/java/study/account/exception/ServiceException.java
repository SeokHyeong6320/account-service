package study.account.exception;

import lombok.Getter;
import study.account.type.ErrorCode;

@Getter
public class ServiceException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    public ServiceException(study.account.type.ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
