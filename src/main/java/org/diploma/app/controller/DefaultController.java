package org.diploma.app.controller;

import org.diploma.app.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class DefaultController {

    @Autowired
    GeneralService generalService;

    @GetMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }
}