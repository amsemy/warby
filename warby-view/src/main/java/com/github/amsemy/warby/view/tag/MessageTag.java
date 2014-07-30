package com.github.amsemy.warby.view.tag;

import com.github.amsemy.warby.view.I18nString;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;

import javax.servlet.jsp.JspTagException;

/**
 * Обработчик для &lt;app:message&gt; тега JSTL.
 */
public class MessageTag extends MessageSupport {

    /**
     * Задаёт I18nString-строку, для которой будет выведено локализированное
     * сообщение.
     *
     * @param  value
     *         Интернационализированная строка.
     * @throws  JspTagException
     */
    public void setValue(Object value) throws JspTagException {
        if (value != null) {
            if (value instanceof I18nString) {
                I18nString str = (I18nString) value;
                keyAttrValue = str.getKey();
                keySpecified = true;
                if (str.getBasename() != null) {
                    bundleAttrValue = BundleSupport
                            .getLocalizationContext(pageContext, str.getBasename());
                    bundleSpecified = true;
                } else {
                    bundleSpecified = false;
                }
                if (str.getArgs() != null) {
                    for (Object arg : str.getArgs()) {
                        addParam(arg);
                    }
                }
            } else {
                keyAttrValue = value.toString();
                keySpecified = true;
            }
        }
    }

}
