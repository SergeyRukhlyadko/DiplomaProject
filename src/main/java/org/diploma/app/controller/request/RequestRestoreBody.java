package org.diploma.app.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestRestoreBody {

    @NotBlank
    @Email(message = "Неверный формат e-mail")
    String email;
}
