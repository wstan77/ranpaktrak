--liquibase formatted sql
--changeset sbeller:AddLookups endDelimiter:go

CREATE TABLE "lk_person_types" (
	"person_type_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_social_profiles" (
	"social_profile_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_address_types" (
	"address_type_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_source_systems" (
	"source_system_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_countries" (
	"country_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_states" (
	"state_lk" varchar(5) PRIMARY KEY,
	"country_lk" varchar(5) NOT NULL,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT "fk_lk_states_country_lk" FOREIGN KEY ("country_lk") REFERENCES "lk_countries"("country_lk");
)

GO

CREATE TABLE "lk_languages" (
	"language_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_genders" (
	"gender_lk" varchar(5) NOT NULL,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "pk_lk_genders" PRIMARY KEY ("gender_lk")
)

GO

CREATE TABLE "lk_email_types" (
	"email_type_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO

CREATE TABLE "lk_phone_types" (
	"phone_type_lk" varchar(5) PRIMARY KEY,
	"description" varchar(100) NOT NULL,
	"short_description" varchar(15) NOT NULL,
	"seq_no" smallint,
	"active_flg" bit NOT NULL DEFAULT 1,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)

GO
