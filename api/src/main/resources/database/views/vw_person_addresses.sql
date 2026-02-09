--liquibase formatted sql
--changeset sbeller:vw_person_addresses runOnChange:true endDelimiter:go
IF OBJECT_ID('dbo.vw_person_addresses', 'V') IS NOT NULL
    DROP VIEW dbo.vw_person_addresses
GO

CREATE VIEW dbo.vw_person_addresses AS
    SELECT
        pa.person_id
        ,a.address_id
        ,a.address_type_lk
        ,a.address
        ,a.city
        ,a.state_lk
        ,a.country_lk
        ,a.zip
    FROM person_addresses pa
    JOIN addresses a ON a.address_id = pa.address_id

GO
