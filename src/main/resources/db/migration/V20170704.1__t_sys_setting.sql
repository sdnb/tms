CREATE TABLE IF NOT EXISTS t_sys_setting (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  recording_path     VARCHAR(200)                   COMMENT '录音存储路径',
  hostname           VARCHAR(200)                   COMMENT 'ip',
  port               INT(10)                        COMMENT '端口',
  create_date        DATETIME                       COMMENT '创建时间',
  modify_date        DATETIME                       COMMENT '修改时间',
  PRIMARY KEY (id)
) COMMENT='系统设置表' DEFAULT CHARSET=utf8;
