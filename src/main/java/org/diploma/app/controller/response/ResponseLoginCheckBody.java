package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.response.dto.FullUserDto;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ResponseLoginCheckBody extends DefaultBody {

    FullUserDto user;

    public ResponseLoginCheckBody(int id,
                                  String name,
                                  String photo,
                                  String email,
                                  boolean moderation,
                                  int moderationCount,
                                  boolean settings) {
        super(true);
        this.user = new FullUserDto(id, name, photo, email, moderation, moderationCount, settings);
    }
}
