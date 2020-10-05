package org.diploma.app.util;

public class StringUtil {

    public static String trimSlash(String str) {
        String res = str.trim();

        if (res.startsWith("/") || res.startsWith("\\")) {
            res = res.substring(1);
        }

        if (res.endsWith("/") || res.endsWith("\\")) {
            res = res.substring(0, res.length() - 1);
        }

        return res;
    }
}
