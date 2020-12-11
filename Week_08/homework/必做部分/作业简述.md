## 第八周作业

**作业要求：**

(必做)设计对前面的订单表数据进行水平分库分表，拆分2个库，每个库16张表。 并在新结构在演示常见的增删改查操作。代码、sql 和配置文件，上传到 Github

(必做)基于hmily TCC或ShardingSphere的Atomikos XA实现一个简单的分布式 事务应用demo(二选一)，提交到github



**作业路径：**

两个作业代码均放在/code/sharding-jdbc-demo-trans目录下工程中



**实现简述**

笔者在本地192.168.3.144 和 192.168.3.146两台物理机器上部署了独立运行的MySQL实例，分别建立了两个同名库java_camp，两个库中分别建立了16个用于水平切分的子表，两个MySQL上的建表语句如下

```mysql
create database java_camp;
use java_camp;
CREATE TABLE `t_order_base_info_0` (
  `order_id` bigint(20) unsigned NOT NULL COMMENT '订单id',
  `supplier_user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '卖方用户id',
  `customer_user_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '买方用户id',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '订单状态',
  `trade_no` varchar(100) NOT NULL DEFAULT '' COMMENT '支付交易号',
  `create_time_stamp` bigint(20) unsigned NOT NULL COMMENT '创建订单时间戳',
  `modify_time_stamp` bigint(20) unsigned NOT NULL COMMENT '订单基本信息变更时间戳',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

CREATE TABLE `t_order_base_info_1` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_2` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_3` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_4` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_5` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_6` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_7` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_8` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_9` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_10` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_11` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_12` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_13` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_14` like t_order_base_info_0;
CREATE TABLE `t_order_base_info_15` like t_order_base_info_0;

```



Java工程中application.properties中对sharding-jdbc水平分库分表策略进行了配置，按照卖家id的奇偶性进行分库，按照买家id%16的余数进行分表，配置信息如下：

```
# 水平分库，两个数据源
spring.shardingsphere.datasource.names=ds0,ds1


# 一个实体类对应多个表，允许覆盖
spring.main.allow-bean-definition-overriding=true

# 数据库源信息
spring.shardingsphere.datasource.ds0.jdbcUrl=jdbc:mysql://192.168.3.144:3306/java_camp?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds0.username=root
spring.shardingsphere.datasource.ds0.password=123456

spring.shardingsphere.datasource.ds1.jdbcUrl=jdbc:mysql://192.168.3.146:3306/java_camp?serverTimezone=GMT%2B8
spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.ds1.username=root
spring.shardingsphere.datasource.ds1.password=123456

# 指定水平分库分表的分布情况
spring.shardingsphere.sharding.tables.t_order_base_info.actual-data-nodes=ds$->{0..1}.t_order_base_info_$->{0..15}

# 指定主键生成策略
spring.shardingsphere.sharding.tables.t_order_base_info.key-generator.column=order_id
spring.shardingsphere.sharding.tables.t_order_base_info.key-generator.type=SNOWFLAKE


# 指定库的分片策略, 根据supplier_user_id卖家id的奇偶性将数据分配到两个库
spring.shardingsphere.sharding.tables.t_order_base_info.database-strategy.inline.sharding-column=supplier_user_id
spring.shardingsphere.sharding.tables.t_order_base_info.database-strategy.inline.algorithm-expression=ds$->{supplier_user_id % 2}

# 指定表的水平分片策略，根据customer_user_id买家的id % 16 的余数分配到16张不同表
spring.shardingsphere.sharding.tables.t_order_base_info.table-strategy.inline.sharding-column=customer_user_id
spring.shardingsphere.sharding.tables.t_order_base_info.table-strategy.inline.algorithm-expression=t_order_base_info_$->{customer_user_id % 16}

# 打开日志
spring.shardingsphere.props.sql.show=true

