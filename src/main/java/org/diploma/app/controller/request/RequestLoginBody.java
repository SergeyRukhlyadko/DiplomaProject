package org.diploma.app.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.validation.FirstOrder;
import org.diploma.app.validation.SecondOrder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestLoginBody {

    @NotBlank(groups = FirstOrder.class)
    @Email(message = "{email.invalid.message}", groups = SecondOrder.class)
    @JsonProperty("e_mail")
    String email;

    @NotBlank(groups = FirstOrder.class)
    String password;
}
