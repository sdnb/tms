CREATE TABLE IF NOT EXISTS t_room_conductor_relative (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  room_id            INT(30)                        COMMENT '编号',
  conductor_id       INT(11)          UNSIGNED      COMMENT '主持人id',
  conductor_name     VARCHAR (30)                   COMMENT '主持人姓名',
  modify_date        DATETIME                       COMMENT '更新时间',
  create_date        DATETIME                       COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='会议室主持人关联表';
