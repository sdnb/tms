CREATE TABLE IF NOT EXISTS t_log (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  oper_res_id        VARCHAR(50)                    COMMENT '操作资源id',
  oper_res_type      INT(1)           UNSIGNED      COMMENT '操作资源类型 1 会议 2 呼叫',
  oper_type          INT(1)           UNSIGNED      COMMENT '操作类型 1 创建 2 操作 3 事件',
  oper_method_id     VARCHAR(100)                   COMMENT '操作资源方法id',
  oper_method_name   VARCHAR(100)                   COMMENT '操作资源方法名称',
  modify_date        DATETIME                       COMMENT '更新时间',
  create_date        DATETIME                       COMMENT '创建时间',
  operator           VARCHAR(50)                    COMMENT '操作人',
  PRIMARY KEY (id)
) COMMENT='日志表' DEFAULT CHARSET=utf8;
