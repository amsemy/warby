package com.github.amsemy.warby.view;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Интернационализированная строка.
 */
public class I18nString {

    /**
     * Массив аргументов для параметрической замены в сообщении.
     */
    protected Object[] args;

    /**
     * Имя ресурсного комплекта.
     */
    protected String basename;

    /**
     * Ключ сообщения.
     */
    protected String key;

    /**
     * Создаёт интернационализированную строку.
     */
    public I18nString() {
        this(null);
    }

    /**
     * Создаёт интернационализированную строку.
     *
     * @param  key
     *         Ключ сообщения.
     */
    public I18nString(String key) {
        this(key, null);
    }

    /**
     * Создаёт интернационализированную строку.
     *
     * @param  key
     *         Ключ сообщения.
     * @param  basename
     *         Имя ресурсного комплекта.
     */
    public I18nString(String key, String basename) {
        this.args = null;
        this.basename = basename;
        this.key = key;
    }

    /**
     * Возвращает массив аргументов для параметрической замены в сообщении.
     *
     * @return  Массив аргументов.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Возвращает имя ресурсного комплекта.
     *
     * @return  Имя ресурсного комплекта.
     */
    public String getBasename() {
        return basename;
    }

    /**
     * Возвращает ключ сообщения.
     *
     * @return  Ключ сообщения.
     */
    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (basename != null ? basename.hashCode() : 0);
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof I18nString)) {
            return false;
        }
        I18nString other = (I18nString) object;
        return (this.basename != null
                && this.key != null
                && this.basename.equals(other.basename)
                && this.key.equals(other.key));
    }

    @Override
    public String toString() {
        return "???" + key + "???";
    }

    /**
     * Возвращает локализированную строку.
     *
     * @param  locale
     *         Текущая локаль.
     * @return  Локализированная строка.
     */
    public String toString(Locale locale) {
        if (basename == null || locale == null) {
            return toString();
        }
        try {
            ResourceBundle rb = ResourceBundle.getBundle(basename, locale);
            return rb.getString(key);
        } catch (MissingResourceException ex) {
            return toString();
        }
    }

}
