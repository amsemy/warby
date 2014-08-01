package com.github.amsemy.warby.view;

import com.github.amsemy.resty.json.RestyJson;
import com.github.amsemy.resty.json.RestyMappingException;
import com.github.amsemy.resty.json.annotation.RstGetter;
import com.github.amsemy.resty.json.annotation.RstPojo;
import com.github.amsemy.warby.view.unit.Unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Шаблонная JSP-страница. Ответ сервера клиенту, который имеет представление в
 * виде шаблонной JSP-страницы. Может включать в себя юниты.
 *
 * @see  Page
 * @see  com.github.amsemy.warby.view.unit.Unit
 */
@RstPojo
public abstract class Template implements Viewable {

    private Locale locale;

    /**
     * Название используемого макета шаблона (из {@code /templates}).
     */
    protected String templateName = null;

    /**
     * Путь до JSP-страницы, которая содержит шаблон.
     */
    protected String path = null;

    /**
     * Список скриптов страницы.
     */
    protected Collection<String> jscriptList = null;

    /**
     * Список стилей страницы.
     */
    protected Collection<String> styleList = null;

    /**
     * Список юнитов, от которых зависит страница.
     */
    protected List<Unit> unitList;

    /**
     * Создаёт страницу.
     */
    private Template() {
        unitList = new ArrayList<>();
    }

    /**
     * Создаёт страницу.
     *
     * @param  locale
     *         Локаль страницы.
     */
    public Template(Locale locale) {
        this();
        this.locale = locale;
    }

    /**
     * Добавляет юнит в зависимости страницы.
     *
     * @param  unit
     *         Юнит.
     */
    public void addUnit(Unit unit) {
        unitList.add(unit);
    }

    private Collection<String> buildJscriptList(List<Unit> unitList) {
        Set<String> jsScripts = new LinkedHashSet<>();
        for (Unit u : unitList) {
            jsScripts.addAll(u.getJscriptList());
        }
        Set<String> result = new LinkedHashSet<>();
        for (String str : jsScripts) {
            str = formatPath(str);
            result.add(str);
        }
        return result;
    }

    private Collection<String> buildStyleList(List<Unit> unitList) {
        Set<String> styles = new LinkedHashSet<>();
        for (Unit u : unitList) {
            styles.addAll(u.getStyleList());
        }
        Set<String> result = new LinkedHashSet<>();
        for (String str : styles) {
            str = formatPath(str);
            result.add(str);
        }
        return result;
    }

    private List<Unit> buildUnitList(List<Unit> unitList) {
        List<Unit> result = new ArrayList<>();
        for(Unit u : unitList) {
            result.addAll(u.buildUnitList());
            result.add(u);
        }
        return result;
    }

    private String formatPath(String str) {
        return str.replaceAll("\\$\\{lang\\}", locale.getLanguage());
    }

    /**
     * Возвращает конфигурацию страницы.
     *
     * @return  Строка, содержащая JSON.
     */
    public String getConfig() {
        try {
            return RestyJson.build(this).toString();
        } catch (RestyMappingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Возвращает список скриптов страницы.
     *
     * @return  Список скриптов страницы.
     */
    public Collection<String> getJscriptList() {
        return jscriptList;
    }

    /**
     * Возвращает язык страницы.
     *
     * @return  Код языка страницы или пустая строка, если язык не определён.
     */
    @RstGetter("lang")
    public String getLang() {
        return locale.getLanguage();
    }

    /**
     * Возвращает список стилей страницы.
     *
     * @return  Список стилей страницы.
     */
    public Collection<String> getStyleList() {
        return styleList;
    }

    /**
     * Возвращает название импользуемого макета шаблона (из {@code /templates}).
     *
     * @return  Название используемого макета шаблона.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Возвращает список юнитов, используемых страницей. Необходимо перекрывать
     * в наследниках.
     *
     * @return  Список юнитов.
     */
    protected List<Unit> getUnitList() {
        return unitList;
    }

    @Override
    public void send(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setToRequest(req);
        req.getRequestDispatcher(path).forward(req, resp);
    }

    /**
     * Помещает страницу в атрибуты запроса.
     *
     * @param  request
     *         Объект, который содержит запрос клиента к сервлету.
     */
    public void setToRequest(ServletRequest request) {
        List<Unit> units = buildUnitList(getUnitList());
        jscriptList = buildJscriptList(units);
        styleList = buildStyleList(units);
        request.setAttribute("templatePage", this);
    }

}
