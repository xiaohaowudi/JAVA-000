package com.javacamp.SeparateReadWriteDemoSimple.Service;

import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadWriteDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.Model.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service("rebuildOrderTableService")
public class RebuildOrderTableServiceImpl implements RebuildOrderTableService {
    @Resource(name = "readWriteDataSource")
    ReadWriteDataSource dataSource;

    @Override
    public void rebuildOrderTable() {
        Connection conn = null;

        try {
            conn = dataSource.getRwConnection();
            conn.setAutoCommit(false);

            PreparedStatement stat = conn.prepareStatement("drop table if exists OrderBaseInfo;");
            stat.execute();

            stat = conn.prepareStatement("CREATE TABLE IF NOT EXISTS OrderBaseInfo (\n" +
                    "    order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单id',\n" +
                    "    supplier_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '卖方用户id',\n" +
                    "    customer_user_id BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '买方用户id',\n" +
                    "    status TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '订单状态',\n" +
                    "    trade_no VARCHAR(100) NOT NULL DEFAULT '' COMMENT '支付交易号',\n" +
                    "\n" +
                    "    create_time_stamp BIGINT UNSIGNED NOT NULL COMMENT '创建订单时间戳',\n" +
                    "    modify_time_stamp BIGINT UNSIGNED NOT NULL COMMENT '订单基本信息变更时间戳',\n" +
                    "    PRIMARY KEY(order_id)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '订单表';");
            stat.execute();

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
