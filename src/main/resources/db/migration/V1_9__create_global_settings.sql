drop table if exists global_settings;

create table global_settings (
    id integer not null auto_increment,
    code varchar(255) not null,
    name varchar(255) not null,
    value bit not null,
    primary key (id)
);

alter table global_settings add constraint UK_global_settings_code unique (code);

insert into global_settings (code, name, value) values
('MULTIUSER_MODE', 'Многопользовательский режим', true),
('POST_PREMODERATION', 'Премодерация постов', true),
('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога ', true);