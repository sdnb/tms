CREATE TABLE IF NOT EXISTS t_phone_book (
  id              INT(11)     UNSIGNED      AUTO_INCREMENT,
  `type`          INT(1)      UNSIGNED      COMMENT '类型 1 系统 2会议室',
  room_id         INT(11)     UNSIGNED      COMMENT '所属会议室id',
  modify_date     DATETIME                  COMMENT '更新时间',
  create_date     DATETIME                  COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='电话簿表';

INSERT INTO t_phone_book (id, type, room_id,modify_date,create_date) VALUE (1, 1, null,now(), now());