package org.diploma.app.controller.request;

import lombok.Getter;
import org.diploma.app.validation.FirstOrder;
import org.diploma.app.validation.SecondOrder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class RequestRestoreBody {

    @NotBlank(groups = FirstOrder.class)
    @Email(message = "неверный формат e-mail", groups = SecondOrder.class)
    private String email;
}
