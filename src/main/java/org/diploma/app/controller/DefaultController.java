package org.diploma.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class DefaultController {

    @GetMapping({"/", "/404", "/login", "/login/registration", "/settings", "/login/restore-password", "/login/change-password/*"})
    String index() {
        return "index";
    }
}