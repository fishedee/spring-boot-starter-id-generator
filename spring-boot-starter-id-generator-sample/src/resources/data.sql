drop table if exists my_generator_config;
create table my_generator_config(
    `key` char(32) not null,
    template char(64) not null,
    step integer not null,
    initial_value char(64) not null,
    primary key(`key`)
)engine=innodb default charset=utf8mb4;

insert into my_generator_config(`key`,template,step,initial_value) values
 ('user.user','{id}',10,1000),
 ('order.sales_order','XSDD{year}{month}{day}{id:8}',10,'0');                                                                    ;
