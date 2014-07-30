package com.github.amsemy.warby;

import static com.github.amsemy.warby.WarbyException.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Параметр действия. Если простого типа, то маппится на параметр запроса.
 * Если POJO-типа, то содержит в себе другие парметры действия.
 */
class WarbyActionParam {

    final Constructor<?> constr;
    final Field field;
    final boolean isPrimitive;
    final boolean isRequired;
    final String name;
    final WarbyParamKind paramKind;
    final List<WarbyActionParam> params;
    final Method setter;
    final Class<?> type;
    final WarbyTypeKind typeKind;

    /**
     * Создаёт параметр действия по минимальному набору параметров.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @param  name
     *         Имя парметра действия.
     * @param  type
     *         Тип данных параметра действия.
     * @param  isRequired
     *         Признак обязательности передачи параметра действию.
     * @param  paramKind
     *         Способ отображения парметра запроса на параметр действия.
     * @param  field
     *         Ссылка на поле POJO-параметра.
     * @param  setter
     *         Ссылка на setter-метод POJO-параметра.
     * @param  isPojo
     *         Признак того, что параметр является POJO.
     * @throws  WarbyException
     *          Если тип параметра не удовлетворяет
     *          {@link com.github.amsemy.warby.annotation.WrbParam допустимым
     *          требованиям}.
     */
    private WarbyActionParam(
            Object obj,
            String name,
            Class<?> type,
            boolean isRequired,
            WarbyParamKind paramKind,
            Field field,
            Method setter,
            boolean isPojo) throws WarbyException {
        this.name = name;
        this.type = type;
        this.isRequired = isRequired;
        this.paramKind = paramKind;
        this.field = field;
        this.setter = setter;
        try {
            if (isPojo) {
                if (type.isArray()) {
                    throw new WarbyException(MSG_POJO_ARRAY,
                            obj.getClass(), "%2$s", name, type);
                }
                if (type.isMemberClass()) {
                    if (Modifier.isStatic(type.getModifiers())) {
                        constr = type.getDeclaredConstructor();
                        typeKind = WarbyTypeKind.NESTED_POJO;
                    } else {
                        constr = type.getDeclaredConstructor(obj.getClass());
                        typeKind = WarbyTypeKind.INNER_POJO;
                    }
                } else {
                    constr = type.getDeclaredConstructor();
                    typeKind = WarbyTypeKind.POJO;
                }
                isPrimitive = false;
                params = new ArrayList<WarbyActionParam>();
            } else {
                Class<?> cl = (type.isArray()) ? type.getComponentType() : type;
                if (cl.isPrimitive() && cl != char.class) {
                    if (cl == boolean.class) {
                        constr = Boolean.class.getConstructor(String.class);
                    } else if (cl == byte.class) {
                        constr = Byte.class.getConstructor(String.class);
                    } else if (cl == double.class) {
                        constr = Double.class.getConstructor(String.class);
                    } else if (cl == float.class) {
                        constr = Float.class.getConstructor(String.class);
                    } else if (cl == int.class) {
                        constr = Integer.class.getConstructor(String.class);
                    } else if (cl == long.class) {
                        constr = Long.class.getConstructor(String.class);
                    } else if (cl == short.class) {
                        constr = Short.class.getConstructor(String.class);
                    } else {
                        throw new WarbyException(MSG_UNSUPPORTED_PARAM_TYPE,
                                obj.getClass(), "%2$s", name, type);
                    }
                    if (type.isArray()) {
                        typeKind = WarbyTypeKind.PRIMITIVE_ARRAY;
                    } else {
                        typeKind = WarbyTypeKind.PRIMITIVE;
                    }
                } else if (cl == Boolean.class
                        || cl == Byte.class
                        || cl == Double.class
                        || cl == Float.class
                        || cl == Integer.class
                        || cl == Long.class
                        || cl == Short.class) {
                    constr = cl.getDeclaredConstructor(String.class);
                    if (type.isArray()) {
                        typeKind = WarbyTypeKind.WRAP_ARRAY;
                    } else {
                        typeKind = WarbyTypeKind.WRAP;
                    }
                } else if (cl == String.class) {
                    constr = null;
                    if (type.isArray()) {
                        typeKind = WarbyTypeKind.STRING_ARRY;
                    } else {
                        typeKind = WarbyTypeKind.STRING;
                    }
                } else if (cl == char.class || cl == Character.class) {
                    constr = null;
                    if (type.isArray()) {
                        typeKind = WarbyTypeKind.CHAR_ARRAY;
                    } else {
                        typeKind = WarbyTypeKind.CHAR;
                    }
                } else {
                    throw new WarbyException(MSG_UNSUPPORTED_PARAM_TYPE,
                            obj.getClass(), "%2$s", name, type);
                }
                isPrimitive = cl.isPrimitive();
                params = null;
            }
        } catch (NoSuchMethodException ex) {
            throw new WarbyException(MSG_WITHOUT_CONSTR,
                    obj.getClass(), "%2$s", name);
        }
    }

