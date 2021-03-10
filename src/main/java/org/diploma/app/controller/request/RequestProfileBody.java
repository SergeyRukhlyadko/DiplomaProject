package org.diploma.app.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.validation.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestProfileBody {

    @NullOrNotBlank(groups = FirstOrder.class)
    String name;

    @NullOrNotBlank(groups = FirstOrder.class)
    @Email(message = "неверный формат e-mail", groups = SecondOrder.class)
    String email;

    @NullOrNotBlank(groups = FirstOrder.class)
    @Size(min = 6, message = "длинна пароля должна быть не менее {min} символов", groups = SecondOrder.class)
    String password;

    String photo;
}
