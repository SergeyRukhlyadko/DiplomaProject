package org.diploma.app.controller.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.model.util.Decision;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class RequestModerationBody {

    @JsonProperty("post_id")
    int postId;

    Decision decision;
}
