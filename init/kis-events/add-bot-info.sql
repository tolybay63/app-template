delete from Channel where id = 2;

insert into Channel (id, type, name, data)
values (2, 2, 'Telegram Bot', jsonb_build_object('token', :'token', 'username', :'username'));