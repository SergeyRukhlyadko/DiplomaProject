package org.diploma.app.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class PostDto {

    int id;
    long timestamp;
    UserDto user;
    String title;
    String announce;
    int likeCount;
    int dislikeCount;
    int commentCount;
    int viewCount;
}
