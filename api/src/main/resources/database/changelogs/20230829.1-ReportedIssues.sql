--liquibase formatted sql
--changeset sbeller:ReportedIssues endDelimiter:go

CREATE TABLE lk_issue_status (
  issue_status_lk varchar(5) PRIMARY KEY,
  description varchar(50) NOT NULL,
  short_description varchar(15) NOT NULL,
  active_flg bit NOT NULL DEFAULT 1,
  seq_no smallint,
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user_id int NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id int NOT NULL DEFAULT 0,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

INSERT INTO lk_issue_status (issue_status_lk, description, short_description, seq_no) VALUES
  ('NEW', 'New', 'New', 10),
  ('JIRA', 'Created JIRA', 'JIRA', 20),
  ('IP', 'In Progress', 'In Progress', 30),
  ('RES', 'Resolved', 'Resolved', 40),
  ('WONT', 'Won''t Fix', 'Won''t Fix', 100)

GO

CREATE TABLE reported_issues (
  reported_issue_id int IDENTITY(1,1) PRIMARY KEY,
  issue_status_lk varchar(5) NOT NULL DEFAULT 'NEW',
  issue_type_lk varchar(5) NOT NULL DEFAULT 'BADDT',
  description varchar(max) NULL,
  screenshot_path varchar(70),
  video_path varchar(70),
  url varchar(max),
  "log" varchar(max),
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user_id int NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id int NOT NULL DEFAULT 0,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reported_issues_issue_status_lk FOREIGN KEY (issue_status_lk)
      REFERENCES lk_issue_status (issue_status_lk),
  CONSTRAINT fk_reported_issues_issue_type_lk FOREIGN KEY (issue_type_lk)
      REFERENCES lk_issue_types (issue_type_lk)
)

GO

INSERT INTO lk_permissions
    (permission_lk, has_target_flg, has_level_flg, description, short_description, seq_no)
    VALUES('REPORTED_ISSUES', 0, 0, 'Reported Issues', 'Reported Issues', 2050)

GO

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk) VALUES
    (1, 'REPORTED_ISSUES', 'ALL', 'READ')

GO
