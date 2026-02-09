--liquibase formatted sql
--changeset sbeller:PopulateIssueTypeLks endDelimiter:go

INSERT INTO lk_issue_types (issue_type_lk, description, short_description, seq_no) VALUES
  ('BADDT', 'Bad Data', 'Bad Data', 10),
  ('MISSD', 'Missing Data', 'Missing Data', 20),
  ('BUG', 'Bug / Not Working', 'Bug', 30),
  ('IMPRV', 'Improvement Idea', 'Improvement', 40),
  ('OTHER', 'Other', 'Other', 50);
