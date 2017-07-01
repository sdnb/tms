CREATE TABLE IF NOT EXISTS t_account_role (
  id              INT(11)        UNSIGNED        AUTO_INCREMENT,
  role_id         INT(11)        UNSIGNED        COMMENT '角色id',
  account_id      INT(11)        UNSIGNED        COMMENT '账号id',
  modify_date     DATETIME                       COMMENT '更新时间',
  create_date     DATETIME                       COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='账户角色表';
