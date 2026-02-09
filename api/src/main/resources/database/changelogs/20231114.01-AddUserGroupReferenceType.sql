--liquibase formatted sql
--changeset sbeller:AddUserGroupReferenceType endDelimiter:go

INSERT INTO lk_reference_types(
	reference_type_lk, description, short_description)
	VALUES
	('USER_GROUP', 'User Group', 'User Group')

GO
