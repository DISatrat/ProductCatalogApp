package aspects;

import aspects.annotaion.Audited;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import service.audit.AuditService;
import util.ApplicationContext;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class AuditAspect {

    private static final Logger logger = Logger.getLogger(AuditAspect.class.getName());
    private static final String RESULT_SUCCESS = " - SUCCESS";
    private static final String RESULT_FAILED = " - FAILED: ";

    private final AuditService auditService;

    public AuditAspect() {
        this.auditService = ApplicationContext.getAuditService();
    }


    @Around("@annotation(audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        logger.info("=== AUDIT ASPECT TRIGGERED ===");
        logger.info("Method: " + joinPoint.getSignature().getName());
        logger.info("Annotation: " + audited.action());

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
            logger.info("Method " + joinPoint.getSignature().getName() +
                    " executed in " + executionTime + "ms");
        }
    }

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

    private String buildDetails(ProceedingJoinPoint joinPoint, String customDetails) {
        if (!customDetails.isEmpty()) {
            return customDetails;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        return "Method: " + methodName + ", Args: " + Arrays.toString(args);
    }
}