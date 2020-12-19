package io.kimmking.dubbo.demo.core.foreign_exchange;



import io.kimmking.dubbo.demo.core.db_oper.MySqlOperUtil;

import java.util.Arrays;

public class ForeignExchangeMySqlImpl implements ForeignExchange {

    private MySqlOperUtil db_util;

    public ForeignExchangeMySqlImpl(String databaseUrl, String username, String password) {
        db_util = new MySqlOperUtil(databaseUrl, username, password);
    }

    @Override
    public boolean holdDollarByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set dollar_hold=dollar_hold + " + amount + " where acct_id = '" + acctId + "' and dollar-dollar_hold >= "+ amount + ";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    @Override
    public boolean holdRmbByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set rmb_hold=rmb_hold + "+ amount +" where acct_id = '"+ acctId +"' and rmb-rmb_hold >= " + amount + ";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    @Override
    public boolean subDollarByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set dollar=dollar-"+ amount +", dollar_hold=dollar_hold-"+ amount + ", rmb=rmb+" + 7*amount + " where acct_id = '"+ acctId + "' and dollar>="+ amount +" and dollar_hold>="+ amount +";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    @Override
    public boolean subRmbByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set rmb=rmb-"+ amount +", rmb_hold=rmb_hold-"+ amount + ", dollar=dollar+" + amount/7 + " where acct_id = '"+ acctId + "' and rmb>="+ amount +" and rmb_hold>="+ amount +";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    @Override
    public boolean addDollarByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set dollar=dollar+" + amount + " where acct_id = '" + acctId + "';"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    @Override
    public boolean addRmbByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
            "update t_account set rmb=rmb+" + amount + " where acct_id = '" + acctId + "';"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }


    // 恢复美元账户冻结固定数值
    @Override
    public boolean recoverHoldDollarByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
                "update t_account set dollar_hold=dollar_hold - " + amount + " where acct_id = '" + acctId + "' and dollar_hold >= "+ amount + ";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }

    // 恢复人民币账户冻结固定数值
    @Override
    public boolean recoverHoldRmbByAccount(String acctId, Long amount) {
        String[] sqls = new String[] {
                "update t_account set rmb_hold=rmb_hold - "+ amount +" where acct_id = '"+ acctId +"' and rmb_hold >= " + amount + ";"
        };

        int[] recCnts = db_util.execSqlInTransaction(Arrays.asList(sqls));
        return recCnts != null && recCnts.length == 1 && recCnts[0] == 1;
    }
}
