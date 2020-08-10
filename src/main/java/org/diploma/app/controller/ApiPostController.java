package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestPostBody;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponseBodyFactory;
import org.diploma.app.controller.response.ResponsePostBody;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.util.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/post")
class ApiPostController  {

    @Autowired
    PostService postService;

    @GetMapping
    ResponseEntity<?> post(@RequestParam int offset, @RequestParam int limit, @RequestParam SortMode mode) {
        Page<Posts> posts = postService.find(offset, limit, mode);
        return ResponseEntity.ok(new ResponseBodyFactory().createResponsePostBody(posts));
    }

    @PostMapping
    ResponseEntity<?> post(Principal principal, @RequestBody RequestPostBody requestBody) {
        try {
            Map<String, String> errors = postService.create(
                principal.getName(),
                requestBody.isActive(),
                requestBody.getTimestamp(),
                requestBody.getTitle(),
                requestBody.getText(),
                requestBody.getTags()
            );

            if (!errors.isEmpty())
                return ResponseEntity.ok(new ErrorBody(errors));
        } catch(EntityNotFoundException enf) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(new DefaultBody(true));
    }

    @GetMapping("/my")
    ResponseEntity<?> myPost() {
        return ResponseEntity.ok("{\n" +
            "  \"count\":0,\n" +
            "  \"posts\":[]\n" +
            "}");
    }

    @GetMapping("/byTag")
    ResponsePostBody postByTag(@RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return new ResponseBodyFactory().createResponsePostBody(postService.findByTag(offset, limit, tag));
    }

    @PostMapping("/like")
    DefaultBody like(Principal principal, @RequestBody HashMap<String, Integer> requestBody) {
        return new DefaultBody(postService.like(principal.getName(), requestBody.get("post_id")));
    }

    @PostMapping("/dislike")
    DefaultBody dislike(Principal principal, @RequestBody HashMap<String, Integer> requestBody) {
        return new DefaultBody(postService.dislike(principal.getName(), requestBody.get("post_id")));
    }

    @GetMapping("/moderation")
    ResponseEntity<?> moderation(Principal principal, @RequestParam int offset, @RequestParam int limit, @RequestParam ModerationStatus status) {
        Page<Posts> posts;
        try {
            posts = postService.find(principal.getName(), offset, limit, status);
        } catch(AccessDeniedException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь не модератор"));
        }

        return ResponseEntity.ok(new ResponseBodyFactory().createResponsePostBody(posts));
    }
}