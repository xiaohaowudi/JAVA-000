路径 https://github.com/xiaohaowudi/JAVA-000/tree/main/Week_07/%E5%91%A8%E5%85%AD%E4%BD%9C%E4%B8%9A/code/SerparateReadWriteDemoSimple 下为读写分离方案V1实现，手工分别用Spring的IOC注入和AOP拦截方式实现了两种读写分离，并在Router中实现了读操作的负载均衡，支持多个从节点在Configuration包下的配置类中进行添加，经单元测试数据读写OK，读写分离路由有效，读操作能在多个从节点中均衡路由

路径 https://github.com/xiaohaowudi/JAVA-000/tree/main/Week_07/%E5%91%A8%E5%85%AD%E4%BD%9C%E4%B8%9A/code/sharding_jdbc_separate_read_write 下为读写分离方案V2实现，用Sharding-JDBC驱动方式在客户端驱动层实现了读写分离的路由，在application.properties文件中对读写分离的策略和数据源进行了配置，配置信息如下：


```

# 读写分离配置文件

# 水平分库，两个数据源
spring.shardingsphere.datasource.names=m0,s0

# 一个实体类对应多个表，允许覆盖
spring.main.allow-bean-definition-overriding=true

# 主数据源
spring.shardingsphere.datasource.m0.url=jdbc:mysql://192.168.3.144:3306/order_info?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.m0.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.m0.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.m0.username=root
spring.shardingsphere.datasource.m0.password=123456

# 从数据源
spring.shardingsphere.datasource.s0.url=jdbc:mysql://192.168.3.145:3306/order_info?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.s0.type=com.alibaba.druid.pool.DruidDataSource
spring.shardingsphere.datasource.s0.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.s0.username=root
spring.shardingsphere.datasource.s0.password=123456

# 主从共同构成逻辑数据源ds0, 一主一从
spring.shardingsphere.sharding.master-slave-rules.ds0.master-data-source-name=m0
spring.shardingsphere.sharding.master-slave-rules.ds0.slave-data-source-names=s0

# 指定user_db库里面t_user表专库专用
spring.shardingsphere.sharding.tables.OrderBaseInfo.actual-data-nodes=ds$->{0}.OrderBaseInfo

# 指定主键生成策略
spring.shardingsphere.sharding.tables.OrderBaseInfo.key-generator.column=order_id
spring.shardingsphere.sharding.tables.OrderBaseInfo.key-generator.type=SNOWFLAKE

# 打开日志
spring.shardingsphere.props.sql.show=true

```

配置文件中配置了两个数据源，分别是已经配置好了主从的两个MySQL物理节点，经单元测试进行数据库的CRUD操作，结合Sharding-JDBC调试日志中的信息验证读写分离的路由有效，数据读写操作正常
