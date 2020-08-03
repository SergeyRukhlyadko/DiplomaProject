package org.diploma.app.controller.request.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class RequestPostBody {

    Date timestamp;

    boolean active;

    String title;

    List<String> tags;

    String text;
}
