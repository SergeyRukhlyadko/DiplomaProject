package org.diploma.app.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.request.post.RequestPostBody;
import org.diploma.app.controller.response.DefaultBody;
import org.diploma.app.controller.response.ErrorBody;
import org.diploma.app.controller.response.ResponsePostBody;
import org.diploma.app.controller.response.dto.PostDto;
import org.diploma.app.controller.response.dto.UserDto;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.model.service.PostService;
import org.diploma.app.model.util.SortMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
        Iterator<Posts> iterator = posts.iterator();

        List<PostDto> postDtoList = new ArrayList<>();
        while(iterator.hasNext()) {
            Posts post = iterator.next();
            Users user = post.getUserId();

            PostDto postDto = new PostDto(
                post.getId(),
                post.getTime().atZone(ZoneId.systemDefault()).toEpochSecond(),
                new UserDto(user.getId(), user.getName()),
                post.getTitle(),
                post.getText(),
                0,
                0,
                post.getPostComments().size(),
                post.getViewCount()
            );

            postDtoList.add(postDto);
        }
        return ResponseEntity.ok(new ResponsePostBody(posts.getTotalElements(), postDtoList));
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

    @PostMapping("/like")
    DefaultBody like(Principal principal, @RequestBody HashMap<String, Integer> requestBody) {
        return new DefaultBody(postService.like(principal.getName(), requestBody.get("post_id")));
    }
}