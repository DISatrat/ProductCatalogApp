package com.example.productcatalog.aspect;

import com.example.productcatalog.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для аудита операций.
 * <p>
 * Автоматически записывает записи аудита для методов, отмеченных аннотацией @Audited.
 * </p>
 *
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final String RESULT_SUCCESS = " - SUCCESS";
    private static final String RESULT_FAILED = " - FAILED: ";

    private final AuditService auditService;

    /**
     * Проводит аудит методов, отмеченных аннотацией @Audited.
     *
     * @param joinPoint точка соединения
     * @param audited   аннотация Audited
     * @return результат метода
     * @throws Throwable если метод выбрасывает исключение
     */
    @Around("@annotation(audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        log.debug("Аспект аудита запущен для метода: {}", joinPoint.getSignature().getName());

        String username = extractUsernameFromParameters(joinPoint);
        String action = audited.action();
        String details = buildDetails(joinPoint, audited.details());

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            auditService.record(username, action, details + RESULT_SUCCESS);
            return result;
        } catch (Exception e) {
            auditService.record(username, action, details + RESULT_FAILED + e.getMessage());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Метод {} выполнен за {}ms", joinPoint.getSignature().getName(), executionTime);
        }
    }

    /**
     * Извлекает имя пользователя из параметров метода.
     *
     * @param joinPoint точка соединения
     * @return имя пользователя или "unknown"
     */
    private String extractUsernameFromParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            if ("username".equals(parameterNames[i]) && args[i] instanceof String) {
                return (String) args[i];
            }
        }

        for (Object arg : args) {
            if (arg instanceof String) {
                return (String) arg;
            }
        }

        return "unknown";
    }

    /**
     * Строит строку подробностей для записи аудита.
     *
     * @param joinPoint точка соединения
     * @param customDetails пользовательские подробности из аннотации
     * @return строка подробностей
     */
    private String buildDetails(ProceedingJoinPoint joinPoint, String customDetails) {
        if (!customDetails.isEmpty()) {
            return customDetails;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        return "Метод: " + methodName + ", Аргументы: " + Arrays.toString(args);
    }
}
