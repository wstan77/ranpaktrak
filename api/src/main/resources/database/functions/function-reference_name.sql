--liquibase formatted sql
--changeset sbeller:reference_name runOnChange:true endDelimiter:go

IF object_id('dbo.reference_name') IS NOT NULL
    DROP FUNCTION dbo.reference_name
GO

CREATE FUNCTION dbo.reference_name
(
	@reference_type_lk varchar(max),
	@reference_id integer
)
RETURNS varchar(max)
begin
    RETURN coalesce (case
        when @reference_type_lk = 'PERSON' then
          (SELECT p.display_name from persons p where p.person_id = @reference_id and p.record_status_lk != 'DL')
        when @reference_type_lk = 'USER' then
          (SELECT concat(u.first_name, ' ', u.last_name) from users u where u.user_id = @reference_id and u.record_status_lk != 'DL')
      end,
      'Archived')
end

GO
