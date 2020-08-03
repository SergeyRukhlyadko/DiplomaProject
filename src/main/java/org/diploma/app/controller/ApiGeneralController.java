package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.InitBody;
import org.diploma.app.controller.response.ResponseTagBody;
import org.diploma.app.controller.response.dto.TagDto;
import org.diploma.app.model.db.entity.Tags;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api")
class ApiGeneralController {

    @Autowired
    AuthService authService;

    @Autowired
    GeneralService generalService;

    //Метод смены флага модератора для удобства
    @GetMapping("/moderator")
    ResponseEntity<?> moderator(@RequestParam String email) {
        try {
            generalService.changeModeratorStatus(email);
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь " + email + " не найден"));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/init")
    InitBody init() {
        return new InitBody("DevPub",
                "Рассказы разработчиков",
                "",
                "",
                "Рухлядко Сергей",
                "2020");
    }

    @GetMapping("/settings")
    ResponseEntity<?> settings(HttpSession session) {
        try {
            return ResponseEntity.ok(generalService.getAllSettings());
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/settings")
    ResponseEntity<?> settings(HttpSession session, @RequestBody HashMap<String, Boolean> settings) {
        try {
            generalService.changeSettings(session.getId(), settings);
        } catch(AccessDeniedException ad) {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь не модератор"));
        } catch(EntityNotFoundException enf) {
            return ResponseEntity.status(400).body(new BadRequestBody(enf.getMessage()));
        } catch(AuthenticationCredentialsNotFoundException acnf) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tag")
    ResponseEntity<?> tag() {
        List<TagDto> tagDtoList = new ArrayList<>();
        for(Tags tag : generalService.getAllTags())
            tagDtoList.add(new TagDto(tag.getName()));

        return ResponseEntity.ok(new ResponseTagBody(tagDtoList));
    }
}