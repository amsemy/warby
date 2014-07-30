package com.github.amsemy.warby;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet, который выполняет те или иные действия в зависимости от
 * параметров запроса.
 *
 * @see  com.github.amsemy.warby.annotation.WrbAction
 */
public abstract class WarbyHttpServlet extends HttpServlet {

    private ThreadLocal<HttpServletRequest> localReq;
    private ThreadLocal<HttpServletResponse> localResp;
    private Warby wrb;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.localReq.set(req);
        this.localResp.set(resp);
        execute();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.localReq.set(req);
        this.localResp.set(resp);
        execute();
    }

    /**
     * Вызывает необходимое действие в зависимости от параметров запроса.
     *
     * @throws  ServletException
     *          Если при выполнении команды было брошено это исключение.
     * @throws  IOException
     *          Если при выполнении команды было брошено это исключение.
     */
    protected void execute() throws ServletException, IOException {
        try {
            wrb.invoke(getReq().getParameterMap());
        } catch (WarbyInvokeException ex) {
            log("Warby invoke exception", ex);
            getResp().sendError(500, ex.toString());
        } catch (WarbyException ex) {
            log("Warby exception", ex);
            getResp().sendError(404, ex.toString());
        }
    }

    @Override
    public void init() throws ServletException {
        localReq = new ThreadLocal<HttpServletRequest>();
        localResp = new ThreadLocal<HttpServletResponse>();
        try {
            wrb = new Warby(this);
        } catch (WarbyException ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Возвращает объект, который содержит запрос клиента к сервлету.
     * Изолирован от других потоков.
     *
     * @return  Запрос клиента к сервлету.
     */
    public HttpServletRequest getReq() {
        return localReq.get();
    }

    /**
     * Возвращает объект, который содержит ответ сервлета клиенту.
     * Изолирован от других потоков.
     *
     * @return  Ответ сервлета клиенту.
     */
    public HttpServletResponse getResp() {
        return localResp.get();
    }

}
