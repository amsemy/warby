package com.github.amsemy.warby.view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSP-страница. Ответ сервера клиенту, который имеет представление в виде
 * JSP-страницы.
 *
 * @see  Template
 */
public abstract class Page implements Viewable {

    /**
     * Путь к JSP-странице.
     */
    protected String path = null;

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }

}
