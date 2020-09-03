package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestPostBody;
import org.diploma.app.controller.response.BadRequestBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponseBodyFactory;
import org.diploma.app.controller.response.ResponsePostBody;
import org.diploma.app.controller.response.ResponsePostByIdBody;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.service.AuthService;
import org.diploma.app.model.service.CheckupService;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.util.PostStatus;
import org.diploma.app.model.util.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/post")
@Validated
class ApiPostController  {

    @Autowired
    ApplicationContext context;

    @Autowired
    AuthService authService;

    @Autowired
    PostService postService;

    @GetMapping
    ResponsePostBody post(@RequestParam @NotNull @Min(0) Integer offset,
                          @RequestParam @NotNull @Min(1) @Max(20) Integer limit,
                          @RequestParam @NotNull SortMode mode) {
        return new ResponsePostBody(postService.findPosts(offset, limit, mode));
    }

    @GetMapping("/search")
    ResponsePostBody search(@RequestParam @NotNull @Min(0) Integer offset,
                            @RequestParam @NotNull @Min(1) @Max(20) Integer limit,
                            @RequestParam String query) {
        Page<Posts> posts;
        if (query.trim().length() == 0) {
            posts = postService.findPosts(offset, limit);
        } else {
            posts = postService.findPostsByTitleOrText(offset, limit, query);
            if (posts.getTotalElements() == 0)
                posts = postService.findPosts(offset, limit);
        }

        return new ResponsePostBody(posts);
    }

    @GetMapping("/byDate")
    ResponseEntity<?> byDate(@RequestParam @NotNull @Min(0) Integer offset,
                             @RequestParam @NotNull @Min(1) @Max(20) Integer limit,
                             @RequestParam String date) {
        try {
            return ResponseEntity.ok(new ResponsePostBody(postService.findPostsByDate(offset, limit, LocalDate.parse(date))));
        } catch(DateTimeParseException e) {
            return ResponseEntity.status(400).body(new BadRequestBody("Неверный формат даты"));
        }
    }

    @GetMapping("/byTag")
    ResponsePostBody byTag(@RequestParam @NotNull @Min(0) Integer offset,
                           @RequestParam @NotNull @Min(1) @Max(20) Integer limit,
                           @RequestParam String tag) {
        return new ResponsePostBody(postService.findPostsByTag(offset, limit, tag));
    }

    @GetMapping("/moderation")
    ResponseEntity<?> moderation(Principal principal,
                                 @RequestParam @NotNull @Min(0) Integer offset,
                                 @RequestParam @NotNull @Min(1) @Max(20) Integer limit,
                                 @RequestParam @NotNull ModerationStatus status) {
        if (authService.isModerator(principal.getName())) {
            return ResponseEntity.ok(new ResponsePostBody(
                postService.findPostsForModeration(principal.getName(), offset, limit, status)
            ));
        }

        return ResponseEntity.status(400).body(new BadRequestBody("Пользователь не модератор"));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> post(Principal principal, @PathVariable int id, @RequestBody RequestPostBody requestBody) {
        CheckupService checkupService = context.getBean("checkupService", CheckupService.class);
        checkupService.title(requestBody.getTitle());
        checkupService.text(requestBody.getText());
        Map<String, String> errors = checkupService.getErrors();

        if (!errors.isEmpty())
            return ResponseEntity.ok().body(new ErrorBody(errors));

        boolean isEdited = postService.editPost(
            principal.getName(),
            id,
            requestBody.isActive(),
            requestBody.getTimestamp(),
            requestBody.getTitle(),
            requestBody.getText(),
            requestBody.getTags()
        );

        if (isEdited) {
            return ResponseEntity.ok(new DefaultBody(isEdited));
        } else {
            return ResponseEntity.status(400).body(new BadRequestBody("Пользователь не является модератором или автором поста"));
        }
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

    @GetMapping("{id}")
    ResponseEntity<?> postId(HttpSession session, @PathVariable int id) {
        try {
            return ResponseEntity.ok(new ResponsePostByIdBody(postService.find(session.getId(), id)));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/my")
    ResponsePostBody postMy(Principal principal, @RequestParam int offset, @RequestParam int limit, @RequestParam PostStatus status) {
        return new ResponseBodyFactory().createResponsePostBody(postService.findMy(principal.getName(), offset, limit, status));
    }

    @PostMapping("/like")
    DefaultBody like(Principal principal, @RequestBody HashMap<String, Integer> requestBody) {
        return new DefaultBody(postService.like(principal.getName(), requestBody.get("post_id")));
    }

    @PostMapping("/dislike")
    DefaultBody dislike(Principal principal, @RequestBody HashMap<String, Integer> requestBody) {
        return new DefaultBody(postService.dislike(principal.getName(), requestBody.get("post_id")));
    }
}