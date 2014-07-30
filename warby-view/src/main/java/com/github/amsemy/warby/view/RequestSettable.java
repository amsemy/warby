package com.github.amsemy.warby.view;

import javax.servlet.ServletRequest;

/**
 * Объект, который можно поместить в атрибуты запроса сервлета. Используется
 * при перенаправлении запроса.
 */
public interface RequestSettable {

    /**
     * Помещает объект в атрибуты запроса.
     *
     * @param  request
     *         Объект, который содержит запрос клиента к сервлету.
     */
    void setToRequest(ServletRequest request);

}
