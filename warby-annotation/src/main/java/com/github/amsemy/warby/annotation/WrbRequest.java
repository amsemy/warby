package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что класс данный является обработчиком запросов. Аннотация
 * применима только к классам.
 *
 * @see  WrbAction
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbRequest {

    /**
     * (Необязательно) Указывает имя параметра запроса, через который передаётся
     * имя действия (Action Identifier). Если отсутствует, то по умолчанию
     * используется имя "aid".
     */
    String aidName() default "aid";

}
