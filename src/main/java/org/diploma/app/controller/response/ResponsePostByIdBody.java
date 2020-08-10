package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.controller.response.dto.CommentDto;
import org.diploma.app.controller.response.dto.UserDto;
import org.diploma.app.controller.response.dto.UserDtoWithPhoto;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ResponsePostByIdBody {

    int id;

    long timestamp;

    boolean active;

    UserDto user;

    String title;

    String text;

    int likeCount;

    int dislikeCount;

    int viewCount;

    List<CommentDto> comments;

    List<String> tags;

    public ResponsePostByIdBody(Posts post) {
        this.id = post.getId();
        this.timestamp = post.getTime().atZone(ZoneId.systemDefault()).toEpochSecond();
        this.active = post.isActive();

        Users user = post.getUserId();
        this.user = new UserDto(user.getId(), user.getName());

        this.title = post.getTitle();
        this.text = post.getText();

        this.likeCount = 0;
        this.dislikeCount = 0;
        List<PostVotes> postVotes = post.getPostVotes();
        for(PostVotes postVote : postVotes) {
            if (postVote.getValue() == 1) {
                this.likeCount++;
            } else {
                this.dislikeCount++;
            }
        }

        this.viewCount = post.getViewCount();

        this.comments = new ArrayList<>();
        post.getPostComments().forEach(comment -> {
            Users commentator = comment.getUserId();
            UserDtoWithPhoto userDtoWithPhoto = new UserDtoWithPhoto(commentator.getId(), commentator.getName(), commentator.getPhoto());
            CommentDto commentDto = new CommentDto(
                comment.getId(),
                comment.getTime().atZone(ZoneId.systemDefault()).toEpochSecond(),
                comment.getText(),
                userDtoWithPhoto
            );
            comments.add(commentDto);
        });

        this.tags = new ArrayList<>();
        post.getTag2posts().forEach(tag2post -> tags.add(tag2post.getTagId().getName()));
    }
}
