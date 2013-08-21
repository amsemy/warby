package warby;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает, что данный метод является обработчиком действия. Аннотация
 * применима только к методам классов, являющихся обработчиком запросов.
 *
 * @see  WrbRequest
 * @see  WrbDefaultAction
 * @see  WrbParam
 * @see  WrbRequiredParams
 * @see  WrbOptionalParams
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrbAction {

    /**
     * (Необязательно) Указывает имя действия. Если отсутствует, то в качестве
     * имени действия будет использовано имя метода.
     */
    String value() default "";

}
