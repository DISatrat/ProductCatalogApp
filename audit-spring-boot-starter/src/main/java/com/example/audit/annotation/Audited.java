package com.example.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для маркировки методов, которые должны быть проверены аудитом.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    /**
     * Описание действия для аудита
     */
    String action();

    /**
     * Дополнительные детали (опционально)
     */
    String details() default "";
}
