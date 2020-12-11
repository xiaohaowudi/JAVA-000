package com.java_camp.distributed_transaction_at.sql_reverse;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// 针对Inverse语句的生成反向SQL的实现类
public class SqlReverseInsert implements SqlReverse {
    @Override
    public String getReverseSql(String sql, String primaryKey) {
        final int MIN_WORD_NUM = 6;

        // 简单字符串处理，把主键和表名提取出来组装一个delete sql语句
        String lower = sql.toLowerCase();
        String[] ss = lower.split("[\\s+,()]");

        List<String> sList = new ArrayList<>(ss.length);
        for (String s : ss) {
            if (!s.trim().equals("")) {
                sList.add(s.trim());
            }
        }

        if (sList.size() < 6) {
            return null;
        }

        if (! (sList.get(0).equals("insert") && sList.get(1).equals("into")) ) {
            return null;
        }

        String tblName = sList.get(2);
        int l = 3, r = sList.size()-1;
        if ( (r-l+1) % 2 != 1) {
            return null;
        }

        if (!sList.get((l+r)>>1).equals("values")) {
            return null;
        }

        String keyVal = null;
        int ii = l, jj = ((l+r)>>1) + 1;
        while (jj < sList.size()) {
            if (sList.get(ii).equals(primaryKey)) {
                keyVal = sList.get(jj);
                break;
            }
            ii++;
            jj++;
        }

        if (keyVal == null) {
            return null;
        }

        return "delete from " + tblName + " where " + primaryKey + " = " + keyVal + ";";
    }
}
