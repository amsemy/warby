package com.github.amsemy.warby.view;

import com.github.amsemy.resty.json.annotation.RstPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Юнит. Описывает набор ресурсов необходимый для формирования страницы или её
 * части. Основные ресурсы это - JS (с их зависимостями) и CSS.
 *
 * @see  Template
 * @see  View
 * @see  Widget
 */
@RstPojo
public abstract class Unit {

    /**
     * Cпискок скриптов, входящих в состав юнита.
     */
    protected final List<String> jscriptList;

    /**
     * Список стилей, входящих в состав юнита.
     */
    protected final List<String> styleList;

    /**
     * Список юнитов, от которых зависит юнит.
     */
    protected final List<Unit> unitList;

    {
        jscriptList = new ArrayList<>();
        styleList = new ArrayList<>();
        unitList = new ArrayList<>();
    }

    /**
     * Добавляет ссылку на скрипт в юнит.
     *
     * @param  jscriptPath
     *         Путь до скрипта.
     */
    public void addJscript(String jscriptPath) {
        jscriptList.add(jscriptPath);
    }

    /**
     * Добавляет ссылку на стиль в юнит.
     *
     * @param  stylePath
     *         Путь до стиля.
     */
    public void addStyle(String stylePath) {
        styleList.add(stylePath);
    }

    /**
     * Добавляет юнит в зависимости текущего юнита.
     *
     * @param  unit
     *         Юнит от которого зависит текущий юнит.
     */
    public void addUnit(Unit unit) {
        unitList.add(unit);
    }

    /**
     * Строит список юнитов, от которых зависит юнит, с разрешением
     * зависимостей.
     *
     * @return  Список юнитов с разрешёнными зависимостями.
     */
    public List<Unit> buildFullUnitList() {
        List<Unit> fullList = new ArrayList<>();
        for (Unit u : getUnitList()) {
            fullList.addAll(u.buildFullUnitList());
        }
        fullList.add(this);
        return fullList;
    }

    /**
     * Возвращает спискок скриптов, входящих в состав юнита.
     *
     * @return  Список путей до скриптов.
     */
    protected List<String> getJscriptList() {
        return jscriptList;
    }

    /**
     * Возвращает список стилей, входящих в состав юнита.
     *
     * @return  Список путей до стилей.
     */
    protected List<String> getStyleList() {
        return styleList;
    }

    /**
     * Возвращает список юнитов, от которых зависит юнит.
     *
     * @return  Список юнитов.
     */
    protected List<Unit> getUnitList() {
        return unitList;
    }

}
