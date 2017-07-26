CREATE TABLE IF NOT EXISTS t_call (
  id                 INT(11)          UNSIGNED      AUTO_INCREMENT,
  res_id             VARCHAR(100)                   COMMENT '呼叫资源id',
  conf_res_id        VARCHAR(100)                   COMMENT '会议资源id',
  phone              VARCHAR(30)                    COMMENT '电话',
  name               VARCHAR(30)                    COMMENT '名字',
  derection          INT(1)                         COMMENT '方向 1 呼入 2 呼出',
  room_id            INT(11)                        COMMENT '会议室id',
  conductor_id       INT(11)                        COMMENT '主持人id',
  status             INT(1)                         COMMENT '呼叫状态',
  voice_mode         INT(1)                         COMMENT '声音收放模式 1 放音+收音 2 收音 3 放音  4 无',
  start_at           DATETIME                       COMMENT '开始时间',
  end_at             DATETIME                       COMMENT '结束时间',
  create_date        DATETIME                       COMMENT '创建时间',
  modify_date        DATETIME                       COMMENT '修改时间',
  PRIMARY KEY (id)
) COMMENT='呼叫记录表' DEFAULT CHARSET=utf8;