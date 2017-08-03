INSERT INTO t_permission (id, name,url, parent_id, type) VALUES
(1,'主页','/',0,1),
(2,'会议','/tms',0,1),
(3,'会议管理','/tms/hy',1,1),
(4,'电话簿','/tms/dhb',1,1),
(5,'录音','/tms/ly',1,1),
(6,'系统管理','/tms/xt',1,1),
(7,'主持人管理','/tms/xt/zcr',2,1),
(8,'会议室管理','/tms/xt/hys',2,1),
(9,'修改密码','/tms/xt/gly',2,1),
(10,'日志管理','/tms/xt/rz',2,1),
(11,'系统设置','/tms/xt/lj',2,1);


INSERT INTO t_role_permission (role_id, permission_id)
 VALUES
 (1, 1),
 (1, 2),
 (1, 3),
 (1, 4),
 (1, 5),
 (1, 6),
 (1, 7),
 (1, 8),
 (1, 9),
 (1, 10),
 (1, 11),

 (2, 1),
 (2, 2),
 (2, 3),
 (2, 4),
 (2, 5),
 (2, 6),
 (2, 9);
