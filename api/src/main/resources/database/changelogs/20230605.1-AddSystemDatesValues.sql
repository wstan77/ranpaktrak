--liquibase formatted sql
--changeset daniel:20230605.1-AddSystemDatesValues endDelimiter:go

CREATE TABLE "lk_system_date_types"
(
    system_date_type_lk varchar(10) PRIMARY KEY,
    end_date_flg bit NOT NULL DEFAULT 0,
    description varchar(100) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_user_id integer NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "system_values"
(
    system_value_key varchar(20) PRIMARY KEY,
    system_value varchar(100) NOT NULL,
    description varchar(100) NOT NULL,
    short_description varchar(15) NOT NULL,
    active_flg bit NOT NULL DEFAULT 1,
    seq_no smallint,
    create_user_id integer NOT NULL DEFAULT 0,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "system_dates"
(
    system_date_year integer NOT NULL,
    system_date_type_lk varchar(10) NOT NULL,
    start_date datetime2(6) NOT NULL,
    end_date datetime2(6),
    comments varchar(max),
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "system_dates_pkey" PRIMARY KEY (system_date_year, system_date_type_lk),
    CONSTRAINT "fk_system_dates_system_date_type_lk" FOREIGN KEY ("system_date_type_lk") REFERENCES "lk_system_date_types"("system_date_type_lk")
)

GO
