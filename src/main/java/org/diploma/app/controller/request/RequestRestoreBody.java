package org.diploma.app.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestRestoreBody {

    @NotNull
    @NotEmpty
    @Email(message = "Неверный формат e-mail")
    String email;
}
