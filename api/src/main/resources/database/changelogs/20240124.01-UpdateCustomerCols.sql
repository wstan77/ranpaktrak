--liquibase formatted sql
--changeset tieds:UpdateCustomerCols endDelimiter:go

alter table customers add district varchar(256) null;
go

alter table customers alter column state_lk varchar(256) null;
go

alter table customers alter column country_lk varchar(256) null;
go

EXEC sp_rename 'customers.state_lk', 'state', 'COLUMN';
go

EXEC sp_rename 'customers.country_lk', 'country', 'COLUMN';
go