CREATE TABLE IF NOT EXISTS t_contact_book_relative (
  id               INT(11)         UNSIGNED      AUTO_INCREMENT,
  book_id          INT(11)         UNSIGNED      COMMENT '所在电话簿id',
  contact_id       INT(11)         UNSIGNED      COMMENT '联系人id',
  modify_date      DATETIME                      COMMENT '更新时间',
  create_date      DATETIME                      COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='联系人电话簿关系表' DEFAULT CHARSET=utf8;
