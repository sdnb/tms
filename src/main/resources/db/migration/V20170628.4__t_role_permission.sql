CREATE TABLE IF NOT EXISTS t_role_permission (
  id              INT(11)       UNSIGNED         AUTO_INCREMENT,
  role_id         INT(11)       UNSIGNED         COMMENT '角色id',
  permission_id   INT(11)       UNSIGNED         COMMENT '权限id',
  modify_date     DATETIME      DEFAULT NULL     COMMENT '更新时间',
  create_date     DATETIME      DEFAULT NULL     COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='角色权限表';