    /**
     * Создаёт параметр действия со способом отображения {@code ARGUMENT}.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @param  name
     *         Имя парметра действия.
     * @param  type
     *         Тип данных параметра действия.
     * @param  isRequired
     *         Признак обязательности передачи параметра действию.
     * @param  isPojo
     *         Признак того, что параметр является POJO.
     * @return  Параметр действия.
     * @throws  WarbyException
     *          Если тип параметра не удовлетворяет
     *          {@link com.github.amsemy.warby.annotation.WrbParam допустимым
     *          требованиям}.
     */
    static WarbyActionParam newArgument(Object obj, String name, Class<?> type,
            boolean isRequired, boolean isPojo) throws WarbyException {
        return new WarbyActionParam(
                obj,
                name,
                type,
                isRequired,
                WarbyParamKind.ARGUMENT,
                null,
                null,
                isPojo);
    }

    /**
     * Создаёт параметр действия со способом отображения {@code FIELD}.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @param  name
     *         Имя парметра действия.
     * @param  type
     *         Тип данных параметра действия.
     * @param  isRequired
     *         Признак обязательности передачи параметра действию.
     * @param  isPojo
     *         Признак того, что параметр является POJO.
     * @param  field
     *         Ссылка на поле POJO-параметра.
     * @return  Параметр действия.
     * @throws  WarbyException
     *          Если тип параметра не удовлетворяет
     *          {@link com.github.amsemy.warby.annotation.WrbParam допустимым
     *          требованиям}.
     */
    static WarbyActionParam newField(Object obj, String name, Class<?> type,
            boolean isRequired, boolean isPojo, Field field) throws WarbyException {
        return new WarbyActionParam(
                obj,
                name,
                type,
                isRequired,
                WarbyParamKind.FIELD,
                field,
                null,
                isPojo);
    }

    /**
     * Создаёт параметр действия со способом отображения {@code SETTER}.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @param  name
     *         Имя парметра действия.
     * @param  type
     *         Тип данных параметра действия.
     * @param  isRequired
     *         Признак обязательности передачи параметра действию.
     * @param  isPojo
     *         Признак того, что параметр является POJO.
     * @param  setter
     *         Ссылка на setter-метод POJO-параметра.
     * @return  Параметр действия.
     * @throws  WarbyException
     *          Если тип параметра не удовлетворяет
     *          {@link com.github.amsemy.warby.annotation.WrbParam допустимым
     *          требованиям}.
     */
    static WarbyActionParam newSetter(Object obj, String name, Class<?> type,
            boolean isRequired, boolean isPojo, Method setter) throws WarbyException {
        return new WarbyActionParam(
                obj,
                name,
                type,
                isRequired,
                WarbyParamKind.SETTER,
                null,
                setter,
                isPojo);
    }

}
