package study.account.exception;

import study.account.type.ErrorCode;

public class AccountException extends ServiceException{

    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

}
