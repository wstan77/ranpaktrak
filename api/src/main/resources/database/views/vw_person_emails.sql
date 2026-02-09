--liquibase formatted sql
--changeset sbeller:vw_person_emails runOnChange:true endDelimiter:go
IF OBJECT_ID('dbo.vw_person_emails', 'V') IS NOT NULL
    DROP VIEW dbo.vw_person_emails
GO

CREATE VIEW dbo.vw_person_emails AS
    SELECT
        pe.person_id
        ,e.email_id
        ,e.email_type_lk
        ,e.email
    FROM person_emails pe
    JOIN emails e ON e.email_id = pe.email_id

GO
