--liquibase formatted sql
--changeset tieds:APIPermissions endDelimiter:go

INSERT INTO lk_permissions (permission_lk, description, short_description, has_target_flg, has_level_flg, default_level_lk, seq_no) VALUES
    ('CUSTOMERS', 'Customers', 'Customers', 0, 0, 'NONE', 12000),
    ('MACHINES', 'Machines', 'Machines', 0, 0, 'NONE', 13000),
    ('SCANS', 'Upload Scans', 'Upload Scans', 0, 0, 'NONE', 14000)
;

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
  SELECT r.role_id, p.permission_lk, 'ALL','READ'
  FROM roles r
  CROSS JOIN lk_permissions p
  WHERE r.role_name = 'ADMIN' and p.permission_lk in ('CUSTOMERS','MACHINES','SCANS')
;

INSERT INTO roles (role_name, role_name_short, seq_no) VALUES
    ('RANPAKUSER', 'Ranpak User', 100),
    ('DISTRIBUTOR', 'Distributor', 200)
;

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
  SELECT r.role_id, p.permission_lk, 'ALL','READ'
  FROM roles r
  CROSS JOIN lk_permissions p
  WHERE r.role_name = 'RANPAKUSER' and p.permission_lk in ('CUSTOMERS','MACHINES','SCANS')
;

INSERT INTO role_permissions (role_id, permission_lk, permission_target_lk, permission_level_lk)
  SELECT r.role_id, p.permission_lk, 'ALL','READ'
  FROM roles r
  CROSS JOIN lk_permissions p
  WHERE r.role_name = 'DISTRIBUTOR' and p.permission_lk in ('CUSTOMERS','MACHINES','SCANS')
;