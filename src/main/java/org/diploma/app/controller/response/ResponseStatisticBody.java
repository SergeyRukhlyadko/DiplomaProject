package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ResponseStatisticBody {

    int postsCount;

    int likesCount;

    int dislikesCount;

    int viewsCount;

    long firstPublication;
}
