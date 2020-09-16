drop table if exists captcha_codes;

create table captcha_codes (
    id integer not null auto_increment,
    code varchar(255) not null,
    secret_code varchar(255) not null,
    time datetime(6) not null,
    primary key (id)
);