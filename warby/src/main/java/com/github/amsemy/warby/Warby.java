package warby;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import static warby.WarbyException.*;

/**
 * Warby.
 */
public class Warby {

    /**
     * Построитель списка действий.
     */
    private class ActionMapBuilder {

        // Переменные текущего разбираемого действия
        WarbyAction action;
        boolean defaultRequire;
        Set<String> exceptSet;
        Set<String> uniqueSet;

        /**
         * Собирает список параметров для текущего действия.
         *
         * @throws  WarbyException
         *          Если были ошибки при построении списка действий.
         */
        private void addActionParams() throws WarbyException {
            Class<?> objCl = Warby.this.obj.getClass();
            // Собрать промежуточные списки параметров
            WrbRequiredParams wrbReqParams
                    = action.method.getAnnotation(WrbRequiredParams.class);
            WrbOptionalParams wrbOptParams
                    = action.method.getAnnotation(WrbOptionalParams.class);
            if (wrbReqParams != null) {
                if (wrbOptParams != null) {
                    throw new WarbyException(MSG_REQUIRE_CONFLICT,
                            objCl);
                } else {
                    exceptSet = new HashSet<String>(Arrays.asList(wrbReqParams.value()));
                    if (exceptSet.size() > 0) {
                        // Указан список обязательных параметров, значит наличие
                        // остальных параметров являются необязательным
                        defaultRequire = false;
                    } else {
                        // Список не указан, наличие всех параметров являются
                        // обязательным
                        defaultRequire = true;
                    }
                }
            } else {
                if (wrbOptParams != null) {
                    exceptSet = new HashSet<String>(Arrays.asList(wrbOptParams.value()));
                    if (exceptSet.size() > 0) {
                        // Указан список необязательных параметров, значит наличие
                        // остальных параметров являются обязательным
                        defaultRequire = true;
                    } else {
                        // Список не указан, наличие всех параметров являются
                        // необязательным
                        defaultRequire = false;
                    }
                } else {
                    // Ни какой список не указан. Все параметры по умолчанию
                    // считаются обязательными
                    exceptSet = new HashSet<String>();
                    defaultRequire = true;
                }
            }
            uniqueSet = new HashSet<String>();
            // Собрать список параметров
            Class<?>[] typeArray = action.method.getParameterTypes();
            Annotation[][] anatatArray = action.method.getParameterAnnotations();
            paramLoop:
            for (int p = 0; p < typeArray.length; p++) {
                for (int a = 0; a < anatatArray[p].length; a++) {
                    if (anatatArray[p][a] instanceof WrbParam) {
                        WrbParam wrbParam = (WrbParam) anatatArray[p][a];
                        WrbPojo wrbPojo = typeArray[p].getAnnotation(WrbPojo.class);
                        boolean isPojo = (wrbPojo != null);
                        boolean paramReq = defaultRequire;
                        // Проверить имя параметра
                        String paramName = wrbParam.value();
                        if (paramName.isEmpty()) {
                            if (!isPojo) {
                                throw new WarbyException(MSG_EMPTY_PARAM_NAME,
                                        objCl, action.name);
                            }
                        } else {
                            if (uniqueSet.contains(paramName)) {
                                throw new WarbyException(MSG_DUBLICATE_PARAMS,
                                        objCl, action.name, paramName);
                            }
                            uniqueSet.add(paramName);
                            paramReq ^= exceptSet.contains(paramName);
                        }
                        // Добавить параметр в список
                        WarbyActionParam param;
                        try {
                            param = WarbyActionParam.newArgument(
                                    Warby.this.obj, paramName, typeArray[p],
                                    paramReq, isPojo);
                        } catch (WarbyException ex) {
                            throw new WarbyException(ex.getMessage(),
                                    objCl, action.name);
                        }
                        if (isPojo) {
                            addPojoParams(param);
                        }
                        action.params.add(param);
                        // Дальше аннотации для этого параметра не интересны
                        continue paramLoop;
                    }
                }
                throw new WarbyException(MSG_UNMAPPEAD_ARGUMENT,
                        objCl, action.name, null, p);
            }
        }

