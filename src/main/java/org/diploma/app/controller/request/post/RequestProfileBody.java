package org.diploma.app.controller.request.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestProfileBody {

    String name;

    String email;

    String password;

    int removePhoto;

    String photo;
}
