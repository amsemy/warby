package com.github.amsemy.warby.controller;

import static com.github.amsemy.warby.controller.WarbyInvokeException.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Действие. Обработчик запросов в зависимости от параметров может выполнять
 * различные действия.
 */
class WarbyAction {

    final Method method;
    final String name;
    List<WarbyActionParam> params;

    /**
     * Создаёт действие.
     *
     * @param  name
     *         Имя действия.
     * @param  method
     *         Метод обработчика запросов, который выполняет действие.
     */
    WarbyAction(String name, Method method) {
        this.name = name;
        this.method = method;
        params = new ArrayList<>();
    }

    /**
     * Создаёт параметр простого массивного типа.
     *
     * @param  param
     *         Параметр действия.
     * @param  obj
     *         Объект, который содержит действие.
     * @param  reqParams
     *         Список параметров запроса.
     * @return  Параметр простого массивного типа.
     * @throws  WarbyInvokeException
     *          Если при создании параметра произошла внутрення ошибка.
     * @throws  WarbyRequireParamException
     *          Если отсутствует обязательный параметр запроса.
     * @throws  WarbyValueFormatException
     *          Если формат передаваемого значения не соответствует типу
     *          параметра действия.
     */
    private Object createArrayParam(WarbyActionParam param,
            Object obj, Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        String[] reqParam = reqParams.get(param.name);
        if (reqParam != null) {
            try {
                Class<?> paramCl = param.type.getComponentType();
                Object arr;
                if (param.typeKind == WarbyTypeKind.CHAR_ARRAY) {
                    if (reqParam.length == 1) {
                        // Строка как массив char
                        arr = Array.newInstance(paramCl, reqParam[0].length());
                        if (reqParam[0] != null) {
                            for (int i = 0; i < reqParam[0].length(); i++) {
                                Array.set(arr, i, reqParam[0].charAt(i));
                            }
                        }
                    } else {
                        // Массив char
                        arr = Array.newInstance(paramCl, reqParam.length);
                        for (int i = 0; i < reqParam.length; i++) {
                            if (reqParam[i] == null) {
                                if (param.isPrimitive) {
                                    throw new WarbyValueFormatException();
                                } else {
                                    Array.set(arr, i, null);
                                }
                            } else if (reqParam[i].length() == 1) {
                                Array.set(arr, i, reqParam[i].charAt(0));
                            } else {
                                throw new WarbyValueFormatException();
                            }
                        }
                    }
                } else {
                    arr = Array.newInstance(paramCl, reqParam.length);
                    for (int i = 0; i < reqParam.length; i++) {
                        if (param.typeKind == WarbyTypeKind.STRING_ARRY) {
                            Array.set(arr, i, reqParam[i]);
                        } else {
                            try {
                                if (reqParam[i] == null) {
                                    if (param.isPrimitive) {
                                        throw new WarbyValueFormatException();
                                    } else {
                                        Array.set(arr, i, null);
                                    }
                                } else {
                                    Array.set(arr, i, param.constr.newInstance(reqParam[i]));
                                }
                            } catch (InvocationTargetException ex) {
                                throw new WarbyValueFormatException();
                            }
                        }
                    }
                }
                return arr;
            } catch (ReflectiveOperationException ex) {
                throw new WarbyInvokeException(MSG_UNABLE_TO_CREATE_PARAM_VALUE,
                        obj.getClass(), name, param.name, "array ", ex);
            }
        } else {
            if (param.isRequired) {
                throw new WarbyRequireParamException();
            }
            return null;
        }
    }

    /**
     * Создаёт параметр.
     *
     * @param  param
     *         Параметр действия.
     * @param  obj
     *         Объект, который содержит действие.
     * @param  reqParams
     *         Список параметров запроса.
     * @return  Параметр.
     * @throws  WarbyInvokeException
     *          Если при создании параметра произошла внутрення ошибка.
     * @throws  WarbyRequireParamException
     *          Если отсутствует обязательный параметр запроса.
     * @throws  WarbyValueFormatException
     *          Если формат передаваемого значения не соответствует типу
     *          параметра действия.
     */
    private Object createParam(WarbyActionParam param,
            Object obj, Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        switch (param.typeKind) {
            case CHAR:
            case PRIMITIVE:
            case STRING:
            case WRAP:
                return createSimpleParam(param, obj, reqParams);
            case CHAR_ARRAY:
            case PRIMITIVE_ARRAY:
            case STRING_ARRY:
            case WRAP_ARRAY:
                return createArrayParam(param, obj, reqParams);
            case INNER_POJO:
            case NESTED_POJO:
            case POJO:
                return createPojoParam(param, obj, reqParams);
            default:
                throw new WarbyInvokeException(MSG_UNSUPPORTED_TYPE_KIND,
                        obj.getClass(), name, param.name, param.typeKind);
        }
    }

