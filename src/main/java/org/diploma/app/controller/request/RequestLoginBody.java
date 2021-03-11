package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.validation.FirstOrder;
import org.diploma.app.validation.SecondOrder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestLoginBody {

    @NotBlank(groups = FirstOrder.class)
    @Email(message = "неверный формат e-mail", groups = SecondOrder.class)
    @JsonProperty("e_mail")
    String email;

    @NotBlank(groups = FirstOrder.class)
    @Size(min = 6, message = "длинна пароля должна быть не менее {min} символов", groups = SecondOrder.class)
    String password;
}
