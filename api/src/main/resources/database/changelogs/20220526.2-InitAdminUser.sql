--liquibase formatted sql
--changeset sbeller:InitAdminUser endDelimiter:go

insert into lk_record_status(record_status_lk, description, short_description, active_flg)
values('AP', 'Approved', 'Approved', 1);

go

insert into users (record_status_lk, username, last_name, first_name)
values('AP', 'admin', 'admin', 'admin');
