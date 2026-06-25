INSERT INTO role (role_name, permissions, description) VALUES
('系统管理员', 'ALL', '系统最高权限，管理所有功能'),
('前台主管', 'room:view,room:update,booking:*,checkin:*,customer:*,bill:view,bill:create,report:view', '前台主管权限'),
('前台接待', 'room:view,booking:create,booking:view,checkin:create,checkin:view,customer:create,customer:view,bill:view', '前台接待权限'),
('财务', 'bill:view,report:*,customer:view,room:view', '财务人员权限');

INSERT INTO user (username, password, real_name, role_id, phone, status) VALUES
('admin', 'kZ1D8lXck7KPsATTIoAfzL2rwEH7/2YODC2nzS61HVi9e+M97X+eNsoOwSxEApoz', '系统管理员', 1, '13800000000', '正常');

INSERT INTO room_type (type_name, bed_type, area, base_price, capacity, description) VALUES
('标准单人间', '单人床', 25.00, 198.00, 1, '经济实惠的标准单人间'),
('标准双人间', '双人床', 30.00, 268.00, 2, '舒适的标准双人间'),
('豪华大床房', '大床', 40.00, 398.00, 2, '宽敞明亮，配备大床'),
('商务套房', '大床', 55.00, 598.00, 2, '商务出行首选，含会客区'),
('总统套房', '大床', 80.00, 1288.00, 4, '顶级奢华总统套房');

INSERT INTO room (room_number, type_id, floor, room_status) VALUES
('101', 1, 1, '空闲'), ('102', 1, 1, '空闲'), ('103', 1, 1, '空闲'),
('201', 2, 2, '空闲'), ('202', 2, 2, '空闲'), ('203', 2, 2, '空闲'),
('301', 3, 3, '空闲'), ('302', 3, 3, '空闲'), ('303', 3, 3, '空闲'),
('401', 4, 4, '空闲'), ('402', 4, 4, '空闲'),
('501', 5, 5, '空闲');