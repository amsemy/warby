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
public abstract class TemplatePage implements RequestSettable, Viewable {

    /**
     * Бин шаблонной страницы.
     */
    @RstPojo
    public class Bean {

        /**
         * Список скриптов страницы.
         */
        public Collection<String> jscriptList;

        /**
         * Список стилей страницы.
         */
        public Collection<String> styleList;

        /**
         * Создаёт бин.
         */
        public Bean() {
            List<Unit> units = buildUnitList(getUnitList());
            jscriptList = buildJscriptList(units);
            styleList = buildStyleList(units);
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
         * Возвращает язык страницы.
         *
         * @return  Код языка страницы или пустая строка, если язык не определён.
         */
        @RstGetter("lang")
        public String getLang() {
            return locale.getLanguage();
        }

        /**
         * Возвращает название импользуемого макета шаблона (из {@code /templates}).
         *
         * @return  Название используемого макета шаблона.
         */
        public String getTemplateName() {
            return templateName;
        }

    }

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
            result.addAll(u.getAllUnits());
            result.add(u);
        }
        return result;
    }

    private String formatPath(String str) {
        return str.replaceAll("\\$\\{lang\\}", locale.getLanguage());
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

    @Override
    public void setToRequest(ServletRequest request) {
        request.setAttribute("templatePage", new Bean());
    }

}
