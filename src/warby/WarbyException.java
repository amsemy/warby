package warby;

/**
 * Общее исключение для всех ошибок, которые возникают при работе с запросами и
 * действиями.
 */
public class WarbyException extends Exception {

    /*
     * %1$s - Class name
     * %2$s - Action name
     * %3$s - Param name
     */
    static final String MSG_EMPTY_AID_NAME
            = "Empty AID name' for '%1$s' class";
    static final String MSG_EMPTY_PARAM_NAME
            = "Action '%2$s' of class '%1$s' has param with empty name";
    static final String MSG_MISSING_REQUEST_ANNOTATION
            = "Class '%1$s' haven't necessary 'WrbRequest' annotation";
    static final String MSG_DUBLICATE_ACTION
            = "Dublicate action '%4$s' of '%1$s' class";
    static final String MSG_DUBLICATE_DEFAULT_ACTION
            = "Dublicate default action of '%1$s' class";
    static final String MSG_DUBLICATE_PARAMS
            = "Action '%2$s' of class '%1$s' have dublicated param '%3$s'";
    static final String MSG_NON_SETTER_METHOD
            = "Action '%2$s' of class '%1$s' have param '%3$s' for non setter method";
    static final String MSG_POJO_ARRAY
            = "Action '%2$s' of class '%1$s' have param '%3$s' with unallowed POJO array type '%4$s'";
    static final String MSG_REQUIRE_CONFLICT
            = "Class '%1$s' has two mutually exclusive annotations 'WrbRequiredParams' and 'WrbOptionalParams'";
    static final String MSG_UNMAPPEAD_ARGUMENT
            = "Action '%2$s' of class '%1$s' mapped to method having unmapped argument[%4$s]";
    static final String MSG_UNSUPPORTED_PARAM_TYPE
            = "Action '%2$s' of class '%1$s' have param '%3$s' with unsupported type '%4$s'";
    static final String MSG_WITHOUT_CONSTR
            = "Action '%2$s' of class '%1$s' have param '%3$s' without necessary constructor";

    /**
     * Создаёт исключение.
     */
    public WarbyException() {
        super();
    }

    /**
     * Создаёт исключение с указанным сообщением. Сообщение может быть записано
     * в лог сервера и/или отображено для пользователя.
     *
     * @param  message
     *         Строка, содержащая сообщение исключения.
     */
    public WarbyException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с указанным шаблонным сообщением. Сообщение может быть
     * записано в лог сервера и/или отображено для пользователя.
     *
     * @param  fromatMessage
     *         {@link java.util.Formatter Форматируемая строка}, содержащая
     *         сообщение исключения.
     * @param  args
     *         Параметры форматируемой строки.
     */
    public WarbyException(String fromatMessage, Object... args) {
        super(String.format(fromatMessage, args));
    }

}
