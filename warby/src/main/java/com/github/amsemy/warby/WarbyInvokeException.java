package com.github.amsemy.warby;

/**
 * Общее исключение для всех ошибок, которые возникают при вызове действия.
 */
public class WarbyInvokeException extends WarbyException {

    /*
     * %1$s - Class name
     * %2$s - Action name
     * %3$s - Param name
     */
    static final String MSG_ACTION_INVOCATION_FAIL
            = "Action '%2$s' of class '%1$s' invocation has failed with '%4$s' exception";
    static final String MSG_NON_ARGUMENT_PARAM_KIND
            = "Unable invoke action '%2$s' of class '%1$s', because param '%3$s' have non 'ARGUMENT' paramKind '%4$s'";
    static final String MSG_NON_MEMBER_PARAM_KIND
            = "Unable invoke action '%2$s' of class '%1$s', because param '%3$s' have non POJO member paramKind '%4$s'";
    static final String MSG_UNABLE_TO_CREATE_PARAM_VALUE
            = "Unable invoke action '%2$s' of class '%1$s', because create a %4$sparam '%3$s' throws '%5$s' exception";
    static final String MSG_UNABLE_TO_SET_MEMBER_VALUE
            = "Unable invoke action '%2$s' of class '%1$s', because when attempting entry a param '%3$s' value throws '%4$s' exception";
    static final String MSG_UNSUPPORTED_TYPE_KIND
            = "Unable invoke action '%2$s' of class '%1$s', because param '%3$s' have unsupported typeKind '%4$s'";

    /**
     * Создаёт исключение без детализирующего сообщения.
     */
    public WarbyInvokeException() {
        super();
    }

    /**
     * Создаёт исключение с детализирующим сообщением.
     *
     * @param  message
     *         Детализирующее сообщение.
     */
    public WarbyInvokeException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с детализирующим сообщением.
     *
     * @param  message
     *         Детализирующее сообщение.
     * @param  params
     *         Параметры для форматирования сообщения.
     *
     * @see  java.util.Formatter
     */
    public WarbyInvokeException(String message, Object... params) {
        super(message, params);
    }

}
