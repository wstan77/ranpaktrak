--liquibase formatted sql
--changeset sbeller:AddAnonymousFlgToUsers endDelimiter:go

ALTER TABLE users ADD anonymous_flg bit NOT NULL DEFAULT 0
