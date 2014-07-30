package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что данный метод является обработчиком действия. Метод будет
 * вызван, если запрос содержит AID с именем данного действия. Аннотация
 * применима только к методам классов, являющихся обработчиком запросов.
 *
 * @see  WrbService
 * @see  WrbDefaultAction
 * @see  WrbParam
 * @see  WrbRequiredParams
 * @see  WrbOptionalParams
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbAction {

    /**
     * (Необязательно) Возвращает имя действия. Если отсутствует, то в качестве
     * имени действия будет использовано имя метода.
     *
     * @return  Имя действия или {@code ""}.
     */
    String value() default "";

}
