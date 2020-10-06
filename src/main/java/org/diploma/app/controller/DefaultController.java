package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.service.GeneralService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
class DefaultController {

    GeneralService generalService;

    public DefaultController(GeneralService generalService) {
        this.generalService = generalService;
    }

    @GetMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }
}