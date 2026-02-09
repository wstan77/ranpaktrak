--liquibase formatted sql
--changeset tieds:DistributorUsers endDelimiter:go

ALTER TABLE users ADD distributor_flg bit NOT NULL DEFAULT 0;
ALTER TABLE users ADD distributor_customer_id int NULL;
alter table users add constraint ref_users_distributor
    foreign key (distributor_customer_id)
    references customers(customer_id);
