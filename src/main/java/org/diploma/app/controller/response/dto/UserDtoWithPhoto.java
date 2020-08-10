package org.diploma.app.controller.response.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UserDtoWithPhoto extends UserDto {

    String photo;

    public UserDtoWithPhoto(int id, String name, String photo) {
        super(id, name);
        this.photo = photo;
    }
}
