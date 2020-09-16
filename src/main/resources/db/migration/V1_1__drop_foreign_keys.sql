call proc_drop_foreign_key('posts', 'FK_posts_moderator_id');
call proc_drop_foreign_key('posts', 'FK_posts_user_id');

call proc_drop_foreign_key('post_votes', 'FK_post_votes_post_id');
call proc_drop_foreign_key('post_votes', 'FK_post_votes_user_id');

call proc_drop_foreign_key('tag2post', 'FK_tag2post_post_id');
call proc_drop_foreign_key('tag2post', 'FK_tag2post_tag_id');

call proc_drop_foreign_key('post_comments', 'FK_post_comments_parent_id');
call proc_drop_foreign_key('post_comments', 'FK_post_comments_post_id');
call proc_drop_foreign_key('post_comments', 'FK_post_comments_user_id');
