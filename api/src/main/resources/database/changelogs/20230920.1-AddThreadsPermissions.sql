--liquibase formatted sql
--changeset sbeller:AddThreadsPermissions endDelimiter:go

INSERT INTO lk_permissions (permission_lk, description, short_description, has_target_flg, has_level_flg, default_level_lk, seq_no)
VALUES ('THREADS', 'Threads', 'Threads', 1, 1, 'READ', 9000)

GO

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
VALUES (1, 'THREADS', 'ALL', 'DELETE'),
    (1, 'THREADS', 'SUB', 'DELETE'),
    (1, 'THREADS', 'OWN', 'DELETE'),
    (2, 'THREADS', 'ALL', 'READ'),
    (2, 'THREADS', 'SUB', 'READ'),
    (2, 'THREADS', 'OWN', 'EDIT')

GO
