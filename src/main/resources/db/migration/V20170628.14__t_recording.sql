CREATE TABLE IF NOT EXISTS t_recording (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  filename           VARCHAR(100)                   COMMENT '文件名',
  start_time         INT(11)          UNSIGNED      COMMENT '操作表id',
  end_time           VARCHAR(50)                    COMMENT '操作人',
  file_path          VARCHAR(200)                   COMMENT '文件存储路径 ',
  conference_no      VARCHAR(50)                    COMMENT '会议编号',
  conference_id      INT(11)          UNSIGNED      COMMENT '会议id',
  room_no            VARCHAR(50)                    COMMENT '会议室编号',
  room_id            INT(11)          UNSIGNED      COMMENT '会议室id',
  create_date        DATETIME                       COMMENT '创建时间',
  modify_date        DATETIME                       COMMENT '修改时间',
  PRIMARY KEY (id),
  UNIQUE KEY (filename)
) COMMENT='录音表';
