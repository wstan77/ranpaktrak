--liquibase formatted sql
--changeset sbeller:AddAssociationTables endDelimiter:go

CREATE TABLE "person_social_profiles" (
	"person_social_profile_id" int IDENTITY(1,1) PRIMARY KEY,
	"person_id" integer NOT NULL,
	"social_profile_lk" varchar(5) NOT NULL,
	"user_name" varchar(100) NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "fk_person_social_profiles_person_id" FOREIGN KEY ("person_id") REFERENCES "persons"("person_id"),
  CONSTRAINT "fk_person_social_profiles_social_profile_lk" FOREIGN KEY ("social_profile_lk") REFERENCES "lk_social_profiles"("social_profile_lk")
)

GO

CREATE TABLE "person_xrefs" (
	"source_system_lk" varchar(5) NOT NULL,
	"source_id" varchar(255) NOT NULL,
	"person_id" integer NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "pk_person_xrefs" PRIMARY KEY ("source_system_lk","source_id","person_id"),
  CONSTRAINT "fk_person_xrefs_source_system_lk" FOREIGN KEY ("source_system_lk") REFERENCES "lk_source_systems"("source_system_lk"),
  CONSTRAINT "fk_person_xrefs_person_id" FOREIGN KEY ("person_id") REFERENCES "persons"("person_id")
)

GO

CREATE TABLE "person_addresses" (
	"address_id" integer NOT NULL,
	"person_id" integer NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "pk_person_addresses" PRIMARY KEY ("address_id","person_id"),
  CONSTRAINT "fk_person_addresses_person_id" FOREIGN KEY ("person_id") REFERENCES "persons"("person_id")
)

GO

CREATE TABLE "person_phone_numbers" (
	"phone_number_id" integer NOT NULL,
	"person_id" integer NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "pk_person_phone_numbers" PRIMARY KEY ("phone_number_id","person_id"),
  CONSTRAINT "fk_person_phone_numbers_person_id" FOREIGN KEY ("person_id") REFERENCES "persons"("person_id")
)

GO

CREATE TABLE "person_emails" (
	"email_id" integer NOT NULL,
	"person_id" integer NOT NULL,
    "create_user_id" integer NOT NULL DEFAULT 0,
    "create_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_change_user_id" integer NOT NULL DEFAULT 0,
    "last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "system_last_change_date" datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT "pk_person_emails" PRIMARY KEY ("email_id","person_id"),
  CONSTRAINT "fk_person_emails_person_id" FOREIGN KEY ("person_id") REFERENCES "persons"("person_id")
)

GO
