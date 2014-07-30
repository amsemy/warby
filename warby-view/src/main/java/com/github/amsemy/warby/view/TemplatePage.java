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
 * @see  SimplePage
 * @see  Unit
 */
@RstPojo
public abstract class TemplatePage implements RequestSettable, Viewable {

    /**
     * Локаль страницы.
     */
    private Locale locale;

    /**
     * Список юнитов, от которых зависит страница.
     */
    protected List<Unit> unitList;

    /**
     * Путь до JSP-страницы, которая содержит шаблон.
     */
    protected String path = null;

    /**
     * Название используемого макета шаблона (из {@code /templates}).
     */
    protected String templateName = null;

    /**
     * Создаёт страницу.
     */
    private TemplatePage() {
        unitList = new ArrayList<>();
    }

    /**
     * Создаёт страницу.
     *
     * @param  locale
     *         Локаль страницы.
     */
    public TemplatePage(Locale locale) {
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

    /**
     * Форматирует путь.
     *
     * @param  str
     *         Путь.
     * @return  Отформатированный путь.
     */
    private String formatPath(String str) {
        return str.replaceAll("\\$\\{lang\\}", locale.getLanguage());
    }

    /**
     * Возвращает список юнитов, от которых зависит страница, с разрешением
     * зависимостей.
     *
     * @param  unitList
     *         Список юнитов.
     * @return  Список юнитов с разрешёнными зависимостями.
     */
    private List<Unit> getAllUnits(List<Unit> unitList) {
        List<Unit> result = new ArrayList<>();
        for(Unit u : unitList) {
            result.addAll(u.getAllUnits());
            result.add(u);
        }
        return result;
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
     * По списку юнитов строит список скриптов.
     *
     * @param  unitList
     *         Список юнитов.
     * @return  Список скриптов.
     */
    private Collection<String> getJscripts(List<Unit> unitList) {
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
     * По списку юнитов строит список стилей.
     *
     * @param  unitList
     *         Список юнитов.
     * @return  Список стилей.
     */
    private Collection<String> getStyles(List<Unit> unitList) {
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

    /**
     * Возвращает название импользуемого макета шаблона (из {@code /templates}).
     *
     * @return  Название используемого макета шаблона.
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Возвращает список юнитов, используемых страницей.
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

    @Override
    public void setToRequest(ServletRequest request) {
        request.setAttribute("templatePage", this);
        List<Unit> units = getAllUnits(getUnitList());
        request.setAttribute("jscriptList", getJscripts(units));
        request.setAttribute("styleList", getStyles(units));
    }

}
