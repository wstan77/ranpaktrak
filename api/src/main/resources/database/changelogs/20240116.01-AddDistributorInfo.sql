--liquibase formatted sql
--changeset tieds:AddDistributorInfo endDelimiter:go

alter table customers add distributor_customer_id int null
go

alter table customers add constraint ref_customers_distributor
    foreign key (distributor_customer_id)
    references customers(customer_id)
go