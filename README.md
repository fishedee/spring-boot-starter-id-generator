![Release](https://jitpack.io/v/fishedee/spring-boot-starter-id-generator.svg)
(https://jitpack.io/#fishedee/spring-boot-starter-id-generator)

# id_generator

Java的ID生成器，功能：

* 性能好，可配置缓存式id获取，按段获取id，只有当前id段都使用完成后才重新拉数据库
* 灵活，支持id中嵌套固定的数字和字母，以及当前日期的信息。例如配置为XSDD{year}{month}{day}{id:8}，生成id值为XSDD2021082200000010。
* 热部署，可以在数据库中动态配置每个key不同的生成方式
* 同步功能，可以指定key的生成是同步生成，保证无间隙的ID生成
* try_id功能，数据库只返回下一个可能的编号，而不是保证唯一的编号，这样是为了尽可能最小化ID间距
* number功能，只提供根据模板生成Counter的编号，不记录编号下一个值，这样可以根据不同时间来生成最新的编号
* 多租户，支持多租户的ID生成
* 延迟配置，支持使用时才配置id-generator-config

## 安装

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.fishedee</groupId>
        <artifactId>spring-boot-starter-id-generator</artifactId>
        <version>1.19</version>
    </dependency>
</dependencies>

```

在项目的pom.xml加入以上配置即可

## 使用

代码在[这里](https://github.com/fishedee/Demo/tree/master/spring-boot-starter-id-generator/Demo)

```sql
drop table if exists my_generator_config;
drop table if exists my_try_generator_config;
drop table if exists my_number_generator_config;

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

create table my_number_generator_config(
   `key` char(32) not null,
   template char(64) not null,
   primary key("key")
)engine=innodb default charset=utf8mb4;

insert into my_generator_config(`key`,template,step,initial_value,is_sync) values
 ('user.user','{id}',10,1000,0),
 ('order.sales_order','XSDD{year}{month}{day}{id:8}',10,'0',0),
 ('order.purchase_order','CGDD{year}{month}{day}{id:8}',1,'0',1);

insert into my_try_generator_config(`key`,template,initial_value)values
('try_id','{id}','1'),
('try_order_id','XS{year}{id:2+}','');

insert into my_number_generator_config("key",template)values
('number_id','{id}'),
('number_order_id','XS{year}{id:2+}'),
('number_order_id2','XS{year}{month}{id:3+}'),
('number_order_id3','XS{year}{month}{day}{id:4+}');
```

初始化数据库

```ini
spring.id-generator.enable = true
#默认表名为id_generator_config，可自定义
#spring.id-generator.table = my_generator_config
#spring.id-generator.try_table = my_try_generator_config
#spring.id-generator.number-table=my_number_generator_config
#spring.id-generator.begin-escape-character = "
#spring.id-generator.end-escape-character = "
```

初始化

```java
package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGeneratorKey;

@IdGeneratorKey("order.sales_order")
public class SalesOrder {
}
```

SalesOrder.java

```java
package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGeneratorKey;

@IdGeneratorKey("user.user")
public class User {
}
```

User.java

```java
package com.fishedee.id_generator.sample;

import com.fishedee.id_generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }

    @Autowired
    private IdGenerator idGenerator;

    @PostConstruct
    public void init(){
        String userKey = "user.user";
        for( int i = 0 ;i != 10 ;i++){
            log.info("{} {}",userKey,idGenerator.next(userKey));
            log.info("{} {}",userKey,idGenerator.next(new User()));
        }

        String orderKey = "order.sales_order";
        for( int i = 0 ;i != 10 ;i++){
            log.info("{} {}",orderKey,idGenerator.next(orderKey));
            log.info("{} {}",orderKey,idGenerator.next(new SalesOrder()));
        }


    }
}
```

App.java

* 可以用String作为key获取下一个id
* 也可以用Object实例拉取它的@IdGeneratorKey注解作为key获取下一个id



