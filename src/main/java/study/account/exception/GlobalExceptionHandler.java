package study.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import study.account.dto.ErrorResponse;

import static study.account.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static study.account.type.ErrorCode.INVALIDATE_REQUEST;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ErrorResponse serviceExceptionHandler(ServiceException e) {
        log.error("{} is occurred.", e.getErrorCode());
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handlerDataIntegrityViolationException
            (DataIntegrityViolationException e) {

        log.error("DataIntegrityViolationException is occurred", e);
        return new ErrorResponse(
                INVALIDATE_REQUEST, INVALIDATE_REQUEST.getDescription()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handlerMethodArgumentNotValidException
            (MethodArgumentNotValidException e) {

        log.error("MethodArgumentNotValidException is occurred", e);
        return new ErrorResponse(
                INVALIDATE_REQUEST, INVALIDATE_REQUEST.getDescription()
        );
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerException(Exception e) {
        log.error("Exception is occurred.", e);
        return new ErrorResponse(
                INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getDescription()
        );
    }


}
