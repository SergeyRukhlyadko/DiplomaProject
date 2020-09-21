package org.diploma.app.model.db.entity.projection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PostsCountByDate {

    String getDate();

    int getCount();

    static Map<String, Integer> toMap(List<PostsCountByDate> list) {
        Map<String, Integer> map = new HashMap<>();
        list.forEach(p -> map.put(p.getDate(), p.getCount()));
        return map;
    }
}
