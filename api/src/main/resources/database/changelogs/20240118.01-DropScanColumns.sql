--liquibase formatted sql
--changeset tieds:DropScanColumns endDelimiter:go

alter table scans drop column batch_record_num;
go

alter table scans drop column batch_record_total;
go