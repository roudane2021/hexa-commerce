package com.roudane.commerce.order.config.aspect;




import com.roudane.commerce.common.domain.annotation.LogTechnicalCall;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TechnicalCallLoggingAspect {

    @Around("@annotation(logTechnicalCall)")
    public Object logTechnicalCall(ProceedingJoinPoint joinPoint, LogTechnicalCall logTechnicalCall) throws Throwable {
        String label = logTechnicalCall.value().isBlank()
                ? joinPoint.getSignature().toShortString()
                : logTechnicalCall.value();

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("{} exécuté en {}ms", label, System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            log.debug("{} échoué après {}ms : {}", label, System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }
}