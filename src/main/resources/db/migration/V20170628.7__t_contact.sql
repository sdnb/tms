CREATE TABLE IF NOT EXISTS t_contact (
  id               INT(11)         UNSIGNED      AUTO_INCREMENT,
  `phone`          VARCHAR(20)     NOT NULL      COMMENT '电话',
  name             VARCHAR(50)     NOT NULL      COMMENT '名字',
  book_id          INT(11)         NOT NULL      COMMENT '所属电话簿',
  modify_date      DATETIME                      COMMENT '更新时间',
  create_date      DATETIME                      COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='联系人' DEFAULT CHARSET=utf8;
