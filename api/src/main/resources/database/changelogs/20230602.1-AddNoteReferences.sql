--liquibase formatted sql
--changeset sbeller:AddNoteReferences endDelimiter:go

CREATE TABLE note_references
(
    note_reference_id int IDENTITY(1,1) PRIMARY KEY,
    note_id integer NOT NULL,
    reference_type_lk varchar(10) NOT NULL,
    reference_id integer NOT NULL,
    create_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_user_id integer NOT NULL DEFAULT 0,
    last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_change_user_id integer NOT NULL DEFAULT 0,
    system_last_change_date datetime2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "fk_note_references_note_id" FOREIGN KEY ("note_id") REFERENCES "notes"("note_id"),
    CONSTRAINT "fk_note_references_reference_type_lk" FOREIGN KEY ("reference_type_lk") REFERENCES "lk_reference_types"("reference_type_lk")
)

GO
