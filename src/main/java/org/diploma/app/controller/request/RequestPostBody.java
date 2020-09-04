package org.diploma.app.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestPostBody {

    @NotNull
    Date timestamp;

    @NotNull
    Boolean active;

    @NotBlank
    @Size(min = 3, max = 255, message = "Длинна заголовка должна быть от {min} до {max} символов")
    String title;

    @NotNull
    List<String> tags;

    @NotBlank
    @Size(min = 50, message = "Длинна текста публкации заголовка должна быть от {min} символов")
    String text;
}
