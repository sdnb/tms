CREATE TABLE IF NOT EXISTS t_account (
  id           INT(11)        UNSIGNED      AUTO_INCREMENT,
  username     VARCHAR(30)    NOT NULL      COMMENT '用户名',
  password     VARCHAR(100)                 COMMENT '密码',
  salt         VARCHAR(30)                  COMMENT 'salt',
  modify_date  DATETIME                     COMMENT '更新时间',
  create_date  DATETIME                     COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY unique_username (username)
) COMMENT='账户表' DEFAULT CHARSET=utf8;

INSERT INTO t_account(username,password,salt,create_date) VALUES('admin','faa83f722f4165fe2a37da657fb52f5f','DJng',SYSDATE());