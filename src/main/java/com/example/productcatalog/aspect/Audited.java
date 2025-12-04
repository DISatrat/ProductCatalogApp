package com.example.productcatalog.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для отметки методов, подлежащих аудиту.
 * <p>
 * Методы, отмеченные аннотацией @Audited, будут иметь свое выполнение
 * записано в журнал аудита.
 * </p>
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /**
     * Тип действия для записи в журнал аудита.
     *
     * @return тип действия
     */
    String action();

    /**
     * Необязательные пользовательские подробности для записи аудита.
     *
     * @return пользовательские подробности
     */
    String details() default "";
}
