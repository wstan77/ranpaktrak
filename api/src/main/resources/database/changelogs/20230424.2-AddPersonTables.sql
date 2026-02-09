--liquibase formatted sql
--changeset sbeller:AddPersonTables endDelimiter:go

CREATE TABLE "addresses" (
	"address_id" int IDENTITY(1,1) PRIMARY KEY,
	"address_type_lk" varchar(5) NOT NULL,
	"address" varchar(250) NOT NULL,
	"city" varchar(150) NOT NULL,
	"state_lk" varchar(5),
	"country_lk" varchar(5) NOT NULL,
	"zip" varchar(20),
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_addresses_address_type_lk" FOREIGN KEY ("address_type_lk") REFERENCES "lk_address_types"("address_type_lk")
)

GO

CREATE TABLE "phone_numbers" (
	"phone_number_id" int IDENTITY(1,1) PRIMARY KEY,
	"phone_type_lk" varchar(5) NOT NULL,
	"phone_number" varchar(25) NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "fk_phone_numbers_phone_type_lk" FOREIGN KEY ("phone_type_lk") REFERENCES "lk_phone_types"("phone_type_lk")
)

GO

CREATE TABLE "emails" (
	"email_id" int IDENTITY(1,1) PRIMARY KEY,
	"email_type_lk" varchar(5) NOT NULL,
	"email" varchar(255) NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "fk_emails_email_type_lk" FOREIGN KEY ("email_type_lk") REFERENCES "lk_email_types"("email_type_lk")
)

GO

CREATE TABLE "persons" (
	"person_id" int IDENTITY(1,1) PRIMARY KEY,
	"record_status_lk" varchar(5) NOT NULL,
	"person_type_lk" varchar(5),
	"pronouns" varchar(50),
	"first_name" varchar(50),
	"legal_first_name" varchar(50),
	"last_name" varchar(50),
	"legal_last_name" varchar(50),
	"middle_name" varchar(50),
	"display_name" varchar(100),
	"date_of_birth" date,
	"gender_lk" varchar(5),
	"primary_language_lk" varchar(5),
	"secondary_language_lk" varchar(5),
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT "fk_persons_person_type_lk" FOREIGN KEY ("person_type_lk") REFERENCES "lk_person_types"("person_type_lk"),
  CONSTRAINT "fk_persons_gender_lk" FOREIGN KEY ("gender_lk") REFERENCES "lk_genders"("gender_lk"),
  CONSTRAINT "fk_persons_primary_language_lk" FOREIGN KEY ("primary_language_lk") REFERENCES "lk_languages"("language_lk"),
  CONSTRAINT "fk_persons_secondary_language_lk" FOREIGN KEY ("secondary_language_lk") REFERENCES "lk_languages"("language_lk"),
  CONSTRAINT "fk_persons_record_status_lk" FOREIGN KEY ("record_status_lk") REFERENCES "lk_record_status"("record_status_lk")
)

GO
