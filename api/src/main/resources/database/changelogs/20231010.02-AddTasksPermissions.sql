--liquibase formatted sql
--changeset sbeller:AddTasksPermissions endDelimiter:go

INSERT INTO lk_permissions (permission_lk, description, short_description, has_target_flg, has_level_flg, default_level_lk, seq_no)
VALUES ('TASKS', 'Tasks', 'Tasks', 1, 1, 'READ', 9050)

GO

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
VALUES (1, 'TASKS', 'ALL', 'DELETE'),
    (1, 'TASKS', 'SUB', 'DELETE'),
    (1, 'TASKS', 'OWN', 'DELETE'),
    (2, 'TASKS', 'ALL', 'NONE'),
    (2, 'TASKS', 'SUB', 'NONE'),
    (2, 'TASKS', 'OWN', 'READ')

GO

