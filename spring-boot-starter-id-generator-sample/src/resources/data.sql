drop table if exists my_generator_config;
drop table if exists my_try_generator_config;

create table my_generator_config(
    `key` char(32) not null,
    template char(64) not null,
    step integer not null,
    initial_value char(64) not null,
    is_sync tinyint not null,
    primary key(`key`)
)engine=innodb default charset=utf8mb4;

create table my_try_generator_config(
    `key` char(32) not null,
    template char(64) not null,
    initial_value char(64) not null,
    primary key(`key`)
)engine=innodb default charset=utf8mb4;

insert into my_generator_config(`key`,template,step,initial_value,is_sync) values
 ('user.user','{id}',10,1000,0),
 ('order.sales_order','XSDD{year}{month}{day}{id:8}',10,'0',0),
 ('order.purchase_order','CGDD{year}{month}{day}{id:8}',1,'0',1);

insert into my_try_generator_config(`key`,template,initial_value)values
('try_id','{id}','1'),
('try_order_id','XS{year}{id:2+}','');
