package com.github.amsemy.warby.view.unit;

import com.github.amsemy.resty.json.annotation.RstField;
import com.github.amsemy.resty.json.annotation.RstPojo;
import com.github.amsemy.warby.view.I18nString;

/**
 * Виджет. Это JSP-код и набор ресурсов к нему. Может быть размещён в секции на
 * шаблонной JSP-странице, либо другом виджете.
 *
 * @see  Unit
 */
@RstPojo
public abstract class Widget extends Unit {

    /**
     * Путь к объекту ResourceBundle, используемого виджетом.
     */
    //TODO: нужно ли???
    protected String bundle = null;

    /**
     * Заголовок виджета.
     */
    protected I18nString caption = null;

    /**
     * Функция-конструктор виджета.
     */
    @RstField
    protected String constructor = "common.unit.Widget";

    /**
     * Имя виджета. Используется как id тега, который содержит код виджета.
     */
    @RstField
    protected final String name;

    /**
     * Путь до JSP-страницы, которая отображет виджет.
     */
    protected String path = null;

    /**
     * Создаёт виджет.
     *
     * @param  name
     *         Имя виджета.
     */
    public Widget(String name) {
        this.name = name;
    }

    /**
     * Возвращает путь к объекту ResourceBundle, используемого виджетом.
     *
     * @return  Имя класса/ресурсного файла.
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Возвращает заголовок виджета.
     *
     * @return  Заголовок виджета.
     */
    public I18nString getCaption() {
        return caption;
    }

    /**
     * Возвращает имя виджета. Используется как id тега, который содержит код
     * виджета.
     *
     * @return  Имя виджета.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает путь до JSP-страницы, которая отображет виджет.
     *
     * @return  Путь до JSP-страницы.
     */
    public String getPath() {
        return path;
    }

}
