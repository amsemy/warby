package com.github.amsemy.warby.view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Объект, представление которого может быть отправлено клиенту.
 */
public interface Viewable {

    /**
     * Отправляет представление объекта клиенту.
     *
     * @param  req
     *         Объект, который содержит запрос клиента к сервлету.
     * @param  resp
     *         Объект, который содержит ответ сервера клиенту.
     * @throws  ServletException
     *          Если ресурс назначения кидает это исключение.
     * @throws  IOException
     *          Если ресурс назначения кидает это исключение.
     */
    void send(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException;

}
