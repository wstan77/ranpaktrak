--liquibase formatted sql
--changeset sbeller:vw_permissions runOnChange:true endDelimiter:go
IF OBJECT_ID('dbo.vw_permissions', 'V') IS NOT NULL
    DROP VIEW dbo.vw_permissions
GO

CREATE VIEW dbo.vw_permissions AS
  SELECT usr.user_id as current_user_id, rp.permission_lk, needsLevel.permission_level_lk, rp.permission_target_lk, sub.user_id as owner_user_id
        FROM users usr
        LEFT JOIN user_roles role ON role.user_id = usr.user_id
        LEFT JOIN lk_permission_levels needsLevel ON needsLevel.permission_level_lk != 'NONE'
        LEFT JOIN role_permissions rp ON rp.role_id = role.role_id
        LEFT JOIN lk_permission_levels hasLevel ON hasLevel.permission_level_lk = rp.permission_level_lk
        LEFT JOIN user_subordinates sub on sub.USER_ID = usr.USER_ID
        WHERE
          (hasLevel.seq_no >= needsLevel.seq_no)
          and (
              rp.permission_target_lk = 'ALL'
              or (rp.permission_target_lk = 'OWN' and (sub.user_id is null or sub.user_id = usr.user_id))
              or (rp.permission_target_lk = 'SUB' and sub.user_id is not null)
          )

GO
