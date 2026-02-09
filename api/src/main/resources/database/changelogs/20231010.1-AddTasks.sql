--liquibase formatted sql
--changeset sbeller:AddTasks endDelimiter:go

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_task_statuses')
BEGIN
	CREATE TABLE lk_task_statuses
(
  task_status_lk varchar(10) NOT NULL,
  "description" varchar(100),
  short_description varchar(15),
  seq_no smallint,
  active_flg bit NOT NULL DEFAULT 1,
  create_user_id integer NOT NULL DEFAULT 0,
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id integer NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_lk_task_statuses PRIMARY KEY (task_status_lk)
)
END

GO

INSERT INTO lk_task_statuses (task_status_lk, "description", short_description, seq_no) VALUES
  ('OPEN', 'Open', 'Open', 1),
  ('PROGRESS', 'In Progress', 'In Progress', 2),
  ('COMPLETED', 'Completed', 'Completed', 3),
  ('CANCELLED', 'Cancelled', 'Cancelled', 4)

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'tasks')
BEGIN
	CREATE TABLE tasks
(
    task_id int IDENTITY(1,1) PRIMARY KEY,
    task varchar(150) NOT NULL,
    "description" varchar,
    task_status_lk varchar(10) NOT NULL DEFAULT 'OPEN',
    assigned_user_id integer,
    reference_type_lk varchar(10),
    reference_id integer,
    due_date datetime2(6),
    record_status_lk varchar(5) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_reference_type_lk FOREIGN KEY (reference_type_lk)
        REFERENCES lk_reference_types (reference_type_lk),
    CONSTRAINT fk_tasks_task_status_lk FOREIGN KEY (task_status_lk)
        REFERENCES lk_task_statuses (task_status_lk),
    CONSTRAINT fk_tasks_record_status_lk FOREIGN KEY (record_status_lk)
        REFERENCES lk_record_status (record_status_lk)
)
END
