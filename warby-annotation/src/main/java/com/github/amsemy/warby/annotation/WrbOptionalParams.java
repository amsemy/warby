package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает список необязательных параметров для действия. Все неуказанные
 * параметры считаются обязательными. Если не указан ни один параметр, то все
 * параметры считаются необязательными. Аннотация применима только к методам
 * классов, являющихся обработчиком запросов. Несовместима с аннотацией
 * {@link WrbRequiredParams}.
 *
 * @see  WrbRequest
 * @see  WrbAction
 * @see  WrbDefaultAction
 * @see  WrbRequiredParams
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbOptionalParams {

    String[] value() default {};

}
