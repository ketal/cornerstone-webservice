package com.github.ketal.cornerstone.webservice.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizedMessages {

    public static String get(final String bundleName, final String exceptionKey, final Object... args) {
        Locale en = new Locale("en", "US");
        ResourceBundle bundle = ResourceBundle.getBundle("locales." + bundleName, en);
        String exception = bundle.getString(exceptionKey);

        if (args == null || args.length == 0) {
            return exception;
        }

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(en);
        formatter.applyPattern(exception);
        return formatter.format(args);
    }
}
