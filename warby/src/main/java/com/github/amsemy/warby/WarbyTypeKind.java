package warby;

/**
 * Информация о типе парметра действия.
 */
enum WarbyTypeKind {

    /** Символ. */
    CHAR,

    /** Массив символов. */
    CHAR_ARRAY,

    /** Внутренний класс POJO. */
    INNER_POJO,

    /** Вложенный класс POJO. */
    NESTED_POJO,

    /** Класс POJO. */
    POJO,

    /** Примитивный тип. */
    PRIMITIVE,

    /** Массив примитивного типа. */
    PRIMITIVE_ARRAY,

    /** Строка. */
    STRING,

    /** Массив строк. */
    STRING_ARRY,

    /** Обёртка. */
    WRAP,

    /** Массив обёрток. */
    WRAP_ARRAY
}
