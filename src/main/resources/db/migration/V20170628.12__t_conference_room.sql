CREATE TABLE IF NOT EXISTS t_conference_room (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  `number`           VARCHAR(30)                    COMMENT '编号',
  is_in_use          INT(1)           UNSIGNED      COMMENT '是否在使用中',
  is_record_enable   INT(1)           UNSIGNED      COMMENT '是否可录音',
  ivr_password       VARCHAR (30)                   COMMENT '会议参会密码',
  max_participant    INT(5)           UNSIGNED      COMMENT '最大参会人数',
  modify_date        DATETIME                       COMMENT '更新时间',
  create_date        DATETIME                       COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE  KEY (number)
) COMMENT='会议室表';
