作业要求：

(必做):基于电商交易场景(用户、商品、订单)，设计一套简单的表结构，提交DDL的SQL文件到Github(后面2周的作业依然要是用到这个表结构)

DDL建表SQL脚本如下：


```sql

DROP TABLE IF EXISTS UserBaseInfo;
CREATE TABLE UserBaseInfo(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
    user_id BIGINT UNSIGNED UNIQUE NOT NULL COMMENT '用户ID',
    user_name varchar(32) NOT NULL COMMENT '用户名',
    user_pass_hash varchar(32) NOT NULL COMMENT '密码hash数值',
    create_time_stamp INT UNSIGNED NOT NULL COMMENT '创建用户时间戳',

    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '用户基本信息变更时间戳',
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户基本信息';

DROP TABLE IF EXISTS UserDetailedInfo;
CREATE TABLE UserDetailedInfo(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
    user_id BIGINT UNSIGNED UNIQUE NOT NULL COMMENT '用户ID',
    nick_name varchar(32) NOT NULL COMMENT '昵称',
    user_email VARCHAR(100) NOT NULL COMMENT '用户邮箱',
    sex ENUM('0', '1', '2') NOT NULL DEFAULT '0' COMMENT '性别',
    birthday DATE NOT NULL COMMENT '生日日期',
    phone_num VARCHAR(20) NOT NULL COMMENT '用户手机号',

    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '用户详细信息变更时间戳',
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户详细信息';

DROP TABLE IF EXISTS ProductBaseInfo;
CREATE TABLE ProductBaseInfo(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
    product_id BIGINT UNSIGNED UNIQUE NOT NULL COMMENT '商品ID',
    product_name varchar(32) NOT NULL COMMENT '商品名称',
    cate_id BIGINT UNSIGNED NOT NULL COMMENT '商品分类id',

    create_time_stamp INT UNSIGNED NOT NULL COMMENT '创建商品信息时间戳',
    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '商品信息变更时间戳',
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '商品基本信息';

DROP TABLE  IF EXISTS ProductDetailedInfo;
CREATE TABLE ProductDetailedInfo(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
    product_id BIGINT UNSIGNED UNIQUE NOT NULL COMMENT '商品ID',
    title VARCHAR(50) NOT NULL DEFAULT '' COMMENT '商品标题',
    description VARCHAR(100) COMMENT '商品描述',
    num BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '库存',
    price DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '价格',
    is_on_promotion ENUM('0', '1') NOT NULL DEFAULT '0' COMMENT '是否促销',
    promotion_price DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '促销价格',
    is_hot ENUM('0', '1') NOT NULL DEFAULT '0' COMMENT '是否热卖',
    is_on_sale ENUM('0', '1') NOT NULL DEFAULT '0' COMMENT '是否上架',
    is_recommended ENUM('0', '1') NOT NULL DEFAULT '0' COMMENT '是否推荐',

    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '商品详细信息变更时间戳',
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '商品详细信息';


DROP TABLE IF EXISTS OrderBaseInfo;
CREATE TABLE IF NOT EXISTS OrderBaseInfo (
    order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单id',
    supplier_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '卖方用户id',
    customer_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '买方用户id',
    status TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '订单状态',
    trade_no VARCHAR(100) NOT NULL DEFAULT '' COMMENT '支付交易号',

    create_time_stamp INT UNSIGNED NOT NULL COMMENT '创建订单时间戳',
    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '订单基本信息变更时间戳',
    PRIMARY KEY(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '订单表';

DROP TABLE IF EXISTS OrderDetailedInfo;
CREATE TABLE IF NOT EXISTS OrderDetailedInfo(
    order_id BIGINT UNSIGNED NOT NULL COMMENT '订单id',
    customer_address VARCHAR(100) NOT NULL COMMENT '收货地址',
    express_id INT UNSIGNED NOT NULL DEFAULT '0' COMMENT '快递公司id',
    express_no VARCHAR(50) NOT NULL DEFAULT '' COMMENT '快递单号',
    transaction_price DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '订单交易价格',
    product_id BIGINT UNSIGNED NULL DEFAULT '0' COMMENT '商品id',
    product_snapshot_id BIGINT UNSIGNED NOT NULL COMMENT '商品快照id',

    modify_time_stamp INT UNSIGNED NOT NULL COMMENT '商品详细信息变更时间戳',
    PRIMARY KEY(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '订单详情';

```
