CREATE TABLE IF NOT EXISTS t_group (
  id               INT(11)         UNSIGNED      AUTO_INCREMENT,
  `book_id`        INT(11)         NOT NULL      COMMENT '所在电话簿',
  name             VARCHAR(50)     NOT NULL      COMMENT '组名',
  modify_date      DATETIME                      COMMENT '更新时间',
  create_date      DATETIME                      COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='分组' DEFAULT CHARSET=utf8;
