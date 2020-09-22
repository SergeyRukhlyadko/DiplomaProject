package org.diploma.app.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.diploma.app.dto.PostDto;
import org.diploma.app.dto.UserDto;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.diploma.app.util.DateTimeUtil;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ResponsePostBody {

    long count;

    List<PostDto> posts;

    public ResponsePostBody(Page<Posts> posts) {
        this.count = posts.getTotalElements();
        this.posts = new ArrayList<>();

        for (Posts post : posts) {
            Users user = post.getUser();

            int likeCount = 0;
            int dislikeCount = 0;
            List<PostVotes> postVotes = post.getPostVotes();
            for (PostVotes postVote : postVotes) {
                if (postVote.getValue() == 1) {
                    likeCount++;
                } else {
                    dislikeCount++;
                }
            }

            PostDto postDto = new PostDto(
                post.getId(),
                DateTimeUtil.toTimestamp(post.getTime()),
                new UserDto(user.getId(), user.getName()),
                post.getTitle(),
                Jsoup.parse(post.getText()).text(),
                likeCount,
                dislikeCount,
                post.getPostComments().size(),
                post.getViewCount()
            );

            this.posts.add(postDto);
        }
    }
}
