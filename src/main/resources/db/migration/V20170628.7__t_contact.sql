CREATE TABLE IF NOT EXISTS t_contact (
  id               INT(11)         UNSIGNED      AUTO_INCREMENT,
  `phone`          VARCHAR(20)        NOT NULL      COMMENT '电话',
  name             VARCHAR(50)     NOT NULL      COMMENT '名字',
  modify_date      DATETIME                      COMMENT '更新时间',
  create_date      DATETIME                      COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE  KEY (phone)
) COMMENT='联系人';
