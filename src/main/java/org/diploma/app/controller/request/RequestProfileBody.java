package org.diploma.app.controller.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.validation.FirstOrder;
import org.diploma.app.validation.NullOrNotBlank;
import org.diploma.app.validation.SecondOrder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class RequestProfileBody {

    @NullOrNotBlank(groups = FirstOrder.class)
    String name;

    @NullOrNotBlank(groups = FirstOrder.class)
    @Email(message = "{email.invalid.message}", groups = SecondOrder.class)
    String email;

    @NullOrNotBlank(groups = FirstOrder.class)
    @Size(message = "{password.length.message}", min = 6, groups = SecondOrder.class)
    String password;

    String photo;
}
