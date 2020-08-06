package org.diploma.app.controller.response;

import org.diploma.app.controller.response.dto.PostDto;
import org.diploma.app.controller.response.dto.UserDto;
import org.diploma.app.model.db.entity.PostVotes;
import org.diploma.app.model.db.entity.Posts;
import org.diploma.app.model.db.entity.Users;
import org.springframework.data.domain.Page;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseBodyFactory {

    public ResponsePostBody createResponsePostBody(Page<Posts> posts) {
        Iterator<Posts> iterator = posts.iterator();

        List<PostDto> postDtoList = new ArrayList<>();
        while(iterator.hasNext()) {
            Posts post = iterator.next();
            Users user = post.getUserId();

            int likeCount = 0;
            int dislikeCount = 0;
            List<PostVotes> postVotes = post.getPostVotes();
            for(PostVotes postVote : postVotes) {
                if (postVote.getValue() == 1) {
                    likeCount++;
                } else {
                    dislikeCount++;
                }
            }

            PostDto postDto = new PostDto(
                post.getId(),
                post.getTime().atZone(ZoneId.systemDefault()).toEpochSecond(),
                new UserDto(user.getId(), user.getName()),
                post.getTitle(),
                post.getText(),
                likeCount,
                dislikeCount,
                post.getPostComments().size(),
                post.getViewCount()
            );

            postDtoList.add(postDto);
        }

        return new ResponsePostBody(posts.getTotalElements(), postDtoList);
    }
}
