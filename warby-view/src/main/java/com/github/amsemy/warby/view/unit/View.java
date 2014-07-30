package com.github.amsemy.warby.view.unit;

import com.github.amsemy.resty.json.annotation.RstPojo;

/**
 * Представление. Виджет который является представлением действия.
 *
 * @see  Unit
 */
@RstPojo
public abstract class View extends Widget {

    {
        constructor = "common.unit.View";
    }

    /**
     * Создаёт представление контента с именем "content-view".
     */
    public View() {
        super("content-view");
    }

    /**
     * Создаёт представление.
     *
     * @param  name
     *         Имя виджета.
     */
    public View(String name) {
        super(name);
    }

}
