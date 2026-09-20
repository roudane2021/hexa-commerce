package com.roudane.commerce.order.config.aspect;




import com.roudane.commerce.common.domain.annotation.LogBusinessAction;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BusinessActionLoggingAspect {

    @Around("@annotation(logBusinessAction)")
    public Object logBusinessAction(ProceedingJoinPoint joinPoint, LogBusinessAction logBusinessAction) throws Throwable {
        String label = logBusinessAction.value();
        String argsDisplay = logBusinessAction.maskArgs() ? "[masqué]" : java.util.Arrays.toString(joinPoint.getArgs());

        log.info("[{}] démarré - args={}", label, argsDisplay);
        try {
            Object result = joinPoint.proceed();
            log.info("[{}] terminé avec succès", label);
            return result;
        } catch (Exception ex) {
            log.warn("[{}] échoué - {}", label, ex.getMessage());
            throw ex;
        }
    }
}