    /**
     * Создаёт параметр POJO типа.
     *
     * @param  param
     *         Параметр действия.
     * @param  obj
     *         Объект, который содержит действие.
     * @param  reqParams
     *         Список параметров запроса.
     * @return  Параметр POJO типа.
     * @throws  WarbyInvokeException
     *          Если при создании параметра произошла внутрення ошибка.
     * @throws  WarbyRequireParamException
     *          Если отсутствует обязательный параметр запроса.
     * @throws  WarbyValueFormatException
     *          Если формат передаваемого значения не соответствует типу
     *          параметра действия.
     */
    private Object createPojoParam(WarbyActionParam param,
            Object obj, Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        Object pojo;
        try {
            param.constr.setAccessible(true); // TODO
            if (param.typeKind == WarbyTypeKind.INNER_POJO) {
                pojo = param.constr.newInstance(obj);
            } else {
                pojo = param.constr.newInstance();
            }
        } catch (ReflectiveOperationException ex) {
            throw new WarbyInvokeException(MSG_UNABLE_TO_CREATE_PARAM_VALUE,
                    obj.getClass(), name, param.name, "POJO ", ex);
        }
        for (WarbyActionParam p : param.params) {
            Object arg = createParam(p, obj, reqParams);
            try {
                switch (p.paramKind) {
                    case FIELD:
                        p.field.setAccessible(true); // TODO
                        p.field.set(pojo, arg);
                        break;
                    case SETTER:
                        p.setter.setAccessible(true); // TODO
                        p.setter.invoke(pojo, arg);
                        break;
                    default:
                        throw new WarbyInvokeException(MSG_NON_MEMBER_PARAM_KIND,
                                obj.getClass(), name, p.name, p.paramKind);
                }
            } catch (ReflectiveOperationException ex) {
                throw new WarbyInvokeException(MSG_UNABLE_TO_SET_MEMBER_VALUE,
                        obj.getClass(), name, param.name, ex);
            }
        }
        return pojo;
    }

    /**
     * Создаёт параметр простого типа.
     *
     * @param  param
     *         Параметр действия.
     * @param  obj
     *         Объект, который содержит действие.
     * @param  reqParams
     *         Список параметров запроса.
     * @return  Параметр простого типа.
     * @throws  WarbyInvokeException
     *          Если при создании параметра произошла внутрення ошибка.
     * @throws  WarbyRequireParamException
     *          Если отсутствует обязательный параметр запроса.
     * @throws  WarbyValueFormatException
     *          Если формат передаваемого значения не соответствует типу
     *          параметра действия.
     */
    private Object createSimpleParam(WarbyActionParam param,
            Object obj, Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        String[] reqParam = reqParams.get(param.name);
        if (reqParam != null) {
            if (reqParam.length > 1) {
                throw new WarbyRequireParamException();
            }
            if (reqParam[0] == null) {
                if (param.isPrimitive) {
                    throw new WarbyValueFormatException();
                } else {
                    return null;
                }
            }
            try {
                if (param.typeKind == WarbyTypeKind.STRING) {
                    return reqParam[0];
                } else if (param.typeKind == WarbyTypeKind.CHAR) {
                    if (reqParam[0].length() != 1) {
                        throw new WarbyValueFormatException();
                    }
                    return reqParam[0].charAt(0);
                } else {
                    try {
                        return param.constr.newInstance(reqParam[0]);
                    } catch (InvocationTargetException ex) {
                        throw new WarbyValueFormatException();
                    }
                }
            } catch (ReflectiveOperationException ex) {
                throw new WarbyInvokeException(MSG_UNABLE_TO_CREATE_PARAM_VALUE,
                        obj.getClass(), name, param.name, "", ex);
            }
        } else {
            if (param.isRequired) {
                throw new WarbyRequireParamException();
            }
            if (param.isPrimitive) {
                throw new WarbyValueFormatException();
            }
            return null;
        }
    }

    /**
     * Вызывает действие.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @param  reqParams
     *         Список параметров запроса.
     * @throws  WarbyInvokeException
     *          Если при вызове действия произошла внутрення ошибка.
     * @throws  WarbyRequireParamException
     *          Если отсутствует обязательный параметр запроса.
     * @throws  WarbyValueFormatException
     *          Если формат передаваемого значения не соответствует типу
     *          параметра действия.
     */
    void invoke(Object obj, Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        // Подготовить список аргументов
        Object[] args = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            WarbyActionParam p = params.get(i);
            if (p.paramKind != WarbyParamKind.ARGUMENT) {
                throw new WarbyInvokeException(MSG_NON_ARGUMENT_PARAM_KIND,
                        obj.getClass(), name, p.name, p.paramKind);
            }
            args[i] = createParam(p, obj, reqParams);
        }
        // Вызвать действие
        try {
            AccessController.doPrivileged(new PrivilegedAction() {
                @Override
                public Object run() {
                    method.setAccessible(true);
                    return null;
                }
            });
            method.invoke(obj, args);
        } catch (ReflectiveOperationException ex) {
            throw new WarbyInvokeException(MSG_ACTION_INVOCATION_FAIL,
                    obj.getClass(), name, null, ex.getCause());
        }
    }

}
