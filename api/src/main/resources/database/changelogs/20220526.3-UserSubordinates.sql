--liquibase formatted sql
--changeset sbeller:UserSubordinates endDelimiter:go

-- Table: user_subordinates

-- DROP TABLE IF EXISTS user_subordinates;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_subordinates')
BEGIN
	CREATE TABLE user_subordinates
  (
    user_id integer NOT NULL,
    subordinate_user_id integer NOT NULL,
    description varchar(100),
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_subordinates PRIMARY KEY (user_id, subordinate_user_id),
    CONSTRAINT fk_user_subordinates_sub_users FOREIGN KEY (subordinate_user_id)
        REFERENCES users (user_id),
    CONSTRAINT fk_user_subordinates_users FOREIGN KEY (user_id)
        REFERENCES users (user_id)
  )
END
