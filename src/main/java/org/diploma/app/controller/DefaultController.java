package org.diploma.app.controller;

import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Controller
class DefaultController {

    @Autowired
    GeneralService generalService;

    @GetMapping({"/", "/404", "/login", "/login/registration", "/settings", "/login/restore-password", "/login/change-password/*"})
    String index() {
        return "index";
    }

    @GetMapping("/upload/**")
    @ResponseBody
    ResponseEntity<?> upload(HttpServletRequest request) throws IOException {
        try {
            return ResponseEntity.ok(generalService.downloadImage(Path.of(request.getRequestURI())));
        } catch(NoSuchFileException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Запрашиваемого файла не существует"));
        }
    }
}