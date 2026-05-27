-- P3: BCrypt — 将现有用户密码更新为 BCrypt 哈希
-- 密码均为 123456
UPDATE sys_user SET password = '$2b$12$KKdnspRrbnGoXOZF4XU61Ox6O1MHqz7CdMXv/CwX.mGUh1.MljV.G' WHERE username IN ('admin', 'zhangsan');
