package org.diploma.app.model.util;

import java.util.Map;

public class NullRemover {

    public static <K, V> void remove(Map<K, V> map) {
        map.remove(null);
        map.forEach((k, v) -> { if (v == null) map.remove(k); });
    }
}
