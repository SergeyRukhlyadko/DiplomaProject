drop procedure if exists proc_drop_foreign_key;

delimiter $$
create procedure proc_drop_foreign_key(in tableName varchar(64), in constraintName varchar(64))
begin
    if exists(
        select * from information_schema.table_constraints
        where
            table_schema    = database()     and
            table_name      = tableName      and
            constraint_name = constraintName and
            constraint_type = 'FOREIGN KEY')
    then
        set @query = concat('alter table ', tableName, ' drop foreign key ', constraintName, ';');
        prepare stmt from @query;
        execute stmt;
        deallocate prepare stmt;
    end if;
end$$
delimiter ;