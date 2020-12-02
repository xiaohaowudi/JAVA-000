package com.javacamp.SeparateReadWriteDemoSimple.Router;


import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadOnlyDataSource;
import com.javacamp.SeparateReadWriteDemoSimple.DataSource.ReadWriteDataSource;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Random;

// 利用随机数取模进行读负载均衡的路由实现
@Component("randomRouter")
@Log
public class RandomReadBlRouterImpl implements DataSourceRouter {
    @Autowired
    List<ReadOnlyDataSource> readDataSources;

    @Autowired
    List<ReadWriteDataSource> writeDataSources;

    @Override
    public ReadOnlyDataSource getReadDataSource() {
        int n = readDataSources.size();
        if (n == 0) {
            return null;
        }

        int idx = (n == 1) ? 1 : new Random().nextInt(n);
        log.info("Router select read data source index " + idx + ", candidates number : " + n);
        return readDataSources.get(idx);
    }

    @Override
    public ReadWriteDataSource getReadWriteDataSource() {
        int n = writeDataSources.size();
        return (n == 0) ? null : writeDataSources.get(0);
    }
}
