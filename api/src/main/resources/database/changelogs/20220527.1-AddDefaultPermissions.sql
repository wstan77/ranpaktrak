--liquibase formatted sql
--changeset sbeller:AddDefaultPermissions endDelimiter:go

INSERT INTO lk_permission_levels (permission_level_lk, description, short_description, seq_no) VALUES
    ('NONE', 'None', '-', 0),
    ('READ', 'Read', 'R', 1),
    ('EDIT', 'Edit', 'E', 2),
    ('DELETE', 'Delete', 'D', 3);

INSERT INTO lk_permission_targets (permission_target_lk, description, short_description, seq_no) VALUES
    ('OWN', 'Own', 'Own', 1),
    ('SUB', 'Subordinate', 'Sub', 2),
    ('ALL', 'All', 'All', 3);

INSERT INTO lk_permissions (permission_lk, description, short_description, has_target_flg, has_level_flg, default_level_lk, seq_no) VALUES
    ('MANAGE_UTILS', 'Manage Users, Roles, and Permissions', 'Manage Users', 0, 0, 'NONE', 10000),
    ('MANAGE_SYSTEM', 'Manage System', 'Manage System', 0, 0, 'NONE', 20000),
    ('NOTES', 'Notes', 'Notes', 1, 1, 'NONE', 30000),
    ('PERSONS', 'Persons', 'Persons', 1, 1, 'NONE', 11000)
;

INSERT INTO roles (role_name, role_name_short, seq_no) VALUES
    ('ADMIN', 'Admin', 10)
;

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
  SELECT r.role_id, p.permission_lk, t.permission_target_lk, case when p.has_level_flg = 1 then 'DELETE' else 'READ' end
  FROM roles r
  CROSS JOIN lk_permissions p
  JOIN lk_permission_targets t ON p.has_target_flg = 1 or t.permission_target_lk = 'ALL'
  WHERE role_name = 'ADMIN'
;

INSERT INTO user_roles (user_id, role_id)
    SELECT u.user_id, r.role_id
    FROM users u
    LEFT JOIN roles r ON r.role_name = 'ADMIN'
    WHERE username = 'admin'
;
