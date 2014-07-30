package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает маппинг параметра действия либо на аргумент метода, реализующего
 * действие, либо на поле или setter-метод {@link WrbPojo POJO класса},
 * являющегося аргументом этого метода. По умолчанию наличие параметра в запросе
 * является обязательным. Аннотация применима только к аргументам методов и к
 * полям и setter-методам POJO-классов. Не применима к массивам POJO-классов.
 *
 * @see  WrbController
 * @see  WrbAction
 * @see  WrbDefaultAction
 * @see  WrbRequiredParams
 * @see  WrbOptionalParams
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbParam {

    /**
     * Возвращает имя параметра запроса.
     *
     * @return  Имя параметра запроса.
     */
    String value() default "";

}
