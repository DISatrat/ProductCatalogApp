package com.example.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Аспект для логирования производительности выполнения методов.
 * <p>
 * Логирует время выполнения для всех методов сервиса и контроллера.
 * Выдает предупреждения о медленных методах, превышающих пороговое значение.
 * </p>
 */
@Slf4j
@Aspect
public class PerformanceLoggingAspect {

    private static final long SLOW_METHOD_THRESHOLD_MS = 1000;

    /**
     * Логирует время выполнения для методов сервиса и контроллера.
     *
     * @param joinPoint точка соединения
     * @return результат метода
     * @throws Throwable если метод выбрасывает исключение
     */
    @Around("execution(* com.example.productcatalog.service.*.*(..)) || " +
            "execution(* com.example.productcatalog.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > SLOW_METHOD_THRESHOLD_MS) {
                log.warn("МЕДЛЕННЫЙ МЕТОД: {} выполнен за {}ms", methodName, executionTime);
            } else {
                log.debug("Метод {} выполнен за {}ms", methodName, executionTime);
            }
        }
    }
}
