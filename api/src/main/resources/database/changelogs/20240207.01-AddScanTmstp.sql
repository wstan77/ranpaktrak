--liquibase formatted sql
--changeset tieds:AddScanTmstp endDelimiter:go

ALTER TABLE dbo.scans ADD scan_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP;
