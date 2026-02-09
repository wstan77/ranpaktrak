--liquibase formatted sql
--changeset sbeller:AddIssueTypeLookupTable endDelimiter:go

CREATE TABLE lk_issue_types (
  issue_type_lk varchar(5) NOT NULL,
  description varchar(50) NOT NULL,
  short_description varchar(15) NOT NULL,
  active_flg bit NOT NULL DEFAULT 1,
  seq_no smallint,
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user_id integer NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id integer NOT NULL DEFAULT 0,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT lk_issue_types_pkey PRIMARY KEY (issue_type_lk)
);
