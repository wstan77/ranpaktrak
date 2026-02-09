--liquibase formatted sql
--changeset sbeller:AddUserGroups endDelimiter:go

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_groups')
BEGIN
	CREATE TABLE user_groups (
    user_group_id int IDENTITY(1,1) PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    create_user_id INTEGER NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id INTEGER NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
END

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_group_users')
BEGIN
	CREATE TABLE user_group_users (
  user_group_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  create_user_id INTEGER NOT NULL DEFAULT 0,
  create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_change_user_id INTEGER NOT NULL DEFAULT 0,
  last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_user_group_users PRIMARY KEY (user_group_id, user_id),
  CONSTRAINT fk_user_group_users_user_group_id FOREIGN KEY (user_group_id)
    REFERENCES user_groups (user_group_id),
  CONSTRAINT fk_user_group_users_user_id FOREIGN KEY (user_id)
    REFERENCES users (user_id)
)
END