        /**
         * Собирает список параметров для указанного POJO-парметра.
         *
         * @param  parentParam
         *         POJO-парметр, для которого надо собрать список параметров.
         * @throws  WarbyException
         *          Если были ошибки при построении списка действий.
         */
        private void addPojoParams(WarbyActionParam parentParam)
                throws WarbyException {
            Class<?> objCl = Warby.this.obj.getClass();
            Class<?> parentCl = parentParam.type;
            for (Field f : parentCl.getDeclaredFields()) {
                WrbParam wrbParam = f.getAnnotation(WrbParam.class);
                if (wrbParam != null) {
                    WrbPojo wrbPojo = f.getType().getAnnotation(WrbPojo.class);
                    boolean isPojo = (wrbPojo != null);
                    boolean paramReq = parentParam.isRequired;
                    // Проверить имя параметра
                    String paramName = wrbParam.value();
                    if (paramName.isEmpty()) {
                            paramName = f.getName();
                    }
                    if (uniqueSet.contains(paramName)) {
                        throw new WarbyException(MSG_DUBLICATE_PARAMS,
                                objCl, action.name, paramName);
                    }
                    uniqueSet.add(paramName);
                    paramReq ^= exceptSet.contains(paramName);
                    // Добавить параметр в список
                    WarbyActionParam param;
                    try {
                        param = WarbyActionParam.newField(
                                Warby.this.obj, paramName, f.getType(),
                                paramReq, isPojo, f);
                    } catch (WarbyException ex) {
                        throw new WarbyException(ex.getMessage(),
                                objCl, action.name);
                    }
                    if (isPojo) {
                        addPojoParams(param);
                    }
                    parentParam.params.add(param);
                }
            }
            for (Method m : parentCl.getDeclaredMethods()) {
                WrbParam wrbParam = m.getAnnotation(WrbParam.class);
                if (wrbParam != null) {
                    WrbPojo wrbPojo = m.getParameterTypes()[0].getAnnotation(WrbPojo.class);
                    boolean isPojo = (wrbPojo != null);
                    boolean paramReq = parentParam.isRequired;
                    // Проверить имя параметра
                    String paramName = wrbParam.value();
                    if (paramName.isEmpty()) {
                            paramName = m.getName();
                    }
                    if (uniqueSet.contains(paramName)) {
                        throw new WarbyException(MSG_DUBLICATE_PARAMS,
                                objCl, action.name, paramName);
                    }
                    uniqueSet.add(paramName);
                    paramReq ^= exceptSet.contains(paramName);
                    // Проверить setter-метод
                    if (m.getParameterTypes().length != 1) {
                        throw new WarbyException(MSG_NON_SETTER_METHOD,
                                objCl, action.name, paramName);
                    }
                    if (m.getReturnType() != void.class) {
                        throw new WarbyException(MSG_NON_SETTER_METHOD,
                                objCl, action.name, paramName);
                    }
                    // Добавить параметр в список
                    WarbyActionParam param;
                    try {
                        param = WarbyActionParam.newSetter(
                                Warby.this.obj, paramName, m.getParameterTypes()[0],
                                paramReq, isPojo, m);
                    } catch (WarbyException ex) {
                        throw new WarbyException(ex.getMessage(),
                                objCl, action.name);
                    }
                    if (isPojo) {
                        addPojoParams(param);
                    }
                    parentParam.params.add(param);
                }
            }
        }

        /**
         * Строит список действий.
         *
         * @throws  WarbyException
         *          Если были ошибки при построении списка действий.
         */
        public void build() throws WarbyException {
            Class<?> objCl = Warby.this.obj.getClass();
            WrbRequest wrbRequest = objCl.getAnnotation(WrbRequest.class);
            if (wrbRequest != null) {
                Warby.this.aidName = wrbRequest.aidName();
                if (!Warby.this.aidName.isEmpty()) {
                    // Найти и добавить все действия
                    for (Method m : objCl.getDeclaredMethods()) {
                        WrbAction wrbAction = m.getAnnotation(WrbAction.class);
                        if (wrbAction != null) {
                            String actionName = (wrbAction.value().isEmpty())
                                    ? m.getName() : wrbAction.value();
                            if (Warby.this.actionMap.get(actionName) != null) {
                                throw new WarbyException(MSG_DUBLICATE_ACTION,
                                        objCl, null, null, actionName);
                            }
                            action = new WarbyAction(actionName, m);
                            addActionParams();
                            Warby.this.actionMap.put(actionName, action);
                        } else {
                            WrbDefaultAction wrbDefAction = m.getAnnotation(WrbDefaultAction.class);
                            if (wrbDefAction != null) {
                                if (Warby.this.defaultAction != null) {
                                    throw new WarbyException(MSG_DUBLICATE_DEFAULT_ACTION,
                                            objCl);
                                }
                                action = new WarbyAction(null, m);
                                addActionParams();
                                Warby.this.defaultAction = action;
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    throw new WarbyException(MSG_EMPTY_AID_NAME,
                            objCl);
                }
            } else {
                throw new WarbyException(MSG_MISSING_REQUEST_ANNOTATION,
                        objCl);
            }
        }

    }

    private Map<String, WarbyAction> actionMap;
    private String aidName;
    private WarbyAction defaultAction;
    private Object obj;

    /**
     * Создаёт объект Warby.
     *
     * @param  obj
     *         Объект, который содержит действие.
     * @throws  WarbyException
     *          Если были ошибки при построении списка действий.
     */
    public Warby(Object obj) throws WarbyException {
        actionMap = new HashMap<String, WarbyAction>();
        aidName = "";
        defaultAction = null;
        this.obj = obj;
        new ActionMapBuilder().build();
    }

    /**
     * Вызывает необходимое действие в зависимости от параметров запроса.
     *
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
    public void invoke(Map<String, String[]> reqParams)
            throws WarbyInvokeException, WarbyRequireParamException,
            WarbyValueFormatException {
        String[] params = reqParams.get(aidName);
        WarbyAction action;
        if (params == null) {
            action = defaultAction;
        } else if (params.length == 1) {
            if (params[0] != null && !params[0].isEmpty()) {
                action = actionMap.get(params[0]);
            } else {
                action = defaultAction;
            }
        } else {
            throw new WarbyRequireParamException();
        }
        if (action != null) {
            action.invoke(obj, reqParams);
        } else {
            throw new WarbyRequireParamException();
        }
    }

}
