--liquibase formatted sql
--changeset sbeller:vw_person_phone_numbers runOnChange:true endDelimiter:go
IF OBJECT_ID('dbo.vw_person_phone_numbers', 'V') IS NOT NULL
    DROP VIEW dbo.vw_person_phone_numbers
GO

CREATE VIEW dbo.vw_person_phone_numbers AS
    SELECT
        ppn.person_id
        ,n.phone_number_id
        ,n.phone_type_lk
        ,n.phone_number
    FROM person_phone_numbers ppn
    JOIN phone_numbers n ON n.phone_number_id = ppn.phone_number_id

GO
