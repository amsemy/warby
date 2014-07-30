package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает список обязательных параметров для действия. Все неуказанные
 * параметры считаются необязательными. Если не указан ни один параметр, то все
 * параметры считаются обязательными. Аннотация применима только к методам
 * классов, являющихся обработчиком запросов. Несовместима с аннотацией
 * {@link WrbOptionalParams}.
 *
 * @see  WrbService
 * @see  WrbAction
 * @see  WrbDefaultAction
 * @see  WrbOptionalParams
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbRequiredParams {

    /**
     * (Необязательно) Возвращает список обязательных параметов.
     *
     * @return  Массив имён параметров запроса.
     */
    String[] value() default {};

}