# mybatis扫描目录
mybatis.mapper-locations=/mybatis/*.xml
```



在src/test/java/com/javacmap/shardingjdbcdemo/ShardingJdbcDemoApplicationTests.java文件中编写了针对分库分表的CRUD测试以及针对Atomikos XA分布式事务的测试



testInsertOrder用例测试了分库分表后的数据插入

```java
    // 测试插入1600条数据，可观察数据库中数据是不是在2个库，16张表中均匀分布
    @Test
    public void testInsertOrder() {
        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 160; customer_id++) {
                //order.setOrderId(order_id);
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }
    }
```



总共插入1600条数据，按照supplier_id 和 customer_id两个维度均匀分布，插入数据后在数据库中查询两个库16张表，每张表记录条数均为50，验证了分库分表的有效性

testQueryOrder用例验证了分库分表之后的查询操作

```java
    // 测试从不同的库分片和表分片中查询160条数据，验证是否都能查询到结果
    @Test
    public void testQueryOrder() {

        int total_query_cnt = 0;
        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                OrderBaseInfoExample example = new OrderBaseInfoExample();
                example.createCriteria().andSupplierUserIdEqualTo(supplier_id).andCustomerUserIdEqualTo(customer_id);
                List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
                if (orders == null || orders.size() == 0) {
                    throw new RuntimeException("query fail!");
                }

                total_query_cnt += orders.size();

                for (OrderBaseInfo order : orders) {
                    log.info(order.toString());
                }
            }
        }

        log.info("total_query_cnt = " + total_query_cnt);
        if (total_query_cnt != 160) {
            throw new RuntimeException("total_query_cnt is not right, right answer is 160, but test result is " + total_query_cnt);
        }
    }

```



通过观察sharding-jdbc日志，查询语句分别到2个库16张表中分别进行，且正常返回160条查询结果，验证分库分表后查询操作的正确性



testUpdateOrder用例测试了数据更新功能

```java
// 测试修改记录再做查询, 先查询卖家是5，买家是8的所有订单信息，将订单中trade_no进行修改，然后再查询验证新的trade_no是否是修改之后的数值
    @Test
    public void testUpdateOrder() {
        OrderBaseInfoExample example = new OrderBaseInfoExample();
        example.createCriteria().andSupplierUserIdEqualTo(5L).andCustomerUserIdEqualTo(8L);

        List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
        for (OrderBaseInfo order : orders) {
            log.info(order.toString());

            // 限定查找记录的supplier_id和customer_id，只会触发一个库一个表的更新操作
            OrderBaseInfoExample updateExample = new OrderBaseInfoExample();
            updateExample.createCriteria()
                    .andSupplierUserIdEqualTo(order.getSupplierUserId())
                    .andCustomerUserIdEqualTo(order.getCustomerUserId())
                    .andOrderIdEqualTo(order.getOrderId());


            order.setTradeNo("this is a test trade_no");
            orderBaseInfoMapper.updateByExample(order, updateExample);
        }

        log.info("#############################################");

        // 重新读取记录
        orders = orderBaseInfoMapper.selectByExample(example);
        for (OrderBaseInfo order : orders) {
            log.info(order.toString());
            if (!order.getTradeNo().equals("this is a test trade_no")) {
                throw new RuntimeException("trade no is not right");
            }
        }
    }
```

先查询先查询卖家是5，买家是8的所有订单信息，将订单中trade_no进行修改，然后再从数据库中将同一条记录读取出来，验证是否为update之后的新记录数值，经验证数据更新功能正确



testDeleteOrder用例测试了分库分表后的删除记录功能

```java
    // 测试删除记录功能，将卖家是5，买家是8的所有订单信息删除掉，然后再次进行查询，验证记录数是否为0
    @Test
    public void testDeleteOrder() {
        OrderBaseInfoExample example = new OrderBaseInfoExample();
        example.createCriteria().andSupplierUserIdEqualTo(5L).andCustomerUserIdEqualTo(8L);

        List<OrderBaseInfo> orders = orderBaseInfoMapper.selectByExample(example);
        log.info("before delete, record number is " + orders.size());

        orderBaseInfoMapper.deleteByExample(example);

        orders = orderBaseInfoMapper.selectByExample(example);
        log.info("after delete, record number is " + orders.size());
        if (orders.size() != 0) {
            throw new RuntimeException("record number should be 0, but record number is " + orders.size());
        }
    }
```

将将卖家是5，买家是8的所有订单信息删除掉，然后再次进行查询，验证记录数是否为0，经测试，所有分片数据库中数据均正确删除



testSuccessTrans用例测试了一个正常的XA事务执行

```java
    // 对正常执行的XA事务进行测试, 插入160条数据，并且关闭junit自动回滚，可观察到到数据库完整插入160条数据
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value=false)  // 禁止JUnis自动回滚
    @ShardingTransactionType(TransactionType.XA)
    public void testSuccessTrans() {

        deleteAllData();

        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                //order.setOrderId(order_id);
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }
    }
```

该用例中禁止了Junit自动回滚，执行了一个正常的XA事务，经验证，事务执行成功，且数据库中总共新增加160条记录，事务正常提交日志如下：

```
2020-12-09 18:03:07.033  INFO 5261 --- [           main] o.s.t.c.transaction.TransactionContext   : Committed transaction for test: [DefaultTestContext@239a307b testClass = ShardingJdbcDemoApplicationTests, testInstance = com.javacmap.shardingjdbcdemo.ShardingJdbcDemoApplicationTests@242ff747, testMethod = testSuccessTrans@ShardingJdbcDemoApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@2a8448fa testClass = ShardingJdbcDemoApplicationTests, locations = '{}', classes = '{class com.javacmap.shardingjdbcdemo.ShardingJdbcDemoApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@16293aa2, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@72d1ad2e, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@6adbc9d, org.springframework.boot.test.web.reactive.server.WebTestClientContextCustomizer@223191a6, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@77fbd92c, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@799f10e1, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@75412c2f], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true]]

2020-12-09 18:03:07.046  INFO 5261 --- [extShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2020-12-09 18:03:07.047  INFO 5261 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
2020-12-09 18:03:07.048  INFO 5261 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2020-12-09 18:03:07.055  INFO 5261 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
2020-12-09 18:03:07.055  INFO 5261 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Shutdown initiated...
2020-12-09 18:03:07.056  INFO 5261 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-2 - Shutdown completed.
2020-12-09 18:03:07.056  INFO 5261 --- [extShutdownHook] c.a.icatch.imp.TransactionServiceImp     : Transaction Service: Entering shutdown (false, 0)...
```

通过日志观察，分布式事务正常提交，且执行成功，据库中总共新增加160条记录，与预期相符



testFailTransRollback用例故意在分布式事务插入数据最后构造异常，触发分布式事务回滚：

```java
    // 测试XA分布式事务回滚, 插入160条数据，最后故意触发异常构造分布式事务回滚，可以观察到所有数据库表中均没有新数据插入，验证分布式事务完整回滚
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ShardingTransactionType(TransactionType.XA)
    public void testFailTransRollback() {
        Random rand = new Random();
        OrderBaseInfo order = new OrderBaseInfo();

        for (long supplier_id = 0; supplier_id < 10; supplier_id++) {
            for (long customer_id = 0; customer_id < 16; customer_id++) {
                order.setSupplierUserId(supplier_id);
                order.setCustomerUserId(customer_id);
                order.setStatus((byte)1);
                order.setTradeNo("abc123456789###" + rand.nextInt(1600));
                order.setCreateTimeStamp(System.currentTimeMillis());
                order.setModifyTimeStamp(System.currentTimeMillis());

                orderBaseInfoMapper.insertSelective(order);
            }
        }

        // 故意触发异常
        int a = 10 / 0;
    }
```

通过观察日志，分布式事务按照预期回滚：

```
2020-12-09 18:06:42.833  INFO 5303 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@239a307b testClass = ShardingJdbcDemoApplicationTests, testInstance = com.javacmap.shardingjdbcdemo.ShardingJdbcDemoApplicationTests@5329f6b3, testMethod = testFailTransRollback@ShardingJdbcDemoApplicationTests, testException = java.lang.ArithmeticException: / by zero, mergedContextConfiguration = [WebMergedContextConfiguration@2a8448fa testClass = ShardingJdbcDemoApplicationTests, locations = '{}', classes = '{class com.javacmap.shardingjdbcdemo.ShardingJdbcDemoApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@16293aa2, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@72d1ad2e, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@6adbc9d, org.springframework.boot.test.web.reactive.server.WebTestClientContextCustomizer@223191a6, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@77fbd92c, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@799f10e1, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@75412c2f], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true]]


java.lang.ArithmeticException: / by zero
```

通过后台读取数据库信息，分库分表后，所有的库和表中没有新增加任何一条记录，验证了分布式事务回滚保证了整体事务的原子性和一致性













