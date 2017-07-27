#角色权限配置
-- INSERT INTO t_role (id, name) VALUES (1,'管理员'), (2, '主持人');
--
-- INSERT INTO t_permission (id, url, name, parent_id, type)
-- VALUES (1, "", null, 1);
--
-- INSERT INTO t_role_permission (role_id, permission_id)
-- VALUES (1, 1), (1,2)