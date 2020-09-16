insert into users (email, is_moderator, name, password, reg_time) values
('user@gmail.com', false, 'user', '$2y$10$mZ.gXGdiV9qcU88hE31hGecNG89yTn93es47SYGKyMdXpDCX.liz2', '2020-01-01 00:00:00'),
('moderator@gmail.com', true, 'moderator', '$2y$10$mZ.gXGdiV9qcU88hE31hGecNG89yTn93es47SYGKyMdXpDCX.liz2', '2020-01-01 00:00:00');

--password 123456

insert into posts (is_active, moderation_status, text, time, title, view_count, user_id) values
(true, 'NEW', 'Some new text from user - user@gmail.com for test.', '2020-01-01 01:00:00', 'User post', 5, 1),
(true, 'NEW', 'Some new text from moderator - moderator@gmail.com for test.', '2020-01-01 01:00:00', 'moderator post', 10, 2);

insert into tags (name) values ('Spring'), ('Java');

insert into tag2post(post_id, tag_id) values (1, 1), (2, 2);
