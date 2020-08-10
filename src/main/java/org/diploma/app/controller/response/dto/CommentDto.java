package org.diploma.app.controller.response.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CommentDto {

    int id;

    long timestamp;

    String text;

    UserDtoWithPhoto user;
}
