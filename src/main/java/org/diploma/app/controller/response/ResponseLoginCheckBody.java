package org.diploma.app.controller.response;

import lombok.Getter;
import org.diploma.app.dto.FullUserDto;

public class ResponseLoginCheckBody extends ResponseDefaultBody {

    @Getter
    private FullUserDto user;

    public ResponseLoginCheckBody(
        int id,
        String name,
        String photo,
        String email,
        boolean moderation,
        int moderationCount,
        boolean settings
    ) {
        super(true);
        this.user = new FullUserDto(id, name, photo, email, moderation, moderationCount, settings);
    }
}
