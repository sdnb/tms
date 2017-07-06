CREATE TABLE IF NOT EXISTS t_log (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  oper_table         VARCHAR(50)                    COMMENT '操作表',
  oper_key           INT(11)          UNSIGNED      COMMENT '操作表id',
  operator           VARCHAR(50)                    COMMENT '操作人',
  type               TINYINT(1)       UNSIGNED      COMMENT '操作类型 1 创建 2 修改 3 删除 ',
  modify_date        DATETIME                       COMMENT '更新时间',
  create_date        DATETIME                       COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='日志表' DEFAULT CHARSET=utf8;
