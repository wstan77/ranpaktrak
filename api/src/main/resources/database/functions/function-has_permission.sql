--liquibase formatted sql
--changeset sbeller:has_permission runOnChange:true endDelimiter:go
IF object_id('dbo.has_permission') IS NOT NULL
    DROP FUNCTION dbo.has_permission
GO

CREATE FUNCTION dbo.has_permission
(
	@current_user_id integer,
	@target_permission_lk varchar(20),
	@target_level_lk varchar(10),
	@owner_user_id integer
)
RETURNS integer
begin
    RETURN CASE WHEN EXISTS(
		SELECT *
		FROM dbo.users usr
		LEFT JOIN dbo.user_roles role ON role.user_id = usr.user_id
		LEFT JOIN dbo.lk_permission_levels needsLevel ON needsLevel.permission_level_lk = @target_level_lk
		LEFT JOIN dbo.role_permissions rp ON rp.role_id = role.role_id AND rp.PERMISSION_LK = @target_permission_lk
		LEFT JOIN dbo.lk_permission_levels hasLevel ON hasLevel.permission_level_lk = rp.permission_level_lk
		LEFT JOIN dbo.user_subordinates sub on sub.SUBORDINATE_USER_ID = @owner_user_id and sub.USER_ID = usr.USER_ID
		WHERE
			usr.user_id = @current_user_id
			and (hasLevel.seq_no >= needsLevel.seq_no)
			and (
                rp.permission_target_lk = 'ALL'
                or (rp.permission_target_lk = 'OWN' and @owner_user_id = @current_user_id)
				or (rp.permission_target_lk = 'SUB' and sub.user_id is not null)
            )

	) THEN 1 ELSE 0 END;

end

GO
