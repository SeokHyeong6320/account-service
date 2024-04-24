package study.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import study.account.aop.AccountLockId;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(study.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp, AccountLockId request) throws Throwable {

        lockService.lock(request.getAccountNumber());
        // lock 취득 시도
        try {
            return pjp.proceed();

        } finally {
            // lock 해제
            lockService.unlock(request.getAccountNumber());
        }

    }

}
