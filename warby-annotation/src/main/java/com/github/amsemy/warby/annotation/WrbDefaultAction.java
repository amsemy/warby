package com.github.amsemy.warby.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что данный метод является обработчиком действия по умолчанию.
 * Действие по умолчанию вызывается без AID и без обязательных параметров.
 * Аннотация применима только к методам классов, являющихся обработчиком
 * запросов.
 *
 * @see  WrbService
 * @see  WrbAction
 * @see  WrbParam
 * @see  WrbRequiredParams
 * @see  WrbOptionalParams
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbDefaultAction {
}
