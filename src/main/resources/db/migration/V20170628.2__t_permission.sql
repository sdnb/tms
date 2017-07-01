CREATE TABLE IF NOT EXISTS t_permission (
  id              int(11)        UNSIGNED     AUTO_INCREMENT,
  url             VARCHAR(100)                COMMENT '资源路径',
  name            VARCHAR(50)                 COMMENT '资源名称',
  parent_id       INT(11)                     COMMENT '父级id 如果无父级则为0',
  type            INT(1)                      COMMENT '类型 1 菜单 2 api',
  modify_date     DATETIME                    COMMENT '更新时间',
  create_date     DATETIME                    COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='资源权限表';
