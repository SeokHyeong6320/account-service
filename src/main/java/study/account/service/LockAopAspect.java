package study.account.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import study.account.aop.AccountLockId;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(study.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp, AccountLockId request) throws Throwable {

        lockService.lock(request.getAccountNumber());

        try {
            return pjp.proceed();

        } finally {
            lockService.unlock(request.getAccountNumber());
        }

    }

}
