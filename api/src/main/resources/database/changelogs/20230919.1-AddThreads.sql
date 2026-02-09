--liquibase formatted sql
--changeset sbeller:AddThreads endDelimiter:go

CREATE TABLE threads
(
    thread_id int IDENTITY(1,1) PRIMARY KEY,
    subject varchar(150) NOT NULL,
    reference_type_lk varchar(10) NOT NULL,
    reference_id integer NOT NULL,
    record_status_lk varchar(5) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_threads_reference_type_lk FOREIGN KEY (reference_type_lk)
        REFERENCES lk_reference_types (reference_type_lk),
    CONSTRAINT fk_threads_record_status_lk FOREIGN KEY (record_status_lk)
        REFERENCES lk_record_status (record_status_lk)
)

GO

CREATE TABLE thread_messages
(
    thread_message_id int IDENTITY(1,1) PRIMARY KEY,
    thread_id int NOT NULL,
    comments varchar,
    record_status_lk varchar(5) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_thread_messages_record_status_lk FOREIGN KEY (record_status_lk)
        REFERENCES lk_record_status (record_status_lk)
)

GO

CREATE TABLE thread_message_tags
(
    thread_message_tag_id int IDENTITY(1,1) PRIMARY KEY,
    thread_message_id integer NOT NULL,
    reference_type_lk varchar(10) NOT NULL,
    reference_id integer NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_thread_message_tags_thread_message_id FOREIGN KEY (thread_message_id)
        REFERENCES thread_messages (thread_message_id),
    CONSTRAINT fk_thread_message_tags_reference_type_lk FOREIGN KEY (reference_type_lk)
        REFERENCES lk_reference_types (reference_type_lk)
)

GO

CREATE TABLE thread_message_attachments
(
    thread_message_attachment_id int IDENTITY(1,1) PRIMARY KEY,
    thread_message_id integer NOT NULL,
    file_name varchar(250),
    file_ext varchar(20),
    attachment varbinary(max),
    record_status_lk varchar(5) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_thread_message_attachments_thread_message_id FOREIGN KEY (thread_message_id)
        REFERENCES thread_messages (thread_message_id),
    CONSTRAINT fk_thread_message_attachments_record_status_lk FOREIGN KEY (record_status_lk)
        REFERENCES lk_record_status (record_status_lk)
)

GO

CREATE TABLE thread_read_status
(
  thread_id integer NOT NULL,
  user_id integer NOT NULL,
  last_read_message_id integer NOT NULL,
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user_id integer NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id integer NOT NULL DEFAULT 0,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_thread_read_status PRIMARY KEY (thread_id, user_id),
  CONSTRAINT fk_thread_read_status_thread_id FOREIGN KEY (thread_id)
      REFERENCES threads (thread_id),
  CONSTRAINT fk_thread_read_status_user_id FOREIGN KEY (user_id)
      REFERENCES users (user_id),
  CONSTRAINT fk_thread_read_status_last_read_message_id FOREIGN KEY (last_read_message_id)
      REFERENCES thread_messages (thread_message_id)
)

GO
