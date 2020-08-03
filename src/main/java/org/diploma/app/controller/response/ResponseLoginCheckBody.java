package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.response.dto.FullUserDto;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ResponseLoginCheckBody extends DefaultBody {

    FullUserDto user;

    public ResponseLoginCheckBody(FullUserDto user) {
        super(true);
        this.user = user;
    }
}
