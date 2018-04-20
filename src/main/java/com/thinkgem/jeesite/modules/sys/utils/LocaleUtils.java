package com.thinkgem.jeesite.modules.sys.utils;

import java.util.Locale;
import java.util.StringTokenizer;

public class LocaleUtils {

    public static Locale stringToLocale(String s) {
        StringTokenizer tempStringTokenizer = new StringTokenizer(s, ",");
        String l = "";
        String c = "";
        if (tempStringTokenizer.hasMoreTokens()) {
            l = (String) tempStringTokenizer.nextElement();
        } else if (tempStringTokenizer.hasMoreTokens()) {
            c = (String) tempStringTokenizer.nextElement();
        }
        return new Locale(l, c);
    }
}
