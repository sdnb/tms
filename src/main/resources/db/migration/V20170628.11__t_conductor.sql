CREATE TABLE IF NOT EXISTS t_conductor (
  id               INT(11)          UNSIGNED      AUTO_INCREMENT,
  `realname`       VARCHAR(50)                    COMMENT '真实名字',
  `username`       VARCHAR(50)                    COMMENT '用户名',
  phone            VARCHAR(20)                    COMMENT '电话',
  account_id       INT(11)          UNSIGNED      COMMENT '登陆账号',
  modify_date      DATETIME                       COMMENT '更新时间',
  create_date      DATETIME                       COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE  KEY (phone)
) COMMENT='会议主持人表';
