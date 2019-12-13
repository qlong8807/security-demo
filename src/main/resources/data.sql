insert into sys_user (id,username, password) values (1,'admin', 'admin'),(2,'abel', 'abel');

insert into sys_role(id,name,status) values(1,'ADMIN','Y'),(2,'USER','Y');

insert into user_role(id,user_id,role_id) values(1,1,1),(2,2,2);

INSERT INTO `sys_resource`(id,pid,name,url) VALUES (1,0, 'ROLE_HOME', 'home'), (2,0, 'ROLE_ADMIN', '/admin');

INSERT INTO `role_resource`(id,role_id,resource_id) VALUES ('1', '1', '1'), ('2', '1', '2'), ('3', '2', '1');