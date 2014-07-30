package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что экземпляры данного класса является POJO-объектам, которые
 * используется для передачи параметров действия. Аннотация применима только
 * либо к простым классам, либо к внутренним классам по отношению к классу
 * обработчика запросов.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbPojo {
}
