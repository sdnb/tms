CREATE TABLE IF NOT EXISTS t_contact_group_relative (
  id               INT(11)         UNSIGNED      AUTO_INCREMENT,
  group_id         INT(11)         UNSIGNED      COMMENT '所在组id',
  contact_id       INT(11)         UNSIGNED      COMMENT '联系人id',
  modify_date      DATETIME                      COMMENT '更新时间',
  create_date      DATETIME                      COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='联系人分组关系表' DEFAULT CHARSET=utf8;
