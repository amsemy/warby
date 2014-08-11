package com.github.amsemy.warby.view;

import com.github.amsemy.resty.json.annotation.RstPojo;

/**
 * Представление. Виджет который является представлением действия.
 *
 * @see  Unit
 */
@RstPojo
public abstract class View extends Widget {

    {
        constructor = "com.github.amsemy.warby.view.View";
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
