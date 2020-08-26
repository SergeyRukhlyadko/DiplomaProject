package org.diploma.app.model.db.entity.projection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface GlobalSettingCodeAndValue {

    String getCode();

    boolean getValue();

    static Map<String, Boolean> toMap(Collection<GlobalSettingCodeAndValue> collection) {
        Map<String, Boolean> map = new HashMap<>();
        collection.forEach(p -> map.put(p.getCode(), p.getValue()));
        return map;
    }
}
