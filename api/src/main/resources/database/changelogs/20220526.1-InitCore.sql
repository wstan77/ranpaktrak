--liquibase formatted sql
--changeset sbeller:InitCore endDelimiter:go

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_record_status')
BEGIN
    CREATE TABLE lk_record_status
    (
        record_status_lk varchar(5) PRIMARY KEY,
        description varchar(50) NOT NULL,
        short_description varchar(15) NOT NULL,
        active_flg bit NOT NULL DEFAULT 1,
        seq_no smallint,
        create_user_id int NOT NULL DEFAULT 0,
        create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_change_user_id int NOT NULL DEFAULT 0,
        last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
        system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
    )
END

-- Table: users

-- DROP TABLE IF EXISTS users;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
	CREATE TABLE users
  (
      user_id int IDENTITY(1,1) PRIMARY KEY,
      record_status_lk varchar(5) NOT NULL,
      username varchar(50) NOT NULL,
      last_name varchar(100),
      first_name varchar(100),
      email_address varchar(75),
      email_verified_flg bit DEFAULT 0,
      last_login_date datetime2(6),
      create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
      create_user_id int NOT NULL DEFAULT 0,
      last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
      last_change_user_id int NOT NULL DEFAULT 0,
      system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT fk_users_lk_record_status FOREIGN KEY (record_status_lk)
          REFERENCES lk_record_status (record_status_lk)
    )
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
	CREATE TABLE roles
(
    role_id int IDENTITY(1,1) PRIMARY KEY,
    role_name varchar(100) NOT NULL,
    role_name_short varchar(15),
    seq_no smallint,
    active_flg bit NOT NULL DEFAULT 1,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
END

GO
-- Table: user_roles

-- DROP TABLE IF EXISTS user_roles;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_roles')
BEGIN
	CREATE TABLE user_roles
(
    user_id int NOT NULL,
    role_id int NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id)
        REFERENCES roles (role_id)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_activities')
BEGIN
	CREATE TABLE user_activities
(
    user_activity_id int IDENTITY(1,1) PRIMARY KEY,
    activity_lk varchar(200) NOT NULL,
    resource_id int,
    related_id int,
    related_number numeric,
    related_string varchar(255),
    related_date varchar(100),
    related_bool bit NOT NULL DEFAULT 0,
    related_object_string varchar(max),
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_agent varchar(max),
    browser_name varchar(100),
    browser_version varchar(10),
    os_name varchar(100),
    os_version varchar(10),
    device_type varchar(100)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_note_types')
BEGIN
	CREATE TABLE lk_note_types
(
    note_type_lk varchar(5) NOT NULL,
    description varchar(50) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT lk_note_types_pkey PRIMARY KEY (note_type_lk)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'notes')
BEGIN
	CREATE TABLE notes
(
    note_id int IDENTITY(1,1) PRIMARY KEY,
    note_type_lk varchar(5) NOT NULL,
    note_date date NOT NULL,
    subject varchar(150) NOT NULL,
    comments varchar(max),
    attachment varbinary(max),
    file_name varchar(250),
    file_ext varchar(20),
    record_status_lk varchar(5) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notes_lk_note_types_note_type_lk FOREIGN KEY (note_type_lk)
        REFERENCES lk_note_types (note_type_lk),
    CONSTRAINT fk_notes_lk_record_statuses_record_status_lk FOREIGN KEY (record_status_lk)
        REFERENCES lk_record_status (record_status_lk)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_permission_targets')
BEGIN
	CREATE TABLE lk_permission_targets
(
    permission_target_lk varchar(5) NOT NULL,
    description varchar(50) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_user_id int NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT lk_permission_targets_pkey PRIMARY KEY (permission_target_lk)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_permission_levels')
BEGIN
	CREATE TABLE lk_permission_levels
(
    permission_level_lk varchar(10) NOT NULL,
    description varchar(50) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_user_id int NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT lk_permission_levels_pkey PRIMARY KEY (permission_level_lk)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lk_permissions')
BEGIN
	CREATE TABLE lk_permissions
(
    permission_lk varchar(15) NOT NULL,
    has_target_flg bit NOT NULL DEFAULT 0,
    has_level_flg bit NOT NULL DEFAULT 0,
    description varchar(50) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_user_id int NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    default_level_lk varchar(10) NOT NULL DEFAULT 'NONE',
    CONSTRAINT lk_permissions_pkey PRIMARY KEY (permission_lk),
    CONSTRAINT fk_lk_permissions_permission_level_lk FOREIGN KEY (default_level_lk)
        REFERENCES lk_permission_levels (permission_level_lk)
)
END

GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'role_permissions')
BEGIN
	CREATE TABLE role_permissions
(
    role_id int NOT NULL,
    permission_lk varchar(15) NOT NULL,
    permission_target_lk varchar(5) NOT NULL,
    permission_level_lk varchar(10) NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id int NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_lk, permission_target_lk),
    CONSTRAINT fk_role_permissions_permission_level_lk FOREIGN KEY (permission_level_lk)
        REFERENCES lk_permission_levels (permission_level_lk),
    CONSTRAINT fk_role_permissions_permission_permission_lk FOREIGN KEY (permission_lk)
        REFERENCES lk_permissions (permission_lk),
    CONSTRAINT fk_role_permissions_target_lk FOREIGN KEY (permission_target_lk)
        REFERENCES lk_permission_targets (permission_target_lk)
)
END

GO

IF NOT EXISTS(SELECT * FROM sys.indexes WHERE name = 'ix_role_permissions_role_id_permission_lk')
  BEGIN
    CREATE INDEX ix_role_permissions_role_id_permission_lk
        ON role_permissions
        (role_id ASC, permission_lk ASC);
  END
GO

IF NOT EXISTS(SELECT * FROM sys.indexes WHERE name = 'ix_role_permissions_role_permission')
  BEGIN
    CREATE INDEX ix_role_permissions_role_permission
        ON role_permissions
        (role_id ASC, permission_lk ASC, permission_target_lk ASC);
  END
GO
