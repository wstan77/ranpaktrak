--liquibase formatted sql
--changeset sbeller:Seed-1 endDelimiter:go

INSERT INTO persons(last_name, first_name, date_of_birth, middle_name, legal_first_name, legal_last_name, record_status_lk, display_name, gender_lk, person_type_lk, primary_language_lk)
VALUES(
    'TestLast',
    'TestFirst',
    '01-01-1990',
    'TestMiddle',
    'LegalFirst',
    'LegalLast',
    'AP',
    'TestFirst TestLast',
    'MAL',
    'EMP',
    'EN'
);
