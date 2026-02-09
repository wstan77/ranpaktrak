--liquibase formatted sql
--changeset sbeller:AddUserGroupColumnToTasks endDelimiter:go

ALTER TABLE tasks ADD assigned_user_group_id int
GO

ALTER TABLE tasks ADD CONSTRAINT fk_tasks_assigned_user_group_id FOREIGN KEY (assigned_user_group_id)
    REFERENCES user_groups (user_group_id)
GO
