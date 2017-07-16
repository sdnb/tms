CREATE TABLE IF NOT EXISTS t_role (
  id              int(11)                UNSIGNED         AUTO_INCREMENT,
  `name`          VARCHAR(100)           NOT     NULL     COMMENT '角色名称',
  modify_date     DATETIME               DEFAULT NULL     COMMENT '更新时间',
  create_date     DATETIME               DEFAULT NULL     COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='角色表' DEFAULT CHARSET=utf8;
