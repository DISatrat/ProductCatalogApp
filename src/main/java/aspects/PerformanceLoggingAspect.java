package aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.logging.Logger;

@Aspect
public class PerformanceLoggingAspect {

    private static final Logger logger = Logger.getLogger(PerformanceLoggingAspect.class.getName());

    @Around("execution(* service.*.*(..)) || execution(* controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            String methodName = joinPoint.getSignature().toShortString();

            if (executionTime > 1000) {
                logger.warning("SLOW METHOD: " + methodName + " executed in " + executionTime + "ms");
            } else {
                logger.info("Method " + methodName + " executed in " + executionTime + "ms");
            }
        }
    }
}